package com.rywent.pixelhabit.data.repository

import com.rywent.pixelhabit.data.local.dao.UserDao
import com.rywent.pixelhabit.data.local.entity.UserEntity


class UserRepository(
    private val userDao: UserDao
) {
    // get user by id
    suspend fun getUserById(userId: String) : UserEntity? {
        return userDao.getUserById(userId)
    }

    // create user
    suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }


    // update user
    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    // partial update
    suspend fun updateUserName(userId: String, name: String) {
        userDao.updateUserName(userId, name)
    }

    // delete
    suspend fun deleteUser(userId: String) {
        userDao.deleteUser(userId)
    }

}