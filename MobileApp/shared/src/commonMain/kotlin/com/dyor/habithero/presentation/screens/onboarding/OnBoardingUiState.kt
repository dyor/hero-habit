package com.dyor.habithero.presentation.screens.onboarding

import com.dyor.habithero.designsystem.generated.resources.UiRes
import com.dyor.habithero.designsystem.generated.resources.ic_logo
import com.dyor.habithero.generated.resources.Res
import com.dyor.habithero.generated.resources.desc_onboarding_page_1
import com.dyor.habithero.generated.resources.desc_onboarding_page_2
import com.dyor.habithero.generated.resources.desc_onboarding_page_3
import com.dyor.habithero.generated.resources.title_onboarding_page_1
import com.dyor.habithero.generated.resources.title_onboarding_page_2
import com.dyor.habithero.generated.resources.title_onboarding_page_3
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

data class OnBoardingScreenData(
    val title: StringResource,
    val description: StringResource,
    val imageRes: DrawableResource,
)

data class HabitPreset(
    val id: String,
    val title: String,
    val category: String,
    val emoji: String,
    val customPrompt: String,
    val isSelected: Boolean = false,
)

data class OnBoardingUiState(
    val pages: List<OnBoardingScreenData> = listOf(
        OnBoardingScreenData(
            Res.string.title_onboarding_page_1,
            Res.string.desc_onboarding_page_1,
            UiRes.drawable.ic_logo,
        ),
        OnBoardingScreenData(
            Res.string.title_onboarding_page_2,
            Res.string.desc_onboarding_page_2,
            UiRes.drawable.ic_logo,
        ),
        OnBoardingScreenData(
            Res.string.title_onboarding_page_3,
            Res.string.desc_onboarding_page_3,
            UiRes.drawable.ic_logo,
        ),
    ),
    val habitPresets: List<HabitPreset> = listOf(
        HabitPreset(
            id = "drink_water",
            title = "Drink Water",
            category = "Health",
            emoji = "💧",
            customPrompt = "controlling elemental tidal waves and glowing hydro energy shields",
            isSelected = true,
        ),
        HabitPreset(
            id = "read_20_mins",
            title = "Read 20 Mins",
            category = "Mind",
            emoji = "📚",
            customPrompt = "studying ancient holographic scrolls inside a secret galactic library of wisdom",
            isSelected = true,
        ),
        HabitPreset(
            id = "daily_walk",
            title = "Daily Walk",
            category = "Fitness",
            emoji = "🚶",
            customPrompt = "walking through a radiant glowing futuristic city skyline at twilight",
            isSelected = true,
        ),
        HabitPreset(
            id = "practice_spanish",
            title = "Practice Spanish",
            category = "Learning",
            emoji = "🗣️",
            customPrompt = "transmitting multilingual galactic transmissions across planetary systems",
            isSelected = false,
        ),
        HabitPreset(
            id = "take_vitamins",
            title = "Take Vitamins",
            category = "Health",
            emoji = "💊",
            customPrompt = "absorbing cosmic vitality crystals and glowing energy shields",
            isSelected = false,
        ),
        HabitPreset(
            id = "eat_healthy",
            title = "Eat Healthy",
            category = "Nutrition",
            emoji = "🥗",
            customPrompt = "fueling with mythical organic power elixirs in an enchanted bio-dome",
            isSelected = false,
        ),
        HabitPreset(
            id = "morning_jog",
            title = "Morning Jog",
            category = "Fitness",
            emoji = "🏃",
            customPrompt = "running at lightning speed through bustling futuristic metropolis with glowing neon trails",
            isSelected = false,
        ),
        HabitPreset(
            id = "meditation",
            title = "Meditation",
            category = "Focus",
            emoji = "🧘",
            customPrompt = "meditating in epic cosmic places like deep space, floating near nebulae, waterfalls, and towering volcanoes",
            isSelected = false,
        ),
        HabitPreset(
            id = "sleep_8_hours",
            title = "Sleep 8 Hours",
            category = "Rest",
            emoji = "😴",
            customPrompt = "recharging in a high-tech stasis rejuvenation pod under a starry cosmos",
            isSelected = false,
        ),
    ),
    val isOnBoardingFinished: Boolean = false,
    // isNewUser distinguishes a fresh completion from an already-onboarded user
    val isNewUser: Boolean = false,
    val navigateToCreateQuest: Boolean = false,
    val isLoading: Boolean = true,
)

sealed interface OnBoardingUiEvent {
    data class OnToggleHabit(val id: String) : OnBoardingUiEvent
    data object OnClickStart : OnBoardingUiEvent
    data object OnClickCreateYourOwnQuest : OnBoardingUiEvent
}
