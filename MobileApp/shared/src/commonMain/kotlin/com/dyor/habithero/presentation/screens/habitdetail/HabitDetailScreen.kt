@file:OptIn(ExperimentalMaterial3Api::class)

package com.dyor.habithero.presentation.screens.habitdetail

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.dyor.habithero.designsystem.components.ButtonSize
import com.dyor.habithero.designsystem.components.LoadingProgress
import com.dyor.habithero.designsystem.components.LoadingProgressMode
import com.dyor.habithero.designsystem.components.ScreenWithToolbar
import com.dyor.habithero.designsystem.generated.resources.UiRes
import com.dyor.habithero.designsystem.generated.resources.ic_back
import com.dyor.habithero.designsystem.theme.AppTheme
import com.dyor.habithero.domain.model.ComicCover
import com.dyor.habithero.domain.model.HeroRole
import com.dyor.habithero.domain.model.Habit
import com.dyor.habithero.presentation.components.ComicCoverImage
import com.dyor.habithero.util.StoreScreenshot
import com.dyor.habithero.util.extensions.asFormattedDate
import com.dyor.habithero.util.extensions.asRelativeTimeString
import com.dyor.habithero.util.extensions.nowEpochMillis
import com.dyor.habithero.util.file.FileManager
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun HabitDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: HabitDetailViewModel,
    onNavigateBack: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HabitDetailScreen(
        modifier = modifier.fillMaxSize(),
        uiState = uiState,
        onUiEvent = viewModel::onUiEvent,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
internal fun HabitDetailScreen(
    modifier: Modifier = Modifier,
    uiState: HabitDetailUiState,
    onUiEvent: (HabitDetailUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
) {
    ScreenWithToolbar(
        modifier = modifier,
        title = uiState.habit?.title ?: "Habit Details",
        navigationIcon = UiRes.drawable.ic_back,
        onNavigationIconClick = onNavigateBack,
        includeBottomInsets = true,
    ) {
        if (uiState.isLoading || uiState.habit == null) {
            LoadingProgress(mode = LoadingProgressMode.FULLSCREEN)
        } else {
            val habit = uiState.habit

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // 1. Habit Hero Header Card (with streak editing, date editing, title editing, and log past check-in)
                item {
                    HabitHeaderCard(
                        habit = habit,
                        onEditTitle = { onUiEvent(HabitDetailUiEvent.OnOpenEditTitleDialog) },
                        onEditDate = { onUiEvent(HabitDetailUiEvent.OnOpenDatePickerForHabit) },
                        onLogPastEntry = { onUiEvent(HabitDetailUiEvent.OnOpenAddPastEntryDialog) },
                    )
                }

                // 2. Custom Prompt AI Scenario Card
                item {
                    CustomPromptEditorCard(
                        customPrompt = uiState.customPromptInput,
                        isSaving = uiState.isSavingPrompt,
                        isGenerating = uiState.isGeneratingPrompt,
                        showSuccess = uiState.showSaveSuccessBanner,
                        onPromptChange = { onUiEvent(HabitDetailUiEvent.OnCustomPromptChange(it)) },
                        onSavePrompt = { onUiEvent(HabitDetailUiEvent.OnSaveCustomPrompt) },
                        onGenerateAiPrompt = { onUiEvent(HabitDetailUiEvent.OnGenerateAiPrompt) },
                    )
                }

                // 3. Recent Milestone Covers Section
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "🖼️ Habit Log & Comic Covers",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        )
                        Text(
                            text = "${uiState.comicCovers.size} entries",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        )
                    }
                }

                if (uiState.comicCovers.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(text = "🦸", fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No Log Entries Yet",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Complete your daily streak for ${habit.title} to mint an epic superhero victory cover or log a daily check-in!",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                } else {
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 4.dp),
                        ) {
                            itemsIndexed(uiState.comicCovers, key = { _, cover -> cover.id }) { index, cover ->
                                HabitCoverThumbnailCard(
                                    cover = cover,
                                    onClick = { onUiEvent(HabitDetailUiEvent.OnClickCover(index)) },
                                    onEditDate = { onUiEvent(HabitDetailUiEvent.OnOpenDatePickerForCover(cover)) },
                                    onDelete = { onUiEvent(HabitDetailUiEvent.OnOpenDeleteCoverConfirm(cover)) },
                                )
                            }
                        }
                    }
                }

                // 4. Delete Habit Section
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { onUiEvent(HabitDetailUiEvent.OnOpenDeleteHabitConfirm) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                    ) {
                        Text(
                            text = "🗑️ Delete Habit",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFFEF4444),
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        if (uiState.isHabitDeleted) {
            LaunchedEffect(Unit) {
                onNavigateBack()
            }
        }

        // Full Screen Swipeable Comic Viewer
        if (uiState.selectedCoverIndex != null && uiState.comicCovers.isNotEmpty()) {
            FullScreenHabitComicViewer(
                covers = uiState.comicCovers,
                initialIndex = uiState.selectedCoverIndex ?: 0,
                onDismiss = { onUiEvent(HabitDetailUiEvent.OnDismissFullScreenCover) },
                onDeleteCover = { cover -> onUiEvent(HabitDetailUiEvent.OnOpenDeleteCoverConfirm(cover)) },
            )
        }

        // Date Picker for Habit Last Completed Date
        if (uiState.showDatePickerForHabit) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = nowEpochMillis(),
            )
            DatePickerDialog(
                onDismissRequest = { onUiEvent(HabitDetailUiEvent.OnDismissDatePickerForHabit) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val selected = datePickerState.selectedDateMillis
                            if (selected != null) {
                                val formatted = selected.asFormattedDate()
                                onUiEvent(HabitDetailUiEvent.OnUpdateHabitDate(formatted))
                            } else {
                                onUiEvent(HabitDetailUiEvent.OnDismissDatePickerForHabit)
                            }
                        },
                    ) {
                        Text("Save Date", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onUiEvent(HabitDetailUiEvent.OnDismissDatePickerForHabit) }) {
                        Text("Cancel")
                    }
                },
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Date Picker for Adding a Past Backdated Entry
        if (uiState.showAddPastEntryDialog) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = nowEpochMillis() - 86400000L, // Default to yesterday
            )
            DatePickerDialog(
                onDismissRequest = { onUiEvent(HabitDetailUiEvent.OnDismissAddPastEntryDialog) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val selected = datePickerState.selectedDateMillis
                            if (selected != null) {
                                onUiEvent(HabitDetailUiEvent.OnAddPastEntry(selected))
                            } else {
                                onUiEvent(HabitDetailUiEvent.OnDismissAddPastEntryDialog)
                            }
                        },
                    ) {
                        Text("Add Entry ⚡", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onUiEvent(HabitDetailUiEvent.OnDismissAddPastEntryDialog) }) {
                        Text("Cancel")
                    }
                },
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Date Picker for Comic Cover Entry Date
        uiState.showDatePickerForCover?.let { cover ->
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = if (cover.createdAt > 0L) cover.createdAt else nowEpochMillis(),
            )
            DatePickerDialog(
                onDismissRequest = { onUiEvent(HabitDetailUiEvent.OnDismissDatePickerForCover) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val selected = datePickerState.selectedDateMillis
                            if (selected != null) {
                                onUiEvent(HabitDetailUiEvent.OnUpdateCoverDate(cover.id, selected))
                            } else {
                                onUiEvent(HabitDetailUiEvent.OnDismissDatePickerForCover)
                            }
                        },
                    ) {
                        Text("Save Date", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onUiEvent(HabitDetailUiEvent.OnDismissDatePickerForCover) }) {
                        Text("Cancel")
                    }
                },
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Delete Confirmation Dialog for Entry / Comic Cover
        uiState.showDeleteCoverConfirm?.let { cover ->
            AlertDialog(
                onDismissRequest = { onUiEvent(HabitDetailUiEvent.OnDismissDeleteCoverConfirm) },
                title = {
                    Text("Delete Entry?", fontWeight = FontWeight.Bold)
                },
                text = {
                    Text("Are you sure you want to delete this habit log entry from '${cover.habitTitle}'? This action cannot be undone.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onUiEvent(HabitDetailUiEvent.OnConfirmDeleteCover(cover.id))
                        },
                    ) {
                        Text("Delete", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onUiEvent(HabitDetailUiEvent.OnDismissDeleteCoverConfirm) }) {
                        Text("Cancel")
                    }
                },
            )
        }

        // Delete Confirmation Dialog for the Entire Habit
        if (uiState.showDeleteHabitConfirm && uiState.habit != null) {
            AlertDialog(
                onDismissRequest = { onUiEvent(HabitDetailUiEvent.OnDismissDeleteHabitConfirm) },
                title = {
                    Text("Delete Habit?", fontWeight = FontWeight.Bold)
                },
                text = {
                    Text("Are you sure you want to delete \"${uiState.habit.title}\" and all of its recorded check-in entries and comic covers? This action cannot be undone.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onUiEvent(HabitDetailUiEvent.OnConfirmDeleteHabit)
                        },
                    ) {
                        Text("Delete Habit", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onUiEvent(HabitDetailUiEvent.OnDismissDeleteHabitConfirm) }) {
                        Text("Cancel")
                    }
                },
            )
        }

        // Edit Quest Name Dialog
        if (uiState.showEditTitleDialog) {
            AlertDialog(
                onDismissRequest = { onUiEvent(HabitDetailUiEvent.OnDismissEditTitleDialog) },
                title = {
                    Text("Edit Quest Name", fontWeight = FontWeight.Bold)
                },
                text = {
                    Column {
                        Text(
                            text = "Update the title of this superhero habit quest:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = uiState.editTitleInput,
                            onValueChange = { onUiEvent(HabitDetailUiEvent.OnEditTitleInputChange(it)) },
                            label = { Text("Quest Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { onUiEvent(HabitDetailUiEvent.OnConfirmEditTitle) },
                        enabled = uiState.editTitleInput.isNotBlank(),
                    ) {
                        Text("Save Quest", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { onUiEvent(HabitDetailUiEvent.OnDismissEditTitleDialog) }) {
                        Text("Cancel")
                    }
                },
            )
        }
    }
}

@Composable
private fun HabitHeaderCard(
    habit: Habit,
    onEditTitle: () -> Unit,
    onEditDate: () -> Unit,
    onLogPastEntry: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = habit.category.uppercase(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🔥 ${habit.streakCount} Day Streak",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = Color(0xFFE53935),
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onEditTitle() }
                    .padding(vertical = 2.dp)
                    .padding(end = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF7F5FFF)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "✏️", fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Last Completed Date Row with Pencil Edit Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .clickable { onEditDate() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "📅 Last Completed: ",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    )
                    Text(
                        text = habit.lastCompletedDate.ifBlank { "Never" },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF7F5FFF)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "✏️", fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Log Past Entry / Backdating Button
            OutlinedButton(
                onClick = onLogPastEntry,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    text = "➕ Log Past Check-In (Forgot Yesterday?)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFFA5B4FC),
                )
            }
        }
    }
}

