package com.dyor.habithero.presentation.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyor.habithero.data.repository.HabitRepository
import com.dyor.habithero.data.source.preferences.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnBoardingViewModel(
    private val userPreferences: UserPreferences,
    private val habitRepository: HabitRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnBoardingUiState(isLoading = true))
    val uiState: StateFlow<OnBoardingUiState> = _uiState.asStateFlow()

    init {
        checkIfOnBoardIsShown()
    }

    fun onUiEvent(event: OnBoardingUiEvent) = viewModelScope.launch {
        when (event) {
            is OnBoardingUiEvent.OnToggleHabit -> {
                _uiState.update { current ->
                    val updated = current.habitPresets.map { preset ->
                        if (preset.id == event.id) {
                            preset.copy(isSelected = !preset.isSelected)
                        } else {
                            preset
                        }
                    }
                    current.copy(habitPresets = updated)
                }
            }

            OnBoardingUiEvent.OnClickStart -> {
                saveSelectedHabits()
                userPreferences.putBoolean(UserPreferences.KEY_IS_ONBOARD_SHOWN, true)
                _uiState.update {
                    it.copy(
                        isOnBoardingFinished = true,
                        isNewUser = true,
                        navigateToCreateQuest = false,
                    )
                }
            }

            OnBoardingUiEvent.OnClickCreateYourOwnQuest -> {
                saveSelectedHabits()
                userPreferences.putBoolean(UserPreferences.KEY_IS_ONBOARD_SHOWN, true)
                _uiState.update {
                    it.copy(
                        isOnBoardingFinished = true,
                        isNewUser = true,
                        navigateToCreateQuest = true,
                    )
                }
            }
        }
    }

    private suspend fun saveSelectedHabits() {
        val selected = _uiState.value.habitPresets.filter { it.isSelected }
        for (habit in selected) {
            habitRepository.createHabit(
                title = habit.title,
                category = habit.category,
                customPrompt = habit.customPrompt,
            )
        }
    }

    fun onFinishHandled() = _uiState.update {
        it.copy(isOnBoardingFinished = false, navigateToCreateQuest = false)
    }

    private fun checkIfOnBoardIsShown() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        if (userPreferences.getBoolean(UserPreferences.KEY_IS_ONBOARD_SHOWN)) {
            _uiState.update { it.copy(isOnBoardingFinished = true, isNewUser = false) }
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
