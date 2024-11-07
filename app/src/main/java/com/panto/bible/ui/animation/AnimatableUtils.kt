package com.panto.bible.ui.animation

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

@Composable
fun animatePageAlpha(target: Any, startOpacity: Float): Animatable<Float, *> {
    val alpha = remember { Animatable(1f) }
    LaunchedEffect(target) {
        alpha.snapTo(startOpacity)
        alpha.animateTo(1 - startOpacity, animationSpec = tween(durationMillis = 2000))
    }
    return alpha
}

@Composable
fun animatePageOffsetY(target: Any, startOffset: Float): Animatable<Float, *> {
    val pageOffsetY = remember { Animatable(startOffset) }

    LaunchedEffect(target) {
        pageOffsetY.snapTo(startOffset)
        pageOffsetY.animateTo(0f, animationSpec = tween(durationMillis = 1000))
    }
    return pageOffsetY
}

@Composable
fun animateMenuAlpha(target: Boolean, startOpacity: Float): Animatable<Float, *> {
    val alpha = remember { Animatable(1f) }
    LaunchedEffect(target) {
        if (target) {
            alpha.snapTo(startOpacity)
            alpha.animateTo(1 - startOpacity, animationSpec = tween(durationMillis = 500))
        } else {
            alpha.animateTo(startOpacity, animationSpec = tween(durationMillis = 500))
        }
    }
    return alpha
}

@Composable
fun animateMenuOffsetY(target: Boolean, startOffset: Float): Animatable<Float, *> {
    val menuOffsetY = remember { Animatable(startOffset) }

    LaunchedEffect(target) {
        if (target) {
            menuOffsetY.snapTo(startOffset)
            menuOffsetY.animateTo(0f, animationSpec = tween(durationMillis = 1000))
        } else {
            menuOffsetY.animateTo(startOffset, animationSpec = tween(durationMillis = 1000))
        }
    }
    return menuOffsetY
}

@Composable
fun animateHighLightVerse(target: Boolean): Animatable<Color, AnimationVector4D> {
    val primaryColor = MaterialTheme.colorScheme.primary
    val color = remember { Animatable(Color.Transparent) }

    LaunchedEffect(Unit) {
        if (target) {
            color.snapTo(primaryColor)
            color.animateTo(
                targetValue = Color.Transparent,
                animationSpec = tween(durationMillis = 2000)
            )
        }
    }
    return color
}