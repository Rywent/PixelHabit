package com.rywent.pixelhabit.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rywent.pixelhabit.data.local.entity.QuestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {

    // Get all quests by user id
    @Query("select * from quests where userId = :userId order by createdAt desc")
    fun getAllQuests(userId: String): Flow<List<QuestEntity>>

    @Query("select * from quests where userId = :userId order by createdAt desc")
    suspend fun getAllQuestsOnce(userId: String): List<QuestEntity>

    // Get active quests (not completed)
    @Query("select * from quests where userId = :userId and isCompleted = 0 order by daysLeft asc")
    fun getActiveQuests(userId: String): Flow<List<QuestEntity>>

    // Get completed quests
    @Query("select * from quests where userId = :userId and isCompleted = 1 order by endDate desc")
    fun getCompletedQuests(userId: String): Flow<List<QuestEntity>>

    // Get quest by id
    @Query("select * from quests where id = :questId")
    suspend fun getQuestById(questId: String): QuestEntity?

    // Get quest by id and user id
    @Query("select * from quests where userId = :userId and id = :id")
    suspend fun getQuestByIdAndUserId(userId: String, id: String): QuestEntity?

    // Insert quest
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: QuestEntity)

    // Update quest
    @Update
    suspend fun updateQuest(quest: QuestEntity)

    // Delete quest
    @Query("delete from quests where id = :id")
    suspend fun deleteQuest(id: String)

    // Update quest progress
    @Query("""
        update quests 
        set currentDay = :currentDay, 
            daysLeft = :daysLeft, 
            completionPercent = :completionPercent,
            updatedAt = :updatedAt
        where id = :questId
    """)
    suspend fun updateProgress(
        questId: String,
        currentDay: Int,
        daysLeft: Int,
        completionPercent: Float,
        updatedAt: Long
    )

    // Mark quest as completed
    @Query("""
        update quests 
        set isCompleted = 1, 
            completionPercent = 1.0,
            updatedAt = :updatedAt
        where id = :questId
    """)
    suspend fun completeQuest(questId: String, updatedAt: Long)

    @Query("delete from quests")
    suspend fun deleteAll()
}