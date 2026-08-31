package com.dyor.habithero.data.repository

import com.dyor.habithero.data.BackgroundExecutor
import com.dyor.habithero.data.source.local.dao.GenerationOutputDao
import com.dyor.habithero.data.source.local.entity.toEntity
import com.dyor.habithero.data.source.local.entity.toModel
import com.dyor.habithero.data.source.remote.apiservices.TemporaryFileUploadApiService
import com.dyor.habithero.domain.exceptions.CreditRequiredException
import com.dyor.habithero.domain.exceptions.PurchaseRequiredException
import com.dyor.habithero.domain.model.credit.CreditConstants
import com.dyor.habithero.domain.model.credit.CreditTransaction
import com.dyor.habithero.domain.model.generation.GenerationInput
import com.dyor.habithero.domain.model.generation.GenerationOutput
import com.dyor.habithero.domain.usecase.AiGenerationProvider
import com.dyor.habithero.root.AppConfiguration
import com.dyor.habithero.util.analytics.Analytics
import com.dyor.habithero.util.file.FileManager
import com.dyor.habithero.util.file.mimeTypeForFileName
import com.dyor.habithero.util.logging.AppLogger
import io.ktor.util.encodeBase64
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

/**
 * Orchestrates one AI generation: spend a credit, upload input files to the cloud, call the
 * [AiGenerationService], then cache + persist the [GenerationOutput] locally (Room) so the
 * gallery can observe it. Network work runs through [BackgroundExecutor]. The AI backend is
 * abstracted by [AiGenerationProvider] (Replicate / OpenAI).
 */
class GenerationRepository(
    private val generationOutputDao: GenerationOutputDao,
    private val aiGenerationProvider: AiGenerationProvider,
    private val creditRepository: CreditRepository,
    private val fileManager: FileManager,
    private val analytics: Analytics,
    private val backgroundExecutor: BackgroundExecutor,
    private val temporaryFileUploadApiService: TemporaryFileUploadApiService,
) {
    private val cachedOutputMap = linkedMapOf<String, GenerationOutput>()

    fun observeAllGenerationOutput(): Flow<Result<List<GenerationOutput>>> = generationOutputDao.getAllFlow()
        .map { entities -> Result.success(entities.map { it.toModel(fileManager) }) }
        .catch { error ->
            AppLogger.e("Error observing generation output: $error")
            emit(Result.failure(error))
        }

    suspend fun generate(input: GenerationInput): Result<GenerationOutput> = backgroundExecutor.execute {
        if (AppConfiguration.PREMIUM_FEATURES_ENABLED) {
            creditRepository.useCredits(CreditConstants.COST_GENERATION)
        }
        analytics.logEvent(event = Analytics.EVENT_CLICKED_GENERATE)
        val updatedGenerationInput = input.uploadFilesIntoCloud()

        val aiGenerationOutputResult =
            aiGenerationProvider.generate(input = updatedGenerationInput)

        return@execute aiGenerationOutputResult.map { generationOutput ->
            val output = generationOutput.outputFile?.fileNameWithExtension?.let {
                fileManager.getAbsoluteFilePathRelativeToInternal(it)
            }
            generationOutput.copy(output = output)
        }.onSuccess { generationOutput ->
            cachedOutputMap[generationOutput.id] = generationOutput
            generationOutputDao.upsert(generationOutput.toEntity())
        }.onFailure { error ->
            if (AppConfiguration.PREMIUM_FEATURES_ENABLED &&
                error !is PurchaseRequiredException &&
                error !is CreditRequiredException
            ) {
                creditRepository.addCredits(
                    amount = CreditConstants.COST_GENERATION,
                    type = CreditTransaction.Type.ADMIN_ADJUSTMENT,
                    description = "Refund for failed generation",
                )
            }
        }
    }

    suspend fun getGenerationOutputById(id: String): Result<GenerationOutput> = backgroundExecutor.execute {
        val cachedOutput = cachedOutputMap[id]
        if (cachedOutput != null) return@execute Result.success(cachedOutput)

        val entity = generationOutputDao.getById(id = id)
            ?: return@execute Result.failure(Exception("Output is not found"))

        Result.success(entity.toModel(fileManager))
    }

    suspend fun delete(id: String) = backgroundExecutor.execute {
        cachedOutputMap.remove(id)
        generationOutputDao.deleteById(id = id)
        Result.success(Unit)
    }

    private suspend fun GenerationInput.uploadFilesIntoCloud(): GenerationInput = copy(
        params = params.mapValues { (_, param) ->
            param.mapFileUploadUrls { fileNameWithExtension ->
                val fileBytes = fileManager.readInternalFileBytes(fileNameWithExtension)
                val mimeType = mimeTypeForFileName(fileNameWithExtension)
                val uploadedUrl = try {
                    temporaryFileUploadApiService.upload(
                        bytes = fileBytes,
                        fileNameWithExtension = fileNameWithExtension,
                        contentType = mimeType,
                    ).asDownloadUrl()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    AppLogger.e("File upload failed: ${e.message}")
                    null
                }
                AppLogger.d("File uploaded. Url: $uploadedUrl")
                if (!uploadedUrl.isNullOrBlank() && (uploadedUrl.startsWith("http://") || uploadedUrl.startsWith("https://"))) {
                    uploadedUrl
                } else {
                    // Data URI fallback: 100% valid URI supported natively by Replicate
                    val base64 = fileBytes.encodeBase64()
                    "data:$mimeType;base64,$base64"
                }
            }
        },
    )
}
