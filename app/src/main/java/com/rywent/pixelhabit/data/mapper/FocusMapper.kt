package com.rywent.pixelhabit.data.mapper

import com.rywent.pixelhabit.data.local.entity.FocusPresetEntity
import com.rywent.pixelhabit.data.local.entity.FocusSessionEntity
import com.rywent.pixelhabit.presentation.screens.focus.FocusPhase
import com.rywent.pixelhabit.presentation.screens.focus.TimerPresetUi
import java.util.UUID


fun FocusPresetEntity.toUi(): TimerPresetUi = TimerPresetUi(
    id = id,
    name = name,
    focusMin = focusMin,
    shortBreakMin = shortBreakMin,
    longBreakMin = longBreakMin,
    longBreakEvery = longBreakEvery,
    isDefault = isDefault
)

fun TimerPresetUi.toEntity(userId: String): FocusPresetEntity = FocusPresetEntity(
    id = id.ifBlank { UUID.randomUUID().toString() },
    userId = userId,
    name = name,
    focusMin = focusMin,
    shortBreakMin = shortBreakMin,
    longBreakMin = longBreakMin,
    longBreakEvery = longBreakEvery,
    isDefault = isDefault,
    createdAt = System.currentTimeMillis()
)


fun FocusPhase.toDb(): String = name

fun String.toFocusPhase(): FocusPhase = try {
    FocusPhase.valueOf(this)
} catch (_: Exception) {
    FocusPhase.FOCUS
}

fun List<FocusSessionEntity>.totalFocusMinutes(): Int =
    filter { it.phase == FocusPhase.FOCUS.name && it.completed }
        .sumOf { it.actualSeconds } / 60

fun List<FocusSessionEntity>.avgFocusSeconds(): Float {
    val focus = filter { it.phase == FocusPhase.FOCUS.name && it.completed }
    if (focus.isEmpty()) return 0f
    return focus.map { it.actualSeconds }.average().toFloat()
}
