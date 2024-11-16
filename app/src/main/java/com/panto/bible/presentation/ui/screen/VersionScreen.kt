package com.panto.bible.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.panto.bible.R
import com.panto.bible.data.local.BibleConstant.VERSION_LIST_KOR
import com.panto.bible.presentation.ui.viewmodel.MainViewModel
import com.panto.bible.presentation.ui.viewmodel.SettingsViewModel
import com.panto.bible.ui.ThemedIconButton

@Composable
fun VersionScreen(
    mainViewModel: MainViewModel,
    settingsViewModel: SettingsViewModel,
    navController: NavHostController
) {
    val currentVersion by mainViewModel.currentVersion.collectAsState() // 현재 버전
    val currentSubVersion by mainViewModel.currentSubVersion.collectAsState() // 현재 대역 버전

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            VersionAppBar(onBackClick = {
                navController.navigate("VerseScreen") {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            })
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.background),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "기본", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(
                    modifier = Modifier.width(2.dp)
                )

                Box(
                    modifier = Modifier.weight(1f), contentAlignment = Alignment.Center
                ) {
                    Text(text = "비교본", style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            HorizontalDivider(
                modifier = Modifier
                    .height(2.dp)
                    .background(Color.Gray.copy(alpha = 0.5f)),
            )

            Row(
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.primary.copy(
                        0.25f
                    )
                ),
                horizontalArrangement = Arrangement.spacedBy(4.dp)

            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(
                                0.25f
                            )
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    VERSION_LIST_KOR.forEachIndexed { vIndex, version ->
                        VersionItem(
                            isCurrent = currentVersion == vIndex,
                            label = version,
                            onClick = {
                                mainViewModel.updateVersion(vIndex)
                            })
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(
                                0.25f
                            )
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    VersionItem(isCurrent = currentSubVersion == -1,
                        label = "없음",
                        onClick = { mainViewModel.updateSubVersion(-1) })
                    VERSION_LIST_KOR.forEachIndexed { vIndex, version ->
                        if (vIndex != currentVersion) {
                            VersionItem(isCurrent = currentSubVersion == vIndex,
                                label = version,
                                onClick = { mainViewModel.updateSubVersion(vIndex) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VersionAppBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp), contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            ThemedIconButton(iconResLight = R.drawable.back_light,
                iconResDark = R.drawable.back_dark,
                modifier = Modifier.size(48.dp),
                onClick = { onBackClick() })
        }
        Box(modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = "번역본 선택", style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun VersionItem(isCurrent: Boolean, label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                color = if (isCurrent) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = if (isCurrent) RoundedCornerShape(12.dp) else RectangleShape
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) { onClick() }, contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}