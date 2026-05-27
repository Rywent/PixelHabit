package com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDaysPicker(
    initiallySelectedDays: List<String>,
    onConfirm: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDays by remember(initiallySelectedDays) {
        mutableStateOf(initiallySelectedDays.toMutableList())
    }

    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Select days",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Choose on which days this habit should repeat",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickActionChip("Every day") {
                    selectedDays = daysOfWeek.toMutableList()
                }
                QuickActionChip("Weekdays") {
                    selectedDays = daysOfWeek.take(5).toMutableList()
                }
                QuickActionChip("Weekends") {
                    selectedDays = daysOfWeek.takeLast(2).toMutableList()
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                daysOfWeek.forEach { day ->
                    val isSelected = selectedDays.contains(day)

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                                )
                                .border(
                                    width = if (isSelected) 0.dp else 2.dp,
                                    color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedDays = (if (isSelected) {
                                        selectedDays - day
                                    } else {
                                        selectedDays + day
                                    }) as MutableList<String>
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { onConfirm(selectedDays.toList()) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                enabled = selectedDays.isNotEmpty(),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Text("Confirm (${selectedDays.size} days selected)")
            }
        }
    }
}

@Composable
private fun QuickActionChip(text: String, onClick: () -> Unit) {
    SuggestionChip(
        onClick = onClick,
        label = { Text(text) },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    )
}