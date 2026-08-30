# User Flow — HeroHabit

## Screen Map & Navigation Hierarchy

```mermaid
flowchart TD
    A["Launch"] --> B["Home Screen (Habit List & Streaks)"]
    B -->|"Tap Checkmark"| C["Celebration Dialog / Screen ('Way to go!')"]
    C -->|"Tap 'Take Selfie & Celebrate'"| D["Camera / Selfie Capture"]
    D -->|"Submit Photo"| E["AI Generation Loading Screen"]
    E -->|"Generation Complete"| F["Comic Book Cover Reveal Screen"]
    F -->|"Save to Gallery"| G["Hall of Heroes (Comic Covers Gallery)"]
    B -->|"Bottom Nav: Gallery"| G
    B -->|"Bottom Nav: Add Habit"| H["Create Habit Dialog/Screen"]
```

## User Journeys
1. **Daily Check-in Flow**:
   - User opens app $\rightarrow$ marks "Morning Jog" complete $\rightarrow$ Streak increments from 6 to 7.
   - Prompt: "Way to go! Take a selfie to mint your Day 7 Comic Cover."
   - User snaps photo $\rightarrow$ Replicate API returns comic cover $\rightarrow$ Cover displayed with share button.
2. **Hall of Heroes Review Flow**:
   - User navigates to Gallery $\rightarrow$ views grid of historical comic book covers with streak milestones.
