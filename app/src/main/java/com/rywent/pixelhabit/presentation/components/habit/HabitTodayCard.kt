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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.input.pointer.pointerInput
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
    onCheckedChange: (Boolean) -> Unit,
    onTodayHabitClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val shadowColor = adaptiveShadowColor()

    val swipeBackgroundColor = if (isCompleted) {
        scheme.errorContainer.copy(alpha = 0.4f)
    } else {
        scheme.primaryContainer.copy(alpha = 0.4f)
    }

    var offsetX by remember { mutableFloatStateOf(0f) }
    val swipeThreshold = 150f

    val scaleY by animateFloatAsState(
        targetValue = if (isCompleted) 0.95f else 1f,
        animationSpec = tween(durationMillis = 400)
    )

    val scaleX by animateFloatAsState(
        targetValue = if (isCompleted) 0.97f else 1f,
        animationSpec = tween(durationMillis = 400)
    )

    val cardHeight by animateDpAsState(
        targetValue = if (isCompleted) 93.dp else 100.dp,
        animationSpec = tween(durationMillis = 400)
    )

    val backgroundAlpha = (offsetX / swipeThreshold).coerceIn(1f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clip(RoundedCornerShape(30.dp))
    ) {
        if (offsetX < -10f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(swipeBackgroundColor.copy(alpha = backgroundAlpha))
                    .padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Rounded.Close else Icons.Rounded.Check,
                    contentDescription = null,
                    tint = if (isCompleted) scheme.error else scheme.primary,
                    modifier = Modifier.size((55 * backgroundAlpha).dp.coerceAtLeast(8.dp))
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .scale(scaleX = scaleX, scaleY = scaleY)
                .shadow(
                    elevation = 8.dp,
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
                        colors = listOf(
                            scheme.surfaceContainerHighest,
                            scheme.surfaceContainerHigh.copy(alpha = 0.8f),
                            scheme.surfaceContainer
                        )
                    )
                )
                .padding(start = 24.dp, top = 14.dp, end = 16.dp, bottom = 14.dp)
                .pointerInput(isCompleted) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX < -swipeThreshold) {
                                onCheckedChange(!isCompleted)
                            }
                            offsetX = 0f
                        },
                        onDragCancel = {
                            offsetX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            offsetX = (offsetX + dragAmount).coerceIn(-swipeThreshold * 1.5f, 0f)
                        }
                    )
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onCheckedChange(!isCompleted) },
                contentAlignment = Alignment.Center
            ) {
                RoundedCheckbox(
                    checked = isCompleted,
                    onCheckedChange = onCheckedChange,
                    size = 30.dp,
                    cornerRadius = 12.dp
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 8.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) {
                        scheme.onSurface.copy(alpha = 0.5f)
                    } else {
                        scheme.onSurface
                    },
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCompleted) {
                        scheme.onSurfaceVariant.copy(alpha = 0.4f)
                    } else {
                        scheme.onSurfaceVariant
                    },
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
            ) {
                if (streak != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocalFireDepartment,
                            contentDescription = null,
                            tint = if (isCompleted) {
                                Color(0xFFCE9428).copy(alpha = 0.5f)
                            } else {
                                Color(0xFFCE9428)
                            },
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$streak${if (streak > 1) "d" else "d"}",
                            color = if (isCompleted) {
                                Color(0xFFCE9428).copy(alpha = 0.5f)
                            } else {
                                Color(0xFFCE9428)
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCompleted) {
                                scheme.surfaceContainerHighest.copy(alpha = 0.5f)
                            } else {
                                scheme.surfaceContainer
                            }
                        )
                        .clickable { onTodayHabitClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isCompleted) {
                            scheme.onSurfaceVariant.copy(alpha = 0.4f)
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
data class TodayHabitData(
    val id: String,
    val name: String,
    val description: String,
    val streak: Int?,
    val icon: ImageVector,
    val isCompleted: Boolean,
)
