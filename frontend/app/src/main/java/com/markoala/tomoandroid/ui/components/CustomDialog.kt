package com.markoala.tomoandroid.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.markoala.tomoandroid.ui.theme.CustomColor

/**
 * 프로젝트 전체에서 사용하는 공용 다이얼로그
 * 일관된 디자인과 사용성을 제공합니다.
 */
@Composable
fun CustomDialog(
    title: String,
    message: String,
    confirmText: String = "확인",
    dismissText: String = "취소",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDangerous: Boolean = false,
    showDismissButton: Boolean = true,
    isLoading: Boolean = false,
    properties: DialogProperties = DialogProperties()
) {
    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        properties = properties,
        containerColor = CustomColor.surface,
        shape = RoundedCornerShape(28.dp),
        title = {
            CustomText(
                text = title,
                type = CustomTextType.title,
                color = if (isDangerous) CustomColor.danger else CustomColor.textPrimary
            )
        },
        text = {
            CustomText(
                text = message,
                type = CustomTextType.body,
                color = CustomColor.textSecondary
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (showDismissButton && !isLoading) {
                    CustomButton(
                        text = dismissText,
                        onClick = onDismiss,
                        style = ButtonStyle.Secondary,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                CustomButton(
                    text = if (isLoading) "처리 중..." else confirmText,
                    onClick = { if (!isLoading) onConfirm() },
                    style = if (isDangerous) ButtonStyle.Danger else ButtonStyle.Primary,
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    )
}

/**
 * 위험한 작업(삭제 등)을 위한 특화 다이얼로그
 */
@Composable
fun DangerDialog(
    title: String,
    message: String,
    confirmText: String = "삭제",
    dismissText: String = "취소",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean = false
) {
    CustomDialog(
        title = title,
        message = message,
        confirmText = confirmText,
        dismissText = dismissText,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        isDangerous = true,
        isLoading = isLoading
    )
}

/**
 * 정보 확인용 다이얼로그 (확인 버튼만 표시)
 */
@Composable
fun InfoDialog(
    title: String,
    message: String,
    confirmText: String = "확인",
    onConfirm: () -> Unit
) {
    CustomDialog(
        title = title,
        message = message,
        confirmText = confirmText,
        dismissText = "",
        onConfirm = onConfirm,
        onDismiss = onConfirm,
        showDismissButton = false
    )
}
