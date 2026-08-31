package com.dyor.habithero.presentation.screens.subscriptions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyor.habithero.data.repository.SubscriptionRepository
import com.dyor.habithero.domain.model.isFree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SubscriptionsViewModel(private val subscriptionRepository: SubscriptionRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SubscriptionsUiState(isLoading = true))
    val uiState: StateFlow<SubscriptionsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            subscriptionRepository.currentSubscriptionFlow.collect { currentSubscription ->
                _uiState.update {
                    it.copy(
                        currentPlan = currentSubscription,
                        isLoading = false,
                        showUpgradePremiumBanner = currentSubscription.isFree,
                        isMock = subscriptionRepository.isMockProvider,
                    )
                }
            }
        }
    }

    fun onUiEvent(event: SubscriptionsUiEvent) {
        when (event) {
            SubscriptionsUiEvent.OnCancelMockSubscription -> viewModelScope.launch {
                subscriptionRepository.cancelMockSubscription()
            }
        }
    }
}
