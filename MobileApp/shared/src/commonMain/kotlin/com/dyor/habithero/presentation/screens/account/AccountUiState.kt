package com.dyor.habithero.presentation.screens.account

import com.dyor.habithero.designsystem.components.SettingsItemUiState
import com.dyor.habithero.designsystem.generated.resources.UiRes
import com.dyor.habithero.designsystem.generated.resources.ic_settings_item_logout
import com.dyor.habithero.designsystem.generated.resources.ic_settings_item_subscriptions
import com.dyor.habithero.designsystem.generated.resources.ic_settings_item_support_legal
import com.dyor.habithero.domain.model.User
import com.dyor.habithero.generated.resources.Res
import com.dyor.habithero.generated.resources.help_and_support
import com.dyor.habithero.generated.resources.logout
import com.dyor.habithero.generated.resources.subscriptions

data class AccountUiState(
    val settingsItemList: List<SettingsItemUiState> = listOf(
        SettingsItemUiState(
            startIcon = UiRes.drawable.ic_settings_item_subscriptions,
            textRes = Res.string.subscriptions,
        ),
        SettingsItemUiState(
            startIcon = UiRes.drawable.ic_settings_item_support_legal,
            textRes = Res.string.help_and_support,
        ),
        SettingsItemUiState(
            startIcon = UiRes.drawable.ic_settings_item_logout,
            textRes = Res.string.logout,
            showEndIcon = false,
        ),
    ),
    val user: User? = null,
    val streakCelebrationInterval: Int = 7,
    val isAddHabitDialogOpen: Boolean = false,
    val newHabitTitle: String = "",
    val newHabitCategory: String = "Fitness",
    val newHabitCustomPrompt: String = "",
    val habitCreatedSuccess: Boolean = false,
    val isLogoutDialogVisible: Boolean = false,
    val showUpgradePremiumBanner: Boolean = false,
)

sealed interface AccountUiEvent {
    data class OnSettingsItemClick(val item: SettingsItemUiState) : AccountUiEvent
    data object OnLogoutConfirmClick : AccountUiEvent
    data object OnLogoutDialogDismiss : AccountUiEvent
    data object OnClickUpgradePremium : AccountUiEvent
    data object OnClickSignIn : AccountUiEvent
    data object OnClickProfile : AccountUiEvent
    data class OnUpdateCelebrationInterval(val interval: Int) : AccountUiEvent
    data object OnClickCreateHabit : AccountUiEvent
    data object OnDismissAddHabitDialog : AccountUiEvent
    data class OnNewHabitTitleChanged(val title: String) : AccountUiEvent
    data class OnNewHabitCategoryChanged(val category: String) : AccountUiEvent
    data class OnNewHabitCustomPromptChanged(val prompt: String) : AccountUiEvent
    data object OnConfirmAddHabit : AccountUiEvent
}
