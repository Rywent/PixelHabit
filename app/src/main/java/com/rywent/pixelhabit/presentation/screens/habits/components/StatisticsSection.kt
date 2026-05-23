package com.rywent.pixelhabit.presentation.screens.habits.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.LibraryAddCheck
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun StatisticsSection(
    modifier: Modifier = Modifier,
    completionRate: Float,
    totalHabitCount: Int,
    habitsCompleted: Int,
    avgFocusTime: Int
    ) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MiniStatisticCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.PieChart,
                title = "Completion",
                value = "${completionRate}%",
                period = "this week"
            )

            MiniStatisticCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.List,
                title = "Active",
                value = totalHabitCount.toString(),
                period = "habits"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MiniStatisticCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Checklist,
                title = "Completed",
                value = habitsCompleted.toString(),
                period = "this week"
            )

            MiniStatisticCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.Timer,
                title = "Focus Time",
                value = avgFocusTime.toString(),
                period = "min avg"
            )
        }
    }
}