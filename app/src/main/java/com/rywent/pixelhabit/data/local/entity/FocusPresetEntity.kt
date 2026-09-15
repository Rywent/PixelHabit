package com.rywent.pixelhabit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Stage(
    val focusMin: Int,
    val breakMin: Int,
    val isLongBreak: Boolean
)

@Entity(tableName = "focus_presets")
data class FocusPresetEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val name: String,
    val focusMin: Int,
    val shortBreakMin: Int,
    val longBreakMin: Int,
    val longBreakEvery: Int = 4,
    val stagesJson: String? = null,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getStages(): List<Stage> {
        return try {
            if (stagesJson.isNullOrBlank()) emptyList()
            else Json.decodeFromString<List<Stage>>(stagesJson)
        } catch (e: Exception) {
            emptyList()
        }
    }
}