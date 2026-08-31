package com.dyor.habithero.subscription.revenuecat

import com.dyor.habithero.subscription.api.SubscriptionProviderFactory

val SubscriptionProviderFactory.Companion.RevenueCat: SubscriptionProviderFactory
    get() = subscriptionProviderFactory

internal expect val subscriptionProviderFactory: SubscriptionProviderFactory
