package com.dyor.habithero.presentation.screens.home

import com.dyor.habithero.domain.model.Habit

data class HomeUiState(
    val habits: List<Habit> = emptyList(),
    val habitCoverUrls: Map<String, String> = emptyMap(),
    val latestOverallCoverUrl: String? = null,
    val isLoading: Boolean = false,
    val celebrationHabit: Habit? = null,
    val isAddHabitDialogOpen: Boolean = false,
    val newHabitTitle: String = "",
    val newHabitCategory: String = "Fitness",
)

sealed interface HomeUiEvent {
    data class OnCompleteHabit(val habit: Habit) : HomeUiEvent
    data class OnDeleteHabit(val id: String) : HomeUiEvent
    data object OnClickAddHabit : HomeUiEvent
    data object OnDismissAddHabitDialog : HomeUiEvent
    data class OnNewHabitTitleChanged(val title: String) : HomeUiEvent
    data class OnNewHabitCategoryChanged(val category: String) : HomeUiEvent
    data object OnConfirmAddHabit : HomeUiEvent
    data object OnCelebrationHandled : HomeUiEvent
}
