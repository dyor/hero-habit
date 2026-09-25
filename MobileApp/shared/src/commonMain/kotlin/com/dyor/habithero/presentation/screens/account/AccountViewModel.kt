package com.dyor.habithero.presentation.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyor.habithero.data.repository.HabitRepository
import com.dyor.habithero.data.repository.SubscriptionRepository
import com.dyor.habithero.data.repository.UserRepository
import com.dyor.habithero.data.source.preferences.UserPreferences
import com.dyor.habithero.designsystem.components.SettingsItemUiState
import com.dyor.habithero.designsystem.generated.resources.UiRes
import com.dyor.habithero.designsystem.generated.resources.ic_coin_credits
import com.dyor.habithero.designsystem.generated.resources.ic_settings_item_support_legal
import com.dyor.habithero.generated.resources.Res
import com.dyor.habithero.generated.resources.comic_cover_credits
import com.dyor.habithero.generated.resources.help_and_support
import com.dyor.habithero.root.AppConfiguration
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountViewModel(
    private val userRepository: UserRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val userPreferences: UserPreferences,
    private val habitRepository: HabitRepository,
) : ViewModel() {

    private val creditsItem = SettingsItemUiState(
        startIcon = UiRes.drawable.ic_coin_credits,
        textRes = Res.string.comic_cover_credits,
    )
    private val supportItem = SettingsItemUiState(
        startIcon = UiRes.drawable.ic_settings_item_support_legal,
        textRes = Res.string.help_and_support,
    )

    private fun settingsItemsFor(): List<SettingsItemUiState> = listOf(creditsItem, supportItem)

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> =
        combine(
            userRepository.currentUser,
            subscriptionRepository.currentSubscriptionFlow,
            _uiState,
        ) { currentUser, currentSubscription, uiState ->
            val user = currentUser.getOrNull()
            uiState.copy(
                user = if (user?.isAnonymous == true && AppConfiguration.AUTH_SOCIAL_LOGIN_ENABLED) null else user,
                settingsItemList = settingsItemsFor(),
                showUpgradePremiumBanner = false,
            )
        }.stateIn(viewModelScope, WhileSubscribed(5000), _uiState.value)

    init {
        loadSettings()
    }

    private fun loadSettings() = viewModelScope.launch {
        val interval = userPreferences.getInt(UserPreferences.KEY_STREAK_CELEBRATION_INTERVAL, 7) ?: 7
        _uiState.update { it.copy(streakCelebrationInterval = interval) }
    }

    fun onUiEvent(event: AccountUiEvent) = viewModelScope.launch {
        when (event) {
            is AccountUiEvent.OnSettingsItemClick -> Unit

            is AccountUiEvent.OnUpdateCelebrationInterval -> {
                userPreferences.putInt(UserPreferences.KEY_STREAK_CELEBRATION_INTERVAL, event.interval)
                _uiState.update { it.copy(streakCelebrationInterval = event.interval) }
            }

            AccountUiEvent.OnClickCreateHabit -> {
                _uiState.update { it.copy(isAddHabitDialogOpen = true) }
            }

            AccountUiEvent.OnDismissAddHabitDialog -> {
                _uiState.update {
                    it.copy(
                        isAddHabitDialogOpen = false,
                        newHabitTitle = "",
                        newHabitCategory = "Fitness",
                        newHabitCustomPrompt = "",
                    )
                }
            }

            is AccountUiEvent.OnNewHabitTitleChanged -> {
                _uiState.update { it.copy(newHabitTitle = event.title) }
            }

            is AccountUiEvent.OnNewHabitCategoryChanged -> {
                _uiState.update { it.copy(newHabitCategory = event.category) }
            }

            is AccountUiEvent.OnNewHabitCustomPromptChanged -> {
                _uiState.update { it.copy(newHabitCustomPrompt = event.prompt) }
            }

            AccountUiEvent.OnConfirmAddHabit -> {
                val title = _uiState.value.newHabitTitle.trim()
                val category = _uiState.value.newHabitCategory.trim().ifBlank { "General" }
                val prompt = _uiState.value.newHabitCustomPrompt.trim()
                if (title.isNotBlank()) {
                    habitRepository.createHabit(title, category, prompt)
                    _uiState.update {
                        it.copy(
                            isAddHabitDialogOpen = false,
                            newHabitTitle = "",
                            newHabitCategory = "Fitness",
                            newHabitCustomPrompt = "",
                            habitCreatedSuccess = true,
                        )
                    }
                    delay(3000)
                    _uiState.update { it.copy(habitCreatedSuccess = false) }
                }
            }

            AccountUiEvent.OnClickUpgradePremium -> Unit

            AccountUiEvent.OnClickProfile -> Unit

            AccountUiEvent.OnClickSignIn -> Unit
        }
    }
}
