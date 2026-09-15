package com.rywent.pixelhabit.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rywent.pixelhabit.data.local.entity.FocusPresetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusPresetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preset: FocusPresetEntity)

    @Update
    suspend fun update(preset: FocusPresetEntity)

    @Delete
    suspend fun delete(preset: FocusPresetEntity)

    @Query("SELECT * FROM focus_presets WHERE userId = :userId ORDER BY isDefault DESC, createdAt ASC")
    fun getAll(userId: String): Flow<List<FocusPresetEntity>>

    @Query("SELECT * FROM focus_presets WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): FocusPresetEntity?

    @Query("DELETE FROM focus_presets WHERE id = :id")
    suspend fun deleteById(id: String)
}
