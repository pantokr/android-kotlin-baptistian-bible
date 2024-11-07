package com.panto.bible.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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
        typography = Typography()
    )
}
