package com.rywent.pixelhabit.data.mapper

import androidx.compose.ui.graphics.Color
import com.rywent.pixelhabit.data.local.entity.QuestEntity
import com.rywent.pixelhabit.presentation.screens.habits.components.QuestData

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
        completionPercent = completionPercent
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
        userId = userId
    )
}