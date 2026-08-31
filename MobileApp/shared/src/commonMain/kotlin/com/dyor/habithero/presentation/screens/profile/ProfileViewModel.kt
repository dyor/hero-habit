package com.dyor.habithero.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyor.habithero.data.repository.CreditRepository
import com.dyor.habithero.data.repository.SubscriptionRepository
import com.dyor.habithero.data.repository.UserRepository
import com.dyor.habithero.data.source.local.dao.ComicCoverDao
import com.dyor.habithero.data.source.preferences.UserPreferences
import com.dyor.habithero.domain.exceptions.UnAuthorizedException
import com.dyor.habithero.domain.model.isFree
import com.dyor.habithero.util.logging.AppLogger
import com.mmk.kmpauth.core.auth.KMPAuthRecentLoginRequiredException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences,
    private val comicCoverDao: ComicCoverDao,
    private val creditRepository: CreditRepository,
    private val subscriptionRepository: SubscriptionRepository,
) : ViewModel() {

    private val currentUserFlow = userRepository.currentUser
        .onEach { result ->
            result.onSuccess { user ->
                _uiState.update { it.copy(isLoading = false, user = user) }
            }.onFailure { error ->
                if (error is UnAuthorizedException) {
                    _uiState.update { it.copy(isLoading = false, user = null) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            }
        }

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> =
        combine(_uiState, currentUserFlow) { updatedUiState, _ -> updatedUiState }
            .stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                ProfileUiState(isLoading = true),
            )

    init {
        loadCelebrationInterval()
        observeStats()
    }

    private fun observeStats() = viewModelScope.launch {
        combine(
            comicCoverDao.getAllFlow(),
            creditRepository.balance,
            subscriptionRepository.currentSubscriptionFlow,
        ) { covers, balance, sub ->
            val plan = if (sub.isFree) "Free" else (sub?.name ?: "Hero Premium")
            _uiState.update {
                it.copy(
                    totalCoversMinted = covers.size,
                    creditBalance = balance,
                    planName = plan,
                )
            }
        }.collectLatest { }
    }

    private fun loadCelebrationInterval() = viewModelScope.launch {
        val interval = userPreferences.getInt(UserPreferences.KEY_STREAK_CELEBRATION_INTERVAL, 7) ?: 7
        _uiState.update { it.copy(streakCelebrationInterval = interval) }
    }

    fun onErrorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onDismissDeleteUserConfirmationDialog() = viewModelScope.launch {
        _uiState.update { it.copy(deleteUserDialogShown = false) }
    }

    fun onConfirmDeleteAccount() = viewModelScope.launch {
        _uiState.update { it.copy(deleteUserDialogShown = false, isLoading = true) }
        userRepository.deleteAccount()
            .onSuccess {
                AppLogger.d("Account is deleted successfully")
                _uiState.update { it.copy(isLoading = false, user = null) }
            }
            .onFailure { error ->
                AppLogger.d("Account deletion failed ${error.message}")
                if (error is KMPAuthRecentLoginRequiredException) {
                    _uiState.update { it.copy(isLoading = false, user = null) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            }
    }

    fun onUiEvent(event: ProfileScreenUiEvent) = viewModelScope.launch {
        when (event) {
            ProfileScreenUiEvent.OnClickDeleteAccount -> {
                _uiState.update { it.copy(deleteUserDialogShown = true) }
            }

            is ProfileScreenUiEvent.OnUpdateCelebrationInterval -> {
                _uiState.update { it.copy(streakCelebrationInterval = event.interval) }
                userPreferences.putInt(UserPreferences.KEY_STREAK_CELEBRATION_INTERVAL, event.interval)
            }
        }
    }
}
