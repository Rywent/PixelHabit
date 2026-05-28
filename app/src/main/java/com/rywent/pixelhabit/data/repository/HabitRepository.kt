package com.rywent.pixelhabit.data.repository

import androidx.room.Transaction
import com.rywent.pixelhabit.data.local.dao.HabitCompletionDao
import com.rywent.pixelhabit.data.local.dao.HabitDao
import com.rywent.pixelhabit.data.local.entity.HabitCompletionEntity
import com.rywent.pixelhabit.data.local.entity.HabitEntity
import com.rywent.pixelhabit.data.model.HabitWithCompletion
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class HabitRepository(
    private val habitDao: HabitDao,
    private val completionDao: HabitCompletionDao
) {
    // get habits for today
    fun getHabitsForToday(userId: String, today: String): Flow<List<HabitWithCompletion>> {
        return habitDao.getHabitsForToday(userId, today)
    }

    // get all habits by user id
    fun getAllHabits(userId: String) : Flow<List<HabitEntity>> {
        return habitDao.getAllHabits(userId)
    }

    // get completion habits by date
    fun getCompletionsByDate(date: String): Flow<List<HabitCompletionEntity>> {
        return completionDao.getCompletionsByDate(date)
    }

    // mark/unmark as completed
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

            val newDone = if (completed) {
                habit.weeklyDone + 1
            } else {
                maxOf(0, habit.weeklyDone - 1)
            }
            val newProgress = if (habit.weeklyGoal > 0) {
                newDone.toFloat() / habit.weeklyGoal
            } else 0f

            habitDao.updateProgress(habitId, newProgress, newDone, System.currentTimeMillis())

            if (completed) {
                val yesterday = LocalDate.now().minusDays(1).toString()
                val yesterdayCompletion = completionDao.getCompletion(habitId, yesterday)

                val newStreak = if (yesterdayCompletion?.completed == true) {
                    habit.currentStreak + 1
                } else {
                    1
                }
                val newBest = maxOf(newStreak, habit.bestStreak)
                habitDao.updateStreak(habitId, newStreak, newBest, System.currentTimeMillis())
            } else {
                val newStreak = calculateCurrentStreak(habitId, date)
                habitDao.updateStreak(habitId, newStreak, habit.bestStreak, System.currentTimeMillis())
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun checkAndResetStreaks(userId: String) {
        val today = LocalDate.now().toString()
        val yesterday = LocalDate.now().minusDays(1).toString()
        val todayDayOfWeek = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)

        val habits = habitDao.getAllHabitsOnce(userId)
        habits.forEach { habit ->
            val shouldHaveDoneYesterday = isHabitForDay(habit, yesterday, LocalDate.now().minusDays(1).dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US))

            if (shouldHaveDoneYesterday) {
                val yesterdayCompletion = completionDao.getCompletion(habit.id, yesterday)
                val todayCompletion = completionDao.getCompletion(habit.id, today)

                if (yesterdayCompletion?.completed != true && todayCompletion?.completed != true) {
                    if (habit.currentStreak > 0) {
                        habitDao.updateStreak(habit.id, 0, habit.bestStreak, System.currentTimeMillis())
                    }
                }
            }
        }
    }

    suspend fun resetWeeklyProgressIfNeeded(userId: String) {
        val today = LocalDate.now()
        val startOfWeek = today.with(java.time.DayOfWeek.MONDAY)

        val habits = habitDao.getAllHabitsOnce(userId)
        habits.forEach { habit ->
            val lastUpdate = java.time.Instant.ofEpochMilli(habit.updatedAt)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()

            if (lastUpdate.isBefore(startOfWeek)) {
                habitDao.updateProgress(
                    habitId = habit.id,
                    progress = 0f,
                    done = 0,
                    updatedAt = System.currentTimeMillis()
                )
            }
        }
    }

    // get habit by id and user id
    suspend fun getHabitByIdByUserId(habitId: String, userId: String) : HabitEntity? {
        return habitDao.getHabitByIdAndByUserId(userId, habitId)
    }

    // insert
    suspend fun insertHabit(habit: HabitEntity){
        habitDao.insertHabit(habit)
    }

    // update
    suspend fun updateHabit(habit: HabitEntity){
        habitDao.updateHabit(habit)
    }

    // delete
    suspend fun deleteHabit(habitId: String){
        habitDao.deleteHabit(habitId)
    }



    private suspend fun calculateCurrentStreak(habitId: String, fromDate: String): Int {
        var streak = 0
        var currentDate = LocalDate.parse(fromDate).minusDays(1)

        while (true) {
            val completion = completionDao.getCompletion(habitId, currentDate.toString())
            if (completion?.completed == true) {
                streak++
                currentDate = currentDate.minusDays(1)
            } else {
                break
            }
        }
        return streak
    }

    private fun isHabitForDay(habit: HabitEntity, date: String, dayOfWeek: String): Boolean {
        return when (habit.frequency) {
            "Every day" -> true
            "Weekdays" -> dayOfWeek !in listOf("Sat", "Sun")
            "Weekends" -> dayOfWeek in listOf("Sat", "Sun")
            "Every other day" -> {
                val startOfYear = LocalDate.of(LocalDate.parse(date).year, 1, 1)
                val dayIndex = LocalDate.parse(date).toEpochDay() - startOfYear.toEpochDay()
                (dayIndex % 2).toInt() == 0
            }
            "Custom" -> habit.customDays?.split(",")?.contains(dayOfWeek) == true
            else -> true
        }
    }

}