#!/bin/bash
# ==============================================================================
# Habit Hero & Indie Playbook Web Public Directory Synchronization Script
# ==============================================================================
# This script ensures that both Habit Hero and Indie Playbook public hosting folders
# are synchronized. This prevents deployments in one repository from deleting or
# overwriting the other app's assets on the shared Firebase Hosting domain.

# Derive paths relative to this script, allowing environment variable overrides
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# Both repos sit side by side, e.g. ~/DyorProjects/kmp/{habit-hero,IndiePlaybook}, so this
# same script works from either repo.
PROJECTS_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"

HABIT_HERO_WEB="${HABIT_HERO_WEB:-"$PROJECTS_DIR/habit-hero/Web/public"}"
INDIE_PLAYBOOK_WEB="${INDIE_PLAYBOOK_WEB:-"$PROJECTS_DIR/IndiePlaybook/Web/public"}"

echo "⏳ Starting Web Synchronization..."

# Guard checks
if [ ! -d "$HABIT_HERO_WEB" ]; then
    echo "❌ Error: Habit Hero public directory not found at $HABIT_HERO_WEB"
    exit 1
fi

if [ ! -d "$INDIE_PLAYBOOK_WEB" ]; then
    echo "❌ Error: Indie Playbook public directory not found at $INDIE_PLAYBOOK_WEB"
    exit 1
fi

# ==============================================================================
# 1. Sync Indie Playbook (Root Legal Pages & Assets) -> Habit Hero
# ==============================================================================
echo "🔹 Syncing Indie Playbook -> Habit Hero..."

# Copy Indie Playbook root policies and assets if they exist
[ -f "$INDIE_PLAYBOOK_WEB/privacy-policy.html" ] && cp "$INDIE_PLAYBOOK_WEB/privacy-policy.html" "$HABIT_HERO_WEB/privacy-policy.html"
[ -f "$INDIE_PLAYBOOK_WEB/terms-conditions.html" ] && cp "$INDIE_PLAYBOOK_WEB/terms-conditions.html" "$HABIT_HERO_WEB/terms-conditions.html"
[ -f "$INDIE_PLAYBOOK_WEB/indie-apps.html" ] && cp "$INDIE_PLAYBOOK_WEB/indie-apps.html" "$HABIT_HERO_WEB/indie-apps.html"
[ -f "$INDIE_PLAYBOOK_WEB/styles-indie.css" ] && cp "$INDIE_PLAYBOOK_WEB/styles-indie.css" "$HABIT_HERO_WEB/styles-indie.css"
[ -f "$INDIE_PLAYBOOK_WEB/config-indie.js" ] && cp "$INDIE_PLAYBOOK_WEB/config-indie.js" "$HABIT_HERO_WEB/config-indie.js"
[ -f "$INDIE_PLAYBOOK_WEB/updateContent-indie.js" ] && cp "$INDIE_PLAYBOOK_WEB/updateContent-indie.js" "$HABIT_HERO_WEB/updateContent-indie.js"

# Sync Indie Playbook isolated subfolder (/indie-playbook/ landing page)
if [ -d "$INDIE_PLAYBOOK_WEB/indie-playbook" ]; then
    mkdir -p "$HABIT_HERO_WEB/indie-playbook"
    rsync -av --delete "$INDIE_PLAYBOOK_WEB/indie-playbook/" "$HABIT_HERO_WEB/indie-playbook/"
fi

# ==============================================================================
# 2. Sync Habit Hero (Root Pages & /habit-hero/) -> Indie Playbook
# ==============================================================================
echo "🔹 Syncing Habit Hero -> Indie Playbook..."

# Copy the shared root pages (the app picker at / and the 404 page)
[ -f "$HABIT_HERO_WEB/index.html" ] && cp "$HABIT_HERO_WEB/index.html" "$INDIE_PLAYBOOK_WEB/index.html"
[ -f "$HABIT_HERO_WEB/404.html" ] && cp "$HABIT_HERO_WEB/404.html" "$INDIE_PLAYBOOK_WEB/404.html"

# Sync Habit Hero isolated subfolder (/habit-hero/ landing page, legal pages and images)
if [ -d "$HABIT_HERO_WEB/habit-hero" ]; then
    mkdir -p "$INDIE_PLAYBOOK_WEB/habit-hero"
    rsync -av --delete "$HABIT_HERO_WEB/habit-hero/" "$INDIE_PLAYBOOK_WEB/habit-hero/"
fi

echo "✅ Web Synchronization Complete!"
echo "👉 Whichever project you deploy from now on will host a perfectly merged set of files!"
