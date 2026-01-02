package com.markoala.tomoandroid.ui.main.meeting.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun MeetingListHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        CustomText(
            text = "모임 타임라인",
            type = CustomTextType.title,
            color = CustomColor.textPrimary
        )
        CustomText(
            text = "완료된 모임과 예정된 모임을 한눈에",
            type = CustomTextType.bodySmall,
            color = CustomColor.textSecondary
        )
    }
}
