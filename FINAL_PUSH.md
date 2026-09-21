# 🚀 Habit Hero — Final Publication & Production Push Guide

This document is the **single source of truth** for publishing **Habit Hero** to both the **Google Play Store** and the **Apple App Store**, including Adapty, Firebase, and backend setup.

---

## 📱 1. Global App Identifiers & URLs

| Property | Value | Notes |
| :--- | :--- | :--- |
| **App Name** | `Habit Hero - Daily Habit Tracker` | Full store title (30 chars max on iOS: `Habit Hero: Habit Tracker`) |
| **Android Package Name / Application ID** | `com.dyor.habitheroapp` | Final. Published from the Dyor Labs LLC account (`com.dyor.habithero` stayed with the personal account) |
| **iOS Bundle Identifier** | `com.dyor.habithero` | Registered in Apple Developer Portal |
| **Monetization Model** | **Paid App ($0.99 upfront)** + Consumable Credit Refills | Includes 10 Starter Credits with download |
| **Support Email** | `matt@dyor.com` | Declared in app and store listings |
| **Privacy Policy URL** | `https://koko-demo-71050.web.app/herohabit/privacy-policy.html` | Subdirectory isolated from peer apps |
| **Terms & Conditions URL** | `https://koko-demo-71050.web.app/herohabit/terms-conditions.html` | Subdirectory isolated from peer apps |
| **Cloud Functions Base URL** | `https://us-central1-koko-demo-71050.cloudfunctions.net` | Secure AI proxy (OpenAI & Replicate) |

---

## 🌐 2. Web & Backend Pre-Flight (Do First)

Deploy the newly created isolated legal pages and Cloud Functions backend:

```bash
# Navigate to Web directory
cd Web

# 1. Deploy isolated web pages (privacy policy & terms)
firebase deploy --only hosting

# 2. Deploy AI proxy backend functions (if not already live)
firebase deploy --only functions
```

* **Verify in Browser:**
  * Open `https://koko-demo-71050.web.app/herohabit/privacy-policy.html`
  * Open `https://koko-demo-71050.web.app/herohabit/terms-conditions.html`

---

## 🤖 3. Google Play Store Launch Checklist

### A. Package Name / Account Migration Decision
> ✅ **DECIDED (2026-09-20): Option 2.** The Play app is `com.dyor.habitheroapp` in the **Dyor Labs LLC** organization account (developer id `7046295222512461008`, app id `4973275812658915597`). `applicationId` in `MobileApp/androidApp/build.gradle.kts`, the Firebase Android client, and Adapty's Android package name all use it. Only the Kotlin namespace and the iOS bundle id remain `com.dyor.habithero`.
>
> Uploads go through fastlane `upload_to_play_store` using the `play-publisher@koko-demo-71050.iam.gserviceaccount.com` key at `MobileApp/distribution/android/google-service-app-publisher.json` (gitignored). The upload keystore is `MobileApp/distribution/android/keystore/keystore.jks` (gitignored, alias `upload`, SHA-1 `79:A4:C6:3E:FA:93:5D:CB:EE:BF:09:B5:37:D5:97:04:0F:0A:15:A1`) — back it up.

---

### B. Google Play Console Setup

1. **Create New App:**
   * Name: `Habit Hero - Daily Habit Tracker`
   * Default language: `English (United States)`
   * App / Game: `App`
   * Free / Paid: **`Paid` ($0.99 USD)**

2. **Main Store Listing:**
   * **Short Description (80 chars):**
     ```text
     Daily habit tracker that turns your consistency into AI superhero comic covers!
     ```
   * **Full Description:**
     ```text
     Build lasting habits and discipline with Habit Hero — the daily habit tracker that turns your milestones into personalized vintage superhero comic book covers starring YOU!

     ⚡ HOW IT WORKS:
     1. Complete Your Quests: Log habits daily with zero-latency offline tracking.
     2. Snap a Victory Selfie: Snap a quick photo when you complete your daily routine.
     3. Mint Your Comic Cover: Hit an X-day streak and transform your selfie into a custom, high-resolution vintage comic book cover powered by AI!
     4. Hall of Heroes: Collect your milestone covers and share them directly with friends.

     Includes 10 starter credits to mint custom comic covers!
     ```
   * **Graphics Assets:**
     * App Icon: `MobileApp/distribution/android/playstore_metadata/app_icon_512.png` (512x512 PNG)
     * Feature Graphic: `MobileApp/distribution/android/playstore_metadata/feature_graphic_1024x500.png` (1024x500 PNG)
     * Phone Screenshots: `MobileApp/distribution/store_screenshots/en/iphone_6_5/*.png`

3. **In-App Products (Consumables):**
   * Go to **Monetize** $\rightarrow$ **Products** $\rightarrow$ **In-app products** $\rightarrow$ Create & **Activate**:

