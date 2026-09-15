package com.rywent.pixelhabit.presentation.screens.about

import androidx.compose.ui.graphics.vector.ImageVector
import com.rywent.pixelhabit.presentation.screens.about.components.ChangelogItem

data class AboutUiState(
    val versions: List<VersionUiState> = emptyList()
)

data class VersionUiState(
    val version: String,
    val releaseDate: String,
    val sections: List<ChangelogSection>
)

data class ChangelogSection(
    val header: String,
    val icon: ImageVector,
    val items: List<ChangelogItem>
)