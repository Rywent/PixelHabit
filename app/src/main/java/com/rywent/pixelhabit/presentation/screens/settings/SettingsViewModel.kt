package com.rywent.pixelhabit.presentation.screens.settings

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rywent.pixelhabit.data.repository.UserRepository
import com.rywent.pixelhabit.data.utils.BackupManager
import com.rywent.pixelhabit.presentation.screens.settings.components.ExportState
import com.rywent.pixelhabit.presentation.screens.settings.components.ImportState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val application: Application,
    private val backupManager: BackupManager,
    private val userRepository: UserRepository
) : ViewModel() {

    private val userId = "default_user"
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var pendingBackupContent: String? = null


    init {
        loadSettings()
    }

    fun toggleHabitReminders(enabled: Boolean) {
        _uiState.update { it.copy(habitRemindersEnabled = enabled) }
        viewModelScope.launch {
            userRepository.updateHabitRemindersEnabled(userId, enabled)
        }
    }

    fun toggleStreakNotifications(enabled: Boolean) {
        _uiState.update { it.copy(streakNotificationsEnabled = enabled) }
        viewModelScope.launch {
            userRepository.updateStreakNotificationsEnabled(userId, enabled)
        }
    }

    fun toggleMotivation(enabled: Boolean) {
        _uiState.update { it.copy(motivationEnabled = enabled) }
        viewModelScope.launch {
            userRepository.updateMotivationEnabled(userId, enabled)
        }
    }


    fun exportData() {
        viewModelScope.launch {
            _uiState.update { it.copy(exportState = ExportState.Loading) }

            val result = backupManager.exportAllData()

            _uiState.update {
                if (result.success) {
                    it.copy(exportState = ExportState.Success(result.fileName ?: "pixelhabit_backup.json"))
                } else {
                    it.copy(exportState = ExportState.Error(result.message))
                }
            }

            kotlinx.coroutines.delay(4000)
            _uiState.update { it.copy(exportState = ExportState.Idle) }
        }
    }

    fun importData(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(importState = ImportState.Loading) }

            try {
                val content = application.contentResolver
                    .openInputStream(uri)?.bufferedReader()
                    ?.use { it.readText() }
                    ?: throw Exception("Could not read file")

                pendingBackupContent = content

                val jsonObject = JSONObject(content)
                val backupSummary = parseBackupJson(jsonObject, content)

                _uiState.update {
                    it.copy(importState = ImportState.ReadyForImport(backupSummary))
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(importState = ImportState.Error(e.localizedMessage ?: "Failed to read file"))
                }
                pendingBackupContent = null
            }
        }
    }

    fun cancelImport() {
        _uiState.update { it.copy(importState = ImportState.Idle) }
    }

    private fun parseBackupJson(jsonObject: JSONObject, rawJson: String): BackupSummary {
        val metadata = BackupMetadata(
            version = jsonObject.optInt("version", 1),
            exportDate = jsonObject.optLong("exportDate", 0)
        )

        // Users
        val users = mutableListOf<BackupUser>()
        val usersArray = jsonObject.optJSONArray("users")
        if (usersArray != null) {
            for (i in 0 until usersArray.length()) {
                val user = usersArray.getJSONObject(i)
                users.add(
                    BackupUser(
                        id = user.getString("id"),
                        name = user.getString("name"),
                        currentStreak = user.optInt("currentStreak", 0),
                        bestStreak = user.optInt("bestStreak", 0),
                        createdAt = user.optLong("createdAt", 0),
                        lastActiveAt = user.optLong("lastActiveAt", 0)
                    )
                )
            }
        }

        // Lifestyles
        val lifestyles = mutableListOf<BackupLifestyle>()
        val lifestylesArray = jsonObject.optJSONArray("lifestyles")
        if (lifestylesArray != null) {
            for (i in 0 until lifestylesArray.length()) {
                val ls = lifestylesArray.getJSONObject(i)
                lifestyles.add(
                    BackupLifestyle(
                        id = ls.getString("id"),
                        name = ls.getString("name"),
                        description = ls.optString("description", ""),
                        iconPath = ls.getString("iconPath"),
                        category = ls.getString("category"),
                        isActive = ls.optBoolean("isActive", true)
                    )
                )
            }
        }

        // Habits
        val habits = mutableListOf<BackupHabit>()
        val habitsArray = jsonObject.optJSONArray("habits")
        if (habitsArray != null) {
            for (i in 0 until habitsArray.length()) {
                val habit = habitsArray.getJSONObject(i)
                habits.add(
                    BackupHabit(
                        id = habit.getString("id"),
                        name = habit.getString("name"),
                        description = habit.optString("description", ""),
                        frequency = habit.getString("frequency"),
                        timeOfDay = habit.getString("timeOfDay"),
                        currentStreak = habit.optInt("currentStreak", 0),
                        bestStreak = habit.optInt("bestStreak", 0),
                        weeklyDone = habit.optInt("weeklyDone", 0),
                        weeklyGoal = habit.optInt("weeklyGoal", 0),
                        lifestyleName = habit.optString("lifestyleName").ifBlank { null }
                    )
                )
            }
        }

        // Completions grouped by date
        val completionsMap = mutableMapOf<String, Int>()
        val completionsArray = jsonObject.optJSONArray("habitCompletions")
        if (completionsArray != null) {
            for (i in 0 until completionsArray.length()) {
                val completion = completionsArray.getJSONObject(i)
                val date = completion.optString("date", "Unknown")
                val completed = completion.optBoolean("completed", false)
                if (completed) {
                    completionsMap[date] = completionsMap.getOrDefault(date, 0) + 1
                }
            }
        }

        val completions = completionsMap.map { BackupCompletion(it.key, it.value) }
            .sortedByDescending { it.date }

        // Quests
        val quests = mutableListOf<BackupQuest>()
        val questsArray = jsonObject.optJSONArray("quests")
        if (questsArray != null) {
            for (i in 0 until questsArray.length()) {
                val quest = questsArray.getJSONObject(i)
                quests.add(
                    BackupQuest(
                        id = quest.getString("id"),
                        name = quest.getString("name"),
                        description = quest.optString("description", ""),
                        totalDays = quest.getInt("totalDays"),
                        currentDay = quest.getInt("currentDay"),
                        daysLeft = quest.getInt("daysLeft"),
                        isCompleted = quest.getBoolean("isCompleted"),
                        completionPercent = quest.optDouble("completionPercent", 0.0).toFloat()
                    )
                )
            }
        }

        return BackupSummary(
            metadata = metadata,
            users = users,
            lifestyles = lifestyles,
            habits = habits,
            completions = completions,
            quests = quests,
            rawJson = rawJson
        )
    }

    fun applyImport() {
        viewModelScope.launch {
            val content = pendingBackupContent ?: return@launch

            _uiState.update { it.copy(importState = ImportState.Loading) }

            try {
                val result = backupManager.importFromJson(content)

                _uiState.update {
                    if (result.success) {
                        it.copy(importState = ImportState.Success)
                    } else {
                        it.copy(importState = ImportState.Error(result.message))
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(importState = ImportState.Error(e.localizedMessage ?: "Import failed"))
                }
            }

            pendingBackupContent = null

            kotlinx.coroutines.delay(4000)
            _uiState.update { it.copy(importState = ImportState.Idle) }
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val (habitReminders, streakNotifications, motivation) =
                userRepository.getNotificationSettings(userId)
            _uiState.update {
                it.copy(
                    habitRemindersEnabled = habitReminders,
                    streakNotificationsEnabled = streakNotifications,
                    motivationEnabled = motivation
                )
            }
        }
    }

    data class SettingsUiState(
        val habitRemindersEnabled: Boolean = true,
        val streakNotificationsEnabled: Boolean = true,
        val motivationEnabled: Boolean = true,
        val exportState: ExportState = ExportState.Idle,
        val importState: ImportState = ImportState.Idle
    )
}