package com.markoala.tomoandroid.ui.main.meeting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.markoala.tomoandroid.ui.components.ButtonStyle
import com.markoala.tomoandroid.ui.components.CustomButton
import com.markoala.tomoandroid.ui.components.CustomText
import com.markoala.tomoandroid.ui.components.CustomTextType
import com.markoala.tomoandroid.ui.components.LoadingDialog
import com.markoala.tomoandroid.ui.components.MorphingDots
import com.markoala.tomoandroid.ui.main.meeting.components.GreetingCard
import com.markoala.tomoandroid.ui.main.meeting.components.MeetingCard
import com.markoala.tomoandroid.ui.main.meeting.components.MeetingListContent
import com.markoala.tomoandroid.ui.theme.CustomColor
import kotlinx.coroutines.delay

@Composable
fun MeetingScreen(
    paddingValues: PaddingValues,
    userName: String,
    onPlanMeetingClick: () -> Unit,
    onMeetingClick: (Int) -> Unit = {},
    meetingViewModel: MeetingViewModel = viewModel()
) {
    val meetings by meetingViewModel.meetings.collectAsState()
    val isLoading by meetingViewModel.isLoading.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Lifecycle: 화면 복귀 시 데이터 다시 불러오기
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                meetingViewModel.fetchMeetings()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // 🔥 로딩 다이얼로그 표시
    if (isLoading) {
        MorphingDots()
    }

    MeetingListContent(
        paddingValues = paddingValues,
        userName = userName,
        meetings = meetings,
        onPlanMeetingClick = onPlanMeetingClick,
        onMeetingClick = onMeetingClick
    )
}
