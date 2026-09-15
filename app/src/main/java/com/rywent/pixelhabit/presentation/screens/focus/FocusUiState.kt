package com.rywent.pixelhabit.presentation.screens.focus

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VolumeOff
import androidx.compose.material.icons.rounded.Park
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Thunderstorm
import androidx.compose.material.icons.rounded.Waves
import androidx.compose.ui.graphics.vector.ImageVector
import com.rywent.pixelhabit.data.local.entity.FocusPresetEntity
import com.rywent.pixelhabit.data.local.entity.Stage
import com.rywent.pixelhabit.presentation.components.habit.TodayHabitData

data class FocusUiState(
    val totalSeconds: Int = 25 * 60,
    val remainingSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val sessionStartedAt: Long = 0L,

    val stages: List<Stage> = emptyList(),
    val currentStageIndex: Int = 0,

    val showInterruptedDialog: Boolean = false,
    val interruptedSessionSeconds: Int = 0,
    val interruptedTotalSeconds: Int = 0,
    val interruptedPhase: FocusPhase = FocusPhase.FOCUS,

    val phase: FocusPhase = FocusPhase.FOCUS,
    val completedFocusSessions: Int = 0,
    val currentCycle: Int = 1,
    val longBreakEvery: Int = 4,

    val selectedMode: FocusMode = FocusMode.POMODORO,
    val focusDurationMin: Int = 25,
    val shortBreakMin: Int = 5,
    val longBreakMin: Int = 15,
    val presets: List<FocusPresetEntity> = emptyList(),

    val selectedSound: AmbientSound = AmbientSound.NONE,
    val isSoundPlaying: Boolean = false,
    val isSilentMode: Boolean = false,

    val linkedHabitId: String? = null,
    val linkedHabitName: String? = null,
    val linkedHabitIcon: ImageVector? = null,
    val todayHabits: List<TodayHabitData> = emptyList(),

    val todayFocusSeconds: Int = 0,
    val todayCompletedCount: Int = 0,

    val showPresetSheet: Boolean = false,
    val showCustomPresetsSheet: Boolean = false,
    val showCreatePresetPanel: Boolean = false,
    val showHabitPicker: Boolean = false,

    val showCelebration: Boolean = false,
    val celebrationType: CelebrationType = CelebrationType.CYCLE,
    val celebrationMessage: String = "",
    val celebrationSubtitle: String = "",

    val isImmersive: Boolean = false
) {
    val progress: Float
        get() = if (totalSeconds <= 0) 0f
        else ((totalSeconds - remainingSeconds).toFloat() / totalSeconds).coerceIn(0f, 1f)

    val formattedTime: String
        get() {
            val m = remainingSeconds / 60
            val s = remainingSeconds % 60
            return "%02d:%02d".format(m, s)
        }

    val phaseLabel: String
        get() = when (phase) {
            FocusPhase.FOCUS -> "Focus"
            FocusPhase.SHORT_BREAK -> "Short Break"
            FocusPhase.LONG_BREAK -> "Long Break"
        }

    val todayFocusFormatted: String
        get() {
            val m = todayFocusSeconds / 60
            return if (m >= 60) "${m / 60}h ${m % 60}m" else "${m}m"
        }

    val customPresets: List<FocusPresetEntity>
        get() = presets.filter { !it.isDefault }
}

enum class FocusPhase { FOCUS, SHORT_BREAK, LONG_BREAK }

enum class FocusMode(val title: String, val subtitle: String) {
    POMODORO("Pomodoro", "25 · 5 · 15"),
    DEEP_WORK("Deep Work", "50 · 10 · 20"),
    FLOW("Flow", "90 · 15 · 30"),
    CUSTOM("Custom", "Your own")
}

enum class AmbientSound(
    val title: String,
    val icon: ImageVector,
    val rawName: String
) {
    NONE("Off", Icons.Outlined.VolumeOff, ""),
    FOREST("Forest", Icons.Rounded.Park, "ambient_forest"),
    BIRDS("Birds", Icons.Rounded.MusicNote, "ambient_birds"),
    RAIN("Rain", Icons.Rounded.Thunderstorm, "ambient_rain"),
    FIRE("Fire", Icons.Rounded.LocalFireDepartment, "ambient_fire"),
    WAVES("Waves", Icons.Rounded.Waves, "ambient_waves")
}

enum class CelebrationType {
    CYCLE,
    FULL_SESSION,
    BREAK_DONE
}