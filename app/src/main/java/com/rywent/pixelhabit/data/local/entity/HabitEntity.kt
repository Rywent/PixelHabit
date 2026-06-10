package com.rywent.pixelhabit.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "habits",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LifestyleEntity::class,
            parentColumns = ["id"],
            childColumns = ["lifestyleId"],
            onDelete = ForeignKey.SET_NULL
        )
    ])
data class HabitEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String = "",

    val iconPath: String,
    val timeOfDayIconPath: String,
    val lifestyleIconPath: String?,

    val colorArgb: Long,
    val lifestyleColorArgb: Long,

    val frequency: String,
    val timeOfDay: String,
    val specificTime: String? = null,
    val lifestyleName: String,
    val customDays: String? = null,


    val weeklyProgress: Float,
    val weeklyDone: Int,
    val weeklyGoal: Int,
    val currentStreak: Int,
    val bestStreak: Int,

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),

    // foreign keys
    val userId: String,
    val lifestyleId: String? = null
)