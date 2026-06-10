package com.rywent.pixelhabit.data.mapper

import androidx.compose.ui.graphics.Color
import com.rywent.pixelhabit.data.local.entity.HabitEntity
import com.rywent.pixelhabit.data.model.HabitWithCompletion
import com.rywent.pixelhabit.presentation.components.habit.HabitData
import com.rywent.pixelhabit.presentation.components.habit.TodayHabitData

// HabitEntity to HabitData (habits list)
fun HabitEntity.toHabitData(): HabitData {
    return HabitData(
        id = id,
        name = name,
        description = description,
        icon = iconPath.toIcon(),
        frequency = frequency,
        timeOfDay = timeOfDay,
        specificTime = specificTime,
        timeOfDayIcon = timeOfDayIconPath.toIcon(),
        customDays = customDays,
        lifestyleName = lifestyleName,
        habitColor = Color(colorArgb.toULong()),
        lifestyleColor = Color(lifestyleColorArgb.toULong()),
        lifestyleIcon = lifestyleIconPath?.toIcon(),
        weeklyProgress = weeklyProgress,
        weeklyDone = weeklyDone,
        weeklyGoal = weeklyGoal,
        currentStreak = currentStreak,
        bestStreak = bestStreak,
        isCompletedToday = false,
        lifestyleId = lifestyleId
    )
}

// HabitWithCompletion to TodayHabitData
fun HabitWithCompletion.toTodayHabitData(): TodayHabitData {
    return TodayHabitData(
        id = habit.id,
        name = habit.name,
        description = habit.description,
        streak = habit.currentStreak,
        icon = habit.iconPath.toIcon(),
        isCompleted = this.isCompleted,
        lifestyleId = habit.lifestyleId
    )
}

// HabitData to HabitEntity (save)
fun HabitData.toEntity(userId: String): HabitEntity {
    return HabitEntity(
        id = id,
        name = name,
        description = description,
        iconPath = icon.toPath(),
        timeOfDayIconPath = timeOfDayIcon.toPath(),
        lifestyleIconPath = lifestyleIcon?.toPath(),
        colorArgb = habitColor.value.toLong(),
        lifestyleColorArgb = lifestyleColor.value.toLong(),
        frequency = frequency,
        timeOfDay = timeOfDay,
        specificTime = specificTime,
        customDays = customDays,
        lifestyleName = lifestyleName,
        weeklyProgress = weeklyProgress,
        weeklyDone = weeklyDone,
        weeklyGoal = weeklyGoal,
        currentStreak = currentStreak,
        bestStreak = bestStreak,
        userId = userId,
        lifestyleId = lifestyleId
    )
}