package com.rywent.pixelhabit.di

import android.content.Context
import androidx.room.Room
import com.rywent.pixelhabit.data.local.AppDatabase
import com.rywent.pixelhabit.data.local.dao.HabitCompletionDao
import com.rywent.pixelhabit.data.local.dao.HabitDao
import com.rywent.pixelhabit.data.local.dao.LifestyleDao
import com.rywent.pixelhabit.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providerDatabase(@ApplicationContext context: Context) : AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pixelhabit_db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideHabitDao(database: AppDatabase): HabitDao {
        return database.habitDao()
    }

    @Provides
    fun provideLifestyleDao(database: AppDatabase): LifestyleDao {
        return database.lifestyleDao()
    }

    @Provides
    fun provideHabitCompletionDao(database: AppDatabase): HabitCompletionDao {
        return database.habitCompletionDao()
    }
}