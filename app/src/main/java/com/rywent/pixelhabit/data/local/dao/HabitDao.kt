package com.rywent.pixelhabit.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rywent.pixelhabit.data.local.entity.HabitEntity
import com.rywent.pixelhabit.data.model.HabitWithCompletion
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    // get all habits by user id
    @Query("select * from habits where userId = :userId")
    fun getAllHabits(userId: String): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE userId = :userId")
    suspend fun getAllHabitsOnce(userId: String): List<HabitEntity>

    // get habit by id
    @Query("select * from habits where id = :habitId")
    suspend fun getHabitById(habitId: String): HabitEntity?

    // get habit by id and user id
    @Query("select * from habits where userId = :userId and id = :id")
    suspend fun getHabitByIdAndByUserId(userId: String, id: String) : HabitEntity?

    // get today habit
    @Query("""
        SELECT h.*, 
               COALESCE(c.completed, 0) AS isCompleted,
               c.completedAt
        FROM habits h
        LEFT JOIN habit_completions c 
            ON h.id = c.habitId AND c.date = :today
        WHERE h.userId = :userId
        GROUP BY h.id
        ORDER BY h.createdAt DESC
    """)
    fun getHabitsForToday(userId: String, today: String): Flow<List<HabitWithCompletion>>

    // get today habits once
    @Query("""
    SELECT h.*, 
           COALESCE(c.completed, 0) AS isCompleted,
           c.completedAt
    FROM habits h
    LEFT JOIN habit_completions c 
        ON h.id = c.habitId AND c.date = :today
    WHERE h.userId = :userId
    GROUP BY h.id
    ORDER BY h.createdAt DESC
""")
    suspend fun getHabitsForTodayOnce(userId: String, today: String): List<HabitWithCompletion>

    // acquire habits based on the time of day
    @Query("SELECT * FROM habits WHERE userId = :userId AND timeOfDay = :timeOfDay")
    fun getHabitsByTimeOfDay(userId: String, timeOfDay: String): Flow<List<HabitEntity>>

    // update progress
    @Query("UPDATE habits SET weeklyProgress = :progress, weeklyDone = :done, updatedAt = :updatedAt WHERE id = :habitId")
    suspend fun updateProgress(habitId: String, progress: Float, done: Int, updatedAt: Long)

    // update streak
    @Query("UPDATE habits SET currentStreak = :streak, bestStreak = :bestStreak, updatedAt = :updatedAt WHERE id = :habitId")
    suspend fun updateStreak(habitId: String, streak: Int, bestStreak: Int, updatedAt: Long)
    // create habit
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertHabit(habit: HabitEntity)

    // update
    @Update
    suspend fun updateHabit(habit: HabitEntity)

    // delete
    @Query("delete from habits where id = :id")
    suspend fun deleteHabit(id: String)
}