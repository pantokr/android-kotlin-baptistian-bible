package com.panto.bible.ui.theme

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
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = TextStyle(
        fontSize = 16.sp,
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
        fontSize = 16.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.Bold
    )
)

val DarkColors = darkColorScheme(
    primary = Color(0xFF484848),
    onPrimary = Color(0xFFEAEAEA),
    secondary = Color(0xFF606060),
    onSecondary = Color(0xFFEAEAEA),
    tertiary = Color(0xFF606060),
    onTertiary = Color(0xFFEAEAEA),
    background = Color(0xFF181818),
    onBackground = Color(0xFFEAEAEA),
    surface = Color(0xFF303030),
    onSurface = Color(0xFFEAEAEA)
)


val LightColors = lightColorScheme(
    primary = Color(0xFFFCD0A1),
    onPrimary = Color(0xFF1F1F1F),
    secondary = Color(0xFFAFD2E9),
    onSecondary = Color(0xFF1F1F1F),
    tertiary = Color(0xFFA690A4),
    onTertiary = Color(0xFF1F1F1F),
    background = Color(0xFFFCFCFC),
    onBackground = Color(0xFF1F1F1F),
    surface = Color(0xFFFCFCFC),
    onSurface = Color(0xFF1F1F1F)
)


@Composable
fun BibleTheme(
    themeMode: Int,
    content: @Composable () -> Unit
) {
    val isDarkMode = when (themeMode) {
        0 -> false // Light
        1 -> true  // Dark
        else -> LocalDarkTheme.current // System
    }

    val colors = if (isDarkMode) {
        DarkColors
    } else {
        LightColors
    }

    MaterialTheme(
        colorScheme = colors,
        content = content,
        typography = Typography,
    )
}
