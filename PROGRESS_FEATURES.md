# Features — HeroHabit

> [!NOTE]
> Record of built features for HeroHabit from the PRD (`AiGuidelines/project/prd.md`).
> **Status key:** `[ ]` not started · `[~]` in progress · `[x]` done

**Source of truth:** `AiGuidelines/project/prd.md` · `user_flow.md` · `ui_ux.md`

---

## Models
- [x] `HabitEntity` — scaffolded ☑ · real columns + custom prompt + date mappers ☑ · DAO queries ☑
- [x] `ComicCoverEntity` — scaffolded ☑ · real columns + mappers ☑ · DAO queries & habit filtering ☑

## Screens
- [x] **Home Screen (Daily Quests)** — scaffolded ☑ · UI per `ui_ux.md` ☑ · habit card tap to details wired ☑
- [x] **Habit Detail Screen** — habit stats ☑ · editable custom prompt for superhero scenarios ☑ · entry date picker ✏️ ☑ · recent milestone comic covers list & swipe viewer ☑
- [x] **Celebration Screen** — selfie picker + custom prompt integration ☑ · Replicate AI generation with 9:16 vertical comic prompt ☑
- [x] **Hall of Heroes (Gallery Screen)** — 2-column grid ☑ · 100% full-bleed swipeable comic viewer with `HorizontalPager` ☑

## Screens that ship with the kit (branded to HeroHabit)
- [x] **onboarding** — branded to superhero habit quests
- [x] **paywall** — monetization toggles disabled (`PREMIUM_FEATURES_ENABLED = false`)

## Optional building blocks
- [x] Remote AI generation — `ReplicateGenerationProvider` (Cloud Functions proxy + Direct fallback)
- [x] Stored setting / state — Room 3 KMP SQLite DB (version 2)
- [x] Image file picking & Base64 Data URI fallback — `FileKit` + `Ktor`

## Validation
- [x] `run-quality-gates` passes (spotless, tests, Android debug build)
- [x] Live emulator testing verified on Pixel 10 Pro
