package com.rywent.pixelhabit.presentation.screens.habits.subScreens

import androidx.compose.animation.*
import androidx.compose.animation.core.*

fun subTabTransitionSpec(forward: Boolean): ContentTransform {
    val duration = 450
    val easing = FastOutSlowInEasing

    val enterSlide = if (forward) {
        slideInHorizontally(
            initialOffsetX = { (it * 0.75f).toInt() },
            animationSpec = tween(duration, easing = easing)
        )
    } else {
        slideInHorizontally(
            initialOffsetX = { -(it * 0.75f).toInt() },
            animationSpec = tween(duration, easing = easing)
        )
    }

    val exitSlide = if (forward) {
        slideOutHorizontally(
            targetOffsetX = { -(it * 0.75f).toInt() },
            animationSpec = tween(duration, easing = easing)
        )
    } else {
        slideOutHorizontally(
            targetOffsetX = { (it * 0.75f).toInt() },
            animationSpec = tween(duration, easing = easing)
        )
    }

    return enterSlide + fadeIn(tween(duration / 2, easing = easing)) togetherWith
            exitSlide + fadeOut(tween(duration / 2, easing = easing))
}