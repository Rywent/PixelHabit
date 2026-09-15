package com.rywent.pixelhabit.presentation.screens.focus.components.panels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class StageDraft(
    val focusMin: Int = 25,
    val breakMin: Int = 5,
    val isLongBreak: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePresetPanel(
    onDismiss: () -> Unit,
    onCreate: (
        name: String,
        focus: Int,
        short: Int,
        long: Int,
        every: Int,
        stagesJson: String?
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scheme = MaterialTheme.colorScheme

    var name by remember { mutableStateOf("") }
    val stages = remember { mutableStateListOf<StageDraft>() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = scheme.surfaceContainerLow,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp)
        ) {
            Text(
                text = "Custom session",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Add focus stages and a break after each one.",
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= 32) name = it },
                label = { Text("Name") },
                placeholder = { Text("e.g. Deep morning") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedVisibility(
                    visible = stages.isEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(scheme.surfaceContainerHigh)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No stages yet. Tap + to add the first focus block.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = scheme.onSurfaceVariant
                        )
                    }
                }

                stages.forEachIndexed { index, stage ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically { it / 2 } + expandVertically(),
                        exit = fadeOut() + slideOutVertically { it / 2 } + shrinkVertically()
                    ) {
                        Column {
                            StageCard(
                                index = index,
                                stage = stage,
                                onChange = { stages[index] = it },
                                onRemove = { stages.removeAt(index) }
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(scheme.primaryContainer.copy(alpha = 0.5f))
                    .clickable {
                        stages.add(StageDraft())
                    }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Add, null, tint = scheme.primary)
                Spacer(Modifier.size(8.dp))
                Text(
                    text = "Add focus stage",
                    color = scheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        if (stages.isEmpty()) return@Button
                        val focusAvg = stages.map { it.focusMin }.average().toInt().coerceIn(1, 180)
                        val shorts = stages.filter { !it.isLongBreak }.map { it.breakMin }
                        val longs = stages.filter { it.isLongBreak }.map { it.breakMin }
                        val short = (shorts.average().toInt().takeIf { shorts.isNotEmpty() } ?: 5)
                            .coerceIn(1, 30)
                        val long = (longs.average().toInt().takeIf { longs.isNotEmpty() } ?: 15)
                            .coerceIn(5, 60)

                        val json = stages.joinToString(prefix = "[", postfix = "]") { s ->
                            """{"focusMin":${s.focusMin},"breakMin":${s.breakMin},"isLongBreak":${s.isLongBreak}}"""
                        }

                        onCreate(
                            name.ifBlank { "Custom ${stages.size} stages" },
                            focusAvg,
                            short,
                            long,
                            stages.size.coerceAtLeast(1),
                            json
                        )
                    },
                    enabled = stages.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
private fun StageCard(
    index: Int,
    stage: StageDraft,
    onChange: (StageDraft) -> Unit,
    onRemove: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    var focus by remember(stage) { mutableFloatStateOf(stage.focusMin.toFloat()) }
    var breakMin by remember(stage) { mutableFloatStateOf(stage.breakMin.toFloat()) }
    var isLong by remember(stage) { mutableStateOf(stage.isLongBreak) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(scheme.surfaceContainerHigh)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Stage ${index + 1}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Rounded.Delete, "Remove", tint = scheme.error, modifier = Modifier.size(20.dp))
            }
        }

        Text("Focus", style = MaterialTheme.typography.labelLarge, color = scheme.onSurfaceVariant)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${focus.toInt()} min", color = scheme.primary, fontWeight = FontWeight.SemiBold)
        }
        Slider(
            value = focus,
            onValueChange = {
                focus = it
                onChange(stage.copy(focusMin = it.toInt(), breakMin = breakMin.toInt(), isLongBreak = isLong))
            },
            valueRange = 5f..120f,
            steps = 22
        )

        Spacer(Modifier.height(8.dp))
        Text("Break after this stage", style = MaterialTheme.typography.labelLarge, color = scheme.onSurfaceVariant)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = !isLong,
                onClick = {
                    isLong = false
                    breakMin = breakMin.coerceIn(5f, 30f)
                    onChange(stage.copy(focusMin = focus.toInt(), breakMin = breakMin.toInt(), isLongBreak = false))
                },
                label = { Text("Short") }
            )
            FilterChip(
                selected = isLong,
                onClick = {
                    isLong = true
                    if (breakMin < 30f) breakMin = 30f
                    onChange(stage.copy(focusMin = focus.toInt(), breakMin = breakMin.toInt(), isLongBreak = true))
                },
                label = { Text("Long") }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${breakMin.toInt()} min", color = scheme.primary, fontWeight = FontWeight.SemiBold)
        }
        Slider(
            value = breakMin,
            onValueChange = {
                breakMin = it
                onChange(stage.copy(focusMin = focus.toInt(), breakMin = it.toInt(), isLongBreak = isLong))
            },
            valueRange = if (isLong) 30f..60f else 5f..30f,
            steps = if (isLong) 5 else 4
        )
    }
}