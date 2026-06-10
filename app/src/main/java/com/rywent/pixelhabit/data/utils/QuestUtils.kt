package com.rywent.pixelhabit.data.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object QuestUtils {

    fun calculateDaysLeft(startDate: String, endDate: String): Int {
        val start = LocalDate.parse(startDate)
        val end = LocalDate.parse(endDate)
        val today = LocalDate.now()

        return if (today.isAfter(end)) {
            0
        } else {
            ChronoUnit.DAYS.between(today, end).toInt() + 1
        }
    }

    fun calculateCurrentDay(startDate: String): Int {
        val start = LocalDate.parse(startDate)
        val today = LocalDate.now()

        return if (today.isBefore(start)) {
            0
        } else {
            ChronoUnit.DAYS.between(start, today).toInt() + 1
        }
    }

    fun calculateCompletionPercent(currentDay: Int, totalDays: Int): Float {
        return if (totalDays > 0) {
            (currentDay.toFloat() / totalDays).coerceIn(0f, 1f)
        } else 0f
    }

    fun isQuestActive(startDate: String, endDate: String, isCompleted: Boolean): Boolean {
        if (isCompleted) return false
        val today = LocalDate.now()
        val start = LocalDate.parse(startDate)
        val end = LocalDate.parse(endDate)

        return !today.isBefore(start) && !today.isAfter(end)
    }

    fun formatDate(date: String): String {
        return try {
            val localDate = LocalDate.parse(date)
            val formatter = DateTimeFormatter.ofPattern("dd MMM")
            localDate.format(formatter)
        } catch (e: Exception) {
            date
        }
    }
}