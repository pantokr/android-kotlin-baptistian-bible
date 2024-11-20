package com.panto.bible.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalDarkTheme = compositionLocalOf { false }  // 기본값 false (라이트 테마)

@Composable
fun DarkThemeProvider(isDarkTheme: Boolean, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalDarkTheme provides isDarkTheme) {
        content()
    }
}