package com.rywent.pixelhabit.data.mapper

import androidx.compose.ui.graphics.Color
import com.rywent.pixelhabit.data.local.entity.QuestEntity
import com.rywent.pixelhabit.presentation.screens.habits.components.QuestData
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.quests.FailureMode

// QuestEntity to QuestData
fun QuestEntity.toQuestData(): QuestData {
    return QuestData(
        id = id,
        name = name,
        description = description,
        icon = iconPath.toIcon(),
        iconColor = Color(iconColorArgb.toULong()),
        totalDays = totalDays,
        currentDay = currentDay,
        daysLeft = daysLeft,
        startDate = startDate,
        endDate = endDate,
        isCompleted = isCompleted,
        completionPercent = completionPercent,
        failureMode = if (failureMode == "FAIL") FailureMode.FAIL else FailureMode.SHIFT,
        isFailed = isFailed,
        lastCompletionDate = lastCompletionDate,
        skippedDays = skippedDays
    )
}

// QuestData to QuestEntity
fun QuestData.toEntity(userId: String): QuestEntity {
    return QuestEntity(
        id = id,
        name = name,
        description = description,
        iconPath = icon.toPath(),
        iconColorArgb = iconColor.value.toLong(),
        totalDays = totalDays,
        currentDay = currentDay,
        daysLeft = daysLeft,
        startDate = startDate,
        endDate = endDate,
        isCompleted = isCompleted,
        completionPercent = completionPercent,
        failureMode = if (failureMode == FailureMode.FAIL) "FAIL" else "SHIFT",
        isFailed = isFailed,
        lastCompletionDate = lastCompletionDate,
        skippedDays = skippedDays,
        userId = userId
    )
}