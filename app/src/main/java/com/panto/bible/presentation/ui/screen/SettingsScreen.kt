package com.panto.bible.presentation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.panto.bible.R
import com.panto.bible.presentation.ui.viewmodel.SettingsViewModel
import com.panto.bible.ui.ThemedIconButton

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    navController: NavHostController
) {

    val fontSize by settingsViewModel.fontSize.collectAsState() // 글자 크기
    val paragraphSpacing by settingsViewModel.paragraphSpacing.collectAsState() // 문단 간격
    val themeMode by settingsViewModel.themeMode.collectAsState() // 테마 모드

    var exFontSize by remember { mutableFloatStateOf(fontSize) }
    var exParagraphSpacing by remember { mutableFloatStateOf(fontSize) }

    BackHandler {
        navController.navigate("VerseScreen") {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            SettingsAppBar(onBackClick = {
                navController.navigate("VerseScreen") {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            })
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("글자 크기")
                Box(modifier = Modifier.width(160.dp)) {
                    SettingsSlider(value = fontSize,
                        valueRange = 8f..24f,
                        onValueChange = { value ->
                            settingsViewModel.updateFontSize(value)
                            exFontSize = value
                        })
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("문단 간격")
                Box(modifier = Modifier.width(160.dp)) {
                    SettingsSlider(
                        value = paragraphSpacing,
                        valueRange = 0f..48f,
                        onValueChange = { value ->
                            settingsViewModel.updateParagraphSpacing(value)
                            exParagraphSpacing = value
                        })
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("테마 모드")

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    ThemeModeButton(label = "시스템",
                        isSelected = themeMode == -1,
                        onClick = { settingsViewModel.updateThemeMode(-1) })
                    ThemeModeButton(label = "라이트",
                        isSelected = themeMode == 0,
                        onClick = { settingsViewModel.updateThemeMode(0) })
                    ThemeModeButton(label = "다크",
                        isSelected = themeMode == 1,
                        onClick = { settingsViewModel.updateThemeMode(1) })
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp), contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(0.5f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(16.dp)

                ) {
                    Text(
                        text = "태초에 하나님이 천지를 창조하시니라", style = TextStyle(
                            fontSize = fontSize.sp,
                            lineHeight = paragraphSpacing.sp,
                            letterSpacing = 0.1f.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(height = paragraphSpacing.dp))

                    Text(
                        text = "땅이 혼돈하고 공허하며", style = TextStyle(
                            fontSize = fontSize.sp,
                            lineHeight = paragraphSpacing.sp,
                            letterSpacing = 0.1f.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsAppBar(onBackClick: () -> Unit) {
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
                text = "개인 설정", style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun SettingsSlider(
    value: Float, valueRange: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit
) {
    Slider(
        value = value,
        onValueChange = { v -> onValueChange(v) },
        valueRange = valueRange,
        steps = 10,
        colors = SliderDefaults.colors(
            activeTrackColor = MaterialTheme.colorScheme.onBackground,
            inactiveTrackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            activeTickColor = Color.Transparent,
            inactiveTickColor = Color.Transparent,
            thumbColor = MaterialTheme.colorScheme.onBackground,
        ),
        modifier = Modifier.padding(4.dp)
    )
}

@Composable
fun ThemeModeButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
            .padding(12.dp), contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            style = TextStyle(fontSize = 14.sp)
        )
    }
}