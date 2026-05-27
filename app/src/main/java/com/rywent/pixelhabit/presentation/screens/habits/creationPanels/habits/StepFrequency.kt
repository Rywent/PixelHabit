package com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class FrequencyOption(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

@Composable
fun StepFrequency(
    selectedFrequency: String,
    selectedCustomDays: List<String>,
    onFrequencySelected: (String) -> Unit,
    onCustomDaysSelected: (List<String>) -> Unit
) {
    val frequencies = remember {
        listOf(
            FrequencyOption("every_day", "Every day", "Repeat daily", Icons.Default.CalendarToday),
            FrequencyOption("weekdays", "Weekdays", "Monday — Friday", Icons.Default.Work),
            FrequencyOption("weekends", "Weekends", "Saturday & Sunday", Icons.Default.Weekend),
            FrequencyOption("every_other_day", "Every other day", "Skip one day", Icons.Default.SwapHoriz),
            FrequencyOption("custom", "Custom", "Choose specific days", Icons.Default.EditCalendar)
        )
    }

    val showCustomPicker = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            text = "How often?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 20.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            frequencies.forEach { freq ->
                val isSelected = freq.id == selectedFrequency

                FrequencyCard(
                    frequency = freq,
                    isSelected = isSelected,
                    onClick = {
                        onFrequencySelected(freq.id)
                        if (freq.id == "custom") {
                            showCustomPicker.value = true
                        }
                    }
                )
            }
        }
    }

    if (showCustomPicker.value) {
        CustomDaysPicker(
            initiallySelectedDays = selectedCustomDays,
            onConfirm = { days ->
                onCustomDaysSelected(days)
                showCustomPicker.value = false
                if (days.size == 7) onFrequencySelected("every_day")
            },
            onDismiss = { showCustomPicker.value = false }
        )
    }
}