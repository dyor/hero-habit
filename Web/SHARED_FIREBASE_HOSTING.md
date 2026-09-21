# 🌐 Shared Firebase Hosting: Habit Hero & Indie Playbook

## 📌 Overview & Context
Both **Habit Hero** (`/Users/mattdyor/StudioProjects/HabitHero`) and **Indie Playbook** (`/Users/mattdyor/StudioProjects/indie-playbook`) are peer projects sharing the **same Firebase project** (`koko-demo-71050`) for Firebase Hosting.

### ⚠️ The Overwrite Risk (Why Sync is Needed)
Firebase Hosting deployments are **atomic**: whenever you run `firebase deploy --only hosting` in one repository, Firebase replaces the entire live hosting site with that repository's local `public/` folder. Without synchronization, deploying one project would delete or overwrite the other project's pages.

To solve this, both projects maintain a synchronized `public/` directory with isolated namespaces.

---

## 🗺️ Live URL Mapping (`https://koko-demo-71050.web.app`)

| App | Purpose | Live URL | Source Files |
| :--- | :--- | :--- | :--- |
| **Habit Hero** | 🏠 Main Landing Page | `https://koko-demo-71050.web.app/` | `index.html`, `styles.css`, `config.js`, `updateContent.js`, `images/` |
| **Habit Hero** | 📄 Privacy Policy | `https://koko-demo-71050.web.app/herohabit/privacy-policy.html` | `herohabit/privacy-policy.html`, `herohabit/config.js` |
| **Habit Hero** | ⚖️ Terms & Conditions | `https://koko-demo-71050.web.app/herohabit/terms-conditions.html` | `herohabit/terms-conditions.html`, `herohabit/config.js` |
| **Indie Playbook** | 📄 Privacy Policy | `https://koko-demo-71050.web.app/privacy-policy.html` | `privacy-policy.html`, `config-indie.js`, `updateContent-indie.js` |
| **Indie Playbook** | ⚖️ Terms & Conditions | `https://koko-demo-71050.web.app/terms-conditions.html` | `terms-conditions.html`, `config-indie.js`, `updateContent-indie.js` |
| **Indie Playbook** | 📖 Interactive Playbook | `https://koko-demo-71050.web.app/playbook.html` | `playbook.html`, `styles-indie.css` |
| **Indie Playbook** | 📱 Indie Apps Redirect | `https://koko-demo-71050.web.app/indie-apps.html` | `indie-apps.html` |

---

## 🔄 How to Keep Both Apps in Sync

A synchronization script, **`sync_web.sh`**, exists in the `Web/` directory of **both** repositories:
* `HabitHero/Web/sync_web.sh`
* `indie-playbook/Web/sync_web.sh`

### What `sync_web.sh` Does:
1. Copies all **Indie Playbook** assets (`privacy-policy.html`, `terms-conditions.html`, `config-indie.js`, `updateContent-indie.js`, `playbook.html`, `styles-indie.css`, `indie-apps.html`) into Habit Hero's `Web/public/`.
2. Copies all **Habit Hero** assets (`index.html`, `styles.css`, `config.js`, `updateContent.js`, `404.html`, `images/`, and the isolated `herohabit/` folder) into Indie Playbook's `Web/public/`.
3. Ensures that whichever repository you deploy from, all pages for **both** apps remain active and intact.

---

## 🚀 How to Deploy Web Changes

Whenever you or an agent update web pages, privacy policies, terms, or landing page copy in **either** project:

### From Habit Hero:
```bash
# If in MobileApp directory:
cd ../Web && ./sync_web.sh && firebase deploy --only hosting

# If in HabitHero root directory:
cd Web && ./sync_web.sh && firebase deploy --only hosting
```

### From Indie Playbook:
```bash
# If in MobileApp directory:
cd ../Web && ./sync_web.sh && firebase deploy --only hosting

# If in indie-playbook root directory:
cd Web && ./sync_web.sh && firebase deploy --only hosting
```
