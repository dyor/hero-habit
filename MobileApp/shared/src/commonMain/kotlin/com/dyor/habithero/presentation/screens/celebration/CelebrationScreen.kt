package com.dyor.habithero.presentation.screens.celebration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.dyor.habithero.designsystem.components.AppButton
import com.dyor.habithero.designsystem.components.ConfettiParticlesAnimated
import com.dyor.habithero.designsystem.components.ScreenWithToolbar
import com.dyor.habithero.designsystem.theme.AppTheme
import com.dyor.habithero.domain.model.ComicCover
import com.dyor.habithero.presentation.components.ComicCoverImage
import com.dyor.habithero.util.StoreScreenshot
import com.dyor.habithero.util.file.FileManager
import com.dyor.habithero.util.file.openCameraPicker
import io.github.vinceglb.filekit.FileKit
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun CelebrationScreen(
    modifier: Modifier = Modifier,
    viewModel: CelebrationViewModel,
    habitId: String,
    habitTitle: String,
    streakCount: Int,
    onNavigateToGallery: () -> Unit,
    onNavigateToHabitDetail: (String) -> Unit = {},
    onNavigateBack: () -> Unit,
    onNavigateToPaywall: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val fileManager: FileManager = koinInject()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(habitId, habitTitle, streakCount) {
        viewModel.onUiEvent(CelebrationUiEvent.Init(habitId, habitTitle, streakCount))
    }

    CelebrationScreen(
        modifier = modifier,
        uiState = uiState,
        onUiEvent = viewModel::onUiEvent,
        onNavigateToGallery = onNavigateToGallery,
        onNavigateToHabitDetail = onNavigateToHabitDetail,
        onNavigateBack = onNavigateBack,
        onNavigateToPaywall = onNavigateToPaywall,
        onShareCover = { url ->
            coroutineScope.launch {
                fileManager.shareFile(url)
            }
        },
    )
}

