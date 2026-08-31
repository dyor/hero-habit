package com.dyor.habithero.presentation.screens.celebration

import com.dyor.habithero.domain.model.ComicCover
import com.dyor.habithero.domain.model.generation.GenerationOutput
import io.github.vinceglb.filekit.PlatformFile

data class CelebrationUiState(
    val habitId: String = "",
    val habitTitle: String = "Habit Quest",
    val streakCount: Int = 1,
    val customPrompt: String = "",
    val selfieFilePath: String? = null,
    val selfieFileName: String? = null,
    val isGenerating: Boolean = false,
    val generationError: String? = null,
    val generatedComicCover: ComicCover? = null,
    val generatedOutput: GenerationOutput? = null,
    val isMilestoneCelebration: Boolean = true,
    val celebrationInterval: Int = 7,
    val nextMilestoneDay: Int = 7,
    val daysUntilNextMilestone: Int = 0,
    val isDailySelfieSaved: Boolean = false,
    val creditBalance: Int = 0,
    val showOutOfCreditsDialog: Boolean = false,
) {
    val canGenerate: Boolean get() = !isGenerating && selfieFilePath != null
}

sealed interface CelebrationUiEvent {
    data class Init(val habitId: String, val habitTitle: String, val streakCount: Int) : CelebrationUiEvent
    data class OnSelfieSelected(val file: PlatformFile?) : CelebrationUiEvent
    data object OnGenerateComicCover : CelebrationUiEvent
    data object OnSaveDailySelfie : CelebrationUiEvent
    data object OnDismissOutOfCreditsDialog : CelebrationUiEvent
    data object OnReset : CelebrationUiEvent
}
