# Paywall

This app's monetization strategy. The paywall converts the motivation built in onboarding into a
trial/purchase — **by honest means only** (no fake scarcity, no buried cancel, no misleading "free").
Fill the `TAILOR PER APP` blanks; the rest are strong defaults.

> Code lives in
> `MobileApp/shared/src/commonMain/kotlin/com/koko/habittracker/presentation/screens/paywall/`
> — `SubscriptionPaywallScreen.kt`, `creditpack/CreditPackPaywallScreen.kt`,
> `remotepaywall/RemotePaywallScreen.kt`, and `PaywallUiStateMapper`.

## Primary model

Subscription-first with credit packs for on-demand top-ups. The subscription grants monthly recurring comic book cover credits to reward ongoing habit consistency.

## Placement

Shown post-onboarding, upon reaching milestone streak celebrations, and when attempting to mint a comic cover with 0 credits.

## Offer architecture

- **Hero Cadet**: $1.99 / month — 10 AI Comic Book Covers per month.
- **Hero Champion**: $5.99 / month — 40 AI Comic Book Covers per month (our most popular active superhero tier).
- **Hero Champion Annual**: $50.00 / year — 40 AI Comic Book Covers per month (save ~30% over the $5.99 monthly plan).

## Trial framing

No fake trial lock-in. 1 free welcome credit is provided to every new recruit to mint their very first comic cover risk-free. Clear "Cancel anytime" disclosure.

## Credit packs

For heroes who want extra covers without changing their monthly plan:
- 10 Comic Cover Credits: $1.99 ($0.20 / cover)
- 40 Comic Cover Credits: $5.99 ($0.15 / cover — Recommended Best Value)
- 100 Comic Cover Credits: $12.99 ($0.13 / cover)

## CTA & trust

Action button: "Continue" / "Unlock Comic Covers ⚡". Transparent cancel anytime messaging, restore purchase capability available on all paywalls. Contact support: matt@dyor.com.

## Measure

Instrument: paywall impressions, trial starts, trial→paid, restore, cancel. Also consider
multi-surface prompts at genuine high-intent moments (see `virality_loops.md`).

> Paywall architecture, the offer/trial science, and the reviewer's rubric live in
> `AiGuidelines/loop/CONVERSION_PLAYBOOK.md` when the self-improve loop is installed.
