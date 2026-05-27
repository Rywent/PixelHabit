package com.rywent.pixelhabit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rywent.pixelhabit.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // get a single record
    @Query("select * from users where id = :userId")
    suspend fun getUserById(userId: String) : UserEntity?

    // get all
    @Query("select * from users")
    fun getAllUsers(): Flow<List<UserEntity>>

    // create user
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity)

    // update user
    @Update
    suspend fun updateUser(user: UserEntity)

    // partial update
    @Query("update users set name =:name where id =:userId")
    suspend fun updateUserName(userId: String, name: String)

    // delete
    @Query("delete from users where id = :userId")
    suspend fun deleteUser(userId: String)
}