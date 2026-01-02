package com.markoala.tomoandroid.ui.main.components

import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues
import com.markoala.tomoandroid.ui.main.BottomTab
import com.markoala.tomoandroid.ui.theme.CustomColor



@Composable
fun ChromeScaffold(
    showChrome: Boolean,
    currentTab: BottomTab?,
    name: String,
    onProfileClick: () -> Unit,
    onTabSelected: (BottomTab) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CustomColor.background,
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
        topBar = {
            if (showChrome) {
                MainHeader(
                    subtitle = if (name.isNotBlank())
                        "${name}님, 토모와 함께해요"
                    else "친구와의 순간을 기록해요",
                    onProfileClick = onProfileClick
                )
            }
        },
        bottomBar = {
            if (showChrome) {
                BottomNavigationBar(
                    selectedTab = currentTab ?: BottomTab.Home,
                    onTabSelected = onTabSelected
                )
            }
        }
    ) { padding ->
        content(padding)
    }
}
