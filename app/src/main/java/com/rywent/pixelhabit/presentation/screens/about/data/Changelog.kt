package com.rywent.pixelhabit.presentation.screens.about.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Upgrade
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
data class ChangelogRoot(
    val versions: List<VersionChangelog>
)

@Serializable
data class VersionChangelog(
    val version: String,
    val releaseDate: String,
    val sections: List<ChangelogSection>
)

@Serializable
data class ChangelogSection(
    val header: String,
    val icon: String,
    val items: List<ChangelogItem>
)

@Serializable
data class ChangelogItem(
    val title: String,
    val description: String
)

fun String.toImageVector(): ImageVector = when (this) {
    "add_circle" -> Icons.Rounded.AddCircle
    "upgrade" -> Icons.Rounded.Upgrade
    "bug_report" -> Icons.Rounded.BugReport
    "star" -> Icons.Rounded.Star
    else -> Icons.Rounded.AddCircle
}