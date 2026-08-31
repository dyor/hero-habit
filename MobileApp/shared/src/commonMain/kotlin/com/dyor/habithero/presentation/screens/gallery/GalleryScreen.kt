package com.dyor.habithero.presentation.screens.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.dyor.habithero.designsystem.components.AppButton
import com.dyor.habithero.designsystem.components.LoadingProgress
import com.dyor.habithero.designsystem.components.LoadingProgressMode
import com.dyor.habithero.designsystem.components.ScreenWithToolbar
import com.dyor.habithero.designsystem.theme.AppTheme
import com.dyor.habithero.domain.model.ComicCover
import com.dyor.habithero.presentation.components.ComicCoverImage
import com.dyor.habithero.util.StoreScreenshot
import com.dyor.habithero.util.extensions.asFormattedDate
import com.dyor.habithero.util.file.FileManager
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun GalleryScreen(
    modifier: Modifier = Modifier,
    viewModel: GalleryViewModel,
    onNavigateToHome: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GalleryScreen(
        modifier = modifier.fillMaxSize(),
        uiState = uiState,
        onUiEvent = { event ->
            when (event) {
                GalleryUiEvent.OnClickGoToQuests -> onNavigateToHome()
                else -> viewModel.onUiEvent(event)
            }
        },
    )
}

@Composable
internal fun GalleryScreen(
    modifier: Modifier = Modifier,
    uiState: GalleryUiState,
    onUiEvent: (GalleryUiEvent) -> Unit,
) {
    ScreenWithToolbar(
        modifier = modifier,
        title = "Hall of Heroes",
        includeBottomInsets = false,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    LoadingProgress(mode = LoadingProgressMode.FULLSCREEN)
                }

                uiState.comicCovers.isEmpty() -> {
                    EmptyHeroesView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        onGoToQuests = { onUiEvent(GalleryUiEvent.OnClickGoToQuests) },
                    )
                }

                else -> {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalItemSpacing = 12.dp,
                    ) {
                        itemsIndexed(uiState.comicCovers, key = { _, cover -> cover.id }) { index, cover ->
                            ComicCoverGridCard(
                                cover = cover,
                                onClick = { onUiEvent(GalleryUiEvent.OnClickCover(index)) },
                            )
                        }
                    }
                }
            }

            // Full-screen swipeable comic cover viewer dialog
            if (uiState.selectedCoverIndex != null && uiState.comicCovers.isNotEmpty()) {
                FullScreenComicViewer(
                    covers = uiState.comicCovers,
                    initialIndex = uiState.selectedCoverIndex ?: 0,
                    onDismiss = { onUiEvent(GalleryUiEvent.OnDismissFullScreen) },
                )
            }
        }
    }
}

@Composable
private fun ComicCoverGridCard(
    cover: ComicCover,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
            ) {
                if (cover.imageUrl.isNotBlank()) {
                    ComicCoverImage(
                        imageUrl = cover.imageUrl,
                        contentDescription = cover.headline,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF312E81),
                                        Color(0xFF1E1B4B),
                                        Color(0xFF0F172A),
                                    ),
                                ),
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "⚡", fontSize = 44.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF6366F1).copy(alpha = 0.4f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                            ) {
                                Text(
                                    text = "CHECK-IN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFA5B4FC),
                                    letterSpacing = 0.5.sp,
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE53935))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = "Day ${cover.streakNumber} 🔥",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = cover.habitTitle.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = cover.headline,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                )
            }
        }
    }
}

