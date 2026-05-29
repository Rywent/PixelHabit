package com.rywent.pixelhabit.data.repository

import androidx.room.Transaction
import com.rywent.pixelhabit.data.local.dao.HabitCompletionDao
import com.rywent.pixelhabit.data.local.dao.HabitDao
import com.rywent.pixelhabit.data.local.entity.HabitCompletionEntity
import com.rywent.pixelhabit.data.local.entity.HabitEntity
import com.rywent.pixelhabit.data.model.HabitWithCompletion
import com.rywent.pixelhabit.data.utils.calculateBestStreak
import com.rywent.pixelhabit.data.utils.calculateNewStreak
import com.rywent.pixelhabit.data.utils.calculateWeeklyDone
import com.rywent.pixelhabit.data.utils.calculateWeeklyProgress
import com.rywent.pixelhabit.data.utils.findPreviousScheduledDate
import com.rywent.pixelhabit.data.utils.isHabitScheduledForDate
import com.rywent.pixelhabit.data.utils.shouldResetStreak
import com.rywent.pixelhabit.data.utils.shouldResetWeeklyProgress
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class HabitRepository(
    private val habitDao: HabitDao,
    private val completionDao: HabitCompletionDao
) {
    fun getHabitsForToday(userId: String, today: String): Flow<List<HabitWithCompletion>> {
        return habitDao.getHabitsForToday(userId, today)
    }

    fun getAllHabits(userId: String): Flow<List<HabitEntity>> {
        return habitDao.getAllHabits(userId)
    }

    fun getCompletionsByDate(date: String): Flow<List<HabitCompletionEntity>> {
        return completionDao.getCompletionsByDate(date)
    }

    suspend fun getWeekCompletions(userId: String): List<HabitCompletionEntity> {
        val today = LocalDate.now()
        val startOfWeek = today.with(java.time.DayOfWeek.MONDAY)
        val endOfWeek = startOfWeek.plusDays(6)

        return completionDao.getCompletionsBetween(
            startDate = startOfWeek.toString(),
            endDate = endOfWeek.toString()
        )
    }

    fun getWeekCompletionsFlow(startDate: String, endDate: String): Flow<List<HabitCompletionEntity>> {
        return completionDao.getCompletionsBetweenFlow(startDate, endDate)
    }

    @Transaction
    suspend fun toggleCompletion(habitId: String, date: String, completed: Boolean) {
        try {
            val existing = completionDao.getCompletion(habitId, date)

            if (existing != null) {
                completionDao.updateCompletion(
                    habitId = habitId,
                    date = date,
                    completed = completed,
                    completedAt = if (completed) System.currentTimeMillis() else null
                )
            } else {
                val completion = HabitCompletionEntity(
                    habitId = habitId,
                    date = date,
                    completed = completed,
                    completedAt = if (completed) System.currentTimeMillis() else null
                )
                completionDao.upsertCompletion(completion)
            }

            val habit = habitDao.getHabitById(habitId) ?: return

            val newDone = calculateWeeklyDone(habit.weeklyDone, completed)
            val newProgress = calculateWeeklyProgress(newDone, habit.weeklyGoal)

            habitDao.updateProgress(habitId, newProgress, newDone, System.currentTimeMillis())

            if (completed) {
                val previousScheduledDate = findPreviousScheduledDate(habit, LocalDate.parse(date))
                val prevCompletion = if (previousScheduledDate != null) {
                    completionDao.getCompletion(habitId, previousScheduledDate.toString())
                } else null

                val newStreak = calculateNewStreak(habit.currentStreak, prevCompletion?.completed == true)
                val newBest = calculateBestStreak(newStreak, habit.bestStreak)
                habitDao.updateStreak(habitId, newStreak, newBest, System.currentTimeMillis())
            } else {
                val newStreak = calculateCurrentStreak(habitId, date, habit)
                habitDao.updateStreak(habitId, newStreak, habit.bestStreak, System.currentTimeMillis())
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun checkAndResetStreaks(userId: String) {
        val today = LocalDate.now()
        val todayString = today.toString()
        val todayDayOfWeek = today.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)

        val habits = habitDao.getAllHabitsOnce(userId)
        habits.forEach { habit ->
            val lastScheduledDate = findPreviousScheduledDate(habit, today)

            if (lastScheduledDate != null) {
                val lastScheduledCompletion = completionDao.getCompletion(
                    habit.id,
                    lastScheduledDate.toString()
                )

                val todayCompletion = completionDao.getCompletion(habit.id, todayString)
                val isScheduledToday = isHabitScheduledForDate(habit, todayString, todayDayOfWeek)

                if (lastScheduledCompletion?.completed != true &&
                    habit.currentStreak > 0) {
                    if ((isScheduledToday && todayCompletion?.completed != true) || !isScheduledToday) {
                        habitDao.updateStreak(habit.id, 0, habit.bestStreak, System.currentTimeMillis())
                    }
                }
            }
        }
    }

    suspend fun resetWeeklyProgressIfNeeded(userId: String) {
        val today = LocalDate.now()

        val habits = habitDao.getAllHabitsOnce(userId)
        habits.forEach { habit ->
            if (shouldResetWeeklyProgress(habit, today)) {
                habitDao.updateProgress(
                    habitId = habit.id,
                    progress = 0f,
                    done = 0,
                    updatedAt = System.currentTimeMillis()
                )
            }
        }
    }

    suspend fun getHabitByIdByUserId(habitId: String, userId: String): HabitEntity? {
        return habitDao.getHabitByIdAndByUserId(userId, habitId)
    }

    suspend fun insertHabit(habit: HabitEntity) {
        habitDao.insertHabit(habit)
    }

    suspend fun updateHabit(habit: HabitEntity) {
        habitDao.updateHabit(habit)
    }

    suspend fun deleteHabit(habitId: String) {
        habitDao.deleteHabit(habitId)
    }

    private suspend fun calculateCurrentStreak(habitId: String, fromDate: String, habit: HabitEntity): Int {
        var streak = 0
        var currentDate = LocalDate.parse(fromDate).minusDays(1)
        var daysChecked = 0

        while (daysChecked < 365) {
            val dayOfWeek = currentDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)

            if (isHabitScheduledForDate(habit, currentDate.toString(), dayOfWeek)) {
                val completion = completionDao.getCompletion(habitId, currentDate.toString())
                if (completion?.completed == true) {
                    streak++
                    currentDate = currentDate.minusDays(1)
                } else {
                    break
                }
            } else {
                currentDate = currentDate.minusDays(1)
            }
            daysChecked++
        }
        return streak
    }

    suspend fun calculateAndUpdateGlobalStreak(userId: String): Int {
        val today = LocalDate.now()
        val todayString = today.toString()
        val todayDayOfWeek = today.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)

        val allHabits = habitDao.getAllHabitsOnce(userId)

        if (allHabits.isEmpty()) return 0

        val scheduledToday = allHabits.filter {
            isHabitScheduledForDate(it, todayString, todayDayOfWeek)
        }

        if (scheduledToday.isEmpty()) {
            return calculateStreakBeforeToday(userId, today.minusDays(1), allHabits)
        }

        val completionsToday = completionDao.getCompletionsByDateOnce(todayString)
        val doneCountToday = scheduledToday.count { habit ->
            completionsToday.any { it.habitId == habit.id && it.completed }
        }

        val isTodayPassed = doneCountToday.toFloat() / scheduledToday.size >= 0.5f

        if (!isTodayPassed) {
            return calculateStreakBeforeToday(userId, today.minusDays(1), allHabits)
        }

        return 1 + calculateStreakBeforeToday(userId, today.minusDays(1), allHabits)
    }

    private suspend fun calculateStreakBeforeToday(
        userId: String,
        fromDate: LocalDate,
        allHabits: List<HabitEntity>
    ): Int {
        var streak = 0
        var currentDate = fromDate

        while (streak < 365) {
            val dateString = currentDate.toString()
            val dayOfWeek = currentDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)

            val scheduled = allHabits.filter {
                isHabitScheduledForDate(it, dateString, dayOfWeek)
            }

            if (scheduled.isEmpty()) {
                currentDate = currentDate.minusDays(1)
                continue
            }

            val completions = completionDao.getCompletionsByDateOnce(dateString)
            val doneCount = scheduled.count { habit ->
                completions.any { it.habitId == habit.id && it.completed }
            }

            if (doneCount.toFloat() / scheduled.size >= 0.5f) {
                streak++
                currentDate = currentDate.minusDays(1)
            } else {
                break
            }
        }

        return streak
    }
}