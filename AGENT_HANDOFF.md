# 🤖 Agent Handoff & Architecture Status: Habit Hero

> **ATTENTION AGENTS**: Read this document first before performing any git operations, builds, or web deployments in this codebase.

---

## 1. 📦 Git Repository Status & Standalone Independence

### A. Repository Migration to Standalone (`hero-habit`)
* **Remote Origin URL:** `https://github.com/dyor/hero-habit.git`
* **Ownership:** 100% independent, standalone GitHub repository owned by `@dyor`.
* **Fork Detachment:** This repository is **NOT** a fork of `KotlinFoundation/kmp-contest-starter-kit`.
  * The local `upstream` remote has been permanently removed.
  * You can push, pull, create branches, and merge without any risk of touching or targeting KotlinFoundation.
  * Your work will **never** trigger accidental PRs to the KotlinConf template.

### B. Active Branches
* **`main`**: The primary default branch. It contains all merged Habit Hero code, models, screens, legal pages, and configuration.
* **`feature/add-subscription`**: The working feature branch where recent feature development occurred (also synced to `origin`).
* **Pulling latest changes:** Run `git pull origin main`.

---

## 2. 🌐 Shared Firebase Hosting (`koko-demo-71050`)

### A. The Setup
Both **Habit Hero** (`/Users/mattdyor/StudioProjects/HabitHero`) and **Indie Playbook** (`/Users/mattdyor/StudioProjects/indie-playbook`) are peer projects sharing Firebase project **`koko-demo-71050`**.

Because Firebase Hosting deployments are atomic (deploying one project wipes out files that only exist in that local repo), both repositories share a synchronized `Web/public` directory structure so neither deployment breaks the other.

### B. Live URL Mapping
* **Habit Hero:**
  * 🏠 Main Landing Page: `https://koko-demo-71050.web.app/`
  * 📄 Privacy Policy: `https://koko-demo-71050.web.app/herohabit/privacy-policy.html`
  * ⚖️ Terms & Conditions: `https://koko-demo-71050.web.app/herohabit/terms-conditions.html`
* **Indie Playbook:**
  * 📄 Privacy Policy: `https://koko-demo-71050.web.app/privacy-policy.html` (loads `config-indie.js`)
  * ⚖️ Terms & Conditions: `https://koko-demo-71050.web.app/terms-conditions.html` (loads `config-indie.js`)
  * 📖 Interactive Playbook: `https://koko-demo-71050.web.app/playbook.html`
  * 📱 Indie Apps Redirect: `https://koko-demo-71050.web.app/indie-apps.html`

### C. The Golden Rule for Deploying Web
**ALWAYS** run `./sync_web.sh` before running `firebase deploy --only hosting`:
```bash
# If inside MobileApp directory:
cd ../Web && ./sync_web.sh && firebase deploy --only hosting

# If inside HabitHero root directory:
cd Web && ./sync_web.sh && firebase deploy --only hosting
```
*(Reference: `Web/SHARED_FIREBASE_HOSTING.md` and agent skill `shared-web-sync`).*

---

## 3. 📱 Mobile App Release Details

* **App Name:** `Habit Hero`
* **Android Application ID:** `com.dyor.habitheroapp` (Dyor Labs LLC Play account; see `FINAL_PUSH.md` §3A)
* **iOS Bundle Identifier:** `com.dyor.habithero`
* **Current Version:** `versionCode = 4`, `versionName = "1.0.3"`
* **Release AAB Path:** `MobileApp/androidApp/build/outputs/bundle/release/androidApp-release.aab`
* **Publication Guide:** See `FINAL_PUSH.md` for complete store publication checklists for both Google Play and Apple App Store.
