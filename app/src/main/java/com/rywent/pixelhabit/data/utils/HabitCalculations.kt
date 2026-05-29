package com.rywent.pixelhabit.data.utils

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

fun shouldResetStreak(
    habit: HabitEntity,
    yesterday: String,
    yesterdayDayOfWeek: String,
    today: String,
    yesterdayCompleted: Boolean,
    todayCompleted: Boolean
): Boolean {
    val shouldHaveDoneYesterday = isHabitScheduledForDate(habit, yesterday, yesterdayDayOfWeek)

    return shouldHaveDoneYesterday && !yesterdayCompleted && !todayCompleted && habit.currentStreak > 0
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