@Composable
private fun CustomPromptEditorCard(
    customPrompt: String,
    isSaving: Boolean,
    isGenerating: Boolean,
    showSuccess: Boolean,
    onPromptChange: (String) -> Unit,
    onSavePrompt: () -> Unit,
    onGenerateAiPrompt: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(end = 12.dp),
            ) {
                Text(
                    text = "Custom Action Scenario",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF7F5FFF))
                        .clickable(enabled = !isGenerating && !isSaving, onClick = onGenerateAiPrompt),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = Color.White,
                        )
                    } else {
                        Text(text = "✨", fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Personalize your hero's setting, superpowers, and artistic scene on milestone covers.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = customPrompt,
                onValueChange = onPromptChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "e.g. running at lightning speed through bustling futuristic metropolis with glowing neon trails",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    )
                },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                ),
            )

            if (showSuccess) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF22C55E).copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = "✅ Custom action scenario saved!",
                        color = Color(0xFF4ADE80),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            AppButton(
                text = if (isSaving) "Saving..." else "Save Scenario ⚡",
                onClick = onSavePrompt,
                enabled = !isSaving,
                size = ButtonSize.SMALL,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun HabitCoverThumbnailCard(
    cover: ComicCover,
    onClick: () -> Unit,
    onEditDate: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
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
                            .padding(12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "⚡", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF6366F1).copy(alpha = 0.4f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = "CHECK-IN",
                                    fontSize = 9.sp,
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
                        .padding(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFE53935))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = "Day ${cover.streakNumber} 🔥",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                    )
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = cover.headline,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val dateStr = if (cover.createdAt > 0L) cover.createdAt.asRelativeTimeString() else "Recently"
                    Text(
                        text = dateStr,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { onEditDate() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = "✏️", fontSize = 10.sp)
                        }

                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color(0x33EF4444))
                                .clickable { onDelete() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = "🗑️", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FullScreenHabitComicViewer(
    covers: List<ComicCover>,
    initialIndex: Int,
    onDismiss: () -> Unit,
    onDeleteCover: (ComicCover) -> Unit,
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
                Box(modifier = Modifier.fillMaxSize()) {
                    ComicCoverImage(
                        imageUrl = cover.imageUrl,
                        contentDescription = cover.headline,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )

                    // Bottom floating gradient
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
                                text = if (cover.createdAt > 0L) "Minted on ${cover.createdAt.asFormattedDate()}" else "Milestone Cover",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            }

            // Top Header: Floating gradient backdrop with Close button, Share, Save, Delete & Page indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent),
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
                            Text(text = "💾", fontSize = 16.sp)
                        }

                        // Delete Action
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .clickable {
                                    val currentCover = covers.getOrNull(pagerState.currentPage)
                                    if (currentCover != null) {
                                        onDeleteCover(currentCover)
                                    }
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = "🗑️", fontSize = 16.sp)
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

// ── Storefront Previews ───────────────────────────────────────────────────────

@androidx.compose.ui.tooling.preview.Preview
@StoreScreenshot(locale = "en", tag = "04_habit_detail")
@Composable
private fun HabitDetailScreenStoreScreenshot_iPhone_en() {
    AppTheme {
        HabitDetailScreen(
            uiState = HabitDetailUiState(
                habit = Habit(
                    id = "1",
                    title = "Pumping Iron",
                    category = "Fitness",
                    streakCount = 10,
                    lastCompletedDate = "2026-08-11",
                ),
                customPromptInput = "lifting heavy glowing barbells beneath a soaring bald eagle in ancient redwoods",
                comicCovers = listOf(
                    ComicCover(
                        id = "1",
                        habitId = "1",
                        habitTitle = "Pumping Iron",
                        streakNumber = 10,
                        headline = "10 Day Streak Hero!",
                        imageUrl = "drawable:cover_pumping_iron",
                        heroRole = HeroRole.SUPERHERO,
                    ),
                ),
                isLoading = false,
            ),
            onUiEvent = {},
            onNavigateBack = {},
        )
    }
}
