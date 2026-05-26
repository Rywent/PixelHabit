package com.rywent.pixelhabit.presentation.screens.habits.subScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rywent.pixelhabit.presentation.components.habit.HabitCard
import com.rywent.pixelhabit.presentation.screens.habits.HabitsUIState
import com.rywent.pixelhabit.presentation.screens.habits.components.NoHabits
import com.rywent.pixelhabit.presentation.screens.habits.components.StatisticsSection

@Composable
fun HabitsSubScreen(
    navigateToHabitDetails: (String) -> Unit,
    uiState: HabitsUIState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = 8.dp)
    ) {
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