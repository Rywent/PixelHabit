package com.rywent.pixelhabit.notifications.motivation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.net.toUri
import java.time.LocalDateTime
import java.time.ZoneId

class MotivationNotificationScheduler(
    private val context: Context
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    data class MotivationTimeSlot(
        val timeOfDay: String,
        val hour: Int,
        val minute: Int,
        val notificationId: Int
    )

    private val timeSlots = listOf(
        MotivationTimeSlot("morning", 8, 0, 1001),
        MotivationTimeSlot("morning", 10, 0, 1002),
        MotivationTimeSlot("afternoon", 12, 0, 1003),
        MotivationTimeSlot("afternoon", 14, 0, 1004),
        MotivationTimeSlot("evening", 18, 0, 1005),
        MotivationTimeSlot("evening", 20, 0, 1006)
    )

    fun scheduleMotivationNotifications() {
        cancelAllMotivationNotifications()

        val now = LocalDateTime.now()

        timeSlots.forEach { slot ->
            scheduleForTimeSlot(slot, now)
        }
    }

    private fun scheduleForTimeSlot(slot: MotivationTimeSlot, now: LocalDateTime) {
        var scheduledTime = LocalDateTime.now()
            .withHour(slot.hour)
            .withMinute(slot.minute)
            .withSecond(0)
            .withNano(0)

        if (scheduledTime.isBefore(now)) {
            scheduledTime = scheduledTime.plusDays(1)
        }

        val triggerTime = scheduledTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val intent = Intent(context, MotivationNotificationReceiver::class.java).apply {
            putExtra("time_of_day", slot.timeOfDay)
            putExtra("notification_id", slot.notificationId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            slot.notificationId,
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
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
                requestExactAlarmPermission()
            }
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        } catch (e: Exception) {
            Log.e("MotivationScheduler", "Failed to schedule", e)
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
                Log.e("MotivationScheduler", "Cannot open exact alarm settings", e)
            }
        }
    }

    fun cancelAllMotivationNotifications() {
        timeSlots.forEach { slot ->
            val intent = Intent(context, MotivationNotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                slot.notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }


}