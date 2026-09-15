package com.rywent.pixelhabit.presentation.screens.home

import com.rywent.pixelhabit.presentation.components.habit.TodayHabitData
import com.rywent.pixelhabit.presentation.screens.habits.components.LifestyleData
import com.rywent.pixelhabit.presentation.screens.home.components.DayStat

data class HomeUiState(
    // loading and user
    val isLoading: Boolean = true,
    val userName: String = "",
    val currentDate: String = "",

    // streak
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val isStreakReset: Boolean = false,

    // panels
    val showStreakPanel: Boolean = false,
    val showSettingsPanel: Boolean = false,
    val showCreateHabitPanel: Boolean = false,
    val showAboutSheet: Boolean = false,

    // statistics panel
    val showStatisticsPanel: Boolean = false,
    val selectedStatisticsTabIndex: Int = 1,
    val lastWeekStat: List<DayStat> = emptyList(),
    val currentMonthStats: List<DayStat> = emptyList(),
    val lastMonthStats: List<DayStat> = emptyList(),


    // streak panel
    val streakPanelValue: Int = 0,

    // habits
    val todayHabits: List<TodayHabitData> = emptyList(),
    val isTodayHabitsExpanded: Boolean = false,

    // postpone
    val showPostponeSheet: Boolean = false,
    val selectedHabitId: String? = null,

    // data
    val weekStat: List<DayStat> = emptyList(),
    val lifestyles: List<LifestyleData> = emptyList(),
)