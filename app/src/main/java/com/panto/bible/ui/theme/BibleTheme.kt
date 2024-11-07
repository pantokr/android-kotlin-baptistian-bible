package com.panto.bible.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    bodyLarge = TextStyle(
        fontSize = 16.sp,  // 기본 폰트 크기
        lineHeight = 24.sp,  // 줄 간격
        letterSpacing = 0.5.sp,  // 문자 간격
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.4.sp,
        fontWeight = FontWeight.Normal
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.3.sp,
        fontWeight = FontWeight.Normal
    ),
    titleLarge = TextStyle(
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.Bold
    ),
    titleMedium = TextStyle(
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.Bold
    ),
    titleSmall = TextStyle(
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.Bold
    )
)

val DarkColors = darkColorScheme(
    primary = Color(0xFF3B2A16),
    onPrimary = Color(0xFFEAE2D5),
    secondary = Color(0xFF6F4C3E),
    onSecondary = Color(0xFFEAE2D5),
    background = Color(0xFF1F1F1F),
    onBackground = Color(0xFFEAE2D5),
    surface = Color(0xFF2D2D2D),
    onSurface = Color(0xFFEAE2D5)
)


val LightColors = lightColorScheme(
    primary = Color(0xFFD9CBAE),
    onPrimary = Color(0xFF1F1F1F),
    secondary = Color(0xFFEAE2D5),
    onSecondary = Color(0xFF1F1F1F),
    background = Color(0xFFF7F2E7),
    onBackground = Color(0xFF1F1F1F),
    surface = Color(0xFFE5DCC5),
    onSurface = Color(0xFF1F1F1F)
)


@Composable
fun BibleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        content = content,
        typography = Typography,
    )
}
