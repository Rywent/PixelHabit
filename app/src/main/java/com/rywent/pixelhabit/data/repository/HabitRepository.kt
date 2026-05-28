package com.rywent.pixelhabit.data.repository

import com.rywent.pixelhabit.data.local.dao.HabitCompletionDao
import com.rywent.pixelhabit.data.local.dao.HabitDao
import com.rywent.pixelhabit.data.local.entity.HabitCompletionEntity
import com.rywent.pixelhabit.data.local.entity.HabitEntity
import com.rywent.pixelhabit.data.model.HabitWithCompletion
import kotlinx.coroutines.flow.Flow

class HabitRepository(
    private val habitDao: HabitDao,
    private val completionDao: HabitCompletionDao
) {
    // get habits for today
    fun getHabitsForToday(userId: String, today: String): Flow<List<HabitWithCompletion>> {
        return habitDao.getHabitsForToday(userId, today)
    }

    // get all habits by user id
    fun getAllHabits(userId: String) : Flow<List<HabitEntity>> {
        return habitDao.getAllHabits(userId)
    }

    // mark/unmark as completed
    suspend fun toggleCompletion(habitId: String, date: String, completed: Boolean) {
        val completion = HabitCompletionEntity(
            habitId = habitId,
            date = date,
            completed = completed,
            completedAt = if (completed) System.currentTimeMillis() else null
        )
        completionDao.upsertCompletion(completion)
    }

    // get habit by id and user id
    suspend fun getHabitByIdByUserId(habitId: String, userId: String) : HabitEntity? {
        return habitDao.getHabitByIdAndByUserId(userId, habitId)
    }

    // insert
    suspend fun insertHabit(habit: HabitEntity){
        habitDao.insertHabit(habit)
    }

    // update
    suspend fun updateHabit(habit: HabitEntity){
        habitDao.updateHabit(habit)
    }

    // delete
    suspend fun deleteHabit(habitId: String){
        habitDao.deleteHabit(habitId)
    }

}