package com.rywent.pixelhabit.presentation.screens.habits.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun HabitsTabSwitcher(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        contentPadding = PaddingValues(horizontal = 0.dp)
    ) {
        items(tabs.size) { index ->
            val isSelected = index == selectedIndex
            val scope = rememberCoroutineScope()
            val pressScaleX = remember { Animatable(1f) }

            Surface(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .scale(scaleX = pressScaleX.value, scaleY = 1f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        scope.launch {
                            pressScaleX.animateTo(
                                targetValue = 1.08f,
                                animationSpec = tween(
                                    durationMillis = 120,
                                    easing = FastOutSlowInEasing
                                )
                            )
                            pressScaleX.animateTo(
                                targetValue = 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                        onTabSelected(index)
                    },
                shape = RoundedCornerShape(50.dp),
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = if (isSelected) 6.dp else 0.dp
            ) {
                Text(
                    text = tabs[index],
                    modifier = Modifier.padding(horizontal = 26.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.surfaceContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}