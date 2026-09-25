
package com.dyor.habithero.presentation.screens.paywall.creditpack

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dyor.habithero.designsystem.components.AppButton
import com.dyor.habithero.designsystem.components.AppCardContainer
import com.dyor.habithero.designsystem.components.ButtonStyle
import com.dyor.habithero.designsystem.components.ScreenWithToolbar
import com.dyor.habithero.designsystem.generated.resources.UiRes
import com.dyor.habithero.designsystem.generated.resources.ic_close
import com.dyor.habithero.designsystem.generated.resources.ic_coin_credits
import com.dyor.habithero.designsystem.generated.resources.ic_sparkles
import com.dyor.habithero.designsystem.theme.AppTheme
import com.dyor.habithero.generated.resources.Res
import com.dyor.habithero.generated.resources.btn_no_thanks
import com.dyor.habithero.generated.resources.credit_available
import com.dyor.habithero.generated.resources.paywall_cp_credits_count
import com.dyor.habithero.generated.resources.paywall_cp_credits_count_one
import com.dyor.habithero.generated.resources.paywall_cp_section_title
import com.dyor.habithero.generated.resources.paywall_cp_subtitle
import com.dyor.habithero.generated.resources.paywall_cp_title
import com.dyor.habithero.generated.resources.paywall_footer_privacy
import com.dyor.habithero.generated.resources.paywall_footer_terms
import com.dyor.habithero.presentation.screens.paywall.PaywallPackageUiState
import com.dyor.habithero.presentation.screens.paywall.PaywallPreviewData
import com.dyor.habithero.presentation.screens.paywall.PaywallUiEvent
import com.dyor.habithero.presentation.screens.paywall.PaywallUiState
import com.dyor.habithero.root.AppConfiguration
import com.dyor.habithero.subscription.api.PurchasePackageId
import com.dyor.habithero.util.StoreScreenshot
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreditPackPaywallScreen(
    uiState: PaywallUiState,
    onUiEvent: (PaywallUiEvent) -> Unit,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    ScreenWithToolbar(
        modifier = modifier.fillMaxSize().background(AppTheme.colors.background),
        title = "",
        includeBottomInsets = true,
        navigationIcon = UiRes.drawable.ic_close,
        onNavigationIconClick = onDismiss,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.sectionSpacing),
            ) {
                Hero()
                CurrentCreditBalanceCard(creditBalance = uiState.creditBalance)
                if (uiState.packages.isNotEmpty()) {
                    PackList(
                        packs = uiState.packages,
                        onSelect = { id -> onUiEvent(PaywallUiEvent.OnSelectPackage(id)) },
                    )
                }
                Spacer(modifier = Modifier.height(AppTheme.spacing.defaultSpacing))
            }
            StickyBuyFooter(
                buyButtonEnabled = uiState.buyButtonEnabled,
                ctaText = uiState.ctaText.value,
                aboveCtaText = uiState.aboveCtaText.value,
                onBuy = { onUiEvent(PaywallUiEvent.OnClickBuy) },
                onDecline = onDismiss,
            )
        }
    }
}

// ── Hero ─────────────────────────────────────────────────────────────────────

@Composable
private fun Hero() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.groupedVerticalElementSpacing),
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(AppTheme.colors.primary.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(UiRes.drawable.ic_coin_credits),
                contentDescription = null,
                tint = AppTheme.colors.primary,
                modifier = Modifier.size(48.dp),
            )
        }
        Text(
            text = stringResource(Res.string.paywall_cp_title),
            style = AppTheme.typography.h3,
            color = AppTheme.colors.text.primary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(Res.string.paywall_cp_subtitle),
            style = AppTheme.typography.bodyLarge,
            color = AppTheme.colors.text.secondary,
            textAlign = TextAlign.Center,
        )
    }
}

// ── Current Credit Balance ───────────────────────────────────────────────────

@Composable
private fun CurrentCreditBalanceCard(creditBalance: Int) {
    AppCardContainer(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AppTheme.colors.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(UiRes.drawable.ic_coin_credits),
                    contentDescription = null,
                    tint = AppTheme.colors.primary,
                    modifier = Modifier.size(24.dp),
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = stringResource(Res.string.credit_available),
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.text.secondary,
                )
                Text(
                    text = if (creditBalance == 1) {
                        stringResource(Res.string.paywall_cp_credits_count_one)
                    } else {
                        stringResource(Res.string.paywall_cp_credits_count, creditBalance)
                    },
                    style = AppTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    color = if (creditBalance > 0) AppTheme.colors.text.primary else AppTheme.colors.status.error,
                )
            }
        }
    }
}

// ── Pack list ────────────────────────────────────────────────────────────────

