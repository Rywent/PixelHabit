package com.rywent.pixelhabit.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "quests",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class QuestEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String = "",

    val iconPath: String,
    val iconColorArgb: Long,

    val totalDays: Int,
    val currentDay: Int,
    val daysLeft: Int,

    val startDate: String,
    val endDate: String,
    val isCompleted: Boolean,
    val completionPercent: Float,

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),

    // foreign keys
    val userId: String
)