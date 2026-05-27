package com.rywent.pixelhabit.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "lifestyles",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LifestyleEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val iconPath: String,
    val iconColorArgb: Long,
    val category: String,
    val createdDate: Long = System.currentTimeMillis(),
    val isActive: Boolean,

    // foreign keys
    val userId: String
)
