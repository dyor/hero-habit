# 🚀 Habit Hero — Pre-Launch Actionable TODO Checklist

This checklist contains **only the remaining tasks** that require your direct action in dashboards and config files. (Compliance and code checks such as zero broad storage permissions are already verified in code).

---

## 🔑 Phase 1: Wire Live Adapty Key into `local.properties`

> **How it works:** When building the release AAB on your machine, Gradle reads `SUBSCRIPTION_PROVIDER_ANDROID_API_KEY` from `MobileApp/local.properties` and compiles it into `BuildConfig`. When a real key is present (instead of empty/placeholder), the app automatically disables the mock demo paywall and activates real Google Play Billing via Adapty.

- [x] **1.1 Get your live Public SDK Key from Adapty:**
  1. Go to [https://app.adapty.io](https://app.adapty.io) $\rightarrow$ select **Habit Hero**.
  2. Click **App Settings** (gear icon) $\rightarrow$ **API Keys**.
  3. Copy the **Public SDK Key** (format: `public_live_...`).
- [x] **1.2 Paste it into `MobileApp/local.properties`:**
  Open `MobileApp/local.properties` and replace:
  ```properties
  SUBSCRIPTION_PROVIDER_ANDROID_API_KEY=
  ```
  with:
  ```properties
  SUBSCRIPTION_PROVIDER_ANDROID_API_KEY=public_live_YOUR_ACTUAL_KEY_HERE
  ```

---

## 💳 Phase 2: Adapty Dashboard Configuration

- [x] **2.1 Connect Google Play Service Account:** (Done — verified uploaded in Adapty Android SDK settings)
- [x] **2.2 Create In-App Consumable Products in Adapty:** (Done — 10, 40, 100 Credits created & synced green)
- [x] **2.3 Configure Placements in Adapty:** (Done — `default` and `credits_pack` placements are Live)

---

## 🏪 Phase 3: Google Play Console Tasks

- [x] **3.1 Create and Activate In-App Products:** (Done — `credit_pack_10`, `credit_pack_40`, `credit_pack_100` active)
- [x] **3.2 Set up Real-Time Developer Notifications (RTDN):** (Done — topic linked in Adapty & Play Console)
- [ ] **3.3 Verify App Content Forms:**
  - **Privacy Policy URL:** Set to `https://koko-demo-71050.web.app/herohabit/privacy-policy.html`
  - **Data Safety Form:** Ensure Analytics (Firebase), Crashlytics, and In-App Purchase history are declared.

---

## 📦 Phase 4: Build & Upload Release Bundle (AAB)

- [ ] **4.1 Build the Signed Release AAB:**
  Run from the `MobileApp/` directory:
  ```bash
  ./gradlew :androidApp:bundleRelease
  ```
  *(Output file will be at: `androidApp/build/outputs/bundle/release/androidApp-release.aab`)*
- [ ] **4.2 Upload to Google Play Console:**
  1. Go to **Release** $\rightarrow$ **Testing** $\rightarrow$ **Internal testing** (or Closed testing / Production).
  2. Click **Create new release**.
  3. Upload `androidApp-release.aab` (`versionCode = 2`, `versionName = "1.0.1"`).
  4. Paste Release Notes:
     ```text
     Initial launch of Habit Hero:
     - Daily habit tracking and photo check-ins
     - AI-generated vintage superhero comic covers for streak milestones
     - Hall of Heroes collection of streak milestone covers
     - 10 starter credits included
     ```
  5. Click **Next** $\rightarrow$ **Save** $\rightarrow$ **Start rollout**.