| Product ID | Product Name | Description | Price (USD) |
| :--- | :--- | :--- | :--- |
| `credit_pack_10` | 10 Comic Cover Credits | Refill pack for 10 custom AI comic covers | **$1.99** |
| `credit_pack_40` | 40 Comic Cover Credits | Best value refill pack for 40 custom AI comic covers | **$5.99** |
| `credit_pack_100` | 100 Comic Cover Credits | Ultimate refill pack for 100 custom AI comic covers | **$12.99** |

4. **Real-Time Developer Notifications (RTDN):**
   * Go to **Monetization setup** $\rightarrow$ **Google Play Billing** $\rightarrow$ Topic name:
     ```text
     projects/koko-demo-71050/topics/adapty-prod-b781a455-539b-4728-8bd2-0d2250bb5913
     ```
   * Click **Send test notification** and click **Save changes**.

5. **Google Cloud Service Account (for Adapty):**
   * In [Google Cloud Console](https://console.cloud.google.com/), go to **IAM & Admin** $\rightarrow$ **Service Accounts**.
   * Existing service account: `adapty-access@koko-demo-71050.iam.gserviceaccount.com` (key already uploaded to Adapty).
   * In Google Play Console $\rightarrow$ **Users & permissions** $\rightarrow$ Invite the service account with `Financial data` and `Manage orders and subscriptions` permissions.
   * Generate a **JSON Key** and upload it to Adapty under **App Settings $\rightarrow$ Android SDK**.

6. **App Content & Declarations:**
   * **Privacy Policy URL:** `https://koko-demo-71050.web.app/herohabit/privacy-policy.html`
   * **Target Audience:** 13+ (Not designed primarily for children).
   * **Data Safety Form:**
     * *Data collected:* App info/performance (Crashlytics), Financial info (In-App Purchase history via Adapty/Google Play), Photos (processed on-device and uploaded securely to cloud proxy only when user mints a cover).
     * *Data encrypted in transit:* Yes.
     * *Data deletion mechanism:* Provided via contact email (`matt@dyor.com`).
   * **Advertising ID:** Declared (Firebase Analytics).
   * **Financial Features:** In-app purchases declared.

7. **Release Track & 20-Tester Requirement:**
   * ⚠️ If your Google Play account is a personal account created after Nov 13, 2023:
     * Deploy first to the **Closed testing** track.
     * Add 20 testers and keep the test active for 14 continuous days before requesting production access.
   * If organization account: You can promote directly from Internal/Closed testing to **Production**.

8. **Build and Upload Android AAB:**
   ```bash
   cd MobileApp
   ./gradlew :androidApp:bundleRelease
   ```
   * AAB Output: `MobileApp/androidApp/build/outputs/bundle/release/androidApp-release.aab`
   * Upload to Play Console $\rightarrow$ Release name `1.0.2 (3)` $\rightarrow$ Save & Rollout.

---

## 🍎 4. Apple App Store Launch Checklist

### A. Apple Developer & App Store Connect Setup

1. **Identifiers & Capabilities:**
   * In [Apple Developer Portal](https://developer.apple.com/account/resources/identifiers/list):
     * Bundle ID: `com.dyor.habithero`
     * Capabilities: `In-App Purchase`, `Push Notifications`.

2. **Create New App in App Store Connect:**
   * Name: `Habit Hero: Daily Habit Tracker`
   * Primary Language: `English (US)`
   * Bundle ID: `com.dyor.habithero`
   * SKU: `habithero_ios_01`
   * User Access: `Full Access`

3. **Pricing & Availability:**
   * Base Price: **$0.99 (Tier 1)**.
   * Set availability to All Countries & Regions.

4. **App Information & Metadata:**
   * **Subtitle (30 chars max):** `Track Habits & Mint AI Comics`
   * **Promotional Text (170 chars):**
     ```text
     Turn daily consistency into superhero mythology. Snap victory selfies, build streaks, and mint vintage comic covers starring YOU! Includes 10 starter credits.
     ```
   * **Keywords (100 chars max):**
     ```text
     habit,tracker,streak,routine,selfie,comic,photo,journal,superhero,discipline,daily,planner,fitness
     ```
   * **Support URL:** `https://koko-demo-71050.web.app/herohabit/privacy-policy.html`
   * **Marketing URL:** `https://koko-demo-71050.web.app`
   * **Privacy Policy URL:** `https://koko-demo-71050.web.app/herohabit/privacy-policy.html`

5. **In-App Purchases (Consumables):**
   * Go to **App Store Connect** $\rightarrow$ **Monetization** $\rightarrow$ **In-App Purchases** $\rightarrow$ Create `Consumable`:

| Reference Name | Product ID | Price Tier | Review Screenshot |
| :--- | :--- | :--- | :--- |
| `10 Comic Cover Credits` | `credit_pack_10` | Tier 2 ($1.99) | `MobileApp/distribution/store_screenshots/en/iphone_6_5/paywall_review_screenshot_credits_CreditPackPaywallStoreScreenshot_iPhone_en.png` |
| `40 Comic Cover Credits` | `credit_pack_40` | Tier 6 ($5.99) | *(Same review screenshot)* |
| `100 Comic Cover Credits` | `credit_pack_100` | Tier 13 ($12.99) | *(Same review screenshot)* |

6. **App Privacy (Nutrition Labels):**
   * **Data Used to Track You:** None.
   * **Data Linked to You:** User ID / Diagnostics (Crash data).
   * **Data Not Linked to You:**
     * *Purchases:* Product interaction / purchase history.
     * *User Content:* Photos (Camera/Gallery uploads for comic generation).
     * *Usage Data:* Product interaction (Analytics).

7. **App Review Information:**
   * Sign-In Required: **No** (Anonymous authentication is automatic).
   * Demo Account: Not required.
   * Review Notes:
     ```text
     Habit Hero is a daily habit tracker that rewards habit completions and streaks with custom comic book covers.
     The app uses 10 starter credits included with the $0.99 upfront purchase. In-app purchases allow purchasing refill packs of consumable credits.
     ```

8. **Build and Archive iOS App:**
   * Open `MobileApp/iosApp/iosApp.xcodeproj` in Xcode.
   * Set scheme to `iosApp` $\rightarrow$ Generic iOS Device (or Any iOS Device).
   * Go to **Product** $\rightarrow$ **Archive** $\rightarrow$ **Distribute App** to App Store Connect / TestFlight.
   * *Or run via Fastlane:*
     ```bash
     cd MobileApp/fastlane
     bundle exec fastlane ios beta
     ```

---

## 💳 5. Adapty Production Configuration

### A. App Settings in Adapty Dashboard ([app.adapty.io](https://app.adapty.io))
1. Select app **Habit Hero**.
2. **Android Settings:**
   * Package Name: `com.dyor.habitheroapp`
   * Service Account Key: Upload Google Play Service Account JSON.
3. **iOS Settings:**
   * Bundle ID: `com.dyor.habithero`
   * App Store Connect API Key / Shared Secret: Upload from App Store Connect $\rightarrow$ Users and Access $\rightarrow$ Integrations.
   * App Store Server Notifications V2 URL: Copy Adapty webhook URL into App Store Connect.

### B. Products & Placements
1. **In-App Products:**
   * Ensure `credit_pack_10`, `credit_pack_40`, and `credit_pack_100` are created with both Android & iOS product IDs linked.
2. **Placements:**
   * `credits_pack` $\rightarrow$ Live with products `10 Credits`, `40 Credits`, `100 Credits`.
   * `default` $\rightarrow$ Live with products `10 Credits`, `40 Credits`, `100 Credits`.

### C. Live SDK Key in `MobileApp/local.properties`
```properties
SUBSCRIPTION_PROVIDER_ANDROID_API_KEY=public_live_YOUR_ACTUAL_ANDROID_KEY
SUBSCRIPTION_PROVIDER_IOS_API_KEY=public_live_YOUR_ACTUAL_IOS_KEY
```
*(When these live keys are set, the app switches from mock paywall to live store billing automatically).*

---

## 🎁 6. Issuing Free Credits to App Reviewers & Testers

* **Google Play Promo Codes:**
  * Play Console $\rightarrow$ **Monetize** $\rightarrow$ **Promo codes** $\rightarrow$ Create 50 one-time use codes for `credit_pack_10`.
* **Apple App Store Promo Codes:**
  * App Store Connect $\rightarrow$ **In-App Purchases** $\rightarrow$ `credit_pack_10` $\rightarrow$ **Promo Codes** $\rightarrow$ Generate 50 codes.
* **Direct Manual Credit Grant in Adapty:**
  * Adapty Dashboard $\rightarrow$ **Profiles** $\rightarrow$ Search user ID $\rightarrow$ Grant access manually.

---

## 🏁 7. Pre-Submission Quick Validation Checklist

- [ ] `firebase deploy --only hosting` deployed the `/herohabit/` privacy policy.
- [ ] `./gradlew :androidApp:assembleDebug` builds cleanly (Verified ✅).
- [ ] `./gradlew :androidApp:bundleRelease` generates `androidApp-release.aab`.
- [ ] In-App Consumable products created and activated in Google Play (`credit_pack_10`, `credit_pack_40`, `credit_pack_100`).
- [ ] In-App Consumable products created in App Store Connect (`credit_pack_10`, `credit_pack_40`, `credit_pack_100`).
- [ ] Live Adapty keys added to `local.properties`.
- [ ] Store screenshots uploaded to both consoles (`distribution/store_screenshots/en/iphone_6_5/`).
- [ ] Ready for review submission! 🚀
