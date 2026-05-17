package com.rywent.pixelhabit.presentation.screens.about

import androidx.compose.ui.graphics.vector.ImageVector
import com.rywent.pixelhabit.presentation.screens.about.components.ChangelogItem

data class AboutUiState(
    val appVersion: String = "0.1.0",
    val releaseDate: String = "May 2026",
    val changelogSections: List<ChangelogSection> = emptyList()
)

data class ChangelogSection(
    val header: String,
    val icon: ImageVector,
    val items: List<ChangelogItem>
)