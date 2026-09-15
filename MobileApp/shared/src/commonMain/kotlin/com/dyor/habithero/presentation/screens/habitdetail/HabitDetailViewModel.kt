package com.dyor.habithero.presentation.screens.habitdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyor.habithero.data.repository.HabitRepository
import com.dyor.habithero.data.source.local.dao.ComicCoverDao
import com.dyor.habithero.data.source.local.entity.toModel
import com.dyor.habithero.data.source.remote.apiservices.ai.OpenAiApiService
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
import kotlin.time.Duration.Companion.milliseconds

class HabitDetailViewModel(
    private val habitId: String,
    private val habitRepository: HabitRepository,
    private val comicCoverDao: ComicCoverDao,
    private val openAiApiService: OpenAiApiService? = null,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitDetailUiState(isLoading = true))
    val uiState: StateFlow<HabitDetailUiState> = _uiState.asStateFlow()

    private var isPromptInitialized = false

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
                val prompt = if (!isPromptInitialized && habit != null) {
                    isPromptInitialized = true
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

            HabitDetailUiEvent.OnGenerateAiPrompt -> generateAiPrompt()

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
                delay(2500.milliseconds)
                _uiState.update { it.copy(showSaveSuccessBanner = false) }
            }
            .onFailure { error ->
                AppLogger.e("Failed to save custom prompt: ${error.message}")
                _uiState.update { it.copy(isSavingPrompt = false) }
            }
    }

    private fun generateAiPrompt() = viewModelScope.launch {
        if (_uiState.value.isGeneratingPrompt) return@launch
        val habit = _uiState.value.habit ?: return@launch
        _uiState.update { it.copy(isGeneratingPrompt = true) }

        var generatedScenario: String? = null
        try {
            if (openAiApiService != null) {
                val response = openAiApiService.createChat {
                    model = "gpt-4o-mini"
                    messages {
                        assistantText(
                            "You are a comic book creative writer. Generate a single, highly vivid, dynamic 1-sentence superhero action scenario describing a superhero performing an epic deed or wielding superpowers inspired by the user's habit title and category. Describe vivid visual elements like glowing energy, cosmic or elemental powers, futuristic technology, heroic poses, or dramatic environments. Return ONLY the scenario phrase (lowercase or natural phrasing, around 10-20 words, no quotes, no markdown, no intro/outro).",
                        )
                        userContentItems {
                            text("Habit: ${habit.title} (Category: ${habit.category})")
                        }
                    }
                }
                val content = response.data?.firstResponseMessageOrNull?.trim()
                if (!content.isNullOrBlank()) {
                    generatedScenario = content.trim().trim('"', '\'', '`', '“', '”', '‘', '’')
                }
            }
        } catch (e: Exception) {
            AppLogger.e("AI prompt generation error: ${e.message}")
        }

        if (generatedScenario.isNullOrBlank()) {
            generatedScenario = getFallbackHeroicScenario(habit.title, habit.category, _uiState.value.customPromptInput)
        }

        _uiState.update {
            it.copy(
                customPromptInput = generatedScenario,
                isGeneratingPrompt = false,
            )
        }
    }

    private fun getFallbackHeroicScenario(title: String, category: String, currentPrompt: String): String {
        val lowerTitle = title.lowercase()
        val lowerCat = category.lowercase()

        val candidates = when {
            lowerTitle.contains("water") || lowerTitle.contains("drink") || lowerTitle.contains("hydrate") -> listOf(
                "controlling elemental tidal waves and glowing hydro energy shields",
                "summoning restorative crystal water fountains atop an ancient emerald sanctuary",
                "channeling pure bio-luminescent healing auroras across an ocean horizon",
                "commanding sapphire water dragons to revitalize an arid alien planet",
            )

            lowerTitle.contains("read") || lowerTitle.contains("book") || lowerTitle.contains("learn") || lowerTitle.contains("study") || lowerTitle.contains("spanish") || lowerTitle.contains("code") || lowerCat.contains("mind") || lowerCat.contains("learn") -> listOf(
                "studying ancient holographic scrolls inside a secret galactic library of wisdom",
                "transmitting multilingual galactic transmissions across planetary systems",
                "decoding quantum star maps in a high-tech crystal observatory",
                "absorbing encyclopedic cosmic lore from orbiting stellar beacons",
                "mastering neon quantum code streams to reshape digital reality",
            )

            lowerTitle.contains("run") || lowerTitle.contains("jog") || lowerTitle.contains("walk") || lowerTitle.contains("step") || lowerTitle.contains("speed") -> listOf(
                "running at lightning speed through bustling futuristic metropolis with glowing neon trails",
                "walking through a radiant glowing futuristic city skyline at twilight",
                "soaring through supersonic sound barriers leaving vibrant energy shockwaves",
                "sprinting across cosmic rainbow bridges bridging distant star clusters",
            )

            lowerTitle.contains("gym") || lowerTitle.contains("lift") || lowerTitle.contains("iron") || lowerTitle.contains("workout") || lowerTitle.contains("push") || lowerTitle.contains("fitness") || lowerCat.contains("fitness") -> listOf(
                "lifting heavy glowing barbells beneath a soaring bald eagle in ancient redwoods",
                "harnessing seismic gravitational force to shatter planetary meteors with bare hands",
                "leaping between orbital skyscrapers with gravity-defying athletic precision",
                "forging indestructible cybernetic armor through intense kinetic power training",
            )

            lowerTitle.contains("meditat") || lowerTitle.contains("zen") || lowerTitle.contains("breath") || lowerTitle.contains("focus") || lowerCat.contains("focus") -> listOf(
                "meditating in epic cosmic places like deep space, floating near nebulae, waterfalls, and towering volcanoes",
                "channeling harmonic psychic resonance to calm a raging planetary vortex",
                "levitating in deep zen focus surrounded by orbiting sacred energy runes",
                "projecting an astral guardian avatar across shimmering celestial realms",
            )

            lowerTitle.contains("sleep") || lowerTitle.contains("rest") || lowerTitle.contains("nap") || lowerCat.contains("rest") -> listOf(
                "recharging in a high-tech stasis rejuvenation pod under a starry cosmos",
                "resting in a celestial moonlit sanctuary sheltered by radiant nebula wings",
                "entering restorative hyper-sleep while drifting past luminous auroras",
            )

            lowerTitle.contains("eat") || lowerTitle.contains("food") || lowerTitle.contains("diet") || lowerTitle.contains("salad") || lowerTitle.contains("vitamin") || lowerCat.contains("nutrition") || lowerCat.contains("health") -> listOf(
                "fueling with mythical organic power elixirs in an enchanted bio-dome",
                "absorbing cosmic vitality crystals and glowing energy shields",
                "gathering radiant solar nectar from celestial floating orchards",
            )

            else -> listOf(
                "unleashing dazzling cosmic photon beams from an orbital space citadel",
                "soaring majestically above a glowing cyberpunk metropolis cloaked in golden light",
                "summoning unstoppable elemental storms to defend a futuristic sanctuary",
                "channeling vibrant auroral energy into an indestructible shield of valor",
                "manipulating quantum energy currents while heroically hovering above the clouds",
            )
        }

        val available = candidates.filter { it != currentPrompt }
        return (if (available.isNotEmpty()) available else candidates).random()
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
