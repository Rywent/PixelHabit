package com.rywent.pixelhabit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rywent.pixelhabit.data.local.entity.HabitCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCompletionDao {

    // insert marker
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCompletion(completion: HabitCompletionEntity)

    @Query("update habit_completions set completed = :completed, completedAt = :completedAt where habitId = :habitId and date = :date")
    suspend fun updateCompletion(habitId: String, date: String, completed: Boolean, completedAt: Long?)

    // get all completion for habit
    @Query("select * from habit_completions where habitId = :habitId order by date desc")
    fun getCompletionsForHabit(habitId: String): Flow<List<HabitCompletionEntity>>


    // get week completion
    @Query("""
    select * from habit_completions 
    where date between :startDate and :endDate 
    and completed = 1
""")
    suspend fun getCompletionsBetween(startDate: String, endDate: String): List<HabitCompletionEntity>

    @Query("""
    select * from habit_completions 
    where date between :startDate and :endDate 
    and completed = 1
""")
    fun getCompletionsBetweenFlow(startDate: String, endDate: String): Flow<List<HabitCompletionEntity>>


    // get marker for a specific day
    @Query("select * from habit_completions where habitId = :habitId and date = :date")
    suspend fun getCompletion(habitId: String, date: String): HabitCompletionEntity?
    @Query("select * from habit_completions")
    suspend fun getAllCompletions(): List<HabitCompletionEntity>

    //get all marks for the day
    @Query("select * from habit_completions where date = :date")
    fun getCompletionsByDate(date: String): Flow<List<HabitCompletionEntity>>

    @Query("select * from habit_completions where date = :date")
    suspend fun getCompletionsByDateOnce(date: String): List<HabitCompletionEntity>

    // get habit history
    @Query("select * from habit_completions where habitId = :habitId order by date desc")
    fun getCompletionsByHabit(habitId: String): Flow<List<HabitCompletionEntity>>

    // delete old records
    @Query("delete from habit_completions where date < :beforeDate")
    suspend fun deleteOldCompletions(beforeDate: String)

    @Query("delete from habit_completions")
    suspend fun deleteAll()
}