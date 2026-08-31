package com.dyor.habithero.designsystem.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dyor.habithero.designsystem.theme.AppTheme
import com.dyor.habithero.designsystem.theme.LocalThemeIsDark

@Composable
fun PreviewHelper(
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit,
) {
    AppTheme(isDarkMode = LocalThemeIsDark.current) {
        FlowRow(
            modifier = Modifier.padding(contentPadding),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.horizontalItemSpacing),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.verticalListItemSpacing),
            content = { content() },
        )
    }
}
