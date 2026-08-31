package com.dyor.habithero.subscription.api

interface SubscriptionProviderFactory {
    companion object {}

    fun createProvider(): SubscriptionProvider

    fun createProviderUi(): SubscriptionProviderUi
}
