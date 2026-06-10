package com.rywent.pixelhabit.notifications.motivation

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

class MotivationNotificationManager(
    private val context: Context
) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showMotivationNotification(
        timeOfDay: String,
        notificationId: Int,
        enabled: Boolean = true
    ) {
        if (!enabled) return

        val message = MotivationNotificationMessages.getByTimeOfDay(timeOfDay)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationChannels.CHANNEL_MOTIVATION)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(message.title)
            .setContentText(message.message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    fun cancelNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
}