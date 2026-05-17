package com.rywent.pixelhabit.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rywent.pixelhabit.presentation.screens.home.components.luminance


@Composable
fun adaptiveShadowColor(): Color {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    return if (isDark) {
        Color.Black.copy(alpha = 0.8f)
    } else {
        Color.Black.copy(alpha = 0.4f)
    }
}

// For standout elements
@Composable
fun adaptiveStandoutShadowColor(): Color {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    return if (isDark) {
        Color.Black.copy(alpha = 0.8f)
    } else {
        Color.Black.copy(alpha = 0.4f)
    }
}