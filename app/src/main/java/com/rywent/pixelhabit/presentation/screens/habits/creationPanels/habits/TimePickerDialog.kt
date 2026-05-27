package com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialTime: String?,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var is24Hour by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour = initialTime?.split(":")?.getOrNull(0)?.toIntOrNull() ?: 8,
        initialMinute = initialTime?.split(":")?.getOrNull(1)?.toIntOrNull() ?: 0,
        is24Hour = is24Hour
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select time",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (is24Hour) "24-hour" else "12-hour (AM/PM)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(
                        checked = is24Hour,
                        onCheckedChange = { is24Hour = it }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val hour = timePickerState.hour
                    val minute = timePickerState.minute

                    val formattedTime = if (is24Hour) {
                        String.format("%02d:%02d", hour, minute)
                    } else {
                        val period = if (hour < 12) "AM" else "PM"
                        val hour12 = when {
                            hour == 0 -> 12
                            hour > 12 -> hour - 12
                            else -> hour
                        }
                        String.format("%d:%02d %s", hour12, minute, period)
                    }

                    onTimeSelected(formattedTime)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}