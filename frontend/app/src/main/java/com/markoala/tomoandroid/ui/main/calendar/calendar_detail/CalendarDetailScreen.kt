package com.markoala.tomoandroid.ui.main.calendar.calendar_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.theme.CustomColor

@Composable
fun CalendarDetailScreen(
    eventId: Int,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CustomText(
            text = "캘린더 상세",
            type = CustomTextType.headline,
            color = CustomColor.textPrimary
        )

        CustomText(
            text = "이벤트 ID: $eventId 상세 페이지",
            type = CustomTextType.body,
            color = CustomColor.textSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomButton(
            text = "뒤로가기",
            style = ButtonStyle.Secondary,
            onClick = onBackClick
        )
    }
}
