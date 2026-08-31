package com.dyor.habithero.subscription.revenuecat

import com.dyor.habithero.subscription.api.SubscriptionProvider
import com.dyor.habithero.subscription.api.SubscriptionProviderFactory
import com.dyor.habithero.subscription.api.SubscriptionProviderUi

internal actual val subscriptionProviderFactory: SubscriptionProviderFactory
    get() =
        object : SubscriptionProviderFactory {
            override fun createProvider(): SubscriptionProvider = RevenueCatSubscriptionProvider()

            override fun createProviderUi(): SubscriptionProviderUi = RevenueCatSubscriptionProviderUi()
        }
