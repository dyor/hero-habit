package com.dyor.habithero.presentation.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.dyor.habithero.designsystem.theme.AppTheme
import com.dyor.habithero.domain.model.Habit
import com.dyor.habithero.presentation.components.ComicCoverImage
import com.dyor.habithero.util.StoreScreenshot

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    onHabitCompleted: (Habit) -> Unit,
    onNavigateToHabitDetail: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.celebrationHabit) {
        uiState.celebrationHabit?.let { habit ->
            onHabitCompleted(habit)
            viewModel.onUiEvent(HomeUiEvent.OnCelebrationHandled)
        }
    }

    HomeScreen(
        modifier = modifier,
        uiState = uiState,
        onUiEvent = viewModel::onUiEvent,
        onNavigateToHabitDetail = onNavigateToHabitDetail,
    )
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onUiEvent: (HomeUiEvent) -> Unit,
    onNavigateToHabitDetail: (String) -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16)),
    ) {
        // Overall Screen Background with most recent selfie/cover (VIBRANT & CLEAR)
        if (uiState.latestOverallCoverUrl != null) {
            AsyncImage(
                model = uiState.latestOverallCoverUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.85f),
            )
        }

        // Soft dark vignette overlay to guarantee readability of headers and text
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCC090D16),
                            Color(0x77090D16),
                            Color(0xDD090D16),
                        ),
                    ),
                ),
        )

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Quests",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    letterSpacing = 0.5.sp,
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B).copy(alpha = 0.9f))
                        .border(1.dp, Color(0xFF475569), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(
                        text = "🔥 ${uiState.habits.sumOf { it.streakCount }} Day Mega Streak",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F),
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF818CF8)),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ACTIVE DAILY QUESTS",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp,
                                color = Color(0xFFC7D2FE),
                            ),
                        )
                    }
                }

                items(uiState.habits, key = { it.id }) { habit ->
                    val coverUrl = uiState.habitCoverUrls[habit.id]
                    QuestCard(
                        habit = habit,
                        coverUrl = coverUrl,
                        onClick = { onNavigateToHabitDetail(habit.id) },
                        onComplete = { onUiEvent(HomeUiEvent.OnCompleteHabit(habit)) },
                    )
                }

                // Inline Create Habit Quest Button
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUiEvent(HomeUiEvent.OnClickAddHabit) },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x661E293B)),
                        border = BorderStroke(
                            width = 1.5.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)),
                            ),
                        ),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Text(text = "➕", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Create a New Habit Quest",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        if (uiState.isAddHabitDialogOpen) {
            AddHabitDialog(
                title = uiState.newHabitTitle,
                category = uiState.newHabitCategory,
                onTitleChanged = { onUiEvent(HomeUiEvent.OnNewHabitTitleChanged(it)) },
                onCategoryChanged = { onUiEvent(HomeUiEvent.OnNewHabitCategoryChanged(it)) },
                onConfirm = { onUiEvent(HomeUiEvent.OnConfirmAddHabit) },
                onDismiss = { onUiEvent(HomeUiEvent.OnDismissAddHabitDialog) },
            )
        }
    }
}

@Composable
private fun AddHabitDialog(
    title: String,
    category: String,
    onTitleChanged: (String) -> Unit,
    onCategoryChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "New Quest", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChanged,
                    label = { Text("Quest (e.g. Morning Jog)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = onCategoryChanged,
                    label = { Text("Category (e.g. Fitness, Mind, Health)") },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = title.isNotBlank()) {
                Text("Create Quest ⚡", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun QuestCard(
    habit: Habit,
    coverUrl: String?,
    onClick: () -> Unit,
    onComplete: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xDD151928)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(126.dp),
        ) {
            // Background Image if recent comic cover exists for this habit
            if (coverUrl != null) {
                ComicCoverImage(
                    imageUrl = coverUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                )
            }

            // Dark Comic Scrim / Frosted Glass Overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        if (coverUrl != null) {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xF5090D16),
                                    Color(0xEE1E293B),
                                    Color(0x66090D16),
                                ),
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xDD1E2640),
                                    Color(0xCC131828),
                                ),
                            )
                        },
                    )
                    .border(
                        width = 1.dp,
                        color = if (coverUrl != null) Color(0x88818CF8) else Color(0x4464748B),
                        shape = RoundedCornerShape(18.dp),
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF6366F1).copy(alpha = 0.45f))
                                    .padding(horizontal = 7.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = "⚡ ${habit.category.uppercase()}",
                                    color = Color(0xFFA5B4FC),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp,
                                )
                            }

                            if (coverUrl != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFFFD54F).copy(alpha = 0.25f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                ) {
                                    Text(
                                        text = "🎨 COVER MINTED",
                                        color = Color(0xFFFFD54F),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = habit.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFEA580C).copy(alpha = 0.35f))
                                    .border(1.dp, Color(0xFFF97316).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = "🔥 ${habit.streakCount} Day Streak",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFFD54F),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    val isCompletedToday = habit.lastCompletedDate.equals("Today", ignoreCase = true)

                    // Action Button (Active or Completed Today)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isCompletedToday) {
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF065F46).copy(alpha = 0.6f), Color(0xFF047857).copy(alpha = 0.6f)),
                                    )
                                } else {
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)),
                                    )
                                },
                            )
                            .border(
                                width = 1.dp,
                                color = if (isCompletedToday) Color(0xFF34D399).copy(alpha = 0.6f) else Color(0xFFA5B4FC).copy(alpha = 0.6f),
                                shape = RoundedCornerShape(14.dp),
                            )
                            .clickable(enabled = !isCompletedToday) { onComplete() }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (isCompletedToday) "COMPLETED ✅" else "COMPLETE ⚡",
                            color = if (isCompletedToday) Color(0xFF6EE7B7) else Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp,
                        )
                    }
                }
            }
        }
    }
}

// ── Storefront Previews ───────────────────────────────────────────────────────

@Preview
@StoreScreenshot(locale = "en", tag = "01_home")
@Composable
private fun HomeScreenStoreScreenshot_iPhone_en() {
    AppTheme {
        HomeScreen(
            uiState = HomeUiState(
                habits = listOf(
                    Habit(
                        id = "1",
                        title = "Morning Jog",
                        category = "Fitness",
                        streakCount = 10,
                        lastCompletedDate = "2026-08-11",
                    ),
                    Habit(
                        id = "2",
                        title = "Read 20 Mins",
                        category = "Mind",
                        streakCount = 15,
                        lastCompletedDate = "2026-08-11",
                    ),
                    Habit(
                        id = "3",
                        title = "Drink 2L Water",
                        category = "Health",
                        streakCount = 3,
                        lastCompletedDate = "2026-08-11",
                    ),
                    Habit(
                        id = "4",
                        title = "Meditation",
                        category = "Focus",
                        streakCount = 3,
                        lastCompletedDate = "2026-08-11",
                    ),
                ),
                habitCoverUrls = mapOf(
                    "1" to "drawable:cover_jogging",
                    "2" to "drawable:cover_hero",
                    "4" to "drawable:cover_meditation",
                ),
                latestOverallCoverUrl = "drawable:cover_hero",
            ),
            onUiEvent = {},
        )
    }
}
