package com.rywent.pixelhabit.presentation.screens.home.components

import android.graphics.Color
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
import androidx.compose.ui.graphics.Color as ComposeColor

@Composable
fun WeekStatistics(
    data: List<DayStat>,
    onWeekClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        )
    }

    val gradientBrush = Brush.linearGradient(
        start = Offset(Float.POSITIVE_INFINITY, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY),
        colors = listOf(
            MaterialTheme.colorScheme.surfaceContainerHighest,
            MaterialTheme.colorScheme.surfaceContainer,
            MaterialTheme.colorScheme.surfaceContainerLowest
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .clip(RoundedCornerShape(16.dp))
            .background(gradientBrush)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isDark) {
                        MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.9f)
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.9f)
                    }
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Week statistics",
                style = MaterialTheme.typography.titleMedium,
                color = if (isDark) {
                    ComposeColor.White.copy(alpha = 0.95f)
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isDark) {
                            ComposeColor.White.copy(alpha = 0.15f)
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        }
                    )
                    .clickable(onClick = onWeekClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowForwardIos,
                    contentDescription = "Open week details",
                    tint = if (isDark) {
                        ComposeColor.White.copy(alpha = 0.9f)
                    } else {
                        onSurfaceVariant
                    },
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
                val maxValue = data.maxOfOrNull { it.value } ?: 1

                data.forEach { day ->
                    val targetHeight = if (maxValue > 0) {
                        (day.value.toFloat() / maxValue * 160).dp
                    } else {
                        8.dp
                    }

                    val animatedHeight = targetHeight * animatedProgress.value

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
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            primaryColor,
                                            primaryColor.copy(alpha = 0.6f)
                                        )
                                    )
                                )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = day.shortName,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isDark) {
                                ComposeColor.White.copy(alpha = 0.7f)
                            } else {
                                onSurfaceVariant
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


data class DayStat(
    val shortName: String,
    val value: Int
)