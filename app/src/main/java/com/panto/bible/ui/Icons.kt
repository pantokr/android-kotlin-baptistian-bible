package com.panto.bible.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

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
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val isDarkTheme = isSystemInDarkTheme()
    val icon = if (isDarkTheme) {
        painterResource(id = iconResDark)
    } else {
        painterResource(id = iconResLight)
    }

    Box(modifier = modifier.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) { onClick() }) {
        Icon(painter = icon, contentDescription = "Themed Icon", modifier = Modifier.padding(12.dp))
    }
}