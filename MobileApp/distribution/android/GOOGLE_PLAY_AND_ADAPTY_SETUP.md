# 🚀 Habit Hero — Google Play & Adapty Automation Guide

This guide is designed for automated execution (e.g. Claude Code with browser automation) or manual setup in **Google Play Console** and **Adapty**.

---

## 📱 App Overview & Identification
- **App Name:** `Habit Hero - Daily Habit Tracker`
- **Package Name / Application ID:** `com.dyor.habithero`
- **Default Language:** English (United States) (`en-US`)
- **App Category:** Health & Fitness / Productivity
- **Monetization Model:** **Paid App ($0.99 upfront)** with **10 Starter Credits** included + Consumable In-App Credit Refill Packs.

---

## 1. 🏪 Google Play Console Tasks

### [COMPLETED ✅] A. App Pricing ($0.99 Paid App)
- **Status:** **DONE** (Configured as Paid App at $0.99 USD).

---

### [COMPLETED ✅] B. Main Store Listing (Metadata & Copy)
- **Status:** **DONE** (App name, Short Description, and Full Description uploaded).

---

### [COMPLETED ✅] C. Store Listing Graphics & Assets
- **Status:** **DONE** (512x512 App Icon, 1024x500 Feature Graphic, and Phone Screenshots uploaded).

---

### [TODO ⏳ - FOCUS HERE] D. In-App Products (Consumable Packs)
Navigate to: **Monetize** $\rightarrow$ **Products** $\rightarrow$ **In-app products** $\rightarrow$ Click **Create product** for each:

| Product ID | Name | Description | Price (USD) | Status |
| :--- | :--- | :--- | :--- | :--- |
| `credit_pack_10` | 10 Comic Cover Credits | One-time refill pack for 10 custom AI comic covers | **$1.99** | Active |
| `credit_pack_40` | 40 Comic Cover Credits | Best value refill pack for 40 custom AI comic covers | **$5.99** | Active |
| `credit_pack_100` | 100 Comic Cover Credits | Ultimate refill pack for 100 custom AI comic covers | **$12.99** | Active |

> **Action:** Click **Save** and then click **Activate** on each product.

---

### [TODO ⏳ - FOCUS HERE] E. Google Cloud Pub/Sub (Real-Time Developer Notifications)
Navigate to: **Monetize** $\rightarrow$ **Monetization setup** $\rightarrow$ **Google Play Billing**

1. Enter Topic Name:
   ```text
   projects/koko-demo-71050/topics/adapty-prod-b781a455-539b-4728-8bd2-0d2250bb5913
   ```
2. Click **Send test notification** and click **Save changes**.

---

### [TODO ⏳ - FOCUS HERE] F. App Release (Upload AAB)
Navigate to: **Release** $\rightarrow$ **Testing** $\rightarrow$ **Internal testing** (or Closed testing / Production)

1. Click **Create new release**.
2. Upload the release App Bundle:
   - **Local File Path:** `/Users/mattdyor/Documents/kmp-contest-starter-kit/MobileApp/androidApp/build/outputs/bundle/release/androidApp-release.aab`
3. Release name: `1.0.1 (2)`
4. Release notes:
   ```text
   Initial launch of Habit Hero:
   - Daily habit tracking and photo check-ins
   - AI-generated vintage superhero comic covers for streak milestones
   - Hall of Heroes collection of streak milestone covers
   - 10 starter credits included
   ```
5. Click **Next** $\rightarrow$ **Save** $\rightarrow$ **Start rollout**.

---

## 2. 💳 Adapty Dashboard Tasks [TODO ⏳ - FOCUS HERE]

Navigate to: **[https://app.adapty.io](https://app.adapty.io)**

### A. App Settings
1. Select or create the app: **`Habit Hero`**
2. In **App Settings** $\rightarrow$ **Android Settings**:
   - Package name: `com.dyor.habithero`
   - Connect Google Play Service Account (or upload Google Play Service Account JSON key).

---

### B. In-App Products Configuration
Navigate to: **Products** $\rightarrow$ Click **Create Product** for each:

1. **Product 1:**
   - **Title:** `10 Credits`
   - **Type / Period:** `Consumable` (or *Lifetime / One-Time*)
   - **Android Product ID:** `credit_pack_10`
2. **Product 2:**
   - **Title:** `40 Credits`
   - **Type / Period:** `Consumable` (or *Lifetime / One-Time*)
   - **Android Product ID:** `credit_pack_40`
3. **Product 3:**
   - **Title:** `100 Credits`
   - **Type / Period:** `Consumable` (or *Lifetime / One-Time*)
   - **Android Product ID:** `credit_pack_100`

---

### C. Paywall & Placement Configuration
Navigate to: **Placements**

1. Create / Edit Placement with **Developer ID:** `credits_pack`
   - Name: `Comic Cover Credit Refills`
   - Add Products: `10 Credits`, `40 Credits`, `100 Credits`.
2. Create / Edit Placement with **Developer ID:** `default`
   - Add Products: `10 Credits`, `40 Credits`, `100 Credits`.

---

### D. Extract Public API Key
Navigate to: **App Settings** (gear icon) $\rightarrow$ **API Keys**

1. Copy the **Public SDK Key** (e.g. `public_live_xxxxxxxxxxxxxxxxxxxx`).
2. Paste it into your `MobileApp/local.properties` file:
   ```properties
   SUBSCRIPTION_PROVIDER_ANDROID_API_KEY=public_live_YOUR_ACTUAL_KEY_HERE
   ```

---

## 🎁 Issuing Free Credits to Users / Testers

1. **Via Google Play Console (Promo Codes):**
   - Go to **Monetize** $\rightarrow$ **Promo codes** $\rightarrow$ **Create promo code**.
   - Select product: `credit_pack_10` (or `credit_pack_40`).
   - Choose quantity (e.g. 50 codes) $\rightarrow$ Download CSV.
   - Anyone redeeming this code in Google Play gets the credits for **$0.00**.

2. **Via Adapty Dashboard (Customer Lookup):**
   - Go to **Profiles / Customers** $\rightarrow$ Find user by Anonymous ID or email.
   - Grant manual access / custom attributes directly from the dashboard.
