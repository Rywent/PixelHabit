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
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun upsertCompletion(completion: HabitCompletionEntity)

    // get marker for a specific day
    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId AND date = :date")
    suspend fun getCompletion(habitId: String, date: String): HabitCompletionEntity?

    //get all marks for the day
    @Query("SELECT * FROM habit_completions WHERE date = :date")
    fun getCompletionsByDate(date: String): Flow<List<HabitCompletionEntity>>

    // get habit history
    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY date DESC")
    fun getCompletionsByHabit(habitId: String): Flow<List<HabitCompletionEntity>>

    // delete old records
    @Query("DELETE FROM habit_completions WHERE date < :beforeDate")
    suspend fun deleteOldCompletions(beforeDate: String)
}