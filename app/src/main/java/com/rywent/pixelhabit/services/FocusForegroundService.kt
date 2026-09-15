package com.rywent.pixelhabit.services

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.rywent.pixelhabit.notifications.focus.FocusNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FocusForegroundService : Service() {

    @Inject
    lateinit var notificationManager: FocusNotificationManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val time = intent?.getStringExtra("time") ?: "00:00"
        val phase = intent?.getStringExtra("phase") ?: "Focus"

        val progressMax = intent?.getIntExtra("progress_max", 100) ?: 100
        val progressCurrent = intent?.getIntExtra("progress_current", 0) ?: 0

        val notification = notificationManager.createTimerNotification(
            time = time,
            phase = phase,
            progressMax = progressMax,
            progressCurrent = progressCurrent
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                1001,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(1001, notification)
        }

        val action = intent?.action
        if (action == "ACTION_UPDATE_TIME") {
            notificationManager.updateNotification(
                time = time,
                phase = phase,
                progressMax = progressMax,
                progressCurrent = progressCurrent
            )
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        notificationManager.cancelNotification()
        super.onDestroy()
    }
}