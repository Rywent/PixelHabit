package com.rywent.pixelhabit.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rywent.pixelhabit.data.local.entity.LifestyleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LifestyleDao {

    // get lifestyle by id
    @Query("select * from lifestyles where id = :lifestyleId")
    suspend fun getLifestyleById(lifestyleId: String) : LifestyleEntity?

    // get lifestyles by user id
    @Query("select * from lifestyles where userId =:userId")
    fun getLifestylesByUserId(userId: String) : Flow<List<LifestyleEntity>>

    // get lifestyle by id and user id
    @Query("select * from lifestyles where userId =:userId and id = :id")
    suspend fun getLifestyleByIdAndByUserId(userId: String, id: String): LifestyleEntity?

    // create
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLifestyle(lifestyle: LifestyleEntity)

    // update
    @Update
    suspend fun updateLifestyle(lifestyle: LifestyleEntity)

    // delete by id
    @Query("delete from lifestyles where id = :id")
    suspend fun deleteLifestyle(id: String)
}