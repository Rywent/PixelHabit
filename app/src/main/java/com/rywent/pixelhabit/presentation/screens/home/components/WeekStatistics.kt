package com.rywent.pixelhabit.presentation.screens.home.components

import androidx.compose.ui.graphics.Color
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rywent.pixelhabit.ui.theme.adaptiveShadowColor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
fun WeekStatistics(
    data: List<DayStat>,
    onWeekClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme

    val animatedHeights = remember {
        List(7) { Animatable(0f) }
    }

    // first
    LaunchedEffect(Unit) {
        val maxValue = data.filter { it.value >= 0 }.maxOfOrNull { it.value }?.toFloat() ?: 0f

        coroutineScope {
            data.forEachIndexed { index, day ->
                launch {
                    val targetValue = calculateTargetHeight(day, maxValue)

                    animatedHeights[index].snapTo(0f)
                    animatedHeights[index].animateTo(
                        targetValue = targetValue,
                        animationSpec = tween(800, easing = FastOutSlowInEasing)
                    )
                }
            }
        }
    }

    // update
    LaunchedEffect(data) {
        val maxValue = data.filter { it.value >= 0 }.maxOfOrNull { it.value }?.toFloat() ?: 0f

        coroutineScope {
            data.forEachIndexed { index, day ->
                launch {
                    val targetValue = calculateTargetHeight(day, maxValue)

                    animatedHeights[index].animateTo(
                        targetValue = targetValue,
                        animationSpec = tween(600, easing = FastOutSlowInEasing)
                    )
                }
            }
        }
    }

    val gradientBrush = Brush.linearGradient(
        start = Offset(Float.POSITIVE_INFINITY, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY),
        colors = listOf(
            scheme.surfaceContainerHighest,
            scheme.surfaceContainerHigh.copy(alpha = 0.8f),
            scheme.surfaceContainer
        )
    )

    val shadowColor = adaptiveShadowColor()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false,
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .background(gradientBrush)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(scheme.surfaceContainerLow)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Week statistics",
                style = MaterialTheme.typography.titleMedium,
                color = scheme.onSurface
            )

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(scheme.primaryContainer.copy(alpha = 0.3f))
                    .clickable(onClick = onWeekClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = "Open week details",
                    tint = scheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                data.forEachIndexed { index, day ->
                    val isFutureDay = day.value < 0

                    val animatedHeight = animatedHeights[index].value.dp

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .height(animatedHeight)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (isFutureDay) {
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                scheme.onSurface.copy(alpha = 0.1f),
                                                scheme.onSurface.copy(alpha = 0.05f)
                                            )
                                        )
                                    } else {
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                scheme.primary,
                                                scheme.primary.copy(alpha = 0.4f)
                                            )
                                        )
                                    }
                                )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = day.shortName,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isFutureDay) {
                                scheme.onSurfaceVariant.copy(alpha = 0.4f)
                            } else {
                                scheme.onSurfaceVariant
                            },
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

fun ComposeColor.luminance(): Float {
    val r = red
    val g = green
    val b = blue
    return 0.299f * r + 0.587f * g + 0.114f * b
}

private fun calculateTargetHeight(day: DayStat, maxValue: Float): Float {
    val MIN_HEIGHT = 8f
    val MAX_HEIGHT = 160f

    return when {
        day.value < 0 -> MIN_HEIGHT
        maxValue == 0f -> MIN_HEIGHT
        day.value == 0 -> MIN_HEIGHT
        else -> (day.value.toFloat() / maxValue) * MAX_HEIGHT
    }
}

data class DayStat(
    val shortName: String,
    val value: Int
)