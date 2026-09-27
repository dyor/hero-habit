// config-indie.js
// Centralized configuration for the marketing landing page and legal documents.
const CONFIG = {
    // App & Developer Branding
    APP_NAME: "Indie Playbook",
    DEVELOPER_OR_COMPANY_NAME: "Indie Playbook",
    WEBSITE_TITLE: "Indie Playbook — Top Cross-Platform Indie Apps, Origin Stories & Growth Playbooks",
    WEBSITE_DESCRIPTION: "Discover the top independent mobile apps built with React Native, Flutter, and Kotlin Multiplatform, and learn how they were built, launched, and grown.",
    CONTACT_EMAIL: "admin@dyor.com",

    // Store Links (leave empty string "" if not yet published to hide badge)
    PLAYSTORE_URL: "https://play.google.com/store/apps/details?id=com.indieplaybook.app",
    APPSTORE_URL: "",

    // Legal Dates (YYYY-MM-DD)
    PRIVACY_POLICY_LAST_UPDATE_DATE: "2026-07-30",
    TERMS_AND_SERVICE_LAST_UPDATE_DATE: "2026-07-30",

    // Navigation & Social
    NAV_LINKS: [
        { label: "Features", href: "/indie-playbook/#features" },
        { label: "How It Works", href: "/indie-playbook/#how-it-works" },
        { label: "FAQ", href: "/indie-playbook/#faq" },
        { label: "Privacy Policy", href: "/privacy-policy.html" },
        { label: "Terms", href: "/terms-conditions.html" }
    ]
};

const TEXT_CONTENT = {
    // Top Bar / Tagline Badge
    HERO_BADGE: "📱 React Native · Flutter · Kotlin Multiplatform",

    // Hero Section
    HERO_TITLE: "Learn how top indie cross-platform apps got built, launched, and grown",
    HERO_SUBTITLE: "Indie Playbook is a curated directory of the most successful independent mobile apps built with cross-platform frameworks, with the origin stories and growth playbooks behind each one.",

    // Features Section
    FEATURES_SECTION_TAG: "WHAT'S INSIDE",
    FEATURES_SECTION_TITLE: "A field guide to the indie app ecosystem",
    FEATURES_SECTION_SUBTITLE: "Whether you're an indie maker, a mobile developer, or an entrepreneur, see what's actually working in production.",

    FEATURE_CARDS: [
        {
            icon: "📱",
            title: "Curated Indie Directory",
            text: "Explore top-ranking independent apps across App Store and Google Play categories, with download estimates, ratings, publishers, and release timelines."
        },
        {
            icon: "🛠️",
            title: "Framework Insights",
            text: "Filter by tech stack (Kotlin Multiplatform, Flutter, or React Native) to see which frameworks power real indie successes."
        },
        {
            icon: "📲",
            title: "Store Presence Tracking",
            text: "Quickly tell Dual Store apps, published on both Google Play and the App Store, apart from App Store exclusives."
        },
        {
            icon: "🌱",
            title: "Origin Stories",
            text: "Learn what motivated each founder to build the first version, and how they validated the idea before scaling."
        },
        {
            icon: "📈",
            title: "Growth Playbooks",
            text: "Study the viral loops, ASO, paid acquisition, and marketing strategies that took apps to thousands of users."
        },
        {
            icon: "💡",
            title: "Community Submissions",
            text: "Nominate a cross-platform indie app you love, or suggest edits to help the playbook grow."
        }
    ],

    // How It Works / Value Props
    WORKFLOW_SECTION_TAG: "HOW IT WORKS",
    WORKFLOW_SECTION_TITLE: "How Indie Playbook works",
    WORKFLOW_STEPS: [
        {
            step: "01",
            title: "Discover",
            text: "Browse a ranked, data-driven directory of indie apps built on App Store ranking and download data from AppFigures."
        },
        {
            step: "02",
            title: "Filter",
            text: "Narrow it down by framework, store presence, or story type to find the apps most relevant to what you're building."
        },
        {
            step: "03",
            title: "Learn",
            text: "Read each app's origin story and growth playbook, then follow links to its website and store pages."
        }
    ],

    // FAQ Section
    FAQ_SECTION_TAG: "FREQUENTLY ASKED QUESTIONS",
    FAQ_SECTION_TITLE: "Got questions? We've got answers",
    FAQS: [
        {
            q: "Is Indie Playbook free?",
            a: "Yes. Indie Playbook is free to download and use, with no ads and no subscriptions."
        },
        {
            q: "Where does the data come from?",
            a: "Rankings, ratings, and download estimates are based on App Store and Google Play ranking data from AppFigures. Origin stories and growth playbooks are researched and curated, and the community can suggest edits."
        },
        {
            q: "Do I need an account?",
            a: "No. You can browse as a guest. Signing in with an email address is optional and lets you submit apps and suggest edits."
        },
        {
            q: "How does Indie Playbook protect my privacy?",
            a: "We collect only what's needed to run the app: your email if you sign in, and crash diagnostics. We don't sell or share your data with third parties, and everything is sent over HTTPS. See our <a href="/privacy-policy.html">Privacy Policy</a> for details."
        },
        {
            q: "How do I delete my account and data?",
            a: "Delete your account directly in the app from your Profile / Settings, or email admin@dyor.com and we'll take care of it."
        }
    ],

    // CTA Banner Section
    CTA_SECTION_TAG: "GET STARTED TODAY",
    CTA_SECTION_TITLE: "Start exploring the indie app ecosystem",
    CTA_SECTION_TEXT: "Download Indie Playbook and see how today's top cross-platform indie apps were built, launched, and grown."
};
