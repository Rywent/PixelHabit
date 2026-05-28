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
        ORDER BY h.createdAt DESC
    """)
    fun getHabitsForToday(userId: String, today: String): Flow<List<HabitWithCompletion>>

    // acquire habits based on the time of day
    @Query("SELECT * FROM habits WHERE userId = :userId AND timeOfDay = :timeOfDay")
    fun getHabitsByTimeOfDay(userId: String, timeOfDay: String): Flow<List<HabitEntity>>

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