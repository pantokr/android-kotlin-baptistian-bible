package com.panto.bible.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
fun ThemedIcon(iconResLight: Int, iconResDark: Int, modifier: Modifier = Modifier) {
    val isDarkTheme = isSystemInDarkTheme()
    val icon = if (isDarkTheme) {
        painterResource(id = iconResDark)
    } else {
        painterResource(id = iconResLight)
    }

    Image(
        painter = icon,
        contentDescription = "Themed Icon",
        modifier = modifier
    )
}

@Composable
fun ThemedIconButton(
    iconResLight: Int,
    iconResDark: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val icon = if (isDarkTheme) {
        painterResource(id = iconResDark)
    } else {
        painterResource(id = iconResLight)
    }

    IconButton(onClick = onClick, modifier = modifier) {
        Icon(painter = icon, contentDescription = "Themed Icon")
    }
}