package com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
data class TimeOfDayOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun StepTimeOfDay(
    selectedTimeOfDay: String,
    selectedSpecificTime: String?,
    onTimeOfDaySelected: (String) -> Unit,
    onSpecificTimeSelected: (String) -> Unit
) {
    val timeOptions = remember {
        listOf(
            TimeOfDayOption("morning", "Morning", "After waking up", Icons.Default.WbSunny, Color(0xFFFFB300)),
            TimeOfDayOption("afternoon", "Afternoon", "Middle of the day", Icons.Default.WbTwilight, Color(0xFF42A5F5)),
            TimeOfDayOption("evening", "Evening", "Before bed", Icons.Default.NightlightRound, Color(0xFF7C4DFF)),
            TimeOfDayOption("anytime", "Anytime", "Flexible time", Icons.Default.Schedule, Color(0xFF26A69A))
        )
    }

    var showTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "When?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 20.dp)
        )

        Text(
            text = "Choose the best time for this habit",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            timeOptions.forEach { option ->
                val isSelected = option.id == selectedTimeOfDay

                TimeOfDayCard(
                    option = option,
                    isSelected = isSelected,
                    selectedTime = if (isSelected && option.id != "anytime") selectedSpecificTime else null,
                    onClick = { onTimeOfDaySelected(option.id) },
                    onSetExactTime = { showTimePicker = true }
                )
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            initialTime = selectedSpecificTime,
            onTimeSelected = { time ->
                onSpecificTimeSelected(time)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}

@Composable
private fun TimeOfDayCard(
    option: TimeOfDayOption,
    isSelected: Boolean,
    selectedTime: String?,
    onClick: () -> Unit,
    onSetExactTime: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    val containerColor = if (isSelected) {
        if (isDark) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.primary
    } else {
        if (isDark) MaterialTheme.colorScheme.surfaceContainerHigh
        else MaterialTheme.colorScheme.surface
    }

    val contentColor = if (isSelected) {
        if (isDark) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val subtitleColor = if (isSelected) {
        if (isDark) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val iconBoxColor = if (isSelected) {
        if (isDark) option.color.copy(alpha = 0.15f)
        else Color.White.copy(alpha = 0.2f)
    } else {
        option.color.copy(alpha = 0.12f)
    }

    val iconTint = if (isSelected) {
        if (isDark) option.color
        else Color.White
    } else {
        option.color
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isSelected) 4.dp else 1.dp,
                shape = RoundedCornerShape(26.dp)
            ),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(iconBoxColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
                Text(
                    text = if (selectedTime != null) "At $selectedTime" else option.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = subtitleColor
                )
            }

            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (isDark) MaterialTheme.colorScheme.primary else Color.White
                )
            }
        }

        if (isSelected && option.id != "anytime") {
            HorizontalDivider(
                color = if (isDark)
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                else
                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedTime ?: "No specific time",
                    style = MaterialTheme.typography.bodyMedium,
                    color = subtitleColor
                )
                TextButton(onClick = onSetExactTime) {
                    Text(
                        text = if (selectedTime != null) "Change" else "Set exact time",
                        color = if (isDark) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}