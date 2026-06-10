package com.rywent.pixelhabit.notifications

import android.content.Context
import androidx.work.WorkManager
import com.rywent.pixelhabit.notifications.habit.HabitNotificationManager
import com.rywent.pixelhabit.notifications.habit.HabitNotificationScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideHabitNotificationManager(
        @ApplicationContext context: Context
    ): HabitNotificationManager = HabitNotificationManager(context)

    @Provides
    @Singleton
    fun provideHabitNotificationScheduler(
        @ApplicationContext context: Context
    ): HabitNotificationScheduler = HabitNotificationScheduler(context)

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager = WorkManager.getInstance(context)
}