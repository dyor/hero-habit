package com.dyor.habithero.presentation.components.premium

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.dyor.habithero.designsystem.components.premium.PremiumFeatureUiState
import com.dyor.habithero.designsystem.components.premium.SuccessfulPurchaseContent
import com.dyor.habithero.util.Constants.subscriptionUrl
import com.dyor.habithero.util.inappreview.rememberInAppReviewTrigger
import com.dyor.habithero.util.logging.AppLogger
import com.dyor.habithero.util.logging.logSuccessfulPurchase

@Composable
fun SuccessfulPurchaseView(
    features: List<PremiumFeatureUiState> = emptyList(),
    expirationDate: String? = null,
    isLifetime: Boolean = false,
    isRecurring: Boolean = true,
    modifier: Modifier = Modifier,
    onContinue: () -> Unit = {},
) {
    val inAppReviewTrigger = rememberInAppReviewTrigger()
    LaunchedEffect(Unit) {
        AppLogger.logSuccessfulPurchase()
        inAppReviewTrigger.triggerAfterSuccessfulPurchase()
    }

    SuccessfulPurchaseContent(
        subscriptionUrl = subscriptionUrl,
        features = features,
        expirationDate = expirationDate,
        isLifetime = isLifetime,
        isRecurring = isRecurring,
        modifier = modifier,
        onContinue = onContinue,
    )
}
