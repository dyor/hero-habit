package com.dyor.habithero.presentation.screens.celebration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyor.habithero.data.repository.CreditRepository
import com.dyor.habithero.data.repository.GenerationRepository
import com.dyor.habithero.data.repository.HabitRepository
import com.dyor.habithero.data.source.local.dao.ComicCoverDao
import com.dyor.habithero.data.source.local.entity.toEntity
import com.dyor.habithero.data.source.preferences.UserPreferences
import com.dyor.habithero.domain.exceptions.CreditRequiredException
import com.dyor.habithero.domain.exceptions.PurchaseRequiredException
import com.dyor.habithero.domain.model.ComicCover
import com.dyor.habithero.domain.model.HeroRole
import com.dyor.habithero.domain.model.generation.GenerationInput
import com.dyor.habithero.domain.model.generation.generationInput
import com.dyor.habithero.root.AppConfiguration
import com.dyor.habithero.util.file.FileManager
import com.dyor.habithero.util.file.absolutePathCommon
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.extension
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class CelebrationViewModel(
    private val generationRepository: GenerationRepository,
    private val habitRepository: HabitRepository,
    private val comicCoverDao: ComicCoverDao,
    private val creditRepository: CreditRepository,
    private val fileManager: FileManager,
    private val userPreferences: UserPreferences,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CelebrationUiState())
    val uiState: StateFlow<CelebrationUiState> = _uiState.asStateFlow()

    init {
        observeCreditBalance()
    }

    private fun observeCreditBalance() = viewModelScope.launch {
        creditRepository.balance.collectLatest { balance ->
            _uiState.update { it.copy(creditBalance = balance, isCreditBalanceLoaded = true) }
        }
    }

    fun onUiEvent(event: CelebrationUiEvent) {
        when (event) {
            is CelebrationUiEvent.Init -> {
                viewModelScope.launch {
                    val interval = userPreferences.getInt(UserPreferences.KEY_STREAK_CELEBRATION_INTERVAL, 7) ?: 7
                    // Guard streakCount > 0: 0 % interval == 0 would otherwise read as a milestone.
                    val isMilestone = event.streakCount > 0 && (event.streakCount % interval == 0)
                    val nextMilestone = if (isMilestone) event.streakCount + interval else (((event.streakCount / interval) + 1) * interval)
                    val daysLeft = nextMilestone - event.streakCount

                    _uiState.update {
                        it.copy(
                            habitId = event.habitId,
                            habitTitle = event.habitTitle,
                            streakCount = event.streakCount,
                            celebrationInterval = interval,
                            isMilestoneCelebration = isMilestone,
                            nextMilestoneDay = nextMilestone,
                            daysUntilNextMilestone = daysLeft,
                            isDailySelfieSaved = false,
                        )
                    }

                    habitRepository.getHabitById(event.habitId).onSuccess { habit ->
                        _uiState.update { it.copy(customPrompt = habit.customPrompt) }
                    }
                }
            }

            is CelebrationUiEvent.OnSelfieSelected -> handleSelfieSelected(event.file, event.forceDailyCheckIn)

            CelebrationUiEvent.OnGenerateComicCover -> generateComicCover()

            CelebrationUiEvent.OnSaveDailySelfie -> saveDailySelfie()

            CelebrationUiEvent.OnDismissOutOfCreditsDialog -> _uiState.update { it.copy(showOutOfCreditsDialog = false) }

            CelebrationUiEvent.OnInsufficientCreditsForCapture -> _uiState.update { it.copy(showOutOfCreditsDialog = true) }

            CelebrationUiEvent.OnReset -> _uiState.update { CelebrationUiState() }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun saveDailySelfie() = viewModelScope.launch {
        val state = _uiState.value
        val selfieUrl = state.selfieFilePath ?: return@launch

        habitRepository.saveOrUpdateComicCoverForToday(
            habitId = state.habitId,
            habitTitle = state.habitTitle,
            streakNumber = state.streakCount,
            headline = "${state.habitTitle} Day ${state.streakCount} Logged!",
            imageUrl = selfieUrl,
            heroRole = HeroRole.HERO_CHECK_IN,
        ).onSuccess { cover ->
            _uiState.update { it.copy(isDailySelfieSaved = true, generatedComicCover = cover) }
        }
    }

    private fun handleSelfieSelected(file: PlatformFile?, forceDailyCheckIn: Boolean = false) = viewModelScope.launch {
        if (file == null) return@launch

        val originalPath = file.absolutePathCommon()
        val uniqueName = fileManager.createNewUniqueFileNameWithExtension(file.extension)

        fileManager.copyFileToInternalDirectory(
            originalFileAbsolutePath = originalPath,
            newFileName = uniqueName,
        ).onSuccess { copiedName ->
            val absolutePath = fileManager.getAbsoluteFilePathRelativeToInternal(copiedName)
            _uiState.update {
                it.copy(
                    selfieFilePath = absolutePath,
                    selfieFileName = copiedName,
                    generationError = null,
                )
            }
            if (_uiState.value.isMilestoneCelebration && !forceDailyCheckIn) {
                generateComicCover()
            } else {
                saveDailySelfie()
            }
        }.onFailure { error ->
            _uiState.update { it.copy(generationError = "Failed to load photo: ${error.message}") }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun generateComicCover() = viewModelScope.launch {
        val state = _uiState.value
        val selfieName = state.selfieFileName ?: return@launch

        if (AppConfiguration.PREMIUM_FEATURES_ENABLED && state.creditBalance <= 0) {
            _uiState.update { it.copy(isGenerating = false, showOutOfCreditsDialog = true) }
            return@launch
        }

        _uiState.update { it.copy(isGenerating = true, generationError = null) }

        val customScenario = if (state.customPrompt.isNotBlank()) {
            ", special custom action scenario: ${state.customPrompt}"
        } else {
            ""
        }

        val prompt = "Full bleed vertical 9:16 portrait comic book cover, vintage superhero comic book art style, dramatic retro halftone illustration, heroic dynamic action pose. Top masthead banner '${state.habitTitle.uppercase()} DAILY', bold vibrant ribbon with exact text '${state.streakCount} DAY STREAK!' (spelled strictly as S-T-R-E-A-K with 'EA', do not spell as 'streek'). Faithfully preserve the subject's gender identity and natural presentation from the uploaded photo: if the person is a woman or girl, depict a powerful female superhero in the iconic style of Wonder Woman, Superwoman, Spider-Woman, Captain Marvel, or Storm; if the person is a man or boy, depict a heroic male superhero. Retain the exact facial features, likeness, hairstyle elements, recognizable look, and specific facial expression (smile, intensity, gaze, and emotion) of the person in the uploaded selfie photo, seamlessly transforming their face into the superhero protagonist$customScenario. High facial resemblance and subject fidelity, distinctive recognizable face from the photo, correct English typography spelling for 'STREAK', full-bleed edge to edge composition, no white borders, no letterboxing, rich vintage comic artwork."

        val input = generationInput {
            stringParam(key = "prompt", value = prompt)
            stringParam(key = "aspect_ratio", value = "9:16")
            image(key = "image_input", fileNameWithExtension = selfieName)
        }

        generationRepository.generate(input)
            .onSuccess { output ->
                val imagePath = output.output ?: ""
                habitRepository.saveOrUpdateComicCoverForToday(
                    habitId = state.habitId,
                    habitTitle = state.habitTitle,
                    streakNumber = state.streakCount,
                    headline = "${state.streakCount} Day Streak Hero!",
                    imageUrl = imagePath,
                    heroRole = HeroRole.SUPERHERO,
                ).onSuccess { comicCover ->
                    _uiState.update {
                        it.copy(
                            isGenerating = false,
                            generatedComicCover = comicCover,
                            generatedOutput = output,
                        )
                    }
                }
            }
            .onFailure { error ->
                if (error is CreditRequiredException || error is PurchaseRequiredException) {
                    _uiState.update {
                        it.copy(
                            isGenerating = false,
                            showOutOfCreditsDialog = true,
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isGenerating = false,
                            generationError = error.message ?: "Failed to generate comic cover",
                        )
                    }
                }
            }
    }
}
