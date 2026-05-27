package com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
    selectedIcon: ImageVector,
    selectedColor: Color,
    selectedCategory: String,
    selectedFrequency: String,
    selectedTimeOfDay: String,
    selectedSpecificTime: String?,
    onCreateHabit: () -> Unit
) {
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

        // Превью карточки привычки
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(32.dp))
        ) {
            HabitCard(
                habit = HabitData(
                    id = "preview",
                    name = if (habitName.isBlank()) "New Habit" else habitName,
                    icon = selectedIcon,
                    frequency = when (selectedFrequency) {
                        "every_day" -> "Every day"
                        "weekdays" -> "Weekdays"
                        "weekends" -> "Weekends"
                        "every_other_day" -> "Every other day"
                        else -> "Custom"
                    },
                    timeOfDay = selectedTimeOfDay.replaceFirstChar { it.uppercase() },
                    timeOfDayIcon = when (selectedTimeOfDay) {
                        "morning" -> Icons.Default.WbSunny
                        "afternoon" -> Icons.Default.WbTwilight
                        "evening" -> Icons.Default.NightlightRound
                        else -> Icons.Default.Schedule
                    },
                    lifestyleName = selectedCategory,
                    lifestyleColor = selectedColor,
                    lifestyleIcon = null,
                    weeklyProgress = 0f,
                    weeklyDone = 0,
                    weeklyGoal = 7,
                    currentStreak = 0,
                    bestStreak = 0
                ),
                onClick = { }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Финальная большая кнопка
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
                text = "Create Habit",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}