# Phase 2 Progress — Integrations

> [!NOTE]
> Progress for Phase 2 of the KMP Contest Starter Kit.
> **Role Labels:**
> - **[User]:** Developer does it in a browser/console.
> - **[Agent]:** AI/developer can execute directly (edit code, write configurations).
> - **[Validate]:** A verification gate.

## 1. Key catalog — `configure-environment`
- [x] **[Agent]** Walk through `local.properties` keys, `gradle.properties` `SUBSCRIPTION_PROVIDER`, `AppConfiguration.kt` fields
- [x] **[User]** `MobileApp/local.properties` exists with `sdk.dir` and `REPLICATE_API_KEY`
- [x] **[Agent]** Run `./scripts/check_env.sh --phase integrations` to see which keys this phase needs

## 2. Firebase project + apps + anonymous auth — `setup-firebase`
- [x] **[Agent]** Renamed package to `com.koko.habittracker` matching Firebase project `koko-demo-71050`
- [x] **[User]** Firebase project active at `koko-demo-71050`
- [x] **[Agent]** Configured Android app (`com.koko.habittracker`)
- [x] **[Agent]** Configured iOS app (`com.koko.habittracker`)
- [x] **[Agent]** Downloaded and placed `google-services.json` → `MobileApp/androidApp/`
- [x] **[Agent]** Downloaded and placed `GoogleService-Info.plist` → `MobileApp/iosApp/iosApp/`
- [x] **[Validate]** `./gradlew :androidApp:assembleDebug` succeeds with real `google-services.json`

## 3. Authentication — anonymous first, social opt-in — `enable-auth`
- [x] **[Agent]** Anonymous / guest auth mode active (`AUTH_SOCIAL_LOGIN_ENABLED = false`)

## 4. Subscriptions — OPTIONAL commercial step (opt-in) — `setup-subscriptions`
- [x] **[Agent]** Monetization disabled per project requirements (`PREMIUM_FEATURES_ENABLED = false`)

## 5. Web proxy deploy + client wiring — `integrate-web-proxy`
- [x] **[Agent]** `REPLICATE_API_KEY` bound to Google Cloud Secret Manager
- [x] **[Agent]** `firebase deploy --only functions` deployed from `Web/` to `koko-demo-71050`
- [x] **[Agent]** `CLOUD_FUNCTIONS_URL` set to `https://us-central1-koko-demo-71050.cloudfunctions.net` in `AppConfiguration.kt`

## 6. Validation gate
- [x] **[Validate]** `./scripts/check_env.sh --phase integrations` is clean (all required keys set)
- [x] **[Validate]** `./gradlew :androidApp:assembleDebug` passes and deployed to emulator
