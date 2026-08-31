package com.dyor.habithero.util

import com.dyor.habithero.data.source.featureflag.FeatureFlagManager
import com.dyor.habithero.data.source.local.DatabaseProvider
import com.dyor.habithero.data.source.local.DatabaseProviderImpl
import com.dyor.habithero.data.source.local.databaseModule
import com.dyor.habithero.data.source.preferences.PreferencesDataStoreProvider
import com.dyor.habithero.data.source.preferences.PreferencesDataStoreProviderImpl
import com.dyor.habithero.presentation.components.ads.AdsManager
import com.dyor.habithero.presentation.components.ads.IosAdsDisplayer
import com.dyor.habithero.util.analytics.Analytics
import com.dyor.habithero.util.file.FileManager
import com.dyor.habithero.util.file.FileManagerImpl
import com.mmk.kmpnotifier.KMPNotifier
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.mmk.kmpnotifier.push.firebase.FirebasePush
import com.mmk.kmpnotifier.push.firebase.onApplicationDidReceiveRemoteNotification
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.experimental.ExperimentalNativeApi

internal actual val platformModule: Module = module {
    includes(databaseModule)
    singleOf(::DatabaseProviderImpl) bind DatabaseProvider::class
    singleOf(::PreferencesDataStoreProviderImpl) bind PreferencesDataStoreProvider::class
    factory<FileManager> { FileManagerImpl() }
    factoryOf(::AppUtilImpl) bind AppUtil::class
}

internal fun swiftLibDependenciesModule(factory: SwiftLibDependencyFactory): Module = module {
    single { factory.provideFeatureFlagManagerImpl() } bind FeatureFlagManager::class
    single { factory.provideFirebaseAnalyticsImpl() } bind Analytics::class
    single { factory.provideAdsManagerImpl() } bind AdsManager::class
    single { factory.provideIosAdsDisplayer() } bind IosAdsDisplayer::class
}

internal actual fun onApplicationStartPlatformSpecific() {
    KMPNotifier.initialize(NotificationPlatformConfiguration.Ios(askNotificationPermissionOnStart = false), FirebasePush)
}

// Bridge so the iOS app forwards remote-notification callbacks without the Swift side
// (or the exported framework) depending on KMPNotifier types directly.
object IosPushNotificationHandler {
    fun onApplicationDidReceiveRemoteNotification(userInfo: Map<Any?, *>) {
        KMPNotifier.onApplicationDidReceiveRemoteNotification(userInfo)
    }
}

actual fun getPlatform(): Platform = Platform.Ios

internal actual val isAndroid = false

@OptIn(ExperimentalNativeApi::class)
internal actual val isDebug = kotlin.native.Platform.isDebugBinary

actual val defaultAsyncDispatcher: CoroutineDispatcher = Dispatchers.IO
