package com.rywent.pixelhabit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val habitId: String? = null,
    val habitName: String? = null,
    val phase: String,
    val plannedSeconds: Int,
    val actualSeconds: Int,
    val completed: Boolean,
    val startedAt: Long,
    val finishedAt: Long,
    val date: String
)
