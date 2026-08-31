package com.dyor.habithero.presentation.screens.habitdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyor.habithero.data.repository.HabitRepository
import com.dyor.habithero.data.source.local.dao.ComicCoverDao
import com.dyor.habithero.data.source.local.entity.toModel
import com.dyor.habithero.domain.model.ComicCover
import com.dyor.habithero.util.logging.AppLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HabitDetailViewModel(
    private val habitId: String,
    private val habitRepository: HabitRepository,
    private val comicCoverDao: ComicCoverDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitDetailUiState(isLoading = true))
    val uiState: StateFlow<HabitDetailUiState> = _uiState.asStateFlow()

    init {
        observeHabitAndCovers()
    }

    private fun observeHabitAndCovers() {
        combine(
            habitRepository.observeHabitById(habitId),
            comicCoverDao.getByHabitIdFlow(habitId),
        ) { habitResult, coverEntities ->
            val habit = habitResult.getOrNull()
            val covers = coverEntities.map { it.toModel() }
            habit to covers
        }.onEach { (habit, covers) ->
            _uiState.update { current ->
                val prompt = if (current.customPromptInput.isEmpty() && habit != null) {
                    habit.customPrompt
                } else {
                    current.customPromptInput
                }
                current.copy(
                    habit = habit,
                    comicCovers = covers,
                    customPromptInput = prompt,
                    isLoading = false,
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onUiEvent(event: HabitDetailUiEvent) {
        when (event) {
            is HabitDetailUiEvent.OnCustomPromptChange -> {
                _uiState.update { it.copy(customPromptInput = event.prompt) }
            }

            HabitDetailUiEvent.OnSaveCustomPrompt -> saveCustomPrompt()

            is HabitDetailUiEvent.OnClickCover -> {
                _uiState.update { it.copy(selectedCoverIndex = event.index) }
            }

            HabitDetailUiEvent.OnDismissFullScreenCover -> {
                _uiState.update { it.copy(selectedCoverIndex = null) }
            }

            HabitDetailUiEvent.OnOpenEditTitleDialog -> {
                val currentTitle = _uiState.value.habit?.title.orEmpty()
                _uiState.update { it.copy(showEditTitleDialog = true, editTitleInput = currentTitle) }
            }

            HabitDetailUiEvent.OnDismissEditTitleDialog -> {
                _uiState.update { it.copy(showEditTitleDialog = false) }
            }

            is HabitDetailUiEvent.OnEditTitleInputChange -> {
                _uiState.update { it.copy(editTitleInput = event.title) }
            }

            HabitDetailUiEvent.OnConfirmEditTitle -> {
                confirmEditTitle()
            }

            is HabitDetailUiEvent.OnOpenDatePickerForCover -> {
                _uiState.update { it.copy(showDatePickerForCover = event.cover) }
            }

            HabitDetailUiEvent.OnDismissDatePickerForCover -> {
                _uiState.update { it.copy(showDatePickerForCover = null) }
            }

            is HabitDetailUiEvent.OnUpdateCoverDate -> {
                updateCoverDate(event.coverId, event.newTimestampMillis)
            }

            HabitDetailUiEvent.OnOpenDatePickerForHabit -> {
                _uiState.update { it.copy(showDatePickerForHabit = true) }
            }

            HabitDetailUiEvent.OnDismissDatePickerForHabit -> {
                _uiState.update { it.copy(showDatePickerForHabit = false) }
            }

            is HabitDetailUiEvent.OnUpdateHabitDate -> {
                updateHabitDate(event.newDateString)
            }

            HabitDetailUiEvent.OnOpenAddPastEntryDialog -> {
                _uiState.update { it.copy(showAddPastEntryDialog = true) }
            }

            HabitDetailUiEvent.OnDismissAddPastEntryDialog -> {
                _uiState.update { it.copy(showAddPastEntryDialog = false) }
            }

            is HabitDetailUiEvent.OnAddPastEntry -> {
                addPastEntry(event.timestampMillis)
            }

            is HabitDetailUiEvent.OnOpenDeleteCoverConfirm -> {
                _uiState.update { it.copy(showDeleteCoverConfirm = event.cover) }
            }

            HabitDetailUiEvent.OnDismissDeleteCoverConfirm -> {
                _uiState.update { it.copy(showDeleteCoverConfirm = null) }
            }

            is HabitDetailUiEvent.OnConfirmDeleteCover -> {
                deleteCover(event.coverId)
            }

            HabitDetailUiEvent.OnOpenDeleteHabitConfirm -> {
                _uiState.update { it.copy(showDeleteHabitConfirm = true) }
            }

            HabitDetailUiEvent.OnDismissDeleteHabitConfirm -> {
                _uiState.update { it.copy(showDeleteHabitConfirm = false) }
            }

            HabitDetailUiEvent.OnConfirmDeleteHabit -> {
                deleteHabit()
            }
        }
    }

    private fun confirmEditTitle() = viewModelScope.launch {
        val newTitle = _uiState.value.editTitleInput.trim()
        if (newTitle.isNotBlank()) {
            habitRepository.updateHabitTitle(habitId, newTitle)
        }
        _uiState.update { it.copy(showEditTitleDialog = false) }
    }

    private fun saveCustomPrompt() = viewModelScope.launch {
        val prompt = _uiState.value.customPromptInput
        _uiState.update { it.copy(isSavingPrompt = true) }
        habitRepository.updateCustomPrompt(habitId, prompt)
            .onSuccess {
                _uiState.update { it.copy(isSavingPrompt = false, showSaveSuccessBanner = true) }
                delay(2500)
                _uiState.update { it.copy(showSaveSuccessBanner = false) }
            }
            .onFailure { error ->
                AppLogger.e("Failed to save custom prompt: ${error.message}")
                _uiState.update { it.copy(isSavingPrompt = false) }
            }
    }

    private fun updateCoverDate(coverId: String, newTimestampMillis: Long) = viewModelScope.launch {
        habitRepository.updateEntryDate(coverId, newTimestampMillis)
        _uiState.update { it.copy(showDatePickerForCover = null) }
    }

    private fun updateHabitDate(newDateString: String) = viewModelScope.launch {
        habitRepository.updateLastCompletedDate(habitId, newDateString)
        _uiState.update { it.copy(showDatePickerForHabit = false) }
    }

    private fun addPastEntry(timestampMillis: Long) = viewModelScope.launch {
        habitRepository.addPastEntry(habitId, timestampMillis)
        _uiState.update { it.copy(showAddPastEntryDialog = false) }
    }

    private fun deleteCover(coverId: String) = viewModelScope.launch {
        habitRepository.deleteEntry(coverId)
        _uiState.update {
            it.copy(
                showDeleteCoverConfirm = null,
                selectedCoverIndex = null,
            )
        }
    }

    private fun deleteHabit() = viewModelScope.launch {
        habitRepository.deleteHabit(habitId)
        _uiState.update {
            it.copy(
                showDeleteHabitConfirm = false,
                isHabitDeleted = true,
            )
        }
    }
}
