package com.rywent.pixelhabit.presentation.screens.habits

import com.rywent.pixelhabit.presentation.components.habit.HabitData


data class HabitsUISate(
    val selectedTabIndex: Int = 0,
    val selectedHabitId: String? = String(),

    // sub screens
    val allHabits: List<HabitData> = emptyList(),

    // week stats
    val completionRate: Float = 0f,
    val totalHabitCount: Int = 0,
    val habitsCompleted: Int = 0,
    val avgFocusTime: Int = 0,

    val isLoading: Boolean = false,
)