package com.rywent.pixelhabit.data.utils

import com.rywent.pixelhabit.data.local.entity.HabitCompletionEntity
import com.rywent.pixelhabit.data.local.entity.HabitEntity
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

fun isHabitScheduledForDate(habit: HabitEntity, date: String, dayOfWeek: String): Boolean {
    return habit.customDays?.split(",")?.contains(dayOfWeek) == true
}

fun findPreviousScheduledDate(habit: HabitEntity, fromDate: LocalDate): LocalDate? {
    var checkDate = fromDate.minusDays(1)
    var daysChecked = 0

    while (daysChecked < 30) {
        val dayOfWeek = checkDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)
        if (isHabitScheduledForDate(habit, checkDate.toString(), dayOfWeek)) {
            return checkDate
        }
        checkDate = checkDate.minusDays(1)
        daysChecked++
    }
    return null
}

fun isScheduledDay(habit: HabitEntity, date: LocalDate): Boolean {
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)
    return isHabitScheduledForDate(habit, date.toString(), dayOfWeek)
}

fun shouldResetWeeklyProgress(habit: HabitEntity, today: LocalDate): Boolean {
    val startOfWeek = today.with(java.time.DayOfWeek.MONDAY)
    val lastUpdate = java.time.Instant.ofEpochMilli(habit.updatedAt)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDate()

    return lastUpdate.isBefore(startOfWeek)
}

fun calculateWeeklyDone(currentDone: Int, completed: Boolean): Int {
    return if (completed) {
        currentDone + 1
    } else {
        maxOf(0, currentDone - 1)
    }
}

fun calculateWeeklyProgress(weeklyDone: Int, weeklyGoal: Int): Float {
    return if (weeklyGoal > 0) {
        weeklyDone.toFloat() / weeklyGoal
    } else 0f
}

fun calculateNewStreak(currentStreak: Int, prevCompleted: Boolean): Int {
    return if (prevCompleted) {
        currentStreak + 1
    } else {
        1
    }
}

fun calculateBestStreak(newStreak: Int, currentBest: Int): Int {
    return maxOf(newStreak, currentBest)
}


data class DayStatus(
    val isCompleted: Boolean,
    val isPostponed: Boolean,
    val isScheduled: Boolean
)

fun getDayStatus(
    habit: HabitEntity,
    date: LocalDate,
    completion: HabitCompletionEntity?
): DayStatus {
    val isScheduled = isScheduledDay(habit, date)
    val isCompleted = completion?.completed == true
    val isPostponed = completion?.isPostponed == true

    return DayStatus(
        isCompleted = isCompleted,
        isPostponed = isPostponed,
        isScheduled = isScheduled
    )
}