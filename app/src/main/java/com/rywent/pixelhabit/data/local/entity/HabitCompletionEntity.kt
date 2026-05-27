package com.rywent.pixelhabit.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "habit_completions",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("habitId", "date")]
)
data class HabitCompletionEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val habitId: String,
    val date: String,
    val completed: Boolean = false,
    val completedAt: Long? = null
)

