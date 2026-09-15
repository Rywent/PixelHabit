package com.rywent.pixelhabit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rywent.pixelhabit.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: FocusSessionEntity)

    @Query("SELECT * FROM focus_sessions WHERE userId = :userId ORDER BY finishedAt DESC")
    fun getAll(userId: String): Flow<List<FocusSessionEntity>>

    @Query("""
        SELECT * FROM focus_sessions 
        WHERE userId = :userId AND date = :date AND phase = 'FOCUS'
        ORDER BY finishedAt DESC
    """)
    fun getFocusSessionsForDate(userId: String, date: String): Flow<List<FocusSessionEntity>>

    @Query("""
        SELECT COALESCE(SUM(actualSeconds), 0) FROM focus_sessions 
        WHERE userId = :userId AND date = :date AND phase = 'FOCUS' AND completed = 1
    """)
    fun getTotalFocusSecondsForDate(userId: String, date: String): Flow<Int>

    @Query("""
        SELECT COALESCE(AVG(actualSeconds), 0) FROM focus_sessions 
        WHERE userId = :userId AND habitId = :habitId AND phase = 'FOCUS' AND completed = 1
    """)
    fun getAvgFocusSecondsForHabit(userId: String, habitId: String): Flow<Float>

    @Query("""
        SELECT COALESCE(SUM(actualSeconds), 0) FROM focus_sessions 
        WHERE userId = :userId AND habitId = :habitId AND phase = 'FOCUS' AND completed = 1
    """)
    suspend fun getTotalFocusSecondsForHabit(userId: String, habitId: String): Int

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE userId = :userId AND date = :date AND phase = 'FOCUS' AND completed = 1")
    fun getCompletedFocusCountForDate(userId: String, date: String): Flow<Int>

    @Query("""
        SELECT COALESCE(SUM(actualSeconds), 0) 
        FROM focus_sessions 
        WHERE userId = :userId AND habitId = :habitId AND date = :date
    """)
    fun getTodayFocusSecondsForHabit(userId: String, habitId: String, date: String): Flow<Int>

    @Query("""
        SELECT COALESCE(SUM(actualSeconds), 0) 
        FROM focus_sessions 
        WHERE userId = :userId AND habitId = :habitId AND date BETWEEN :startDate AND :endDate
    """)
    fun getWeeklyFocusSecondsForHabit(
        userId: String,
        habitId: String,
        startDate: String,
        endDate: String
    ): Flow<Int>
}
