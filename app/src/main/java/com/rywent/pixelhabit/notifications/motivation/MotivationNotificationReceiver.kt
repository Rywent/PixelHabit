package com.rywent.pixelhabit.notifications.motivation

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.rywent.pixelhabit.PixelHabitApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MotivationNotificationReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val timeOfDay = intent.getStringExtra("time_of_day") ?: return
        val notificationId = intent.getIntExtra("notification_id", 0)

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            Log.w("MotivationReceiver", "No POST_NOTIFICATIONS permission")
            return
        }

        scope.launch {
            try {
                val app = context.applicationContext as PixelHabitApplication

                val (_, _, motivationEnabled) = app.userRepository.getNotificationSettings("default_user")

                app.motivationNotificationManager.showMotivationNotification(
                    timeOfDay = timeOfDay,
                    notificationId = notificationId,
                    enabled = motivationEnabled
                )
            } catch (e: Exception) {
                Log.e("MotivationReceiver", "Error: ${e.message}")
            }
        }
    }
}