@Composable
private fun FullScreenComicViewer(
    covers: List<ComicCover>,
    initialIndex: Int,
    onDismiss: () -> Unit,
    fileManager: FileManager = koinInject(),
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = initialIndex.coerceIn(0, covers.size - 1),
        pageCount = { covers.size },
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                val cover = covers[page]
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    ComicCoverImage(
                        imageUrl = cover.imageUrl,
                        contentDescription = cover.headline,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )

                    // Bottom information gradient overlay floating on top of full-bleed image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Black.copy(alpha = 0.92f),
                                    ),
                                ),
                            )
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    text = cover.habitTitle.uppercase(),
                                    color = Color(0xFFFFD54F),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    letterSpacing = 1.sp,
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFE53935))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                ) {
                                    Text(
                                        text = "${cover.streakNumber} Day Streak 🔥",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = cover.headline,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Swipe left / right to browse Hall of Heroes",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            }

            // Top Header: Floating gradient backdrop with Close button, Share, Save, & Page indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.75f),
                                Color.Transparent,
                            ),
                        ),
                    )
                    .statusBarsPadding()
                    .padding(start = 16.dp, end = 16.dp, top = 36.dp, bottom = 12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "✕",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        // Share Action Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF6366F1))
                                .clickable {
                                    coroutineScope.launch {
                                        val currentCover = covers.getOrNull(pagerState.currentPage)
                                        if (currentCover != null && currentCover.imageUrl.isNotBlank()) {
                                            fileManager.shareFile(currentCover.imageUrl)
                                        }
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 7.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text(
                                    text = "Share",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = "↗",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }

                        // Save to Gallery Action
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .clickable {
                                    coroutineScope.launch {
                                        val currentCover = covers.getOrNull(pagerState.currentPage)
                                        if (currentCover != null && currentCover.imageUrl.isNotBlank()) {
                                            fileManager.saveImageToGallery(currentCover.imageUrl)
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "💾",
                                fontSize = 16.sp,
                            )
                        }

                        // Page Indicator
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = "${pagerState.currentPage + 1} / ${covers.size}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyHeroesView(
    modifier: Modifier = Modifier,
    onGoToQuests: () -> Unit,
) {
    Column(
        modifier = modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF6366F1).copy(alpha = 0.2f))
                .padding(horizontal = 10.dp, vertical = 4.dp),
        ) {
            Text(
                text = "✨ PREVIEW ISSUES",
                color = Color(0xFFA5B4FC),
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp,
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Hall of Heroes",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Complete your daily habit quests to mint and collect vintage superhero comic covers starring you!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Sample Milestone Issues Preview Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SampleMilestoneCard(
                modifier = Modifier.weight(1f),
                title = "READ 20 MINS",
                icon = "🦸‍♂️",
                tag = "1 DAY 🔥",
                accentColor = Color(0xFFFFD54F),
            )
            SampleMilestoneCard(
                modifier = Modifier.weight(1f),
                title = "MEDITATION",
                icon = "🧘",
                tag = "COSMIC 🌌",
                accentColor = Color(0xFF38BDF8),
            )
            SampleMilestoneCard(
                modifier = Modifier.weight(1f),
                title = "PUMPING IRON",
                icon = "🦸‍♀️",
                tag = "7 DAYS 🏆",
                accentColor = Color(0xFF4ADE80),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        AppButton(
            text = "Start Your First Quest ⚡",
            onClick = onGoToQuests,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SampleMilestoneCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: String,
    tag: String,
    accentColor: Color,
) {
    Card(
        modifier = modifier.height(175.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, accentColor, RoundedCornerShape(12.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B)),
                    ),
                )
                .padding(6.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(accentColor)
                        .padding(vertical = 2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Black,
                        fontSize = 8.sp,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                    )
                }

                Text(text = icon, fontSize = 42.sp)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFE53935))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = tag,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp,
                    )
                }
            }
        }
    }
}

// ── Storefront Previews ───────────────────────────────────────────────────────

@androidx.compose.ui.tooling.preview.Preview
@StoreScreenshot(locale = "en", tag = "02_hall_of_heroes")
@Composable
private fun GalleryScreenStoreScreenshot_iPhone_en() {
    AppTheme {
        GalleryScreen(
            uiState = GalleryUiState(
                comicCovers = listOf(
                    ComicCover(
                        id = "1",
                        habitId = "1",
                        habitTitle = "Read 20 Mins",
                        streakNumber = 15,
                        headline = "15 Day Streak Hero!",
                        imageUrl = "drawable:cover_read_20_mins",
                        heroRole = "Superhero",
                    ),
                    ComicCover(
                        id = "2",
                        habitId = "2",
                        habitTitle = "Pumping Iron",
                        streakNumber = 10,
                        headline = "10 Day Streak Hero!",
                        imageUrl = "drawable:cover_pumping_iron",
                        heroRole = "Superhero",
                    ),
                    ComicCover(
                        id = "3",
                        habitId = "3",
                        habitTitle = "Meditation",
                        streakNumber = 3,
                        headline = "3 Day Streak Hero!",
                        imageUrl = "drawable:cover_meditation",
                        heroRole = "Superhero",
                    ),
                ),
            ),
            onUiEvent = {},
        )
    }
}
