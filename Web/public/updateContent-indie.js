// updateContent-indie.js - Dynamic hydration for marketing site & legal documents

function loadConfigAndUpdateContent() {
    if (typeof CONFIG === 'undefined') {
        console.warn("CONFIG object not found in config.js");
        return;
    }

    // Replace text placeholders
    document.querySelectorAll(".app-name").forEach(function(el) {
        el.textContent = CONFIG.APP_NAME;
    });

    document.querySelectorAll(".developer-or-company-name").forEach(function(el) {
        el.textContent = CONFIG.DEVELOPER_OR_COMPANY_NAME;
    });

    document.querySelectorAll(".contact-email").forEach(function(el) {
        el.href = "mailto:" + CONFIG.CONTACT_EMAIL;
        el.textContent = CONFIG.CONTACT_EMAIL;
    });

    document.querySelectorAll(".privacy-policy-last-update-date").forEach(function(el) {
        el.textContent = CONFIG.PRIVACY_POLICY_LAST_UPDATE_DATE;
    });

    document.querySelectorAll(".terms-and-services-last-update-date").forEach(function(el) {
        el.textContent = CONFIG.TERMS_AND_SERVICE_LAST_UPDATE_DATE;
    });

    document.querySelectorAll(".current-year").forEach(function(el) {
        el.textContent = new Date().getFullYear();
    });

    // Set SEO metadata if on main index page
    if (window.location.pathname.endsWith("/") || window.location.pathname.endsWith("/index.html") || window.location.pathname === "") {
        if (CONFIG.WEBSITE_TITLE) {
            document.title = CONFIG.WEBSITE_TITLE;
        }
        const metaDesc = document.querySelector('meta[name="description"]');
        if (metaDesc && CONFIG.WEBSITE_DESCRIPTION) {
            metaDesc.setAttribute('content', CONFIG.WEBSITE_DESCRIPTION);
        }
    }

    // Configure Store Links
    document.querySelectorAll(".app-store-link").forEach(function(el) {
        if (CONFIG.APPSTORE_URL && CONFIG.APPSTORE_URL.trim() !== "") {
            el.href = CONFIG.APPSTORE_URL;
            el.style.display = 'inline-block';
        } else {
            el.style.display = 'none';
        }
    });

    document.querySelectorAll(".play-store-link").forEach(function(el) {
        if (CONFIG.PLAYSTORE_URL && CONFIG.PLAYSTORE_URL.trim() !== "") {
            el.href = CONFIG.PLAYSTORE_URL;
            el.style.display = 'inline-block';
        } else {
            el.style.display = 'none';
        }
    });

    // If TEXT_CONTENT is available, hydrate landing page sections
    if (typeof TEXT_CONTENT !== 'undefined') {
        // Hero Section
        const heroBadge = document.querySelector(".hero-badge-text");
        if (heroBadge && TEXT_CONTENT.HERO_BADGE) heroBadge.textContent = TEXT_CONTENT.HERO_BADGE;

        const heroTitle = document.querySelector(".hero-title");
        if (heroTitle && TEXT_CONTENT.HERO_TITLE) heroTitle.textContent = TEXT_CONTENT.HERO_TITLE;

        const heroSubtitle = document.querySelector(".hero-subtitle");
        if (heroSubtitle && TEXT_CONTENT.HERO_SUBTITLE) heroSubtitle.textContent = TEXT_CONTENT.HERO_SUBTITLE;

        // Features Section
        const featTag = document.querySelector("#features .section-tag");
        if (featTag && TEXT_CONTENT.FEATURES_SECTION_TAG) featTag.textContent = TEXT_CONTENT.FEATURES_SECTION_TAG;

        const featTitle = document.querySelector("#features .section-title");
        if (featTitle && TEXT_CONTENT.FEATURES_SECTION_TITLE) featTitle.textContent = TEXT_CONTENT.FEATURES_SECTION_TITLE;

        const featSubtitle = document.querySelector("#features .section-subtitle");
        if (featSubtitle && TEXT_CONTENT.FEATURES_SECTION_SUBTITLE) featSubtitle.textContent = TEXT_CONTENT.FEATURES_SECTION_SUBTITLE;

        const featuresGrid = document.querySelector(".features-grid");
        if (featuresGrid && Array.isArray(TEXT_CONTENT.FEATURE_CARDS) && TEXT_CONTENT.FEATURE_CARDS.length > 0) {
            featuresGrid.innerHTML = TEXT_CONTENT.FEATURE_CARDS.map(function(card) {
                return `
                    <div class="feature-card">
                        <div class="feature-icon-wrapper">${card.icon || '✨'}</div>
                        <h3 class="feature-title">${card.title}</h3>
                        <p class="feature-text">${card.text}</p>
                    </div>
                `;
            }).join("");
        }

        // How it Works / Workflow
        const workflowGrid = document.querySelector(".workflow-grid");
        if (workflowGrid && Array.isArray(TEXT_CONTENT.WORKFLOW_STEPS) && TEXT_CONTENT.WORKFLOW_STEPS.length > 0) {
            workflowGrid.innerHTML = TEXT_CONTENT.WORKFLOW_STEPS.map(function(step) {
                return `
                    <div class="workflow-card">
                        <span class="step-number">${step.step}</span>
                        <h3 class="workflow-title">${step.title}</h3>
                        <p class="workflow-text">${step.text}</p>
                    </div>
                `;
            }).join("");
        }

        // FAQs
        const faqContainer = document.querySelector(".faq-container");
        if (faqContainer && Array.isArray(TEXT_CONTENT.FAQS) && TEXT_CONTENT.FAQS.length > 0) {
            faqContainer.innerHTML = TEXT_CONTENT.FAQS.map(function(item) {
                return `
                    <div class="faq-item">
                        <h3 class="faq-question">${item.q}</h3>
                        <p class="faq-answer">${item.a}</p>
                    </div>
                `;
            }).join("");
        }

        // CTA Section
        const ctaTag = document.querySelector(".cta-box .section-tag");
        if (ctaTag && TEXT_CONTENT.CTA_SECTION_TAG) ctaTag.textContent = TEXT_CONTENT.CTA_SECTION_TAG;

        const ctaTitle = document.querySelector(".cta-box .section-title");
        if (ctaTitle && TEXT_CONTENT.CTA_SECTION_TITLE) ctaTitle.textContent = TEXT_CONTENT.CTA_SECTION_TITLE;

        const ctaText = document.querySelector(".cta-box .section-subtitle");
        if (ctaText && TEXT_CONTENT.CTA_SECTION_TEXT) ctaText.textContent = TEXT_CONTENT.CTA_SECTION_TEXT;
    }
}

// Auto-run when DOM is ready
if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", loadConfigAndUpdateContent);
} else {
    loadConfigAndUpdateContent();
}