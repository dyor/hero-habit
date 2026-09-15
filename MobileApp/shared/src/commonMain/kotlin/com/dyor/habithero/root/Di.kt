package com.dyor.habithero.root

import com.dyor.habithero.common.BuildConfig
import com.dyor.habithero.data.BackgroundExecutor
import com.dyor.habithero.data.repository.CreditRepository
import com.dyor.habithero.data.repository.GenerationRepository
import com.dyor.habithero.data.repository.HabitRepository
import com.dyor.habithero.data.repository.SubscriptionRepository
import com.dyor.habithero.data.repository.UserRepository
import com.dyor.habithero.data.source.ai.OpenAiImageGenerationProvider
import com.dyor.habithero.data.source.ai.ReplicateGenerationProvider
import com.dyor.habithero.data.source.preferences.PreferencesDataStoreProvider
import com.dyor.habithero.data.source.preferences.UserPreferences
import com.dyor.habithero.data.source.preferences.UserPreferencesImpl
import com.dyor.habithero.data.source.remote.HttpClientFactory
import com.dyor.habithero.data.source.remote.apiservices.ApiService
import com.dyor.habithero.data.source.remote.apiservices.TemporaryFileUploadApiService
import com.dyor.habithero.data.source.remote.apiservices.ai.AiTransport
import com.dyor.habithero.data.source.remote.apiservices.ai.OpenAiApiService
import com.dyor.habithero.data.source.remote.apiservices.ai.ReplicateApiService
import com.dyor.habithero.domain.model.credit.creditSystemConfig
import com.dyor.habithero.domain.usecase.AiGenerationProvider
import com.dyor.habithero.presentation.screens.account.AccountViewModel
import com.dyor.habithero.presentation.screens.celebration.CelebrationViewModel
import com.dyor.habithero.presentation.screens.creditbalance.CreditBalanceViewModel
import com.dyor.habithero.presentation.screens.gallery.GalleryViewModel
import com.dyor.habithero.presentation.screens.generationresult.GenerationResultViewModel
import com.dyor.habithero.presentation.screens.habitdetail.HabitDetailViewModel
import com.dyor.habithero.presentation.screens.home.HomeViewModel
import com.dyor.habithero.presentation.screens.onboarding.OnBoardingViewModel
import com.dyor.habithero.presentation.screens.paywall.PaywallViewModel
import com.dyor.habithero.presentation.screens.profile.ProfileViewModel
import com.dyor.habithero.presentation.screens.subscriptions.SubscriptionsViewModel
import com.dyor.habithero.subscription.api.MockSubscriptionProvider
import com.dyor.habithero.subscription.api.NoOpSubscriptionProviderUi
import com.dyor.habithero.subscription.api.SubscriptionProvider
import com.dyor.habithero.subscription.api.SubscriptionProviderFactory
import com.dyor.habithero.subscription.api.SubscriptionProviderUi
import com.dyor.habithero.util.ApplicationScope
import com.dyor.habithero.util.Constants
import com.dyor.habithero.util.defaultAsyncDispatcher
import com.dyor.habithero.util.extensions.nowEpochMillis
import com.dyor.habithero.util.isAndroid
import com.dyor.habithero.util.logging.Logger
import com.dyor.habithero.util.logging.NapierLogger
import com.dyor.habithero.util.logging.TelegramLogger
import com.dyor.habithero.util.platformModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

/**
 * Koin module graph for the shared app. [AppInitializer] loads [appModules] at startup.
 * Layered by concern: [domainModule] (pure, empty), [dataModule] (infra + repositories),
 * [presentationModule] (ViewModels), plus the per-target expect/actual `platformModule`.
 */

// Empty by design — the domain layer is pure (models/exceptions), nothing to inject.
private val domainModule = module {
}

