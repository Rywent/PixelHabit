package com.rywent.pixelhabit.data.repository

import com.rywent.pixelhabit.data.local.dao.UserDao
import com.rywent.pixelhabit.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDao: UserDao
) {
    companion object {
        const val DEFAULT_USER_ID = "default_user"
        const val DEFAULT_USER_NAME = "User"
    }

    suspend fun ensureDefaultUser(): UserEntity {
        val existing = userDao.getUserById(DEFAULT_USER_ID)
        if (existing != null) return existing

        val newUser = UserEntity(
            id = DEFAULT_USER_ID,
            name = DEFAULT_USER_NAME
        )
        userDao.insertUser(newUser)
        return newUser
    }

    fun getUserFlow(userId: String): Flow<UserEntity?> {
        return userDao.getUserFlow(userId)
    }

    suspend fun getUserById(userId: String): UserEntity? {
        return userDao.getUserById(userId)
    }

    suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    suspend fun updateUserName(userId: String, name: String) {
        userDao.updateUserName(userId, name)
    }

    suspend fun updateStreak(userId: String, streak: Int, bestStreak: Int) {
        val currentBest = userDao.getUserById(userId)?.bestStreak ?: 0
        userDao.updateStreak(
            userId = userId,
            streak = streak,
            bestStreak = maxOf(streak, currentBest),
            updatedAt = System.currentTimeMillis()
        )
    }

    suspend fun deleteUser(userId: String) {
        userDao.deleteUser(userId)
    }
}