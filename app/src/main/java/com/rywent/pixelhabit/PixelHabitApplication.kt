package com.rywent.pixelhabit

import android.app.Application
import com.rywent.pixelhabit.data.repository.HabitRepository
import com.rywent.pixelhabit.data.repository.UserRepository
import com.rywent.pixelhabit.notifications.habit.HabitNotificationManager
import com.rywent.pixelhabit.notifications.habit.HabitNotificationScheduler
import com.rywent.pixelhabit.notifications.motivation.MotivationNotificationManager
import com.rywent.pixelhabit.notifications.motivation.MotivationNotificationScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PixelHabitApplication : Application() {
    @Inject
    lateinit var habitNotificationManager: HabitNotificationManager

    @Inject
    lateinit var habitNotificationScheduler: HabitNotificationScheduler

    @Inject
    lateinit var habitRepository: HabitRepository

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var motivationNotificationManager: MotivationNotificationManager

    @Inject
    lateinit var motivationNotificationScheduler: MotivationNotificationScheduler


    override fun onCreate() {
        super.onCreate()

        motivationNotificationScheduler.scheduleMotivationNotifications()
    }
}