package com.rywent.pixelhabit.presentation.screens.habits.subScreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateBounds
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rywent.pixelhabit.presentation.components.habit.HabitCard
import com.rywent.pixelhabit.presentation.components.habit.HabitData
import com.rywent.pixelhabit.presentation.screens.habits.HabitsUISate
import com.rywent.pixelhabit.presentation.screens.habits.components.NoHabits
import com.rywent.pixelhabit.presentation.screens.habits.components.StatisticsSection

@Composable
fun HabitsSubScreen(
    navigateToHabitDetails: (String) -> Unit,
    uiState: HabitsUISate,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        StatisticsSection(
            completionRate = uiState.completionRate,
            totalHabitCount = uiState.totalHabitCount,
            habitsCompleted = uiState.habitsCompleted,
            avgFocusTime = uiState.avgFocusTime
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.allHabits.isEmpty()) {
            NoHabits()
        } else {
            uiState.allHabits.forEach { habit ->
                HabitCard(
                    habit = habit,
                    onClick = {
                        navigateToHabitDetails(habit.id)
                    },
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}