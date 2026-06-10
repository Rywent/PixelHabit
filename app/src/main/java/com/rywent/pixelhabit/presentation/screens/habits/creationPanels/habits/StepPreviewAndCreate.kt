package com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rywent.pixelhabit.presentation.components.habit.HabitCard
import com.rywent.pixelhabit.presentation.components.habit.HabitData

@Composable
fun StepPreviewAndCreate(
    habitName: String,
    description: String,
    selectedIcon: ImageVector,
    selectedColor: Color,
    selectedCategory: String,
    selectedCategoryColor: Color,
    selectedCategoryIcon: ImageVector?,
    selectedFrequency: String,
    selectedTimeOfDay: String,
    selectedSpecificTime: String?,
    selectedCustomDays: List<String> = emptyList(),
    isEditing: Boolean = false,
    onCreateHabit: () -> Unit
) {
    val weeklyGoal = when (selectedFrequency) {
        "every_day" -> 7
        "weekdays" -> 5
        "weekends" -> 2
        "every_other_day" -> 4
        "custom" -> selectedCustomDays.size
        else -> 7
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Almost done!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Here's how your habit will look",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            HabitCard(
                habit = HabitData(
                    id = "preview",
                    name = habitName.ifBlank { "New Habit" },
                    description = description,
                    icon = selectedIcon,
                    frequency = when (selectedFrequency) {
                        "every_day" -> "Every day"
                        "weekdays" -> "Weekdays"
                        "weekends" -> "Weekends"
                        "every_other_day" -> "Every other day"
                        "custom" -> {
                            val dayShortcuts = mapOf(
                                "Monday" to "Mn", "Tuesday" to "Tu", "Wednesday" to "Wd",
                                "Thursday" to "Th", "Friday" to "Fr", "Saturday" to "St", "Sunday" to "Sn"
                            )
                            selectedCustomDays.take(3).joinToString(",") { dayShortcuts[it] ?: it.take(2) } +
                                    if (selectedCustomDays.size > 3) ", +${selectedCustomDays.size - 3}d" else ""
                        }
                        else -> "Every day"
                    },
                    timeOfDay = selectedTimeOfDay,
                    timeOfDayIcon = when (selectedTimeOfDay) {
                        "morning" -> Icons.Default.WbSunny
                        "afternoon" -> Icons.Default.WbTwilight
                        "evening" -> Icons.Default.NightlightRound
                        else -> Icons.Default.Schedule
                    },
                    specificTime = selectedSpecificTime,
                    lifestyleName = selectedCategory,
                    lifestyleColor = selectedCategoryColor,
                    habitColor = selectedColor,
                    lifestyleIcon = selectedCategoryIcon,
                    weeklyProgress = 0f,
                    weeklyDone = 0,
                    weeklyGoal = weeklyGoal,
                    currentStreak = 0,
                    bestStreak = 0
                ),
                onClick = { }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onCreateHabit,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = selectedColor
            )
        ) {
            Text(
                text = if (isEditing) "Save Changes" else "Create Habit",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}