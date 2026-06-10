// presentation/screens/settings/BackupDataModels.kt
package com.rywent.pixelhabit.presentation.screens.settings

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupMetadata(
    val version: Int,
    val exportDate: Long
) {
    fun getFormattedDate(): String = SimpleDateFormat(
        "dd MMMM yyyy, HH:mm:ss",
        Locale.getDefault()
    ).format(Date(exportDate))
}

data class BackupUser(
    val id: String,
    val name: String,
    val currentStreak: Int,
    val bestStreak: Int,
    val createdAt: Long,
    val lastActiveAt: Long
)

data class BackupLifestyle(
    val id: String,
    val name: String,
    val description: String,
    val iconPath: String,
    val category: String,
    val isActive: Boolean
)

data class BackupHabit(
    val id: String,
    val name: String,
    val description: String,
    val frequency: String,
    val timeOfDay: String,
    val currentStreak: Int,
    val bestStreak: Int,
    val weeklyDone: Int,
    val weeklyGoal: Int,
    val lifestyleName: String?
)

data class BackupCompletion(
    val date: String,
    val count: Int
)

data class BackupQuest(
    val id: String,
    val name: String,
    val description: String,
    val totalDays: Int,
    val currentDay: Int,
    val daysLeft: Int,
    val isCompleted: Boolean,
    val completionPercent: Float
)

data class BackupSummary(
    val metadata: BackupMetadata,
    val users: List<BackupUser>,
    val lifestyles: List<BackupLifestyle>,
    val habits: List<BackupHabit>,
    val completions: List<BackupCompletion>,
    val quests: List<BackupQuest>,
    val rawJson: String
) {
    val totalUsers: Int get() = users.size
    val totalLifestyles: Int get() = lifestyles.size
    val totalHabits: Int get() = habits.size
    val totalCompletions: Int get() = completions.sumOf { it.count }
    val totalQuests: Int get() = quests.size
    val completedQuests: Int get() = quests.count { it.isCompleted }
}