package com.rywent.pixelhabit.presentation.screens.focus

data class TimerPresetUi(
    val id: String = "",
    val name: String,
    val focusMin: Int,
    val shortBreakMin: Int,
    val longBreakMin: Int,
    val longBreakEvery: Int = 4,
    val isDefault: Boolean = false
)
