package com.rywent.pixelhabit.data.repository

import com.rywent.pixelhabit.data.local.dao.QuestDao
import com.rywent.pixelhabit.data.local.entity.QuestEntity
import com.rywent.pixelhabit.data.utils.QuestUtils
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class QuestRepository(
    private val questDao: QuestDao
) {

    fun getAllQuests(userId: String): Flow<List<QuestEntity>> {
        return questDao.getAllQuests(userId)
    }

    fun getActiveQuests(userId: String): Flow<List<QuestEntity>> {
        return questDao.getActiveQuests(userId)
    }

    fun getCompletedQuests(userId: String): Flow<List<QuestEntity>> {
        return questDao.getCompletedQuests(userId)
    }

    suspend fun getQuestById(questId: String): QuestEntity? {
        return questDao.getQuestById(questId)
    }

    suspend fun insertQuest(quest: QuestEntity) {
        questDao.insertQuest(quest)
    }

    suspend fun updateQuest(quest: QuestEntity) {
        questDao.updateQuest(quest)
    }

    suspend fun deleteQuest(questId: String) {
        questDao.deleteQuest(questId)
    }

    suspend fun incrementQuestProgress(questId: String) {
        val quest = questDao.getQuestById(questId) ?: return

        if (quest.isCompleted) return

        val newCurrentDay = quest.currentDay + 1
        val newDaysLeft = maxOf(0, quest.daysLeft - 1)
        val newCompletionPercent = QuestUtils.calculateCompletionPercent(newCurrentDay, quest.totalDays)

        if (newCurrentDay >= quest.totalDays) {
            questDao.completeQuest(questId, System.currentTimeMillis())
        } else {
            questDao.updateProgress(
                questId = questId,
                currentDay = newCurrentDay,
                daysLeft = newDaysLeft,
                completionPercent = newCompletionPercent,
                updatedAt = System.currentTimeMillis()
            )
        }
    }

    suspend fun updateAllQuestsProgress(userId: String) {
        val quests = questDao.getAllQuestsOnce(userId)
        val today = LocalDate.now().toString()

        quests.forEach { quest ->
            if (!quest.isCompleted) {
                val newCurrentDay = QuestUtils.calculateCurrentDay(quest.startDate)
                val newDaysLeft = QuestUtils.calculateDaysLeft(quest.startDate, quest.endDate)
                val newCompletionPercent = QuestUtils.calculateCompletionPercent(newCurrentDay, quest.totalDays)

                if (newCurrentDay >= quest.totalDays) {
                    questDao.completeQuest(quest.id, System.currentTimeMillis())
                } else if (newCurrentDay != quest.currentDay) {
                    questDao.updateProgress(
                        questId = quest.id,
                        currentDay = newCurrentDay,
                        daysLeft = newDaysLeft,
                        completionPercent = newCompletionPercent,
                        updatedAt = System.currentTimeMillis()
                    )
                }
            }
        }
    }
}