// config-indie.js
// Centralized configuration for the marketing landing page and legal documents.
const CONFIG = {
    // App & Developer Branding
    APP_NAME: "Indie Playbook",
    DEVELOPER_OR_COMPANY_NAME: "Indie Playbook",
    WEBSITE_TITLE: "Indie Playbook — Build, Launch & Grow Your Mobile Apps",
    WEBSITE_DESCRIPTION: "Indie Playbook is the ultimate companion for indie makers and developers to turn app ideas into successful, revenue-generating mobile apps.",
    CONTACT_EMAIL: "admin@dyor.com",

    // Store Links (leave empty string "" if not yet published to hide badge)
    PLAYSTORE_URL: "https://play.google.com/store/apps",
    APPSTORE_URL: "https://apps.apple.com/app",

    // Legal Dates (YYYY-MM-DD)
    PRIVACY_POLICY_LAST_UPDATE_DATE: "2026-07-30",
    TERMS_AND_SERVICE_LAST_UPDATE_DATE: "2026-07-30",

    // Navigation & Social
    NAV_LINKS: [
        { label: "Features", href: "#features" },
        { label: "How It Works", href: "#how-it-works" },
        { label: "FAQ", href: "#faq" },
        { label: "Privacy Policy", href: "privacy-policy.html" },
        { label: "Terms", href: "terms-conditions.html" }
    ]
};

const TEXT_CONTENT = {
    // Top Bar / Tagline Badge
    HERO_BADGE: "🚀 Built for Indie Developers & Creators",

    // Hero Section
    HERO_TITLE: "Turn Ideas into Shipped Mobile Apps Faster",
    HERO_SUBTITLE: "From concept and validated feature planning to multiplatform launch and growth. The complete playbook to build, launch, and monetize your apps.",

    // Features Section
    FEATURES_SECTION_TAG: "POWERFUL FEATURES",
    FEATURES_SECTION_TITLE: "Everything you need to launch with confidence",
    FEATURES_SECTION_SUBTITLE: "Designed from the ground up to eliminate guesswork and speed up your development workflow.",

    FEATURE_CARDS: [
        {
            icon: "💡",
            title: "Idea & Flow Validation",
            text: "Brainstorm high-impact app concepts, structure intuitive user flows, and validate core features before writing code."
        },
        {
            icon: "⚡",
            title: "Rapid Multiplatform Development",
            text: "Leverage cross-platform power across Android, iOS, Desktop, and Web with shared business logic and native performance."
        },
        {
            icon: "💳",
            title: "Turnkey Monetization",
            text: "Seamlessly integrate in-app purchases, subscriptions, and flexible credit models built for maximum lifetime value."
        },
        {
            icon: "🔒",
            title: "Privacy & Compliance First",
            text: "Stay fully compliant with Apple App Store and Google Play privacy policies, data deletion rules, and safety guidelines."
        },
        {
            icon: "📊",
            title: "Built-in Analytics & Growth",
            text: "Track engagement, diagnose crashes in real time, and deploy updates with robust crash reporting and remote config."
        },
        {
            icon: "🎨",
            title: "Modern Native Design System",
            text: "Craft polished, accessible user experiences using pre-tested components, dark mode, and fluid typography."
        }
    ],

    // How It Works / Value Props
    WORKFLOW_SECTION_TAG: "SEAMLESS WORKFLOW",
    WORKFLOW_SECTION_TITLE: "How Indie Playbook works",
    WORKFLOW_STEPS: [
        {
            step: "01",
            title: "Plan & Structure",
            text: "Define your product requirements, target audience, and feature roadmap with structured playbooks."
        },
        {
            step: "02",
            title: "Build & Iterate",
            text: "Develop features quickly with clean architecture, offline-ready local storage, and real-time testing."
        },
        {
            step: "03",
            title: "Publish & Scale",
            text: "Deploy to Google Play and the Apple App Store with full store compliance, automated builds, and marketing landing pages."
        }
    ],

    // FAQ Section
    FAQ_SECTION_TAG: "FREQUENTLY ASKED QUESTIONS",
    FAQ_SECTION_TITLE: "Got questions? We've got answers",
    FAQS: [
        {
            q: "Which platforms are supported?",
            a: "Indie Playbook is built for Android and iOS devices, with desktop and web companions supported out of the box."
        },
        {
            q: "How does Indie Playbook protect user privacy?",
            a: "We prioritize privacy: all personal data is transmitted securely over HTTPS/TLS, minimal telemetry is collected for crash diagnostics, and users can request account and data deletion at any time directly in the app."
        },
        {
            q: "How do I request account and data deletion?",
            a: "You can delete your account and associated data directly within the app under Profile/Settings > Delete Account, or by contacting our support team at our contact email."
        },
        {
            q: "Can I use the app for free?",
            a: "Yes! Core features are accessible for free. Optional premium upgrades and subscriptions unlock advanced tools and unlimited access."
        }
    ],

    // CTA Banner Section
    CTA_SECTION_TAG: "GET STARTED TODAY",
    CTA_SECTION_TITLE: "Ready to ship your next app?",
    CTA_SECTION_TEXT: "Join developers building sustainable, revenue-generating mobile apps. Download Indie Playbook today."
};
