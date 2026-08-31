# Starter Kit Friction Log & Audit

This log tracks all ambiguities, friction points, agent decision points, and potential improvements identified while developing **HeroHabit** using the KMP Contest Starter Kit skills.

---

## Log Entries

### 1. Two-Window Working Directory & Script Path Resolution
* **Observation**: In `skills/getting-started/SKILL.md` (line 56), the command is written as `./scripts/check_env.sh --phase getting-started` assuming root execution. However, the scripts actually reside in `MobileApp/scripts/`.
* **Agent Decision**: Navigated to `MobileApp/` to run `./scripts/check_env.sh` (or invoked `./MobileApp/scripts/check_env.sh`).
* **Recommendation**: Add a top-level `./scripts` symlink (`scripts -> MobileApp/scripts`) or explicitly document `cd MobileApp/` for client scripts to make it bulletproof for automated agents.

### 2. Autonomous Product Scope & No-Paywall Configuration
* **Observation**: The user specified: *"habit tracker with selfie celebration -> Replicate comic book cover, no paywall, don't ask many questions"*.
* **Agent Decision**:
  - Selected App Name: **HeroHabit**
  - Selected App ID: **`com.kotlinfoundation.herohabit`**
  - Configured `AppConfiguration.PREMIUM_FEATURES_ENABLED = false`
  - Designed Room entities for Habits, Habit Logs, and Comic Covers.
  - Configured Replicate API client in shared code with fallback/proxy support in `Web/functions`.

### 3. Package Refactoring Non-Interactive Shell Flag
* **Observation**: In non-interactive terminal execution, `./scripts/refactor_package.sh` exits with code 1 (`Error: non-interactive shell — re-run with -y/--yes to proceed.`).
* **Agent Decision**: Added `--yes` flag to automated command invocation.
* **Recommendation**: Mention the `-y` / `--yes` flag in `skills/refactor-package/SKILL.md` and `skills/getting-started/SKILL.md` for CLI / agent execution.

### 4. `generate_screen.sh` Argument Parsing on `--help`
* **Observation**: Passing `--help` to `./scripts/generate_screen.sh --help` treats `--help` as the screen name and generates `--helpScreen`, `--helpViewModel`, and registers `--helpScreenRoute` in `Routes.kt`.
* **Agent Decision**: Removed the `--help` files, cleaned up `Routes.kt`, `AppNavigation.kt`, and `Di.kt`.
* **Recommendation**: Add standard argument validation in `generate_screen.sh` (`if [[ "$1" == "--help" || "$1" == "-h" ]]; then show_help; exit 0; fi`).

### 5. Parameterized Navigation Routes in `generate_screen.sh`
* **Observation**: `generate_screen.sh` always generates `data object <Name>ScreenRoute : ScreenRoute`.
* **Agent Decision**: Converted `CelebrationScreenRoute` to a `data class` with default values for `habitId`, `habitTitle`, `streakCount` so parameters can be passed through Compose Navigation.

### 6. Room 3 Local Storage Scaffolding Flow
* **Observation**: `make_local.sh` creates the minimal scaffold (`id: String`), which is very clean.
* **Agent Decision**: Extended `HabitEntity` and `ComicCoverEntity` with streak counters, completion dates, and image URLs, and built `HabitRepository` with auto-seeding of sample quests on first launch.

### 7. Dual AI Routing (Direct Mode vs Web Proxy)
* **Observation**: `AppConfiguration.USE_AI_PROXY_SERVER = null` automatically uses direct mode when `local.properties` contains `REPLICATE_API_KEY` and transitions to Cloud Functions proxy when `CLOUD_FUNCTIONS_URL` is configured.
* **Agent Decision**: Configured `Web/public/config.js` for HeroHabit marketing site and verified `Web/functions` Replicate proxy endpoints.
