package com.panto.bible.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.panto.bible.ui.theme.LocalDarkTheme

@Composable
fun ThemedImage(iconResLight: Int, iconResDark: Int, modifier: Modifier = Modifier) {
    val isDarkTheme = LocalDarkTheme.current
    val icon = if (isDarkTheme) iconResDark else iconResLight

    Image(
        painter = painterResource(id = icon),
        contentDescription = "Themed Icon",
        modifier = modifier,
    )
}

@Composable
fun ThemedVectorIconButton(
    iconRes: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val icon = painterResource(id = iconRes)

    Box(modifier = modifier.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) { onClick() }) {
        Icon(
            painter = icon,
            contentDescription = "Themed Icon",
            modifier = Modifier.padding(12.dp),
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}