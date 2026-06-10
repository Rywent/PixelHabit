package com.rywent.pixelhabit.presentation.screens.habits

import com.rywent.pixelhabit.data.local.entity.HabitCompletionEntity
import com.rywent.pixelhabit.presentation.components.habit.HabitData
import com.rywent.pixelhabit.presentation.screens.habits.components.HabitsFilter
import com.rywent.pixelhabit.presentation.screens.habits.components.LifestyleData
import com.rywent.pixelhabit.presentation.screens.habits.components.QuestData


data class HabitsUIState(
    // navigation
    val selectedTabIndex: Int = 0,  // 0 = Habits, 1 = Lifestyle, 2 = Quests
    val selectedHabitId: String? = null,
    val selectedLifestyleId: String? = null,
    val selectedQuestId: String? = null,

    // panels
    val showCreateHabitPanel: Boolean = false,
    val showCreateLifestylePanel: Boolean = false,
    val showCreateQuestPanel: Boolean = false,
    val showHabitDetailsPanel: Boolean = false,
    val showLifestyleDetailsPanel: Boolean = false,
    val showQuestDetailsPanel: Boolean = false,
    val showEditHabitPanel: Boolean = false,
    val showEditLifestylePanel: Boolean = false,
    val showFilterPanel: Boolean = false,

    // data
    val allHabits: List<HabitData> = emptyList(),
    val lifestyles: List<LifestyleData> = emptyList(),
    val quests: List<QuestData> = emptyList(),

    // habit
    val selectedHabit: HabitData? = null,
    val selectedHabitCompletions: List<HabitCompletionEntity> = emptyList(),
    val editingHabit: HabitData? = null,
    val currentFilter: HabitsFilter = HabitsFilter.all(),

    // lifestyle
    val selectedLifestyle: LifestyleData? = null,
    val editingLifestyle: LifestyleData? = null,

    // Lifestyle statistics
    val totalLifestyleHabitCount: Int = 0,
    val lifestyleHabitsCompletedToday: Int = 0,
    val lifestyleWeeklyActivity: Int = 0,
    val lifestyleMonthlyGoal: Int = 0,
    val lifestyleMonthlyProgress: Int = 0,

    // quest


    // week statistics
    val completionRate: Float = 0f,
    val totalHabitCount: Int = 0,
    val habitsCompleted: Int = 0,
    val avgFocusTime: Int = 0,


    val isLoading: Boolean = false,
    val error: String? = null
)