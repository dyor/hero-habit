package com.dyor.habithero.presentation.components.ads

interface AdsManager {
    fun initialize()
    val interstitialAdLoader: FullScreenAdLoader
    val rewardedAdLoader: FullScreenAdLoader
}
