package com.rywent.pixelhabit.data.model

import androidx.room.Embedded
import com.rywent.pixelhabit.data.local.entity.HabitEntity

data class HabitWithCompletion(
    @Embedded
    val habit: HabitEntity,
    val isCompleted: Boolean,
    val completedAt: Long?
)
