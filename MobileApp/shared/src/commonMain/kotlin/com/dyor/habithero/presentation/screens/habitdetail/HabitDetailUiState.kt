package com.dyor.habithero.presentation.screens.habitdetail

import com.dyor.habithero.domain.model.ComicCover
import com.dyor.habithero.domain.model.Habit

data class HabitDetailUiState(
    val habit: Habit? = null,
    val comicCovers: List<ComicCover> = emptyList(),
    val customPromptInput: String = "",
    val isSavingPrompt: Boolean = false,
    val isGeneratingPrompt: Boolean = false,
    val showSaveSuccessBanner: Boolean = false,
    val selectedCoverIndex: Int? = null,
    val showEditTitleDialog: Boolean = false,
    val editTitleInput: String = "",
    val showDatePickerForCover: ComicCover? = null,
    val showDatePickerForHabit: Boolean = false,
    val showAddPastEntryDialog: Boolean = false,
    val showDeleteCoverConfirm: ComicCover? = null,
    val showDeleteHabitConfirm: Boolean = false,
    val isHabitDeleted: Boolean = false,
    val isLoading: Boolean = true,
)

sealed interface HabitDetailUiEvent {
    data class OnCustomPromptChange(val prompt: String) : HabitDetailUiEvent
    data object OnSaveCustomPrompt : HabitDetailUiEvent
    data object OnGenerateAiPrompt : HabitDetailUiEvent
    data class OnClickCover(val index: Int) : HabitDetailUiEvent
    data object OnDismissFullScreenCover : HabitDetailUiEvent
    data object OnOpenEditTitleDialog : HabitDetailUiEvent
    data object OnDismissEditTitleDialog : HabitDetailUiEvent
    data class OnEditTitleInputChange(val title: String) : HabitDetailUiEvent
    data object OnConfirmEditTitle : HabitDetailUiEvent
    data class OnOpenDatePickerForCover(val cover: ComicCover) : HabitDetailUiEvent
    data object OnDismissDatePickerForCover : HabitDetailUiEvent
    data class OnUpdateCoverDate(val coverId: String, val newTimestampMillis: Long) : HabitDetailUiEvent
    data object OnOpenDatePickerForHabit : HabitDetailUiEvent
    data object OnDismissDatePickerForHabit : HabitDetailUiEvent
    data class OnUpdateHabitDate(val newDateString: String) : HabitDetailUiEvent
    data object OnOpenAddPastEntryDialog : HabitDetailUiEvent
    data object OnDismissAddPastEntryDialog : HabitDetailUiEvent
    data class OnAddPastEntry(val timestampMillis: Long) : HabitDetailUiEvent
    data class OnOpenDeleteCoverConfirm(val cover: ComicCover) : HabitDetailUiEvent
    data object OnDismissDeleteCoverConfirm : HabitDetailUiEvent
    data class OnConfirmDeleteCover(val coverId: String) : HabitDetailUiEvent
    data object OnOpenDeleteHabitConfirm : HabitDetailUiEvent
    data object OnDismissDeleteHabitConfirm : HabitDetailUiEvent
    data object OnConfirmDeleteHabit : HabitDetailUiEvent
}