@Composable
fun CelebrationScreen(
    modifier: Modifier = Modifier,
    uiState: CelebrationUiState,
    onUiEvent: (CelebrationUiEvent) -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToHabitDetail: (String) -> Unit = {},
    onNavigateBack: () -> Unit,
    onNavigateToPaywall: () -> Unit = {},
    onShareCover: (String) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    var hasAutoLaunchedCamera by remember { mutableStateOf(false) }

    // Auto-launch camera picker on screen entry if selfie has not been taken yet
    LaunchedEffect(uiState.habitId) {
        if (uiState.habitId.isNotBlank() && !hasAutoLaunchedCamera && uiState.selfieFilePath == null && uiState.generatedComicCover == null && !uiState.isDailySelfieSaved) {
            hasAutoLaunchedCamera = true
            val file = FileKit.openCameraPicker()
            if (file != null) {
                onUiEvent(CelebrationUiEvent.OnSelfieSelected(file))
            } else if (!uiState.isMilestoneCelebration) {
                // If user cancels camera on regular day, return straight to quests
                onNavigateBack()
            }
        }
    }

    // On regular check-in days (non-milestone), immediately return to Quests page as soon as selfie is saved
    LaunchedEffect(uiState.isDailySelfieSaved, uiState.isMilestoneCelebration) {
        if (uiState.isDailySelfieSaved && !uiState.isMilestoneCelebration) {
            onNavigateBack()
        }
    }

    if (uiState.showOutOfCreditsDialog) {
        AlertDialog(
            onDismissRequest = { onUiEvent(CelebrationUiEvent.OnDismissOutOfCreditsDialog) },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color(0xFF151928),
            title = {
                Text(
                    text = "⚡ Out of Comic Cover Credits",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color(0xFFFFD54F),
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "You reached your Day ${uiState.streakCount} milestone for ${uiState.habitTitle}, but you need 1 Comic Cover Credit to mint this AI superhero cover!",
                        fontSize = 14.sp,
                        color = Color.White,
                    )
                    Text(
                        text = "• Hero Cadet: $1.99/mo (10 covers)\n• Hero Champion: $5.99/mo (40 covers)\n• Hero Annual: $50.00/yr (40 covers/mo, Save ~30%)\n\nOr save your victory selfie check-in now so your streak is never broken!",
                        fontSize = 13.sp,
                        color = Color(0xFFA5B4FC),
                    )
                }
            },
            confirmButton = {
                AppButton(
                    text = "⚡ Upgrade / Get Credits",
                    onClick = {
                        onUiEvent(CelebrationUiEvent.OnDismissOutOfCreditsDialog)
                        onNavigateToPaywall()
                    },
                )
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        onUiEvent(CelebrationUiEvent.OnDismissOutOfCreditsDialog)
                        onUiEvent(CelebrationUiEvent.OnSaveDailySelfie)
                    },
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("📸 Save Selfie Check-In", color = Color(0xFFA5B4FC))
                }
            },
        )
    }

    ScreenWithToolbar(
        modifier = modifier,
        title = if (uiState.isMilestoneCelebration) "Milestone Celebration" else "Daily Check-In",
        onNavigationIconClick = onNavigateBack,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isMilestoneCelebration) {
                ConfettiParticlesAnimated(
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (uiState.generatedComicCover != null) {
                    // Comic Cover Reveal Card (Completed State)
                    ComicRevealCard(
                        cover = uiState.generatedComicCover,
                        onClickImage = { onNavigateToHabitDetail(uiState.generatedComicCover.habitId) },
                        onViewGallery = onNavigateToGallery,
                        onBack = onNavigateBack,
                        onShareCover = onShareCover,
                    )
                } else if (uiState.isMilestoneCelebration) {
                    // Milestone AI Comic Cover Minting Flow
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "🎨 Minting Milestone Comic Cover",
                                fontWeight = FontWeight.Black,
                                fontSize = 19.sp,
                                textAlign = TextAlign.Center,
                                color = Color.White,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Habit Hero is turning your selfie into a vintage superhero comic book cover starring you on '${uiState.habitTitle.uppercase()}' with your ${uiState.streakCount}-day streak!",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp,
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // If Generating: show spinner and message visibly right here
                            if (uiState.isGenerating) {
                                if (uiState.selfieFilePath != null) {
                                    Box(
                                        modifier = Modifier
                                            .width(150.dp)
                                            .aspectRatio(0.7f)
                                            .clip(RoundedCornerShape(14.dp))
                                            .border(3.dp, Color(0xFFFFD54F), RoundedCornerShape(14.dp)),
                                    ) {
                                        AsyncImage(
                                            model = uiState.selfieFilePath,
                                            contentDescription = "Selfie Preview",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize(),
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                }

                                CircularProgressIndicator(
                                    modifier = Modifier.size(36.dp),
                                    color = Color(0xFFFFD54F),
                                    strokeWidth = 3.5.dp,
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "⚡ Minting your vintage comic cover...",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F),
                                    textAlign = TextAlign.Center,
                                )
                            } else if (uiState.generationError != null) {
                                Text(
                                    text = uiState.generationError,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                AppButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "🔄 Try Again (Take Photo)",
                                    onClick = {
                                        coroutineScope.launch {
                                            val file = FileKit.openCameraPicker()
                                            if (file != null) {
                                                onUiEvent(CelebrationUiEvent.OnSelfieSelected(file))
                                            }
                                        }
                                    },
                                )
                            } else {
                                // Selfie hasn't been taken yet (e.g. camera dismissed)
                                AppButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "📸 Open Camera",
                                    onClick = {
                                        coroutineScope.launch {
                                            val file = FileKit.openCameraPicker()
                                            if (file != null) {
                                                onUiEvent(CelebrationUiEvent.OnSelfieSelected(file))
                                            }
                                        }
                                    },
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = onNavigateBack,
                                    shape = RoundedCornerShape(12.dp),
                                ) {
                                    Text("Skip to Quests ➔", color = Color(0xFFA5B4FC))
                                }
                            }
                        }
                    }
                } else {
                    // Non-Milestone Daily Victory Check-In
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "📸 Daily Photo Check-In",
                                fontWeight = FontWeight.Black,
                                fontSize = 19.sp,
                                textAlign = TextAlign.Center,
                                color = Color.White,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Keep the momentum going! Snap a quick photo to log your Day ${uiState.streakCount} check-in for ${uiState.habitTitle}.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp,
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Next Milestone Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E293B))
                                    .border(1.dp, Color(0xFF6366F1), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                            ) {
                                Text(
                                    text = "🎯 Next Comic Cover Milestone: Day ${uiState.nextMilestoneDay} (${uiState.daysUntilNextMilestone} days left)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F),
                                    textAlign = TextAlign.Center,
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // If selfie camera was dismissed:
                            AppButton(
                                modifier = Modifier.fillMaxWidth(),
                                text = "📸 Open Camera",
                                onClick = {
                                    coroutineScope.launch {
                                        val file = FileKit.openCameraPicker()
                                        if (file != null) {
                                            onUiEvent(CelebrationUiEvent.OnSelfieSelected(file))
                                        }
                                    }
                                },
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = onNavigateBack,
                                shape = RoundedCornerShape(12.dp),
                            ) {
                                Text("Complete Without Photo ⚡", color = Color(0xFFA5B4FC))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComicRevealCard(
    cover: ComicCover,
    onClickImage: () -> Unit = {},
    onViewGallery: () -> Unit,
    onBack: () -> Unit,
    onShareCover: (String) -> Unit = {},
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "🏆 ENTRY SAVED TO LOG!",
                color = Color(0xFF4ADE80),
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = cover.headline,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (cover.imageUrl.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .aspectRatio(0.7f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(3.dp, Color(0xFFFFD54F), RoundedCornerShape(12.dp))
                        .clickable { onClickImage() },
                ) {
                    ComicCoverImage(
                        imageUrl = cover.imageUrl,
                        contentDescription = cover.headline,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (cover.imageUrl.isNotBlank()) {
                    AppButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = "🎉 Share Comic Cover",
                        onClick = { onShareCover(cover.imageUrl) },
                    )
                }

                AppButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "📖 View in Log",
                    onClick = onViewGallery,
                )

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onBack,
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Back to Quests ➔", color = Color(0xFFA5B4FC))
                }
            }
        }
    }
}

// ── Storefront Previews ───────────────────────────────────────────────────────

@Preview
@StoreScreenshot(locale = "en", tag = "03_celebration")
@Composable
private fun CelebrationScreenStoreScreenshot_iPhone_en() {
    AppTheme {
        CelebrationScreen(
            uiState = CelebrationUiState(
                habitTitle = "Read 20 Mins",
                streakCount = 7,
                isMilestoneCelebration = true,
                generatedComicCover = ComicCover(
                    id = "preview",
                    habitId = "1",
                    habitTitle = "Read 20 Mins",
                    streakNumber = 7,
                    headline = "7 Day Streak Champion!",
                    imageUrl = "drawable:cover_read_20_mins",
                    createdAt = 0L,
                ),
            ),
            onUiEvent = {},
            onNavigateToGallery = {},
            onNavigateToHabitDetail = {},
            onNavigateBack = {},
            onNavigateToPaywall = {},
            onShareCover = {},
        )
    }
}
