package com.rywent.pixelhabit.di

import com.rywent.pixelhabit.data.local.dao.HabitCompletionDao
import com.rywent.pixelhabit.data.local.dao.HabitDao
import com.rywent.pixelhabit.data.local.dao.LifestyleDao
import com.rywent.pixelhabit.data.local.dao.QuestDao
import com.rywent.pixelhabit.data.local.dao.UserDao
import com.rywent.pixelhabit.data.repository.HabitRepository
import com.rywent.pixelhabit.data.repository.LifestyleRepository
import com.rywent.pixelhabit.data.repository.QuestRepository
import com.rywent.pixelhabit.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideHabitRepository(
        habitDao: HabitDao,
        completionDao: HabitCompletionDao
    ): HabitRepository {
        return HabitRepository(habitDao, completionDao)
    }

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepository(userDao)
    }

    @Provides
    @Singleton
    fun provideLifestyleRepository(lifestyleDao: LifestyleDao): LifestyleRepository {
        return LifestyleRepository(lifestyleDao)
    }

    @Provides
    @Singleton
    fun provideQuestRepository(questDao: QuestDao): QuestRepository {
        return QuestRepository(questDao)
    }
}