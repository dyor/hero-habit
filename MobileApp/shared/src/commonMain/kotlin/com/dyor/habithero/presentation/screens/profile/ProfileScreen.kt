package com.dyor.habithero.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dyor.habithero.designsystem.components.LoadingProgress
import com.dyor.habithero.designsystem.components.LoadingProgressMode
import com.dyor.habithero.designsystem.components.ScreenWithToolbar
import com.dyor.habithero.designsystem.components.SettingItemListContainer
import com.dyor.habithero.designsystem.components.SettingsItemUiState
import com.dyor.habithero.designsystem.components.UserInput
import com.dyor.habithero.designsystem.components.modals.AppDialog
import com.dyor.habithero.designsystem.components.modals.DeleteUserConfirmation
import com.dyor.habithero.designsystem.components.modals.DialogType
import com.dyor.habithero.designsystem.generated.resources.UiRes
import com.dyor.habithero.designsystem.generated.resources.btn_delete_account
import com.dyor.habithero.designsystem.generated.resources.ic_back
import com.dyor.habithero.designsystem.generated.resources.ic_delete
import com.dyor.habithero.designsystem.generated.resources.ic_profile_img_placeholder
import com.dyor.habithero.designsystem.theme.AppTheme
import com.dyor.habithero.domain.model.User
import com.dyor.habithero.generated.resources.Res
import com.dyor.habithero.generated.resources.title_screen_profile
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel,
    onSignInRequired: () -> Unit,
    onNavigateToBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.signInActionRequired) {
        if (uiState.signInActionRequired) {
            onSignInRequired()
        }
    }
    if (uiState.deleteUserDialogShown) {
        DeleteUserConfirmation(
            onConfirm = viewModel::onConfirmDeleteAccount,
            onDismiss = viewModel::onDismissDeleteUserConfirmationDialog,
        )
    }

    if (uiState.errorMessage.isNullOrEmpty().not()) {
        AppDialog(
            type = DialogType.ERROR,
            text = uiState.errorMessage,
            onConfirm = { viewModel.onErrorMessageShown() },
        )
    }
    if (uiState.isLoading) {
        LoadingProgress(mode = LoadingProgressMode.FULLSCREEN)
    } else {
        val currentUser = uiState.user
        currentUser?.let {
            ScreenWithToolbar(
                modifier = modifier.fillMaxSize().background(AppTheme.colors.background),
                title = stringResource(Res.string.title_screen_profile),
                navigationIcon = UiRes.drawable.ic_back,
                onNavigationIconClick = onNavigateToBack,
                isScrollableContent = true,
                includeBottomInsets = true,
            ) {
                ProfileScreen(
                    modifier = Modifier.fillMaxSize(),
                    currentUser = it,
                    streakCelebrationInterval = uiState.streakCelebrationInterval,
                    totalCoversMinted = uiState.totalCoversMinted,
                    creditBalance = uiState.creditBalance,
                    planName = uiState.planName,
                    onUiEvent = viewModel::onUiEvent,
                )
            }
        }
    }
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    currentUser: User,
    streakCelebrationInterval: Int = 7,
    totalCoversMinted: Int = 0,
    creditBalance: Int = 0,
    planName: String = "Free",
    onUiEvent: (ProfileScreenUiEvent) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sectionSpacing),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Hero Comic Stats Card
        androidx.compose.material3.Card(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            colors = androidx.compose.material3.CardDefaults.cardColors(
                containerColor = androidx.compose.ui.graphics.Color(0xFF1E2640),
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "🏆 Hero Comic Stats & Plan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = androidx.compose.ui.graphics.Color.White,
                )
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(
                            text = "Covers Minted",
                            fontSize = 12.sp,
                            color = androidx.compose.ui.graphics.Color(0xFFA5B4FC),
                        )
                        Text(
                            text = "🎨 $totalCoversMinted",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = androidx.compose.ui.graphics.Color(0xFFFFD54F),
                        )
                    }

                    Column {
                        Text(
                            text = "Cover Credits",
                            fontSize = 12.sp,
                            color = androidx.compose.ui.graphics.Color(0xFFA5B4FC),
                        )
                        Text(
                            text = "⚡ $creditBalance",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = androidx.compose.ui.graphics.Color(0xFF38BDF8),
                        )
                    }

                    Column {
                        Text(
                            text = "Current Plan",
                            fontSize = 12.sp,
                            color = androidx.compose.ui.graphics.Color(0xFFA5B4FC),
                        )
                        Text(
                            text = planName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.ui.graphics.Color(0xFF4ADE80),
                        )
                    }
                }
            }
        }

        // Profile Picture
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(currentUser.photoUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(UiRes.drawable.ic_profile_img_placeholder),
            error = painterResource(UiRes.drawable.ic_profile_img_placeholder),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(100.dp).clip(CircleShape),
        )

        // Full Name
        UserInputWithLabel(label = "Display Name") {
            UserInput(
                value = currentUser.displayName ?: "",
                readOnly = true,
                onValueChange = {},
            )
        }

        // Email
        UserInputWithLabel(label = "Email") {
            UserInput(
                value = currentUser.email ?: "",
                readOnly = true,
                onValueChange = {},
            )
        }

        // Streak Celebration Interval Setting
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Streak Celebration Interval",
                style = AppTheme.typography.bodyExtraLarge,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.text.primary,
            )
            Text(
                text = "Choose how often you are invited to mint an AI superhero comic cover. On other days, you record a daily victory selfie.",
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.text.secondary,
            )

            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val intervals = listOf(1, 3, 5, 7, 14, 30)
                items(intervals.size) { index ->
                    val days = intervals[index]
                    val isSelected = days == streakCelebrationInterval
                    androidx.compose.material3.FilterChip(
                        selected = isSelected,
                        onClick = {
                            onUiEvent(ProfileScreenUiEvent.OnUpdateCelebrationInterval(days))
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

        SettingItemListContainer(
            onClick = { onUiEvent(ProfileScreenUiEvent.OnClickDeleteAccount) },
            itemTextStyle = AppTheme.typography.h5.copy(fontWeight = FontWeight.SemiBold),
            itemList = listOf(
                SettingsItemUiState(
                    textRes = UiRes.string.btn_delete_account,
                    startIcon = UiRes.drawable.ic_delete,
                    showEndIcon = false,
                    textIconColor = AppTheme.colors.status.error,
                ),
            ),
        )
    }
}

@Composable
fun UserInputWithLabel(
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    userInput: @Composable () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.defaultSpacing),
    ) {
        Text(
            text = label,
            style = AppTheme.typography.bodyExtraLarge,
            fontWeight = FontWeight.SemiBold,
            color = AppTheme.colors.text.primary,
        )
        userInput()
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    AppTheme {
        ProfileScreen(
            currentUser = User(id = "1", displayName = "Jane Doe", email = "jane@example.com"),
            onUiEvent = {},
        )
    }
}
