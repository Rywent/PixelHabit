package com.rywent.pixelhabit.data.repository

import com.rywent.pixelhabit.data.local.dao.FocusPresetDao
import com.rywent.pixelhabit.data.local.dao.FocusSessionDao
import com.rywent.pixelhabit.data.local.entity.FocusPresetEntity
import com.rywent.pixelhabit.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FocusRepository @Inject constructor(
    private val sessionDao: FocusSessionDao,
    private val presetDao: FocusPresetDao
) {
    private val userId = "default_user"

    // ── Sessions ─────────────────────────────────────────────────────────────

    suspend fun saveSession(
        habitId: String?,
        habitName: String?,
        phase: String,
        plannedSeconds: Int,
        actualSeconds: Int,
        completed: Boolean,
        startedAt: Long
    ) {
        val now = System.currentTimeMillis()
        sessionDao.insert(
            FocusSessionEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                habitId = habitId,
                habitName = habitName,
                phase = phase,
                plannedSeconds = plannedSeconds,
                actualSeconds = actualSeconds,
                completed = completed,
                startedAt = startedAt,
                finishedAt = now,
                date = LocalDate.now().toString()
            )
        )
    }

    fun getTodayFocusSeconds(): Flow<Int> =
        sessionDao.getTotalFocusSecondsForDate(userId, LocalDate.now().toString())

    fun getTodayCompletedCount(): Flow<Int> =
        sessionDao.getCompletedFocusCountForDate(userId, LocalDate.now().toString())

    fun getAvgFocusForHabit(habitId: String): Flow<Float> =
        sessionDao.getAvgFocusSecondsForHabit(userId, habitId)

    // ── Presets ──────────────────────────────────────────────────────────────

    fun getPresets(): Flow<List<FocusPresetEntity>> = presetDao.getAll(userId)

    suspend fun insertPreset(preset: FocusPresetEntity) = presetDao.insert(preset)

    suspend fun updatePreset(preset: FocusPresetEntity) = presetDao.update(preset)

    suspend fun deletePreset(id: String) = presetDao.deleteById(id)

    suspend fun ensureDefaultPresets() {
        val existing = presetDao.getAll(userId)
        // simple one-shot insert if empty – called from ViewModel init
    }

    suspend fun seedDefaultPresetsIfEmpty() {
        // caller checks emptiness
        val defaults = listOf(
            FocusPresetEntity(
                id = "preset_pomo",
                userId = userId,
                name = "Classic Pomodoro",
                focusMin = 25,
                shortBreakMin = 5,
                longBreakMin = 15,
                longBreakEvery = 4,
                isDefault = true
            ),
            FocusPresetEntity(
                id = "preset_deep",
                userId = userId,
                name = "Deep Work",
                focusMin = 50,
                shortBreakMin = 10,
                longBreakMin = 20,
                longBreakEvery = 3,
                isDefault = true
            ),
            FocusPresetEntity(
                id = "preset_flow",
                userId = userId,
                name = "Flow Session",
                focusMin = 90,
                shortBreakMin = 15,
                longBreakMin = 30,
                longBreakEvery = 2,
                isDefault = true
            ),
            FocusPresetEntity(
                id = "preset_sprint",
                userId = userId,
                name = "Quick Sprint",
                focusMin = 15,
                shortBreakMin = 3,
                longBreakMin = 10,
                longBreakEvery = 4,
                isDefault = true
            )
        )
        defaults.forEach { presetDao.insert(it) }
    }

    fun getTodayFocusSecondsForHabit(habitId: String): Flow<Int> =
        sessionDao.getTodayFocusSecondsForHabit(userId, habitId, LocalDate.now().toString())

    fun getWeeklyFocusSecondsForHabit(habitId: String): Flow<Int> {
        val today = LocalDate.now()
        val startOfWeek = today.with(java.time.DayOfWeek.MONDAY).toString()
        return sessionDao.getWeeklyFocusSecondsForHabit(userId, habitId, startOfWeek, today.toString())
    }
}
