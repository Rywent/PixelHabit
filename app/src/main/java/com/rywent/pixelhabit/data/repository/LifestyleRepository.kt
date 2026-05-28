package com.rywent.pixelhabit.data.repository

import com.rywent.pixelhabit.data.local.dao.LifestyleDao
import com.rywent.pixelhabit.data.local.entity.LifestyleEntity
import kotlinx.coroutines.flow.Flow

class LifestyleRepository(
    private val lifestyleDao: LifestyleDao
) {
    // get lifestyle by id
    suspend fun getLifestyleById(lifestyleId: String): LifestyleEntity? {
        return lifestyleDao.getLifestyleById(lifestyleId)
    }

    // get lifestyles by user id
    fun getLifestylesByUserId(userId: String): Flow<List<LifestyleEntity>> {
        return lifestyleDao.getLifestylesByUserId(userId)
    }

    // get lifestyle by id and user id
    suspend fun getLifestyleByIdAndUserId(lifestyleId: String, userId: String): LifestyleEntity? {
        return lifestyleDao.getLifestyleByIdAndByUserId(userId, lifestyleId)
    }

    // create
    suspend fun insertLifestyle(lifestyle: LifestyleEntity) {
        lifestyleDao.insertLifestyle(lifestyle)
    }

    // update
    suspend fun updateLifestyle(lifestyle: LifestyleEntity) {
        lifestyleDao.updateLifestyle(lifestyle)
    }

    // delete
    suspend fun deleteLifestyle(lifestyleId: String) {
        lifestyleDao.deleteLifestyle(lifestyleId)
    }
}