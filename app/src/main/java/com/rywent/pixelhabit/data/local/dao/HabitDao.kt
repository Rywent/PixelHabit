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

    @Query("select * from habits where userId = :userId")
    suspend fun getAllHabitsOnce(userId: String): List<HabitEntity>

    // get habit by id
    @Query("select * from habits where id = :habitId")
    suspend fun getHabitById(habitId: String): HabitEntity?

    // get habit by id and user id
    @Query("select * from habits where userId = :userId and id = :id")
    suspend fun getHabitByIdAndByUserId(userId: String, id: String) : HabitEntity?

    @Query("select * from habits where lifestyleId = :lifestyleId and userId = :userId")
    fun getHabitsByLifestyleId(lifestyleId: String, userId: String): Flow<List<HabitEntity>>

    // НОВЫЙ МЕТОД - получить привычки по lifestyleId (suspend версия)
    @Query("select * from habits where lifestyleId = :lifestyleId and userId = :userId")
    suspend fun getHabitsByLifestyleIdOnce(lifestyleId: String, userId: String): List<HabitEntity>

    // get today habit
    @Query("""
        select h.*, 
               coalesce(c.completed, 0) as isCompleted,
               c.completedAt
        from habits h
        left join habit_completions c 
            on h.id = c.habitId and c.date = :today
        where h.userId = :userId
        group by h.id
        order by h.createdAt desc
    """)
    fun getHabitsForToday(userId: String, today: String): Flow<List<HabitWithCompletion>>

    // get today habits once
    @Query("""
    select h.*, 
           coalesce(c.completed, 0) as isCompleted,
           c.completedAt
    from habits h
    left join habit_completions c 
        on h.id = c.habitId and c.date = :today
    where h.userId = :userId
    group by h.id
    order by h.createdAt desc
""")
    suspend fun getHabitsForTodayOnce(userId: String, today: String): List<HabitWithCompletion>

    // acquire habits based on the time of day
    @Query("select * from habits where userId = :userId and timeOfDay = :timeOfDay")
    fun getHabitsByTimeOfDay(userId: String, timeOfDay: String): Flow<List<HabitEntity>>

    // update progress
    @Query("update habits set weeklyProgress = :progress, weeklyDone = :done, updatedAt = :updatedAt where id = :habitId")
    suspend fun updateProgress(habitId: String, progress: Float, done: Int, updatedAt: Long)

    // update streak
    @Query("update habits set currentStreak = :streak, bestStreak = :bestStreak, updatedAt = :updatedAt where id = :habitId")
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

    @Query("delete from habits")
    suspend fun deleteAll()
}