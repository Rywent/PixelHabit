package com.rywent.pixelhabit.data.utils

import com.rywent.pixelhabit.data.local.entity.HabitCompletionEntity
import com.rywent.pixelhabit.data.local.entity.HabitEntity
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

object LifestyleStatsUtils {

    data class LifestyleStats(
        val totalHabitsCount: Int = 0,
        val completedHabitsToday: Int = 0,
        val weeklyActivity: Int = 0,
        val monthlyGoal: Int = 0,
        val monthlyProgress: Int = 0
    )

    fun calculateLifestyleStats(
        habits: List<HabitEntity>,
        completions: List<HabitCompletionEntity>
    ): LifestyleStats {
        if (habits.isEmpty()) {
            return LifestyleStats()
        }

        val today = LocalDate.now()
        val todayString = today.toString()
        val todayDayOfWeek = today.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)


        val currentMonth = today.monthValue
        val currentYear = today.year
        val firstDayOfMonth = LocalDate.of(currentYear, currentMonth, 1)
        val lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth())


        val todayCompletions = completions.filter { it.date == todayString }


        val weekFields = WeekFields.of(Locale.getDefault())
        val currentWeek = today.get(weekFields.weekOfWeekBasedYear())

        val weekCompletions = completions.filter { completion ->
            val completionDate = LocalDate.parse(completion.date)
            completionDate.get(weekFields.weekOfWeekBasedYear()) == currentWeek &&
                    completionDate.year == today.year
        }


        val monthCompletions = completions.filter { completion ->
            val completionDate = LocalDate.parse(completion.date)
            completionDate.monthValue == currentMonth &&
                    completionDate.year == currentYear
        }

        var totalHabits = 0
        var completedToday = 0
        var weeklyActivity = 0
        var monthlyProgress = 0
        var monthlyGoal = 0

        habits.forEach { habit ->
            totalHabits++

            val isScheduledToday = isHabitScheduledForDate(habit, todayString, todayDayOfWeek)
            if (isScheduledToday) {
                val completion = todayCompletions.find { it.habitId == habit.id }
                if (completion?.completed == true) {
                    completedToday++
                }
            }

            val habitWeekCompletions = weekCompletions.filter { it.habitId == habit.id }
            weeklyActivity += habitWeekCompletions.count { it.completed }


            val habitMonthCompletions = monthCompletions.filter { it.habitId == habit.id }
            monthlyProgress += habitMonthCompletions.count { it.completed }


            monthlyGoal += calculateMonthlyScheduledCount(habit, firstDayOfMonth, lastDayOfMonth)
        }

        return LifestyleStats(
            totalHabitsCount = totalHabits,
            completedHabitsToday = completedToday,
            weeklyActivity = weeklyActivity,
            monthlyGoal = monthlyGoal,
            monthlyProgress = monthlyProgress.coerceAtMost(monthlyGoal)
        )
    }
    private fun calculateMonthlyScheduledCount(
        habit: HabitEntity,
        firstDayOfMonth: LocalDate,
        lastDayOfMonth: LocalDate
    ): Int {
        var scheduledCount = 0
        var currentDate = firstDayOfMonth

        while (!currentDate.isAfter(lastDayOfMonth)) {
            val dayOfWeek = currentDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)

            if (isHabitScheduledForDate(habit, currentDate.toString(), dayOfWeek)) {
                scheduledCount++
            }

            currentDate = currentDate.plusDays(1)
        }

        return scheduledCount
    }

    private fun isHabitScheduledForDate(
        habit: HabitEntity,
        date: String,
        dayOfWeek: String
    ): Boolean {
        return when (habit.frequency.lowercase()) {
            "daily", "every day" -> true
            "weekly", "weekdays", "weekends", "every other day", "custom" -> {
                if (habit.customDays.isNullOrEmpty()) return false
                val scheduledDays = habit.customDays.split(",").map { it.trim() }

                when {
                    scheduledDays.any { it.length <= 3 && it[0].isLetter() } -> {
                        scheduledDays.contains(dayOfWeek)
                    }
                    else -> {
                        val scheduledDates = scheduledDays.mapNotNull { it.toIntOrNull() }
                        val dayOfMonth = LocalDate.parse(date).dayOfMonth
                        scheduledDates.contains(dayOfMonth)
                    }
                }
            }
            "monthly" -> {
                if (habit.customDays.isNullOrEmpty()) return false
                val scheduledDates = habit.customDays.split(",").mapNotNull { it.trim().toIntOrNull() }
                val dayOfMonth = LocalDate.parse(date).dayOfMonth
                scheduledDates.contains(dayOfMonth)
            }
            else -> false
        }
    }
}