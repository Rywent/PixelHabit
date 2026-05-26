package com.rywent.pixelhabit.presentation.screens.habits

import com.rywent.pixelhabit.presentation.components.habit.HabitData
import com.rywent.pixelhabit.presentation.screens.habits.components.LifestyleData
import com.rywent.pixelhabit.presentation.screens.habits.components.QuestData


data class HabitsUIState(
    // navigation
    val selectedTabIndex: Int = 0,
    val selectedHabitId: String? = String(),
    val selectedLifestyleId: String? = String(),
    val selectedQuestId: String? = String(),

    // panels
    val showCreateHabitPanel: Boolean = false,
    val showCreateLifestylePanel: Boolean = false,
    val showCreateQuestPanel: Boolean = false,

    // sub screens
    val allHabits: List<HabitData> = emptyList(),
    val lifestyles: List<LifestyleData> = emptyList(),
    val quests: List<QuestData> = emptyList(),

    // week stats
    val completionRate: Float = 0f,
    val totalHabitCount: Int = 0,
    val habitsCompleted: Int = 0,
    val avgFocusTime: Int = 0,

    val isLoading: Boolean = false,
)