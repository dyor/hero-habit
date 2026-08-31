package com.dyor.habithero.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dyor.habithero.designsystem.generated.resources.UiRes
import com.dyor.habithero.designsystem.generated.resources.ic_back
import com.dyor.habithero.designsystem.theme.AppTheme
import com.dyor.habithero.designsystem.util.defaultScreenPadding
import com.dyor.habithero.designsystem.util.scrollableScreenPadding
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/**
 * @param includeBottomInsets - If bottom nav is visible, then this should be false, otherwise set true
 */
@Composable
fun ScreenWithToolbar(
    modifier: Modifier = Modifier,
    title: String = "",
    titleContent: @Composable (() -> Unit)? = null,
    includeBottomInsets: Boolean = false,
    toolbarExtraContent: @Composable RowScope.() -> Unit = {},
    onNavigationIconClick: (() -> Unit)? = null,
    navigationIcon: DrawableResource? = UiRes.drawable.ic_back,
    isScrollableContent: Boolean = false,
    content: @Composable () -> Unit,
) {
    Column(
        modifier =
        modifier
            .background(AppTheme.colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
            .then(
                if (includeBottomInsets) {
                    Modifier.windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom),
                    )
                } else {
                    Modifier
                },
            ),
    ) {
        AppToolbar(
            title = title,
            titleContent = titleContent,
            navigationIcon = if (onNavigationIconClick != null && navigationIcon != null) painterResource(navigationIcon) else null,
            onNavigationIconClick = { onNavigationIconClick?.invoke() },
            actions = toolbarExtraContent,
        )
        val contentModifier =
            if (isScrollableContent) {
                Modifier.scrollableScreenPadding(
                    scrollState = rememberScrollState(),
                    appSpacing = AppTheme.spacing,
                )
            } else {
                Modifier.defaultScreenPadding(appSpacing = AppTheme.spacing)
            }

        Box(modifier = contentModifier) {
            content()
        }
    }
}

@Composable
fun ScreenWithProgressedToolbar(
    modifier: Modifier = Modifier,
    nbTotalSteps: Int,
    nbCompletedSteps: Int,
    onNavigationIconClick: () -> Unit,
    isScrollableContent: Boolean = false,
    content: @Composable () -> Unit,
) {
    Column(
        modifier =
        modifier
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
    ) {
        AppToolbarWithProgress(
            nbTotalSteps = nbTotalSteps,
            nbCompletedSteps = nbCompletedSteps,
            onNavigationIconClick = onNavigationIconClick,
        )
        val contentModifier =
            if (isScrollableContent) {
                Modifier.scrollableScreenPadding(
                    scrollState = rememberScrollState(),
                    appSpacing = AppTheme.spacing,
                )
            } else {
                Modifier.defaultScreenPadding(appSpacing = AppTheme.spacing)
            }

        Box(modifier = contentModifier) {
            content()
        }
    }
}
