package com.rywent.pixelhabit.presentation.screens.habits.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
    val animatedCompletion = remember { Animatable(0f) }
    val animatedTotal = remember { Animatable(0f) }
    val animatedCompleted = remember { Animatable(0f) }
    val animatedFocus = remember { Animatable(0f) }

    LaunchedEffect(completionRate) {
        animatedCompletion.animateTo(
            targetValue = completionRate,
            animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing)
        )
    }
    LaunchedEffect(totalHabitCount) {
        animatedTotal.animateTo(
            targetValue = totalHabitCount.toFloat(),
            animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing)
        )
    }
    LaunchedEffect(habitsCompleted) {
        animatedCompleted.animateTo(
            targetValue = habitsCompleted.toFloat(),
            animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing)
        )
    }
    LaunchedEffect(avgFocusTime) {
        animatedFocus.animateTo(
            targetValue = avgFocusTime.toFloat(),
            animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing)
        )
    }

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
                value = "${String.format("%.1f", animatedCompletion.value)}%",
                period = "this week"
            )

            MiniStatisticCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.List,
                title = "Active",
                value = "${animatedTotal.value.toInt()}",
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
                value = "${animatedCompleted.value.toInt()}",
                period = "this week"
            )

            MiniStatisticCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.Timer,
                title = "Focus Time",
                value = "${animatedFocus.value.toInt()}",
                period = "min avg"
            )
        }
    }
}