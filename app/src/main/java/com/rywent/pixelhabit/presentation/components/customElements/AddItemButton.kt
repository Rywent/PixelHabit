package com.rywent.pixelhabit.presentation.components.customElements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

@Composable
fun AddItemButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(110.dp)
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                clip = false
            )
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                    bounded = true
                ),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Add",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(46.dp)
        )
    }
}