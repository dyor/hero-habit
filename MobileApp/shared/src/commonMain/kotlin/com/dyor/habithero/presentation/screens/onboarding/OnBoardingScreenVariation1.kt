package com.dyor.habithero.presentation.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dyor.habithero.designsystem.components.AnimatedHorizontalPager
import com.dyor.habithero.designsystem.components.AppButton
import com.dyor.habithero.designsystem.components.ButtonStyle
import com.dyor.habithero.designsystem.components.HorizontalPagerIndicator
import com.dyor.habithero.designsystem.components.HorizontalPagerIndicatorStyle
import com.dyor.habithero.designsystem.components.ScreenTitle
import com.dyor.habithero.designsystem.generated.resources.UiRes
import com.dyor.habithero.designsystem.generated.resources.ic_check
import com.dyor.habithero.designsystem.theme.AppTheme
import com.dyor.habithero.generated.resources.Res
import com.dyor.habithero.generated.resources.btn_create_your_own_quest
import com.dyor.habithero.generated.resources.btn_get_started
import com.dyor.habithero.generated.resources.btn_next
import com.dyor.habithero.generated.resources.btn_skip
import com.dyor.habithero.generated.resources.cover_read_20_mins
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.min

@Composable
fun OnBoardingScreenVariation1(
    modifier: Modifier = Modifier,
    uiState: OnBoardingUiState,
    onUiEvent: (OnBoardingUiEvent) -> Unit,
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(vertical = AppTheme.spacing.largeSpacing),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val pagerState = rememberPagerState(
            initialPage = 0,
            initialPageOffsetFraction = 0f,
            pageCount = { uiState.pages.size },
        )
        val isLastPage = pagerState.currentPage == (pagerState.pageCount - 1)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .padding(horizontal = AppTheme.spacing.outerSpacing),
        ) {
            Spacer(modifier = Modifier.weight(1f))
            AnimatedVisibility(
                visible = isLastPage.not(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                SkipButton(
                    text = stringResource(Res.string.btn_skip),
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(uiState.pages.lastIndex) }
                    },
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AnimatedHorizontalPager(
                pagerState = pagerState,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
            ) { pageIndex ->
                when (pageIndex) {
                    0 -> OnBoardingPageStory1(
                        item = uiState.pages[0],
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.spacing.outerSpacing),
                    )

                    1 -> OnBoardingPageStory2(
                        item = uiState.pages[1],
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.spacing.outerSpacing),
                    )

                    else -> OnBoardingPageQuests(
                        item = uiState.pages[2],
                        habitPresets = uiState.habitPresets,
                        onToggleHabit = { onUiEvent(OnBoardingUiEvent.OnToggleHabit(it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.spacing.outerSpacing),
                    )
                }
            }
        }

        HorizontalPagerIndicator(
            modifier = Modifier.padding(top = AppTheme.spacing.sectionSpacing),
            size = pagerState.pageCount,
            selectedIndex = pagerState.currentPage,
            style = HorizontalPagerIndicatorStyle.STYLE1,
            onClickIndicator = { index ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(
                        page = index,
                        animationSpec = tween(),
                    )
                }
            },
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = AppTheme.spacing.outerSpacing,
                    end = AppTheme.spacing.outerSpacing,
                    top = AppTheme.spacing.sectionSpacing,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (isLastPage) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    AppButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(Res.string.btn_get_started),
                        onClick = { onUiEvent(OnBoardingUiEvent.OnClickStart) },
                    )
                    AppButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(Res.string.btn_create_your_own_quest),
                        style = ButtonStyle.ALTERNATIVE,
                        onClick = { onUiEvent(OnBoardingUiEvent.OnClickCreateYourOwnQuest) },
                    )
                }
            } else {
                AppButton(
                    text = stringResource(Res.string.btn_next),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        coroutineScope.launch {
                            val nextPage = min(
                                pagerState.currentPage + 1,
                                uiState.pages.lastIndex,
                            )
                            pagerState.animateScrollToPage(
                                page = nextPage,
                                animationSpec = tween(),
                            )
                        }
                    },
                )
            }
        }
    }
}

// ── Screen 1: Daily Photo Journal for All Habits ─────────────────────────────

@Composable
private fun OnBoardingPageStory1(
    item: OnBoardingScreenData,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        OnBoardingCardAllHabitsPhotoJournal()

        Spacer(modifier = Modifier.height(6.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ScreenTitle(
                text = stringResource(item.title),
                textAlign = TextAlign.Center,
            )

            Text(
                text = stringResource(item.description),
                style = AppTheme.typography.bodyLarge,
                color = AppTheme.colors.text.primary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
            )
        }
    }
}

