# Product Requirements Document (PRD) — HeroHabit

## 1. Vision & Core Value Proposition
**HeroHabit** is a daily habit tracker that turns personal consistency into superhero mythology. When users complete a daily habit entry, the app celebrates their streak by capturing a celebratory selfie and using Replicate AI to transform it into a personalized vintage comic book cover with the user depicted as the superhero of their daily quest (e.g., "7 Day Jog Streak! The Flash of the Suburbs").

## 2. Target Audience & Core Use Cases
- **Audience**: Individuals who want engaging visual motivation to build lasting daily habits (fitness, reading, mindfulness, nutrition).
- **Core Loop**:
  1. Open app and view daily habits with active streak counters.
  2. Tap to complete habit for the day.
  3. Trigger Celebration Flow: "Way to go — take a selfie and celebrate!"
  4. Capture/select selfie via Camera/Gallery.
  5. AI generates a personalized Comic Book Cover (Replicate AI / Comic Style).
  6. Save & view Comic Book Cover in the "Hall of Heroes" gallery.

## 3. Key MVP Features (Phase 1)
1. **Habit Tracker (Room Database)**:
   - Create, list, check-off daily habits.
   - Dynamic streak calculation.
2. **Celebration & Selfie Flow**:
   - Permission-gated camera/gallery picker (`Calf`).
3. **AI Comic Generator (Ktor + Replicate)**:
   - Generate superhero comic cover with custom streak headline.
4. **Hall of Heroes Gallery**:
   - Browse past earned comic covers.
5. **No Paywall Mode**:
   - All features 100% free (`PREMIUM_FEATURES_ENABLED = false`).

## 4. Technical Stack & Data Architecture
- **Local DB**: Room 3 KMP (`HabitEntity`, `HabitLogEntity`, `HeroComicCoverEntity`).
- **Networking**: Ktor 3.3 with JSON serialization.
- **Backend / Proxy**: Firebase Cloud Functions (`Web/functions`) proxying Replicate API keys.
- **Web**: Firebase Hosting marketing landing page (`Web/public`).