// Infrastructure + repositories: scopes/dispatchers, preferences, network, auth & subscription
// providers (selected via Constants), repositories, loggers, AI provider, and the credit system.
private val dataModule = module {
    singleOf(::ApplicationScope)
    factory { defaultAsyncDispatcher } bind CoroutineContext::class
    factory { BackgroundExecutor.IO } bind BackgroundExecutor::class

    // Preferences Source. The DataStore instance stays out of the Koin graph on
    // purpose — generic types erase to `DataStore`, so a second DataStore<T>
    // registered later would silently collide with this one.
    single { UserPreferencesImpl(get<PreferencesDataStoreProvider>().providePreferencesDataStore()) } bind UserPreferences::class

    // Remote source
    single { HttpClientFactory.default() }
    single(named("aiDirectClient")) { HttpClientFactory.noAuth() }
    single { AiTransport(proxyClient = get(), directClient = get(named("aiDirectClient"))) }
    single { TemporaryFileUploadApiService(HttpClientFactory.fileUpload()) }

    factoryOf(::ApiService)
    factoryOf(::OpenAiApiService)
    factoryOf(::ReplicateApiService)

    // Subscription Provider. When no real SDK key is set (isSubscriptionMockActive), swap in the
    // MockSubscriptionProvider so the paywall/purchase/unlock flow is explorable with zero keys.
    // Auto-reverts to the real provider (Adapty/RevenueCat) the moment a key is configured.
    factory { AppConfiguration.subscriptionProviderFactory } bind SubscriptionProviderFactory::class
    single {
        if (isSubscriptionMockActive()) {
            val userPreferences = get<UserPreferences>()
            MockSubscriptionProvider(
                readPremiumPurchased = {
                    userPreferences.getBoolean(MockSubscriptionProvider.KEY_MOCK_PREMIUM_PURCHASED, false)
                },
                writePremiumPurchased = {
                    userPreferences.putBoolean(MockSubscriptionProvider.KEY_MOCK_PREMIUM_PURCHASED, it)
                },
                premiumAccessId = Constants.PAYWALL_PREMIUM_ACCESS,
                creditPackPrefix = Constants.CREDIT_PACK_PRODUCT_ID_PREFIX,
                creditPackPlacementId = Constants.PAYWALL_PLACEMENT_CREDITS_PACK,
                currentTimeMillis = ::nowEpochMillis,
            )
        } else {
            get<SubscriptionProviderFactory>().createProvider()
        }
    } bind SubscriptionProvider::class
    factory {
        if (isSubscriptionMockActive()) {
            NoOpSubscriptionProviderUi
        } else {
            get<SubscriptionProviderFactory>().createProviderUi()
        }
    } bind SubscriptionProviderUi::class

    // Repositories
    single { UserRepository(get(), get(), get(), get()) }
    single { SubscriptionRepository(get(), get(), get(), get()) }
    single { GenerationRepository(get(), get(), get(), get(), get(), get(), get()) }
    single { HabitRepository(get(), get(), get()) }

    // Loggers
    factory { TelegramLogger(get(), get(), get()) } bind Logger::class
    factory { NapierLogger() } bind Logger::class

//    factory<AiGenerationProvider> { OpenAiImageGenerationProvider(get(), get()) }
    factory<AiGenerationProvider> { ReplicateGenerationProvider(get(), get()) }

    initializeCreditSystem()
}

// ViewModels, scoped per NavEntry. PaywallViewModel takes a placementId param.
private val presentationModule = module {
    viewModelOf(::OnBoardingViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::GalleryViewModel)
    viewModelOf(::ProfileViewModel)
    viewModel { (placementId: String?) ->
        PaywallViewModel(
            placementId = placementId,
            subscriptionRepository = get(),
            creditRepository = get(),
            userRepository = get(),
            featureFlagManager = get(),
        )
    }
    viewModelOf(::AccountViewModel)
    viewModelOf(::SubscriptionsViewModel)
    viewModelOf(::GenerationResultViewModel)
    viewModelOf(::CreditBalanceViewModel)

    viewModelOf(::CelebrationViewModel)
    viewModel { (habitId: String) ->
        HabitDetailViewModel(
            habitId = habitId,
            habitRepository = get(),
            comicCoverDao = get(),
            openAiApiService = get(),
        )
    }
    // Add new view models below — generate_screen.sh inserts here.
}

private fun Module.initializeCreditSystem() {
    single {
        val appCreditSystemConfig = creditSystemConfig {
            // 10 free comic book covers for all new recruits to experience their AI superhero covers!
            oneTimeBonus("welcome_bonus_credit", 10)
        }

        CreditRepository(appCreditSystemConfig, get(), get(), get(), get())
    }
}

/**
 * True when the active-platform subscription SDK key is still a placeholder, i.e. no real
 * Adapty/RevenueCat account is wired yet. While true the app runs [MockSubscriptionProvider] so the
 * whole paywall → purchase → unlock flow is explorable with zero keys. Auto-off once a real key is set.
 */
private fun isSubscriptionMockActive(): Boolean {
    val key =
        if (isAndroid) {
            BuildConfig.SUBSCRIPTION_PROVIDER_ANDROID_API_KEY
        } else {
            BuildConfig.SUBSCRIPTION_PROVIDER_IOS_API_KEY
        }
    return key.isBlank() || key == MockSubscriptionProvider.PLACEHOLDER_KEY
}

// All Koin modules loaded at startup. platformModule is the expect/actual per-target module.
internal val appModules: List<Module> get() = platformModule + domainModule + dataModule + presentationModule
