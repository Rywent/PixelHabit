package com.rywent.pixelhabit.data.mapper

import androidx.compose.ui.graphics.Color
import com.rywent.pixelhabit.data.local.entity.LifestyleEntity
import com.rywent.pixelhabit.presentation.screens.habits.components.LifestyleData

fun LifestyleEntity.toLifestyleData(): LifestyleData {
    return LifestyleData(
        id = id,
        name = name,
        description = description,
        icon = iconPath.toIcon(),
        iconColor = Color(iconColorArgb.toULong()),
        category = category,
        createdDate = createdDate.toFormattedDate(),
        isActive = isActive
    )
}

fun LifestyleData.toEntity(userId: String): LifestyleEntity {
    return LifestyleEntity(
        id = id,
        name = name,
        description = description,
        iconPath = icon.toPath(),
        iconColorArgb = iconColor.value.toLong(),
        category = category,
        createdDate = System.currentTimeMillis(),
        isActive = isActive,
        userId = userId
    )
}