---
name: shared-web-sync
description: Synchronizes and deploys shared web and legal assets between peer apps Habit Hero and Indie Playbook on Firebase project koko-demo-71050. Use whenever updating privacy policies, terms, or landing pages in either app before deploying hosting.
---

# Shared Web & Legal Pages Sync (`shared-web-sync`)

## Context & Architecture
Both **Habit Hero** (`/Users/mattdyor/StudioProjects/HabitHero`) and **Indie Playbook** (`/Users/mattdyor/StudioProjects/indie-playbook`) share the same Firebase Hosting project: **`koko-demo-71050`**.

Because Firebase Hosting deploys are atomic (a deploy from one repo wipes out files that only exist in that repo), both repos share a synchronized `Web/public` directory structure so neither app's deployment breaks the other.

### URL Mapping on `https://koko-demo-71050.web.app`:
* **Habit Hero:**
  * 🏠 Main Landing Page: `https://koko-demo-71050.web.app/` (uses `index.html` + `config.js`)
  * 📄 Privacy Policy: `https://koko-demo-71050.web.app/herohabit/privacy-policy.html`
  * ⚖️ Terms & Conditions: `https://koko-demo-71050.web.app/herohabit/terms-conditions.html`
* **Indie Playbook:**
  * 📄 Privacy Policy: `https://koko-demo-71050.web.app/privacy-policy.html` (uses `config-indie.js` + `updateContent-indie.js`)
  * ⚖️ Terms & Conditions: `https://koko-demo-71050.web.app/terms-conditions.html` (uses `config-indie.js` + `updateContent-indie.js`)

---

## The Synchronization Mechanism
A dedicated shell script `sync_web.sh` is placed in the `Web/` folder of **both** repositories:
* `/Users/mattdyor/StudioProjects/HabitHero/Web/sync_web.sh`
* `/Users/mattdyor/StudioProjects/indie-playbook/Web/sync_web.sh`

### What it does:
1. Copies Indie Playbook's root legal files (`privacy-policy.html`, `terms-conditions.html`, `config-indie.js`, `updateContent-indie.js`) into Habit Hero's `Web/public/`.
2. Copies Habit Hero's landing page (`index.html`, `styles.css`, `config.js`, `updateContent.js`, `404.html`), `images/`, and the isolated `/herohabit/` folder into Indie Playbook's `Web/public/`.
3. Ensures that deploying from **either** project results in all 5 URLs remaining live and intact.

---

## Standard Execution Command
Whenever you or an agent make changes to legal policies, config files, or landing pages in **either** app, run this command to sync and deploy:

### If you are in the `MobileApp/` subfolder:
```bash
cd ../Web && ./sync_web.sh && firebase deploy --only hosting
```

### If you are in the repository root (`HabitHero/` or `indie-playbook/`):
```bash
cd Web && ./sync_web.sh && firebase deploy --only hosting
```

---

## Agent Instructions
* If the user asks to update privacy policies or terms for either app, apply the changes to the appropriate file.
* Always execute `./sync_web.sh` before running `firebase deploy --only hosting` to ensure cross-app consistency.
* Never remove the `config-indie.js` split from Indie Playbook's root legal pages, as that prevents Habit Hero's `config.js` from overriding Indie Playbook's metadata.
