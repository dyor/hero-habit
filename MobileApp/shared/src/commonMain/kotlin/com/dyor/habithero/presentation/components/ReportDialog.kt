package com.dyor.habithero.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.dyor.habithero.designsystem.components.UserInput
import com.dyor.habithero.designsystem.components.modals.AppDialog
import com.dyor.habithero.designsystem.components.modals.DialogType
import com.dyor.habithero.generated.resources.Res
import com.dyor.habithero.generated.resources.ai_content_report_btn_dialog_confirm
import com.dyor.habithero.generated.resources.ai_content_report_btn_dialog_dismiss
import com.dyor.habithero.generated.resources.ai_content_report_dialog_input_label
import com.dyor.habithero.generated.resources.ai_content_report_dialog_message
import com.dyor.habithero.generated.resources.ai_content_report_dialog_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReportDialog(
    onDismiss: () -> Unit,
    onSubmitReport: (String) -> Unit,
) {
    var reportText by rememberSaveable { mutableStateOf("") }

    AppDialog(
        title = stringResource(Res.string.ai_content_report_dialog_title),
        text = stringResource(Res.string.ai_content_report_dialog_message),
        type = DialogType.ERROR,
        content = {
            UserInput(
                label = stringResource(Res.string.ai_content_report_dialog_input_label),
                value = reportText,
                onValueChange = { reportText = it },
            )
        },
        image = {},
        btnConfirmText = stringResource(Res.string.ai_content_report_btn_dialog_confirm),
        btnDismissText = stringResource(Res.string.ai_content_report_btn_dialog_dismiss),
        onConfirm = { onSubmitReport(reportText) },
        onDismiss = onDismiss,
    )
}
