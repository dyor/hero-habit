package com.dyor.habithero.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyor.habithero.data.repository.HabitRepository
import com.dyor.habithero.data.source.local.dao.ComicCoverDao
import com.dyor.habithero.domain.model.Habit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val habitRepository: HabitRepository,
    private val comicCoverDao: ComicCoverDao,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            habitRepository.seedInitialHabitsIfEmpty()
        }
        observeData()
    }

    private fun observeData() = viewModelScope.launch {
        combine(
            habitRepository.observeAllHabits(),
            comicCoverDao.getAllFlow(),
        ) { habitResult, coverEntities ->
            val habitList = habitResult.getOrNull() ?: emptyList()
            val coverMap = mutableMapOf<String, String>()
            for (cover in coverEntities) {
                if (!coverMap.containsKey(cover.habitId)) {
                    coverMap[cover.habitId] = cover.imageUrl
                }
            }
            val latestOverall = coverEntities.firstOrNull()?.imageUrl

            _uiState.update { current ->
                current.copy(
                    habits = habitList,
                    habitCoverUrls = coverMap,
                    latestOverallCoverUrl = latestOverall,
                    isLoading = false,
                )
            }
        }.collectLatest { }
    }

    fun onUiEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.OnCompleteHabit -> completeHabit(event.habit)

            is HomeUiEvent.OnDeleteHabit -> deleteHabit(event.id)

            HomeUiEvent.OnClickAddHabit -> _uiState.update { it.copy(isAddHabitDialogOpen = true) }

            HomeUiEvent.OnDismissAddHabitDialog -> _uiState.update {
                it.copy(isAddHabitDialogOpen = false, newHabitTitle = "")
            }

            is HomeUiEvent.OnNewHabitTitleChanged -> _uiState.update { it.copy(newHabitTitle = event.title) }

            is HomeUiEvent.OnNewHabitCategoryChanged -> _uiState.update { it.copy(newHabitCategory = event.category) }

            HomeUiEvent.OnConfirmAddHabit -> createHabit()

            HomeUiEvent.OnCelebrationHandled -> _uiState.update { it.copy(celebrationHabit = null) }
        }
    }

    private fun completeHabit(habit: Habit) = viewModelScope.launch {
        habitRepository.completeHabit(habit.id).onSuccess { updatedHabit ->
            _uiState.update { it.copy(celebrationHabit = updatedHabit) }
        }
    }

    private fun deleteHabit(id: String) = viewModelScope.launch {
        habitRepository.deleteHabit(id)
    }

    private fun createHabit() = viewModelScope.launch {
        val title = _uiState.value.newHabitTitle.trim()
        val category = _uiState.value.newHabitCategory.trim()
        if (title.isNotBlank()) {
            habitRepository.createHabit(title, category)
            _uiState.update { it.copy(isAddHabitDialogOpen = false, newHabitTitle = "") }
        }
    }
}
