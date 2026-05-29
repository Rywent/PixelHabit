package com.rywent.pixelhabit.data.mapper

import com.rywent.pixelhabit.data.local.entity.UserEntity
import com.rywent.pixelhabit.presentation.screens.home.UserData

fun UserEntity.toUserData(): UserData {
    return UserData(
        id = this.id,
        name = this.name,
        currentStreak = this.currentStreak,
        bestStreak = this.bestStreak
    )
}