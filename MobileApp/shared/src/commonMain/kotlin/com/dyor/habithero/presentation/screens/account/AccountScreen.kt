package com.dyor.habithero.presentation.screens.account

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dyor.habithero.designsystem.components.AppButton
import com.dyor.habithero.designsystem.components.AppCardContainer
import com.dyor.habithero.designsystem.components.ButtonSize
import com.dyor.habithero.designsystem.components.ScreenWithToolbar
import com.dyor.habithero.designsystem.components.SettingItemListContainer
import com.dyor.habithero.designsystem.components.SmallTitle
import com.dyor.habithero.designsystem.components.premium.UpgradePremiumBanner
import com.dyor.habithero.designsystem.components.premium.UpgradePremiumBannerStyle
import com.dyor.habithero.designsystem.generated.resources.UiRes
import com.dyor.habithero.designsystem.generated.resources.ic_arrow_right
import com.dyor.habithero.designsystem.generated.resources.ic_copy_content
import com.dyor.habithero.designsystem.generated.resources.ic_profile_img_placeholder
import com.dyor.habithero.designsystem.theme.AppTheme
import com.dyor.habithero.domain.model.User
import com.dyor.habithero.generated.resources.Res
import com.dyor.habithero.generated.resources.comic_cover_credits
import com.dyor.habithero.generated.resources.help_and_support
import com.dyor.habithero.generated.resources.subscriptions
import com.dyor.habithero.generated.resources.title_screen_account
import com.dyor.habithero.generated.resources.title_sign_in
import com.dyor.habithero.root.AppConfiguration
import com.dyor.habithero.root.AppGlobalUiState
import com.dyor.habithero.util.UiMessage
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    viewModel: AccountViewModel,
    onNavigateHelpAndSupport: () -> Unit,
    onNavigatePaywall: () -> Unit,
    onNavigateSignIn: () -> Unit,
    onNavigateProfile: () -> Unit,
    onNavigateSubscriptions: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AccountScreen(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.background),
        uiState = uiState,
        onUiEvent = {
            when (it) {
                is AccountUiEvent.OnSettingsItemClick -> {
                    when (it.item.textRes) {
                        Res.string.help_and_support -> onNavigateHelpAndSupport()
                        Res.string.comic_cover_credits -> onNavigatePaywall()
                        Res.string.subscriptions -> onNavigatePaywall()
                        else -> viewModel.onUiEvent(it)
                    }
                }

                is AccountUiEvent.OnClickUpgradePremium -> {
                    onNavigatePaywall()
                }

                AccountUiEvent.OnClickSignIn -> {
                    onNavigateSignIn()
                }

                AccountUiEvent.OnClickProfile -> {
                    onNavigateProfile()
                }

                else -> viewModel.onUiEvent(it)
            }
        },
    )
}

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    uiState: AccountUiState,
    onUiEvent: (AccountUiEvent) -> Unit,
) {
    ScreenWithToolbar(
        modifier = modifier,
        isScrollableContent = true,
        title = stringResource(Res.string.title_screen_account),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (uiState.showUpgradePremiumBanner) {
                UpgradePremiumBanner(
                    style = UpgradePremiumBannerStyle.SMALL,
                    onClick = { onUiEvent(AccountUiEvent.OnClickUpgradePremium) },
                )
            }

            // User Profile / Hero ID
            if (AppConfiguration.AUTH_SOCIAL_LOGIN_ENABLED || uiState.user?.id?.isNotEmpty() == true) {
                ProfileInfoBox(user = uiState.user, onClick = {
                    if (uiState.user == null) {
                        onUiEvent(AccountUiEvent.OnClickSignIn)
                    } else {
                        onUiEvent(AccountUiEvent.OnClickProfile)
                    }
                })
            }

            // 1. Create New Habit Quest Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "⚡ Habit Management",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Create custom daily superhero habit quests.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            )
                        }
                    }

                    if (uiState.habitCreatedSuccess) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF22C55E).copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = "✅ New habit quest created and added to Quests!",
                                color = Color(0xFF4ADE80),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AppButton(
                        text = "➕ Create New Habit Quest",
                        onClick = { onUiEvent(AccountUiEvent.OnClickCreateHabit) },
                        size = ButtonSize.SMALL,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            // 2. Comic Book Celebration Interval Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "🎨 Comic Book Celebration Interval",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Choose how often you are invited to mint an AI superhero comic cover. On other days, you record a quick daily check-in.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val intervals = listOf(1, 3, 5, 7, 14, 30)
                        items(intervals.size) { index ->
                            val days = intervals[index]
                            val isSelected = days == uiState.streakCelebrationInterval
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    onUiEvent(AccountUiEvent.OnUpdateCelebrationInterval(days))
                                },
                                label = {
                                    Text(
                                        text = if (days == 7) "7 Days (Default)" else "$days Days",
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.sp,
                                    )
                                },
                            )
                        }
                    }
                }
            }

            // 3. Support & Other Links
            SettingItemListContainer(
                itemList = uiState.settingsItemList,
                onClick = { onUiEvent(AccountUiEvent.OnSettingsItemClick(it)) },
            )
        }

        // Create Habit Dialog
        if (uiState.isAddHabitDialogOpen) {
            AddHabitDialog(
                title = uiState.newHabitTitle,
                category = uiState.newHabitCategory,
                customPrompt = uiState.newHabitCustomPrompt,
                onTitleChanged = { onUiEvent(AccountUiEvent.OnNewHabitTitleChanged(it)) },
                onCategoryChanged = { onUiEvent(AccountUiEvent.OnNewHabitCategoryChanged(it)) },
                onCustomPromptChanged = { onUiEvent(AccountUiEvent.OnNewHabitCustomPromptChanged(it)) },
                onConfirm = { onUiEvent(AccountUiEvent.OnConfirmAddHabit) },
                onDismiss = { onUiEvent(AccountUiEvent.OnDismissAddHabitDialog) },
            )
        }
    }
}

