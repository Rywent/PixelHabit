package com.rywent.pixelhabit.notifications.habit

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rywent.pixelhabit.MainActivity
import com.rywent.pixelhabit.R
import com.rywent.pixelhabit.notifications.NotificationChannels

class HabitNotificationManager(
    private val context: Context
) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showHabitReminder(
        habitName: String,
        habitId: String,
        scheduledTime: String? = null,
        notificationId: Int,
        enabled: Boolean = true
    ) {
        if (!enabled) return

        val (title, text) = HabitNotificationMessages.getRandomNotification(habitName, scheduledTime)

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("habit_id", habitId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationChannels.CHANNEL_HABIT_REMINDER)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }


    fun cancelNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
}