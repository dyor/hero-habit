package com.dyor.habithero.data.repository

import com.dyor.habithero.domain.model.ComicCover
import com.dyor.habithero.generated.resources.Res
import com.dyor.habithero.generated.resources.ai_content_report_submitted_msg
import com.dyor.habithero.root.AppGlobalUiState
import com.dyor.habithero.util.UiMessage
import com.dyor.habithero.util.analytics.Analytics
import com.dyor.habithero.util.logging.AppLogger

/**
 * Records user reports of offensive / inappropriate AI-generated comic covers (Google Play
 * AI-Generated Content policy: users must be able to flag AI output without leaving the app).
 *
 * Each report is sent as the [Analytics.EVENT_REPORTED_AI_CONTENT] Firebase Analytics event
 * (review under Events in the Firebase console) and logged via [AppLogger].
 */
class ContentReportRepository(
    private val analytics: Analytics,
) {
    fun reportComicCover(cover: ComicCover, reason: String) {
        val trimmedReason = reason.trim().ifBlank { "unspecified" }
        analytics.logEvent(
            event = Analytics.EVENT_REPORTED_AI_CONTENT,
            params = mapOf(
                Analytics.PARAM_CONTENT_ID to cover.id,
                Analytics.PARAM_HERO_ROLE to cover.heroRole,
                // Firebase Analytics truncates string params at 100 chars.
                Analytics.PARAM_REPORT_REASON to trimmedReason.take(100),
            ),
        )
        AppLogger.i("Reported AI comic cover ${cover.id} (role=${cover.heroRole}). Reason: $trimmedReason")
        AppGlobalUiState.showUiMessage(UiMessage.Resource(Res.string.ai_content_report_submitted_msg))
    }
}
