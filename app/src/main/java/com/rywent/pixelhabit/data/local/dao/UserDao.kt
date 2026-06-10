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

    @Query("update users set currentStreak = :streak, bestStreak = :bestStreak, updatedAt = :updatedAt where id = :userId")
    suspend fun updateStreak(userId: String, streak: Int, bestStreak: Int, updatedAt: Long)

    @Query("select currentStreak from users where id = :userId")
    fun getStreakFlow(userId: String): Flow<Int?>

    @Query("select * from users where id = :userId")
    fun getUserFlow(userId: String): Flow<UserEntity?>


    // create user
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity)

    // update user
    @Update
    suspend fun updateUser(user: UserEntity)

    // partial update
    @Query("update users set name =:name where id =:userId")
    suspend fun updateUserName(userId: String, name: String)

    @Query("update users set habitRemindersEnabled = :enabled where id = :userId")
    suspend fun updateHabitRemindersEnabled(userId: String, enabled: Boolean)

    @Query("update users set streakNotificationsEnabled = :enabled where id = :userId")
    suspend fun updateStreakNotificationsEnabled(userId: String, enabled: Boolean)

    @Query("update users set motivationEnabled = :enabled where id = :userId")
    suspend fun updateMotivationEnabled(userId: String, enabled: Boolean)


    // delete
    @Query("delete from users where id = :userId")
    suspend fun deleteUser(userId: String)

    @Query("delete from users")
    suspend fun deleteAll()
}