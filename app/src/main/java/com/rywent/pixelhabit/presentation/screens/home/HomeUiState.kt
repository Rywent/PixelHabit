package com.rywent.pixelhabit.presentation.screens.home

import com.rywent.pixelhabit.presentation.components.habit.TodayHabitData
import com.rywent.pixelhabit.presentation.screens.home.components.DayStat

data class HomeUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val currentDate: String = "",
    val currentStreak: Int = 0,
    val weekStat: List<DayStat> = emptyList(),
    val todayHabits: List<TodayHabitData> = emptyList(),
    val isTodayHabitsExpanded: Boolean = false,
    val showAboutSheet: Boolean = false
)