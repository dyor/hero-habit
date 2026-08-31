package com.dyor.habithero.util

import com.dyor.habithero.data.source.featureflag.FeatureFlagManager
import com.dyor.habithero.presentation.components.ads.AdsManager
import com.dyor.habithero.presentation.components.ads.IosAdsDisplayer
import com.dyor.habithero.util.analytics.Analytics

/**
This factory is used to help to use swift libraries in KMP. Actual implementations are provided in swift.
 */
interface SwiftLibDependencyFactory {
    fun provideFeatureFlagManagerImpl(): FeatureFlagManager
    fun provideFirebaseAnalyticsImpl(): Analytics
    fun provideAdsManagerImpl(): AdsManager
    fun provideIosAdsDisplayer(): IosAdsDisplayer
}
