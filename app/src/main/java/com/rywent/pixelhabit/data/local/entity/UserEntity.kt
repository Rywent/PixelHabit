package com.rywent.pixelhabit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity (
    @PrimaryKey
    val id: String,
    val name: String,

    val currentStreak: Int = 0,
    val bestStreak: Int = 0,

    val habitRemindersEnabled: Boolean = true,
    val streakNotificationsEnabled: Boolean = true,
    val motivationEnabled: Boolean = true,

    val createdAt: Long = System.currentTimeMillis(),
    val lastActiveAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)