package com.rywent.pixelhabit.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object NotificationChannels {
    const val CHANNEL_HABIT_REMINDER = "habit_reminder_channel"
    const val CHANNEL_STREAK = "streak_channel"
    const val CHANNEL_MOTIVATION = "motivation_channel"

    fun createChannels(context: Context) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)

        val habitChannel = NotificationChannel(
            CHANNEL_HABIT_REMINDER,
            "Habit Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminders to complete your habits"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 200, 100, 200)
            setShowBadge(true)
        }


        val streakChannel = NotificationChannel(
            CHANNEL_STREAK,
            "Streak Updates",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Updates about your habit streaks"
            setShowBadge(true)
        }


        val motivationChannel = NotificationChannel(
            CHANNEL_MOTIVATION,
            "Motivation",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Daily motivation to keep you going"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 100)
            setShowBadge(false)
        }

        notificationManager.createNotificationChannels(
            listOf(habitChannel, streakChannel, motivationChannel)
        )
    }
}