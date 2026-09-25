---
name: push-push
description: >-
  Rapid, two-step release pipeline for Android to Google Play — (1) push signed AAB to the internal
  track for solo verification on-device, and (2) push to production with release notes. Fast and
  low-friction release flow ("bang bang"). Triggers on "push-push", "release", "ship it", or
  "publish to play store".
---

# Push-Push 🚀 — Rapid Google Play Release Flow

Fast, two-step deployment process to safely and quickly release updates to Google Play:
1. **Push to Internal Track** → Test on Matt's phone (internal tester).
2. **Push to Production** → Upload release notes and submit for Google review.

Uses Fastlane with the service account key configured at `~/credentials/google-service-app-publisher.json`.

---

## The Workflow

### Step 1: Push to Internal Track (Test on Phone)

Run directly via Fastlane from `MobileApp/`:
```bash
bundle exec fastlane android playstore_release track:internal submit_for_review:false
```

#### What happens:
- Builds the signed release AAB (`:androidApp:bundleRelease`).
- Uploads directly to Google Play's **Internal testing** track.
- Avoids full store review lag so Matt can test immediately.

#### Matt's Solo Verification Checklist (on phone):
1. Open **Google Play Store** on the testing phone.
2. Install / update **Habit Hero**.
3. Verify:
   - App launches without crash (confirms manifest and Firebase initialization).
   - Core screens and features function (Quests, habit tracking, streak milestones).
   - Comic cover generator and paywall flow render properly.

---

### Step 2: Push to Production (Ship It)

Once on-device testing passes, update the changelog in:
- `distribution/android/playstore_metadata/en-US/changelogs/default.txt` (and `<versionCode>.txt`)
- `distribution/whatsnew/whatsnew-en-US`

Then promote directly to production via Fastlane from `MobileApp/`:

```bash
bundle exec fastlane android playstore_release track:production release_status:completed submit_for_review:true
```

#### What happens:
- Uploads the signed AAB to Google Play's **Production** track with changelogs.
- Sets `submit_for_review:true` to automatically submit the release for review.

---

## Reference Files & Paths

- **Fastlane Config:** `MobileApp/fastlane/Fastfile`
- **Play Store Service Account:** `~/credentials/google-service-app-publisher.json`
- **Release Notes:**
  - `MobileApp/distribution/android/playstore_metadata/en-US/changelogs/`
  - `MobileApp/distribution/whatsnew/whatsnew-en-US`
