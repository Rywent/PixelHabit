package com.rywent.pixelhabit.presentation.components.habit

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rywent.pixelhabit.presentation.components.customElements.RoundedCheckbox
import com.rywent.pixelhabit.ui.theme.adaptiveShadowColor
import kotlin.math.roundToInt

@Composable
fun HabitTodayCard(
    modifier: Modifier = Modifier,
    name: String,
    description: String,
    streak: Int?,
    icon: ImageVector,
    isCompleted: Boolean,
    isPostponed: Boolean = false,
    onCheckedChange: (Boolean) -> Unit,
    onPostpone: (() -> Unit)? = null
) {
    // Color scheme
    val scheme = MaterialTheme.colorScheme
    val shadowColor = adaptiveShadowColor()
    val hapticFeedback = LocalHapticFeedback.current
    val hasReachedThreshold = remember { mutableStateOf(false) }

    // Swipe colors
    val leftSwipeColor = when {
        isCompleted -> scheme.errorContainer.copy(alpha = 0.4f)
        isPostponed -> scheme.primaryContainer.copy(alpha = 0.4f)
        else -> scheme.primaryContainer.copy(alpha = 0.4f)
    }

    val rightSwipeColor = scheme.tertiaryContainer.copy(alpha = 0.4f)

    // Swipe state
    var offsetX by remember { mutableFloatStateOf(0f) }
    val swipeThreshold = 150f

    // Animations
    val scaleY by animateFloatAsState(
        targetValue = when {
            isCompleted -> 0.95f
            isPostponed -> 0.97f
            else -> 1f
        },
        animationSpec = tween(durationMillis = 400)
    )

    val scaleX by animateFloatAsState(
        targetValue = when {
            isCompleted -> 0.97f
            isPostponed -> 0.98f
            else -> 1f
        },
        animationSpec = tween(durationMillis = 400)
    )

    val cardHeight by animateDpAsState(
        targetValue = when {
            isCompleted -> 93.dp
            isPostponed -> 96.dp
            else -> 100.dp
        },
        animationSpec = tween(durationMillis = 400)
    )

    val leftAlpha = (-offsetX / swipeThreshold).coerceIn(0f, 1f)
    val rightAlpha = (offsetX / swipeThreshold).coerceIn(0f, 1f)

    val isRightSwipeEnabled = !isCompleted && !isPostponed

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clip(RoundedCornerShape(30.dp))
    ) {
        // Left swipe background
        if (offsetX < -10f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(leftSwipeColor.copy(alpha = leftAlpha))
                    .padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = when {
                        isCompleted -> Icons.Rounded.Close
                        isPostponed -> Icons.AutoMirrored.Rounded.Undo
                        else -> Icons.Rounded.Check
                    },
                    contentDescription = null,
                    tint = when {
                        isCompleted -> scheme.error
                        isPostponed -> scheme.primary
                        else -> scheme.primary
                    },
                    modifier = Modifier.size((55 * leftAlpha).dp.coerceAtLeast(8.dp))
                )
            }
        }

        // Right swipe background
        if (offsetX > 10f && isRightSwipeEnabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(rightSwipeColor.copy(alpha = rightAlpha))
                    .padding(start = 24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    imageVector = Icons.Rounded.Schedule,
                    contentDescription = null,
                    tint = scheme.tertiary,
                    modifier = Modifier.size((55 * rightAlpha).dp.coerceAtLeast(8.dp))
                )
            }
        }

        // Main card
        Row(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .scale(scaleX = scaleX, scaleY = scaleY)
                .shadow(
                    elevation = when {
                        isPostponed -> 4.dp
                        isCompleted -> 2.dp
                        else -> 8.dp
                    },
                    shape = RoundedCornerShape(30.dp),
                    clip = false,
                    ambientColor = shadowColor,
                    spotColor = shadowColor
                )
                .clip(RoundedCornerShape(30.dp))
                .background(
                    Brush.linearGradient(
                        start = Offset(Float.POSITIVE_INFINITY, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY),
                        colors = when {
                            isPostponed -> listOf(
                                scheme.surfaceContainerHighest.copy(alpha = 0.6f),
                                scheme.surfaceContainerHigh.copy(alpha = 0.4f),
                                scheme.surfaceContainer.copy(alpha = 0.3f)
                            )
                            isCompleted -> listOf(
                                scheme.surfaceContainerHighest.copy(alpha = 0.4f),
                                scheme.surfaceContainerHigh.copy(alpha = 0.3f),
                                scheme.surfaceContainer.copy(alpha = 0.2f)
                            )
                            else -> listOf(
                                scheme.surfaceContainerHighest,
                                scheme.surfaceContainerHigh.copy(alpha = 0.8f),
                                scheme.surfaceContainer
                            )
                        }
                    )
                )
                .padding(start = 24.dp, top = 14.dp, end = 16.dp, bottom = 14.dp)
                .pointerInput(isCompleted, isPostponed, isRightSwipeEnabled) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            when {
                                offsetX <= -swipeThreshold -> {
                                    when {
                                        isCompleted -> onCheckedChange(false)
                                        isPostponed -> onCheckedChange(false)
                                        else -> onCheckedChange(true)
                                    }
                                }
                                offsetX >= swipeThreshold && isRightSwipeEnabled -> {
                                    onPostpone?.invoke()
                                }
                            }
                            offsetX = 0f
                            hasReachedThreshold.value = false
                        },
                        onDragCancel = {
                            offsetX = 0f
                            hasReachedThreshold.value = false
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            val maxRightOffset = if (isRightSwipeEnabled) {
                                swipeThreshold * 1.5f
                            } else {
                                0f
                            }

                            val newOffset = (offsetX + dragAmount).coerceIn(
                                -swipeThreshold * 1.5f,
                                maxRightOffset
                            )
                            offsetX = newOffset

                            val thresholdReached = newOffset <= -swipeThreshold ||
                                    (isRightSwipeEnabled && newOffset >= swipeThreshold)
                            if (thresholdReached && !hasReachedThreshold.value) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                hasReachedThreshold.value = true
                            }
                            if (!thresholdReached && hasReachedThreshold.value) {
                                hasReachedThreshold.value = false
                            }
                        }
                    )
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable {
                        onCheckedChange(!isCompleted)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                contentAlignment = Alignment.Center
            ) {
                RoundedCheckbox(
                    checked = isCompleted,
                    onCheckedChange = onCheckedChange,
                    size = 30.dp,
                    cornerRadius = 12.dp
                )
            }

            // Name and description
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 8.dp)
            ) {
                // Name
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isCompleted -> scheme.onSurface.copy(alpha = 0.4f)
                        isPostponed -> scheme.onSurface.copy(alpha = 0.5f)
                        else -> scheme.onSurface
                    },
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))

                // Description or Snoozed badge
                if (isPostponed) {
                    // Snoozed badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 1.dp)
                            .background(
                                scheme.tertiaryContainer.copy(alpha = 0.4f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Schedule,
                            contentDescription = null,
                            tint = scheme.onTertiaryContainer,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Snoozed",
                            style = MaterialTheme.typography.bodySmall,
                            color = scheme.onTertiaryContainer,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    // Description
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isCompleted) {
                            scheme.onSurfaceVariant.copy(alpha = 0.3f)
                        } else {
                            scheme.onSurfaceVariant
                        },
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Streak and icon
            Row(
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
            ) {
                // Streak
                if (streak != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocalFireDepartment,
                            contentDescription = null,
                            tint = when {
                                isCompleted -> scheme.tertiary.copy(alpha = 0.3f)
                                isPostponed -> scheme.tertiary.copy(alpha = 0.5f)
                                else -> scheme.tertiary
                            },
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$streak${if (streak > 1) "d" else "d"}",
                            color = when {
                                isCompleted -> scheme.tertiary.copy(alpha = 0.3f)
                                isPostponed -> scheme.tertiary.copy(alpha = 0.5f)
                                else -> scheme.tertiary
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }

                // Habit icon
                if (isPostponed) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                scheme.tertiaryContainer.copy(alpha = 0.5f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Schedule,
                            contentDescription = null,
                            tint = scheme.onTertiaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCompleted) {
                                    scheme.surfaceContainerHighest.copy(alpha = 0.4f)
                                } else {
                                    scheme.surfaceContainer
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isCompleted) {
                                scheme.onSurfaceVariant.copy(alpha = 0.3f)
                            } else {
                                scheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

data class TodayHabitData(
    val id: String,
    val name: String,
    val description: String,
    val streak: Int?,
    val icon: ImageVector,
    val isCompleted: Boolean,
    val isPostponed: Boolean = false,
    val postponeReason: String? = null,
    val lifestyleId: String? = null
)