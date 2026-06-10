package com.rywent.pixelhabit.presentation.screens.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rywent.pixelhabit.presentation.screens.settings.BackupSummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


sealed class ExportState {
    object Idle : ExportState()
    object Loading : ExportState()
    data class Success(val fileName: String) : ExportState()
    data class Error(val message: String) : ExportState()
}

sealed class ImportState {
    object Idle : ImportState()
    object Loading : ImportState()
    data class ReadyForImport(val backupSummary: BackupSummary) : ImportState()
    data object Success : ImportState()
    data class Error(val message: String) : ImportState()
    object Cancelled : ImportState()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupPanel(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onExport: () -> Unit,
    onImportFileSelected: () -> Unit,
    importState: ImportState,
    exportState: ExportState,
    onApplyImport: () -> Unit,
    onCancelImport: () -> Unit
) {
    if (!isVisible) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { SheetDragHandle() }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            item {
                Column {
                    Text(
                        text = "Backup & Restore",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Secure your progress or restore from a previous backup",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Export Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = exportState !is ExportState.Loading) { onExport() },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (exportState is ExportState.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(28.dp),
                                        strokeWidth = 2.5.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Icon(
                                        Icons.Rounded.CloudUpload,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Export Data",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Create a complete backup of all your habits, quests, and progress",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Rounded.Folder,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Saved to Downloads folder",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Icon(
                            Icons.Rounded.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Export Status
            when (val state = exportState) {
                is ExportState.Success -> {
                    item {
                        SuccessAlertCard(
                            title = "Backup Created Successfully",
                            message = "File: ${state.fileName}\nLocation: Downloads folder"
                        )
                    }
                }
                is ExportState.Error -> {
                    item {
                        ErrorAlertCard(
                            title = "Export Failed",
                            message = state.message
                        )
                    }
                }
                else -> {}
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }

            // Import Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = importState !is ImportState.Loading) { onImportFileSelected() },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (importState is ImportState.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(28.dp),
                                        strokeWidth = 2.5.dp,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                } else {
                                    Icon(
                                        Icons.Rounded.CloudDownload,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Restore from Backup",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Select a JSON backup file to restore your data",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Rounded.Warning,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.error,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "This will replace all current data",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }

                        Icon(
                            Icons.Rounded.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Import Preview
            when (val state = importState) {
                is ImportState.ReadyForImport -> {
                    item {
                        BackupPreviewCard(
                            backupSummary = state.backupSummary,
                            onApplyImport = onApplyImport,
                            onCancelImport = onCancelImport
                        )
                    }
                }
                is ImportState.Success -> {
                    item {
                        SuccessAlertCard(
                            title = "Import Complete!",
                            message = "Data has been restored successfully.\nPlease restart the app to see changes."
                        )
                    }
                }
                is ImportState.Error -> {
                    item {
                        ErrorAlertCard(
                            title = "Import Failed",
                            message = state.message
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun SuccessAlertCard(title: String, message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun ErrorAlertCard(title: String, message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Rounded.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun BackupPreviewCard(
    backupSummary: BackupSummary,
    onApplyImport: () -> Unit,
    onCancelImport: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            BackupMetadataHeader(backupSummary)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatsItem(
                    icon = Icons.Rounded.Person,
                    value = backupSummary.totalUsers.toString(),
                    label = "Users"
                )
                StatsItem(
                    icon = Icons.Rounded.Category,
                    value = backupSummary.totalLifestyles.toString(),
                    label = "Lifestyles"
                )
                StatsItem(
                    icon = Icons.Rounded.CheckCircle,
                    value = backupSummary.totalHabits.toString(),
                    label = "Habits"
                )
                StatsItem(
                    icon = Icons.Rounded.EmojiEvents,
                    value = "${backupSummary.completedQuests}/${backupSummary.totalQuests}",
                    label = "Quests"
                )
            }

            if (backupSummary.users.isNotEmpty()) {
                SectionCard(
                    title = "Users",
                    icon = Icons.Rounded.People,
                    count = backupSummary.users.size
                ) {
                    backupSummary.users.forEach { user ->
                        ListItem(
                            headlineContent = { Text(user.name, fontWeight = FontWeight.Medium) },
                            supportingContent = {
                                Text("Current streak: ${user.currentStreak} days • Best streak: ${user.bestStreak} days")
                            },
                            leadingContent = {
                                Icon(Icons.Rounded.Person, contentDescription = null)
                            }
                        )
                    }
                }
            }

            if (backupSummary.lifestyles.isNotEmpty()) {
                SectionCard(
                    title = "Lifestyles",
                    icon = Icons.Rounded.Category,
                    count = backupSummary.lifestyles.size
                ) {
                    backupSummary.lifestyles.forEach { lifestyle ->
                        ListItem(
                            headlineContent = { Text(lifestyle.name, fontWeight = FontWeight.Medium) },
                            supportingContent = {
                                if (lifestyle.description.isNotEmpty()) {
                                    Text(lifestyle.description.take(100), maxLines = 2, overflow = TextOverflow.Ellipsis)
                                }
                            },
                            leadingContent = {
                                Icon(Icons.Rounded.Label, contentDescription = null)
                            },
                            trailingContent = {
                                if (!lifestyle.isActive) {
                                    Text("Inactive", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                    }
                }
            }

            if (backupSummary.habits.isNotEmpty()) {
                SectionCard(
                    title = "Habits",
                    icon = Icons.Rounded.CheckCircle,
                    count = backupSummary.habits.size
                ) {
                    backupSummary.habits.forEach { habit ->
                        ListItem(
                            headlineContent = { Text(habit.name, fontWeight = FontWeight.Medium) },
                            supportingContent = {
                                Column {
                                    Text("${habit.frequency} • ${habit.timeOfDay}", style = MaterialTheme.typography.bodySmall)
                                    if (habit.weeklyGoal > 0) {
                                        Text("Weekly progress: ${habit.weeklyDone}/${habit.weeklyGoal}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text("Current streak: ${habit.currentStreak} days", style = MaterialTheme.typography.bodySmall)
                                    if (habit.lifestyleName != null) {
                                        Text("Lifestyle: ${habit.lifestyleName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            },
                            leadingContent = {
                                Icon(Icons.Rounded.Task, contentDescription = null)
                            }
                        )
                    }
                }
            }

            if (backupSummary.completions.isNotEmpty()) {
                SectionCard(
                    title = "Habit Completions",
                    icon = Icons.Rounded.CalendarMonth,
                    count = backupSummary.completions.sumOf { it.count }
                ) {
                    backupSummary.completions.forEach { completion ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatCompletionDate(completion.date),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ) {
                                Text("${completion.count} completed")
                            }
                        }
                    }
                }
            }

            if (backupSummary.quests.isNotEmpty()) {
                SectionCard(
                    title = "Quests",
                    icon = Icons.Rounded.EmojiEvents,
                    count = backupSummary.quests.size
                ) {
                    backupSummary.quests.forEach { quest ->
                        ListItem(
                            headlineContent = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(quest.name, fontWeight = FontWeight.Medium)
                                    if (quest.isCompleted) {
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.primary
                                        ) {
                                            Text("Completed")
                                        }
                                    }
                                }
                            },
                            supportingContent = {
                                Column {
                                    LinearProgressIndicator(
                                        progress = { quest.completionPercent / 100f },
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        color = MaterialTheme.colorScheme.tertiary,
                                        trackColor = MaterialTheme.colorScheme.tertiaryContainer,
                                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                                    )
                                    Text("Progress: ${quest.currentDay}/${quest.totalDays} days", style = MaterialTheme.typography.bodySmall)
                                    if (!quest.isCompleted && quest.daysLeft > 0) {
                                        Text("${quest.daysLeft} days left", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary)
                                    }
                                }
                            },
                            leadingContent = {
                                Icon(
                                    if (quest.isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (quest.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }
            }

            Button(
                onClick = onApplyImport,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Rounded.Restore, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Restore Data", fontWeight = FontWeight.Medium)
            }

            OutlinedButton(
                onClick = onCancelImport,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.Close, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cancel", fontWeight = FontWeight.Medium)
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.Warning,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.error,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "This will replace ALL your current data",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun BackupMetadataHeader(backupSummary: BackupSummary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Rounded.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Backup Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Version ${backupSummary.metadata.version} • ${formatDate(backupSummary.metadata.exportDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatsItem(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            }
        }
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        "$count",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            HorizontalDivider()
            content()
        }
    }
}



@Composable
private fun SheetDragHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.width(40.dp),
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}



private fun formatDate(timestamp: Long): String {
    return if (timestamp > 0) {
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(timestamp))
    } else {
        "Unknown date"
    }
}

private fun formatCompletionDate(dateString: String): String {
    return try {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString)
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}