@Composable
private fun PackList(
    packs: List<PaywallPackageUiState>,
    onSelect: (PurchasePackageId) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.groupedVerticalElementSpacing)) {
        Text(
            text = stringResource(Res.string.paywall_cp_section_title),
            style = AppTheme.typography.h6,
            color = AppTheme.colors.text.primary,
            fontWeight = FontWeight.Bold,
        )
        packs.forEach { pkg ->
            PackRow(
                pkg = pkg,
                onTap = { onSelect(pkg.id) },
            )
        }
    }
}

@Composable
private fun PackRow(
    pkg: PaywallPackageUiState,
    onTap: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    val containerColor =
        if (pkg.isSelected) AppTheme.colors.primary.copy(alpha = 0.12f) else AppTheme.colors.surfaceContainer
    val borderColor = when {
        pkg.isSelected -> AppTheme.colors.primary
        else -> AppTheme.colors.outline
    }
    val borderWidth = if (pkg.isSelected) 2.dp else 1.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(containerColor)
            .border(width = borderWidth, color = borderColor, shape = shape)
            .clickable { onTap() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AppTheme.colors.primary.copy(alpha = if (pkg.isSelected) 0.25f else 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(UiRes.drawable.ic_sparkles),
                contentDescription = null,
                tint = AppTheme.colors.primary,
                modifier = Modifier.size(18.dp),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = pkg.title.value,
                    style = AppTheme.typography.h6,
                    color = AppTheme.colors.text.primary,
                    fontWeight = FontWeight.Bold,
                )
                if (pkg.savingsBadge != null) {
                    BestValuePill(text = pkg.savingsBadge.value)
                }
            }
            if (pkg.subtitle.isNotEmpty()) {
                Text(
                    text = pkg.subtitle.value,
                    style = AppTheme.typography.bodySmall,
                    color = AppTheme.colors.text.secondary,
                )
            }
        }

        Text(
            text = pkg.priceText.value,
            style = AppTheme.typography.h6,
            color = if (pkg.isSelected) AppTheme.colors.primary else AppTheme.colors.text.primary,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun BestValuePill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF22C55E))
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(
            text = text,
            style = AppTheme.typography.bodyExtraSmall,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
        )
    }
}

// ── Sticky footer ────────────────────────────────────────────────────────────

@Composable
private fun StickyBuyFooter(
    buyButtonEnabled: Boolean,
    ctaText: String,
    aboveCtaText: String,
    onBuy: () -> Unit,
    onDecline: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = aboveCtaText,
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.text.primary,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(AppTheme.spacing.groupedVerticalElementSpacing))
        AppButton(
            text = ctaText,
            style = ButtonStyle.PRIMARY,
            enabled = buyButtonEnabled,
            onClick = onBuy,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(AppTheme.spacing.groupedVerticalElementSpacing))
        // Credits are optional, not a wall: give the same exit as the close icon somewhere
        // people will actually look for it, in grey so it does not compete with the CTA.
        AppButton(
            text = stringResource(Res.string.btn_no_thanks),
            style = ButtonStyle.SECONDARY,
            onClick = onDecline,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(AppTheme.spacing.defaultSpacing))
        FooterLinksRow()
    }
}

@Composable
internal fun FooterLinksRow() {
    val uriHandler = LocalUriHandler.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FooterLink(
            text = stringResource(Res.string.paywall_footer_privacy),
            onClick = {
                if (AppConfiguration.URL_PRIVACY_POLICY.isNotEmpty()) uriHandler.openUri(AppConfiguration.URL_PRIVACY_POLICY)
            },
        )
        Text(
            text = "·",
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.text.secondary,
            modifier = Modifier.padding(horizontal = 2.dp),
        )
        FooterLink(
            text = stringResource(Res.string.paywall_footer_terms),
            onClick = {
                if (AppConfiguration.URL_TERMS_CONDITIONS.isNotEmpty()) uriHandler.openUri(AppConfiguration.URL_TERMS_CONDITIONS)
            },
        )
    }
}

@Composable
private fun FooterLink(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = AppTheme.typography.bodySmall,
        color = AppTheme.colors.text.secondary,
        fontWeight = FontWeight.Medium,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 4.dp),
    )
}

// ── Previews ────────────────────────────────────────────────────────────────

@Preview
@StoreScreenshot(locale = "en", tag = "paywall_review_screenshot_credits")
@Composable
private fun CreditPackPaywallStoreScreenshot_iPhone_en() {
    AppTheme {
        CreditPackPaywallScreen(uiState = PaywallPreviewData.creditPackState(), onUiEvent = {})
    }
}

@Preview
@Composable
private fun CreditPackPaywallScreenPreview() {
    AppTheme {
        CreditPackPaywallScreen(
            uiState = PaywallPreviewData.creditPackState(),
            onUiEvent = {},
            onDismiss = {},
        )
    }
}
