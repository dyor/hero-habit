package com.dyor.habithero.example

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.dyor.habithero.designsystem.theme.AppTheme
import com.dyor.habithero.domain.model.Habit
import com.dyor.habithero.presentation.screens.home.HomeScreen
import com.dyor.habithero.presentation.screens.home.HomeUiEvent
import com.dyor.habithero.presentation.screens.home.HomeUiState
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class SampleComposeUiTest {
    @Test
    fun `shows active habits and completes habit on click`() = runComposeUiTest {
        val sampleHabit = Habit(
            id = "habit-1",
            title = "Morning Jog",
            category = "Fitness",
            streakCount = 6,
        )
        val events = mutableListOf<HomeUiEvent>()

        setContent {
            AppTheme {
                HomeScreen(
                    uiState = HomeUiState(habits = listOf(sampleHabit)),
                    onUiEvent = { events += it },
                )
            }
        }

        onNodeWithText("Morning Jog").assertExists()
        onNodeWithText("COMPLETE ⚡").performClick()

        assertTrue(
            events.any { it is HomeUiEvent.OnCompleteHabit },
            "expected OnCompleteHabit event, got $events",
        )
    }
}
