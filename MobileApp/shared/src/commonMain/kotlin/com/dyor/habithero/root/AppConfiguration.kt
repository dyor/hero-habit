package com.dyor.habithero.root

import com.dyor.habithero.subscription.config.activeSubscriptionProviderFactory

/**
 * Per-app configuration a developer sets/flips when spinning up an app on this kit — behavioral toggles,
 * backend URLs, contact/legal info, and the auth/subscription provider selectors.
 *
 * Compile-time (baked into the binary). This is distinct from:
 * - `Constants` — framework details unlikely to change per app (DB/prefs file names, paywall
 *   entitlement/placement ids, product-id conventions).
 * - `FeatureFlagManager` — runtime flags controllable remotely via Firebase Remote Config.
 */
object AppConfiguration {

    // Live privacy policy URL
    const val URL_PRIVACY_POLICY = "https://koko-demo-71050.web.app/herohabit/privacy-policy.html"

    // Live terms & conditions URL
    const val URL_TERMS_CONDITIONS = "https://koko-demo-71050.web.app/herohabit/terms-conditions.html"

    // Real support email
    const val CONTACT_EMAIL = "matt@dyor.com"

    // Numeric App Store id (set after App Store Connect creation)
    const val APPSTORE_APP_ID = ""

    /**
     * How AI (OpenAI/Replicate) calls are routed.
     * Use true for production so all calls go through secure Firebase Cloud Functions.
     */
    val USE_AI_PROXY_SERVER: Boolean? = true

    /**
     * CLOUD_FUNCTIONS_URL should be something like: "https://REGION-PROJECT_ID.cloudfunctions.net"
     * Regions:
     * US(Default): us-central1
     * EU: europe-west1
     *
     * This is used AI proxy functions such as OpenAi, Replicate
     */
    const val CLOUD_FUNCTIONS_URL = "https://us-central1-koko-demo-71050.cloudfunctions.net"

    // Enables Apple and Google sign-in. If false, only anonymous login is supported.
    // Default false — anonymous auth is the easiest path to a working app (just Firebase +
    // Anonymous sign-in). Flip to true to add Google/Apple (see the enable-auth skill for the
    // extra config: GOOGLE_WEB_CLIENT_ID, iOS Info.plist client IDs, Sign In with Apple capability).
    const val AUTH_SOCIAL_LOGIN_ENABLED = false

    /**
     * Whether the app has any **premium (paid/gated) features**.
     *
     * - `false`: **no premium features** — everything is unlocked and free.
     * - `true`: the app HAS premium features — subscriptions, paywall, and credits are available
     *   to gate or limit features.
     */
    const val PREMIUM_FEATURES_ENABLED = true

    /**
     * The subscription provider is chosen in ONE place — the `SUBSCRIPTION_PROVIDER`
     * Gradle property in `gradle.properties` (default: `ADAPTY`):
     *
     *   SUBSCRIPTION_PROVIDER=ADAPTY
     *   or
     *   SUBSCRIPTION_PROVIDER=REVENUECAT
     *
     *   Also in local.properties add the following:
     *
     *   SUBSCRIPTION_PROVIDER_ANDROID_API_KEY=YOUR_API_KEY
     *   SUBSCRIPTION_PROVIDER_IOS_API_KEY=YOUR_API_KEY
     *
     * That property decides which provider module is on the classpath; this accessor
     * resolves to whichever one is linked. Do NOT name a concrete provider here.
     */
    val subscriptionProviderFactory get() = activeSubscriptionProviderFactory
}
