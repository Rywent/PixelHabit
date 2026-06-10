package com.rywent.pixelhabit.notifications.habit

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.rywent.pixelhabit.data.local.entity.HabitEntity
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import androidx.core.net.toUri

class HabitNotificationScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun scheduleHabitReminder(habit: HabitEntity) {
        cancelHabitReminders(habit.id)

        val time = parseTime(habit.specificTime)
        val scheduledDays = parseScheduledDays(habit.customDays, habit.frequency)

        scheduledDays.forEach { dayOfWeek ->
            scheduleForSpecificDay(habit, time, dayOfWeek)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun scheduleForSpecificDay(habit: HabitEntity, time: LocalTime, targetDay: Int) {
        val now = LocalDateTime.now()
        var nextDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.of(targetDay)))

        val targetDateTime = nextDate.atTime(time)
        if (targetDateTime.isBefore(now)) {
            nextDate = nextDate.plusWeeks(1)
        }

        val triggerTime = nextDate.atTime(time)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val notificationId = (habit.id.hashCode() and 0x7FFFFFFF) + targetDay

        val intent = Intent(context, HabitNotificationReceiver::class.java).apply {
            putExtra("habit_id", habit.id)
            putExtra("habit_name", habit.name)
            putExtra("notification_id", notificationId)
            putExtra("scheduled_time", String.format("%02d:%02d", time.hour, time.minute))
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
                Log.d("NotificationScheduler", "Exact alarm scheduled for ${habit.name} at $nextDate $time")
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
                Log.w("NotificationScheduler", "No exact alarm permission. Used inexact for ${habit.name}")

                requestExactAlarmPermission()
            }
        } catch (e: SecurityException) {
            Log.e("NotificationScheduler", "SecurityException: No permission for exact alarms", e)
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        } catch (e: Exception) {
            Log.e("NotificationScheduler", "Failed to schedule alarm", e)
        }
    }

    private fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }

    fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !canScheduleExactAlarms()) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = "package:${context.packageName}".toUri()
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("NotificationScheduler", "Cannot open exact alarm settings", e)
            }
        }
    }

    fun cancelHabitReminders(habitId: String) {
        for (day in 1..7) {
            val notificationId = (habitId.hashCode() and 0x7FFFFFFF) + day
            val intent = Intent(context, HabitNotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun parseTime(timeString: String?): LocalTime {
        if (timeString.isNullOrBlank()) return LocalTime.of(10, 0)

        return try {
            val trimmed = timeString.trim().uppercase()
            if (trimmed.matches(Regex("\\d{1,2}:\\d{2}"))) {
                val parts = trimmed.split(":")
                LocalTime.of(parts[0].toInt(), parts[1].toInt())
            } else {
                LocalTime.parse(trimmed.split(" ")[0])
            }
        } catch (e: Exception) {
            Log.e("NotificationScheduler", "Failed to parse time: $timeString", e)
            LocalTime.of(10, 0)
        }
    }

    private fun parseScheduledDays(customDays: String?, frequency: String): List<Int> {
        return when (frequency) {
            "Every day" -> (1..7).toList()
            "Weekdays" -> listOf(1, 2, 3, 4, 5)
            "Weekends" -> listOf(6, 7)
            "Every other day" -> listOf(1, 3, 5, 7)
            "Custom" -> customDays?.split(",")?.mapNotNull {
                when (it.trim()) {
                    "Mon" -> 1; "Tue" -> 2; "Wed" -> 3; "Thu" -> 4
                    "Fri" -> 5; "Sat" -> 6; "Sun" -> 7
                    else -> null
                }
            } ?: (1..7).toList()
            else -> (1..7).toList()
        }
    }
}