package com.dyor.habithero.presentation.components.ads

import android.content.Context
import com.dyor.habithero.presentation.components.ads.interstitial.InterstitialAdLoader
import com.dyor.habithero.presentation.components.ads.rewarded.RewardedAdLoader
import com.google.android.gms.ads.MobileAds

class AdsManagerImpl(private val context: Context) : AdsManager {
    override fun initialize() {
        MobileAds.initialize(context)
    }

    override val interstitialAdLoader: FullScreenAdLoader by lazy { InterstitialAdLoader(context) }
    override val rewardedAdLoader: FullScreenAdLoader by lazy { RewardedAdLoader(context) }
}
