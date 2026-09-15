package com.rywent.pixelhabit.notifications.focus

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rywent.pixelhabit.MainActivity
import com.rywent.pixelhabit.R
import com.rywent.pixelhabit.notifications.NotificationChannels
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class FocusNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationId = 1001

    fun createTimerNotification(
        time: String,
        phase: String,
        progressMax: Int,
        progressCurrent: Int
    ): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "PixelHabit • ${phase.lowercase().replace("_", "")}"
        val text = "Remaining: $time"

        return NotificationCompat.Builder(context, NotificationChannels.CHANNEL_FOCUS)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(title)
            .setContentText(text)
            .setOngoing(true)
            .setOnlyAlertOnce(true) // Prevents resetting sound/vibration on every tick update
            .setContentIntent(pendingIntent)
            .setProgress(progressMax, progressCurrent, false)
            .build()
    }

    fun updateNotification(
        time: String,
        phase: String,
        progressMax: Int,
        progressCurrent: Int
    ) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification = createTimerNotification(time, phase, progressMax, progressCurrent)
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    fun cancelNotification() {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
}