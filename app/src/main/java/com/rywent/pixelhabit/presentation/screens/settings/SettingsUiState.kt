package com.rywent.pixelhabit.presentation.screens.settings

import com.rywent.pixelhabit.presentation.screens.settings.components.ExportState
import com.rywent.pixelhabit.presentation.screens.settings.components.ImportState

data class SettingsUiState(
    val habitRemindersEnabled: Boolean = true,
    val streakNotificationsEnabled: Boolean = true,
    val motivationEnabled: Boolean = true,
    val exportState: ExportState = ExportState.Idle,
    val importState: ImportState = ImportState.Idle,

    val showAboutSheet: Boolean = false
)