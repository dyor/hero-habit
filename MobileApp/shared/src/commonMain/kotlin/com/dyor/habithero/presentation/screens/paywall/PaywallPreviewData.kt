package com.dyor.habithero.presentation.screens.paywall

import com.dyor.habithero.designsystem.util.UiText
import com.dyor.habithero.subscription.api.Price
import com.dyor.habithero.subscription.api.PurchasePackage
import com.dyor.habithero.subscription.api.PurchasePackageId
import com.dyor.habithero.util.Constants

/**
 * Fixture builders for `@Preview` and store-screenshot composables. Kept out of
 * the screen files so previews can stay one-liners and the same data can be
 * reused across multiple preview functions (default state, trial state, etc.).
 *
 * These are *display fixtures only* — they bypass the real
 * [PaywallUiStateMapper] so previews never depend on string resources or
 * billing logic. For tests that exercise the mapper, build real
 * [PurchasePackage] lists and call the mapper directly.
 */
internal object PaywallPreviewData {

    /** A fully-formed subscription pricing card, ready to drop into a preview list. */
    fun subscriptionPackage(
        id: String,
        title: UiText,
        subtitle: UiText,
        priceText: UiText,
        isSelected: Boolean = false,
        isRecommended: Boolean = false,
        savingsBadge: UiText? = null,
    ): PaywallPackageUiState = PaywallPackageUiState(
        purchasePackage = PurchasePackage(
            id = PurchasePackageId(id),
            price = Price(amount = 0f, currencyCodeOrSymbol = "$", localizedString = "$0"),
            title = id,
        ),
        title = title,
        subtitle = subtitle,
        priceText = priceText,
        isSelected = isSelected,
        isRecommended = isRecommended,
        savingsBadge = savingsBadge,
    )

    /** A credit-pack row with a parsable product id (so `parseCreditAmount` works in tests). */
    fun creditPack(
        credits: Int,
        priceText: String,
        perCreditText: String,
        isSelected: Boolean = false,
        isBestValue: Boolean = false,
    ): PaywallPackageUiState = PaywallPackageUiState(
        purchasePackage = PurchasePackage(
            id = PurchasePackageId("${Constants.CREDIT_PACK_PRODUCT_ID_PREFIX}$credits"),
            price = Price(amount = 0f, currencyCodeOrSymbol = "$", localizedString = priceText),
            title = "$credits credits",
        ),
        title = UiText.of("$credits credits"),
        subtitle = UiText.of(perCreditText),
        priceText = UiText.of(priceText),
        isSelected = isSelected,
        isRecommended = isBestValue,
        savingsBadge = if (isBestValue) UiText.of("BEST VALUE") else null,
    )

    /**
     * Three-tier subscription state (Cadet $1.99/mo / Champion $5.99/mo / Champion Annual $50.00/yr)
     * with the annual plan selected and flagged as best value (save 30%).
     */
    fun subscriptionState(trialAvailable: Boolean = false): PaywallUiState {
        val cadet = subscriptionPackage(
            id = "hero_cadet_monthly",
            title = UiText.of("Hero Cadet (10 Covers)"),
            subtitle = UiText.of("$0.46 per week"),
            priceText = UiText.of("$1.99/month"),
        )
        val champion = subscriptionPackage(
            id = "hero_champion_monthly",
            title = UiText.of("Hero Champion (40 Covers)"),
            subtitle = UiText.of("$1.38 per week"),
            priceText = UiText.of("$5.99/month"),
        )
        val annual = subscriptionPackage(
            id = "hero_champion_annual",
            title = UiText.of("Hero Champion Annual"),
            subtitle = UiText.of("$0.96 per week"),
            priceText = UiText.of("$50.00/year"),
            isSelected = true,
            isRecommended = true,
            savingsBadge = UiText.of("SAVE 30%"),
        )
        return PaywallUiState(
            isLoading = false,
            packages = listOf(cadet, champion, annual),
            buyButtonEnabled = true,
            ctaText = UiText.of("Continue"),
            aboveCtaText = UiText.of("Cancel anytime"),
            belowCtaText = null,
        )
    }

    /**
     * Subscription state where the selected plan has a paid-intro phase.
     */
    fun paidIntroSubscriptionState(): PaywallUiState {
        val monthly = subscriptionPackage(
            id = "hero_cadet_monthly",
            title = UiText.of("Hero Cadet"),
            subtitle = UiText.of("$0.46 per week"),
            priceText = UiText.of("$1.99/month"),
            isSelected = true,
        )
        val annual = subscriptionPackage(
            id = "hero_champion_annual",
            title = UiText.of("Hero Champion Annual"),
            subtitle = UiText.of("$0.96 per week"),
            priceText = UiText.of("$50.00/year"),
            isRecommended = true,
            savingsBadge = UiText.of("BEST VALUE"),
        )
        return PaywallUiState(
            isLoading = false,
            packages = listOf(monthly, annual),
            buyButtonEnabled = true,
            ctaText = UiText.of("Continue"),
            aboveCtaText = UiText.of("Cancel anytime"),
            belowCtaText = null,
        )
    }

    /** Three credit packs (10 / 40 / 100) with the 40-pack flagged as BEST VALUE. */
    fun creditPackState(): PaywallUiState = PaywallUiState(
        isLoading = false,
        packages = listOf(
            creditPack(10, "$1.99", "$0.20 per cover"),
            creditPack(40, "$5.99", "$0.15 per cover", isBestValue = true, isSelected = true),
            creditPack(100, "$12.99", "$0.13 per cover"),
        ),
        buyButtonEnabled = true,
        ctaText = UiText.of("Buy credits"),
        aboveCtaText = UiText.of("No subscription · One-time purchase"),
        mode = PaywallMode.CREDIT_PACK,
    )
}
