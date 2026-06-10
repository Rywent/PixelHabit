package com.rywent.pixelhabit.notifications.habit

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

class HabitNotificationReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "HabitReceiver"
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return


        val habitId = intent.getStringExtra("habit_id") ?: return
        val habitName = intent.getStringExtra("habit_name") ?: "Привычка"
        val notificationId = intent.getIntExtra("notification_id", 0)
        val scheduledTime = intent.getStringExtra("scheduled_time")

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Нет permission POST_NOTIFICATIONS")
            return
        }

        scope.launch {
            try {
                val app = context.applicationContext as PixelHabitApplication

                app.habitNotificationManager.showHabitReminder(
                    habitName = habitName,
                    habitId = habitId,
                    scheduledTime = scheduledTime,
                    notificationId = notificationId
                )


                val habit = app.habitRepository.getHabitByIdByUserId(habitId, "default_user")
                habit?.let { app.habitNotificationScheduler.scheduleHabitReminder(it) }

            } catch (e: Exception) {
                Log.w(TAG, "Error: $e")
            }
        }
    }
}