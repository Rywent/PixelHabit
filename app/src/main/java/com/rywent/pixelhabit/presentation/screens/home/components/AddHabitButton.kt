package com.rywent.pixelhabit.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rywent.pixelhabit.ui.theme.adaptiveStandoutShadowColor

@Composable
fun AddHabitButton(
    modifier: Modifier = Modifier,
    onAddHabit: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val shadowColor = adaptiveStandoutShadowColor()

    Box(
        modifier = modifier
            .size(110.dp)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                clip = false,
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .clip(CircleShape)
            .background(scheme.primary)
            .clickable(onClick = onAddHabit),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = null,
            tint = scheme.onPrimary,
            modifier = Modifier.size(42.dp)
        )
    }
}
