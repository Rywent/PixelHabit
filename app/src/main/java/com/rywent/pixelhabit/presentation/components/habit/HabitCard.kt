package com.rywent.pixelhabit.presentation.components.habit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rywent.pixelhabit.presentation.components.customElements.CustomCircularProgress
import com.rywent.pixelhabit.ui.theme.adaptiveShadowColor

@Composable
fun HabitCard(
    habit: HabitData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val shadowColor = adaptiveShadowColor()
    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = shadowColor.copy(alpha = 0.5f),
                ambientColor = shadowColor.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        bounded = true
                    ),
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClick()
                    }
                )
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // habit icon
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                habit.lifestyleColor.copy(alpha = 0.12f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = habit.icon,
                            contentDescription = null,
                            tint = habit.lifestyleColor,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // main content
                    Column(modifier = Modifier.weight(1f)) {
                        // title
                        Text(
                            text = habit.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // frequency and time of day
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = habit.frequency,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (habit.timeOfDay.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(10.dp))
                                Icon(
                                    imageVector = habit.timeOfDayIcon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = habit.specificTime ?: habit.timeOfDay,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // tag
                        if (habit.lifestyleName.isNotEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                habit.lifestyleIcon?.let {
                                    Icon(
                                        imageVector = it,
                                        contentDescription = null,
                                        tint = habit.lifestyleColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = habit.lifestyleName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = habit.lifestyleColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // progress and streak section
                    Column(horizontalAlignment = Alignment.End) {
                        // weekly progress ring
                        CustomCircularProgress(
                            progress = habit.weeklyProgress,
                            size = 68.dp,
                            strokeWidth = 6.5.dp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // current streak
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFFB923C),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${habit.currentStreak}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFB923C),
                                fontSize = 17.sp
                            )
                        }
                    }
                }

                // bottom section
                if (habit.weeklyGoal > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        thickness = DividerDefaults.Thickness,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // weekly progress text
                        Text(
                            text = "${habit.weeklyDone}/${habit.weeklyGoal} this week",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (habit.bestStreak > 0) {
                            // best streak
                            Text(
                                text = "Best • ${habit.bestStreak}d",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}
data class HabitData(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val frequency: String,
    val timeOfDay: String,
    val timeOfDayIcon: ImageVector,
    val specificTime: String? = null,
    val customDays: String? = null,
    val lifestyleName: String,
    val lifestyleColor: Color,
    val lifestyleIcon: ImageVector?,
    val weeklyProgress: Float,
    val weeklyDone: Int,
    val weeklyGoal: Int,
    val currentStreak: Int,
    val bestStreak: Int,
    val isCompletedToday: Boolean = false
)