@Composable
private fun OnBoardingCardAllHabitsPhotoJournal() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 280.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131A2A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFFFFD54F))), RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF13192B)),
                    ),
                )
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // Header Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF6366F1).copy(alpha = 0.35f))
                            .border(1.dp, Color(0xFF818CF8), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = "📸 DAILY PHOTO JOURNAL",
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            color = Color(0xFFC7D2FE),
                            letterSpacing = 0.5.sp,
                        )
                    }

                    Text(
                        text = "🦸 ALL HABITS",
                        color = Color(0xFFFFD54F),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                // Mock Habit Items
                HabitPhotoJournalItem(
                    emoji = "💧",
                    title = "Drink 2L Water",
                    streak = "3-Day Streak",
                    statusText = "Photo Logged",
                    statusColor = Color(0xFF4ADE80),
                )

                HabitPhotoJournalItem(
                    emoji = "📚",
                    title = "Read 20 Mins",
                    streak = "5-Day Streak",
                    statusText = "Photo Logged",
                    statusColor = Color(0xFF4ADE80),
                )

                HabitPhotoJournalItem(
                    emoji = "🚶",
                    title = "Daily Walk",
                    streak = "7-Day Streak",
                    statusText = "Photo Logged",
                    statusColor = Color(0xFF4ADE80),
                )

                HabitPhotoJournalItem(
                    emoji = "🧘",
                    title = "Meditation",
                    streak = "2-Day Streak",
                    statusText = "Ready for Photo ⚡",
                    statusColor = Color(0xFFFFD54F),
                )
            }
        }
    }
}

@Composable
private fun HabitPhotoJournalItem(
    emoji: String,
    title: String,
    streak: String,
    statusText: String,
    statusColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E293B).copy(alpha = 0.8f))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF312E81)),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = emoji, fontSize = 16.sp)
            }

            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                )
                Text(
                    text = "🔥 $streak",
                    color = Color(0xFFFFD54F),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(statusColor.copy(alpha = 0.2f))
                .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
        ) {
            Text(
                text = statusText,
                color = statusColor,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
            )
        }
    }
}

// ── Screen 2: 7-Day Streak Celebratory Comic Cover ───────────────────────────

@Composable
private fun OnBoardingPageStory2(
    item: OnBoardingScreenData,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        OnBoardingCard7DayStreakComicReward()

        Spacer(modifier = Modifier.height(6.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ScreenTitle(
                text = stringResource(item.title),
                textAlign = TextAlign.Center,
            )

            Text(
                text = stringResource(item.description),
                style = AppTheme.typography.bodyLarge,
                color = AppTheme.colors.text.primary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
            )
        }
    }
}

@Composable
private fun OnBoardingCard7DayStreakComicReward() {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(310.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(3.dp, Color(0xFFFFD54F), RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp)),
        ) {
            Image(
                painter = painterResource(Res.drawable.cover_read_20_mins),
                contentDescription = "1 Day Streak Vintage Comic Cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

// ── Screen 3: Choose Quests Selection ────────────────────────────────────────

@Composable
private fun OnBoardingPageQuests(
    item: OnBoardingScreenData,
    habitPresets: List<HabitPreset>,
    onToggleHabit: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            ScreenTitle(
                text = stringResource(item.title),
                textAlign = TextAlign.Center,
            )

            Text(
                text = stringResource(item.description),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.text.secondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            habitPresets.forEach { preset ->
                HabitPresetSelectableCard(
                    preset = preset,
                    onToggle = { onToggleHabit(preset.id) },
                )
            }
        }
    }
}

@Composable
private fun HabitPresetSelectableCard(
    preset: HabitPreset,
    onToggle: () -> Unit,
) {
    val isSelected = preset.isSelected
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) Color(0xFF818CF8) else Color(0xFF334155),
                    shape = RoundedCornerShape(14.dp),
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFF312E81) else Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = preset.emoji, fontSize = 18.sp)
                }

                Column {
                    Text(
                        text = preset.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                    Text(
                        text = preset.category.uppercase(),
                        color = if (isSelected) Color(0xFFA5B4FC) else Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            // Checkmark Indicator
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color(0xFF6366F1) else Color.Transparent)
                    .border(
                        width = 1.5.dp,
                        color = if (isSelected) Color(0xFF818CF8) else Color(0xFF64748B),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (isSelected) {
                    Icon(
                        painter = painterResource(UiRes.drawable.ic_check),
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SkipButton(
    text: String,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(
            color = AppTheme.colors.text.secondary,
            fontWeight = FontWeight.SemiBold,
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}