@Composable
private fun AddHabitDialog(
    title: String,
    category: String,
    customPrompt: String,
    onTitleChanged: (String) -> Unit,
    onCategoryChanged: (String) -> Unit,
    onCustomPromptChanged: (String) -> Unit,
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
                    label = { Text("Habit Title (e.g. Morning Jog)") },
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
                OutlinedTextField(
                    value = customPrompt,
                    onValueChange = onCustomPromptChanged,
                    label = { Text("Custom AI Scenario (Optional)") },
                    placeholder = {
                        Text(
                            "e.g. running at lightning speed with glowing neon trails",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        )
                    },
                    minLines = 2,
                    maxLines = 4,
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
private fun ProfileInfoBox(user: User?, onClick: () -> Unit) {
    val clipboardManager = LocalClipboardManager.current
    AppCardContainer(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            if (AppConfiguration.AUTH_SOCIAL_LOGIN_ENABLED) {
                onClick()
            } else {
                user?.id?.let {
                    clipboardManager.setText(AnnotatedString(it))
                    AppGlobalUiState.showUiMessage(UiMessage.Message("User ID is copied to clipboard"))
                }
            }
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.horizontalItemSpacing),
        ) {
            if (AppConfiguration.AUTH_SOCIAL_LOGIN_ENABLED) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(user?.photoUrl)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(UiRes.drawable.ic_profile_img_placeholder),
                    error = painterResource(UiRes.drawable.ic_profile_img_placeholder),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(60.dp).clip(CircleShape),
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.groupedVerticalElementSpacingSmall),
                ) {
                    val displayName =
                        if (user == null) stringResource(Res.string.title_sign_in) else user.displayName

                    SmallTitle(text = displayName ?: "User Name")
                    user?.email?.let { email ->
                        Text(
                            email,
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.text.secondary,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }

                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = vectorResource(UiRes.drawable.ic_arrow_right),
                    contentDescription = null,
                    tint = AppTheme.colors.text.primary,
                )
            } else {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF6366F1).copy(alpha = 0.35f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = "⚡ HERO ID",
                            color = Color(0xFFA5B4FC),
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        modifier = Modifier.weight(1f),
                        text = user?.id ?: "",
                        style = AppTheme.typography.bodySmall,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                    )
                }

                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = vectorResource(UiRes.drawable.ic_copy_content),
                    contentDescription = "Copy User ID",
                    tint = Color(0xFF818CF8),
                )
            }
        }
    }
}


@Preview
@Composable
private fun AccountScreenPreview() {
    AppTheme {
        AccountScreen(uiState = AccountUiState(), onUiEvent = {})
    }
}
