package com.rywent.pixelhabit.presentation.screens.focus.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun PresetListSheet(
    title: String,
    presets: List<com.rywent.pixelhabit.data.local.entity.FocusPresetEntity>,
    currentFocus: Int,
    currentShort: Int,
    onApply: (com.rywent.pixelhabit.data.local.entity.FocusPresetEntity) -> Unit,
    onDelete: (String) -> Unit,
    onCreate: () -> Unit,
    onDismiss: () -> Unit,
    emptyHint: String = ""
) {
    val scheme = MaterialTheme.colorScheme
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = scheme.surfaceContainerLow
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
            ) {
                if (presets.isEmpty() && emptyHint.isNotBlank()) {
                    Text(
                        emptyHint,
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                }

                presets.forEach { preset ->
                    val active = preset.focusMin == currentFocus && preset.shortBreakMin == currentShort
                    Card(
                        onClick = { onApply(preset) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (active) scheme.primaryContainer else scheme.surfaceContainerHigh
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    preset.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (active) scheme.onPrimaryContainer else scheme.onSurface
                                )

                                val subtitleText = if (!preset.isDefault) {
                                    "${preset.longBreakEvery} stages (Custom)"
                                } else {
                                    "${preset.focusMin} · ${preset.shortBreakMin} · ${preset.longBreakMin}  · every ${preset.longBreakEvery}"
                                }

                                Text(
                                    text = subtitleText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (active) scheme.onPrimaryContainer.copy(alpha = 0.8f) else scheme.onSurfaceVariant
                                )
                            }

                            if (!preset.isDefault) {
                                IconButton(onClick = { onDelete(preset.id) }) {
                                    Icon(Icons.Rounded.Delete, "Delete", tint = scheme.error)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Card(
                onClick = onCreate,
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = scheme.surfaceContainerHigh),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("+ Create custom preset", color = scheme.primary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}