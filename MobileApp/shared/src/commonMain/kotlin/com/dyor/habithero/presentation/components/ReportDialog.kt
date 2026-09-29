package com.dyor.habithero.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dyor.habithero.data.repository.ContentReportRepository
import com.dyor.habithero.designsystem.components.UserInput
import com.dyor.habithero.designsystem.components.modals.AppDialog
import com.dyor.habithero.designsystem.components.modals.DialogType
import com.dyor.habithero.domain.model.ComicCover
import com.dyor.habithero.generated.resources.Res
import com.dyor.habithero.generated.resources.ai_content_report_btn_dialog_confirm
import com.dyor.habithero.generated.resources.ai_content_report_btn_dialog_dismiss
import com.dyor.habithero.generated.resources.ai_content_report_btn_open_long
import com.dyor.habithero.generated.resources.ai_content_report_dialog_input_label
import com.dyor.habithero.generated.resources.ai_content_report_dialog_message
import com.dyor.habithero.generated.resources.ai_content_report_dialog_title
import com.dyor.habithero.generated.resources.ai_content_report_reason_offensive
import com.dyor.habithero.generated.resources.ai_content_report_reason_other
import com.dyor.habithero.generated.resources.ai_content_report_reason_sexual
import com.dyor.habithero.generated.resources.ai_content_report_reason_violent
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/**
 * Lets the user flag AI-generated content as offensive without leaving the app
 * (Google Play AI-Generated Content policy). [onSubmitReport] receives the chosen reason plus any
 * optional details, e.g. "Violent or disturbing: looks gory".
 */
@Composable
fun ReportDialog(
    onDismiss: () -> Unit,
    onSubmitReport: (String) -> Unit,
) {
    val reasons = listOf(
        stringResource(Res.string.ai_content_report_reason_offensive),
        stringResource(Res.string.ai_content_report_reason_sexual),
        stringResource(Res.string.ai_content_report_reason_violent),
        stringResource(Res.string.ai_content_report_reason_other),
    )
    var selectedReason by rememberSaveable { mutableStateOf<String?>(null) }
    var reportText by rememberSaveable { mutableStateOf("") }

    AppDialog(
        title = stringResource(Res.string.ai_content_report_dialog_title),
        text = stringResource(Res.string.ai_content_report_dialog_message),
        type = DialogType.ERROR,
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                reasons.forEach { reason ->
                    FilterChip(
                        modifier = Modifier.fillMaxWidth(),
                        selected = selectedReason == reason,
                        onClick = { selectedReason = reason },
                        label = { Text(reason) },
                    )
                }
                UserInput(
                    label = stringResource(Res.string.ai_content_report_dialog_input_label),
                    value = reportText,
                    onValueChange = { reportText = it },
                )
            }
        },
        image = {},
        btnConfirmText = stringResource(Res.string.ai_content_report_btn_dialog_confirm),
        btnDismissText = stringResource(Res.string.ai_content_report_btn_dialog_dismiss),
        onConfirm = {
            val details = reportText.trim()
            val reason = listOfNotNull(selectedReason, details.ifBlank { null }).joinToString(": ")
            onSubmitReport(reason)
        },
        onDismiss = onDismiss,
    )
}

/**
 * Hosts the [ReportDialog] for comic covers. [content] receives a `requestReport(cover)` callback
 * to wire to a "Report" button; the dialog opens for that cover and submits via [onSubmitReport].
 */
@Composable
fun ReportComicCoverHost(
    onSubmitReport: (cover: ComicCover, reason: String) -> Unit,
    content: @Composable (requestReport: (ComicCover) -> Unit) -> Unit,
) {
    var coverToReport by remember { mutableStateOf<ComicCover?>(null) }

    content { cover -> coverToReport = cover }

    coverToReport?.let { cover ->
        ReportDialog(
            onDismiss = { coverToReport = null },
            onSubmitReport = { reason ->
                onSubmitReport(cover, reason)
                coverToReport = null
            },
        )
    }
}

/**
 * Self-contained "⚑ Report this AI cover" link: opens [ReportDialog] and files the report via
 * [ContentReportRepository]. Needs Koin — for previewable screens use [ReportComicCoverHost] and
 * pass the submit callback down instead.
 */
@Composable
fun ReportComicCoverButton(
    cover: ComicCover,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFFF8A80),
    contentReportRepository: ContentReportRepository = koinInject(),
) {
    ReportComicCoverHost(onSubmitReport = contentReportRepository::reportComicCover) { requestReport ->
        ReportComicCoverLink(
            modifier = modifier,
            color = color,
            onClick = { requestReport(cover) },
        )
    }
}

@Composable
fun ReportComicCoverLink(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFFF8A80),
) {
    Text(
        text = stringResource(Res.string.ai_content_report_btn_open_long),
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 4.dp),
    )
}
