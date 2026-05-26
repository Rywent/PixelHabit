package com.rywent.pixelhabit.presentation.screens.habits.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
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
fun QuestCard(
    quest: QuestData,
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
                shape = RoundedCornerShape(24.dp),
                spotColor = shadowColor.copy(alpha = 0.5f),
                ambientColor = shadowColor.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (quest.isCompleted)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
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
                .padding(20.dp)
        ) {
            // header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            if (quest.isCompleted)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            else
                                quest.iconColor.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (quest.isCompleted)
                            Icons.Rounded.CheckCircle
                        else
                            quest.icon,
                        contentDescription = null,
                        tint = if (quest.isCompleted)
                            MaterialTheme.colorScheme.primary
                        else
                            quest.iconColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Name
                    Text(
                        text = quest.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Description
                    if (quest.description.isNotEmpty()) {
                        Text(
                            text = quest.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                }

                Spacer(modifier = Modifier.width(12.dp))

                // Days
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${quest.daysLeft}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (quest.isCompleted)
                            MaterialTheme.colorScheme.primary
                        else if (quest.daysLeft <= 2)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (quest.isCompleted) "done" else "days left",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // progress
            Column {
                LinearProgressIndicator(
                    progress = { quest.completionPercent },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (quest.isCompleted)
                        MaterialTheme.colorScheme.primary
                    else
                        quest.iconColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )

                Spacer(modifier = Modifier.height(8.dp))

                // days and dates
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Day ${quest.currentDay}/${quest.totalDays}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${quest.startDate} — ${quest.endDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

data class QuestData(
    val id: String,
    val name: String,
    val description: String = "",
    val icon: ImageVector,
    val iconColor: Color,
    val totalDays: Int,
    val currentDay: Int,
    val daysLeft: Int,
    val startDate: String,
    val endDate: String,
    val isCompleted: Boolean,
    val completionPercent: Float,
)