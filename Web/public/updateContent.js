function loadConfigAndUpdateContent() {
    document.querySelectorAll(".app-name").forEach(function(element) {
        if (CONFIG && CONFIG.APP_NAME) element.textContent = CONFIG.APP_NAME;
    });
    document.querySelectorAll(".developer-or-company-name").forEach(function(element) {
        if (CONFIG && CONFIG.DEVELOPER_OR_COMPANY_NAME) element.textContent = CONFIG.DEVELOPER_OR_COMPANY_NAME;
    });
    document.querySelectorAll(".contact-email").forEach(function(element) {
        if (CONFIG && CONFIG.CONTACT_EMAIL) {
            element.href = "mailto:" + CONFIG.CONTACT_EMAIL;
            element.textContent = CONFIG.CONTACT_EMAIL;
        }
    });
    document.querySelectorAll(".privacy-policy-last-update-date").forEach(function(element) {
        if (CONFIG && CONFIG.PRIVACY_POLICY_LAST_UPDATE_DATE) element.textContent = CONFIG.PRIVACY_POLICY_LAST_UPDATE_DATE;
    });
    document.querySelectorAll(".terms-and-services-last-update-date").forEach(function(element) {
        if (CONFIG && CONFIG.TERMS_AND_SERVICE_LAST_UPDATE_DATE) element.textContent = CONFIG.TERMS_AND_SERVICE_LAST_UPDATE_DATE;
    });
    document.querySelectorAll(".current-year").forEach(function(element) {
        element.textContent = new Date().getFullYear();
    });

    // Set website title and description
    if (CONFIG && CONFIG.WEBSITE_TITLE) {
        document.title = CONFIG.WEBSITE_TITLE;
    }
    const metaDesc = document.querySelector('meta[name="description"]');
    if (metaDesc && CONFIG && CONFIG.WEBSITE_DESCRIPTION) {
        metaDesc.setAttribute('content', CONFIG.WEBSITE_DESCRIPTION);
    }

    // Set app store and play store links
    document.querySelectorAll(".app-store-link").forEach(function(element) {
        if (CONFIG && CONFIG.APPSTORE_URL && CONFIG.APPSTORE_URL.trim() !== "") { 
            element.href = CONFIG.APPSTORE_URL;
            element.style.display = 'inline-block'; 
        }
    });

    document.querySelectorAll(".play-store-link").forEach(function(element) {
        if (CONFIG && CONFIG.PLAYSTORE_URL && CONFIG.PLAYSTORE_URL.trim() !== "") { 
            element.href = CONFIG.PLAYSTORE_URL;
            element.style.display = 'inline-block'; 
        }
    });

    // Hero Section
    if (typeof TEXT_CONTENT !== 'undefined') {
        const setIfExists = (selector, text) => {
            const el = document.querySelector(selector);
            if (el && text) el.textContent = text;
        };

        setIfExists(".hero-title", TEXT_CONTENT.HERO_TITLE);
        setIfExists(".hero-subtitle", TEXT_CONTENT.HERO_SUBTITLE);
        setIfExists(".hero-badge", TEXT_CONTENT.HERO_BADGE);
    }
}

document.addEventListener("DOMContentLoaded", function() {
    const script = document.createElement('script');
    script.src = 'config.js';
    script.onload = loadConfigAndUpdateContent;
    document.head.appendChild(script);
});