package com.dyor.habithero.presentation.screens.profile

import com.dyor.habithero.domain.model.User

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val deleteUserDialogShown: Boolean = false,
    val errorMessage: String? = null,
    val streakCelebrationInterval: Int = 7,
    val totalCoversMinted: Int = 0,
    val creditBalance: Int = 0,
    val planName: String = "Free",
) {
    val signInActionRequired: Boolean get() = user == null && isLoading.not()
}

sealed interface ProfileScreenUiEvent {
    data object OnClickDeleteAccount : ProfileScreenUiEvent
    data class OnUpdateCelebrationInterval(val interval: Int) : ProfileScreenUiEvent
}
