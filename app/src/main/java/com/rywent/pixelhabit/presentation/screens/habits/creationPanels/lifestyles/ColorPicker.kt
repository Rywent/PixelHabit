package com.rywent.pixelhabit.presentation.screens.habits.creationPanels.lifestyles

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ColorPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    onMoreColorsClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val colorRows = remember {
        listOf(
            listOf(
                Color(0xFF4CAF50), Color(0xFF8BC34A), Color(0xFF009688),
                Color(0xFF00BCD4), Color(0xFF2196F3), Color(0xFF3F51B5)
            ),
            listOf(
                Color(0xFF673AB7), Color(0xFF9C27B0), Color(0xFFE91E63),
                Color(0xFFF44336), Color(0xFFFF5722), Color(0xFFFF9800)
            ),
            listOf(
                Color(0xFFFFC107), Color(0xFFCDDC39), Color(0xFF607D8B),
                Color(0xFF795548), Color(0xFF424242), Color(0xFFFFFFFF)
            )
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Круглая раскрывающаяся кнопка
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(if (expanded) 24.dp else 50.dp),
            tonalElevation = if (expanded) 2.dp else 0.dp,
            color = if (expanded)
                MaterialTheme.colorScheme.surfaceContainerHigh
            else
                MaterialTheme.colorScheme.surface
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(selectedColor)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColor == Color.White) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Color",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Choose your lifestyle color",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Раскрывающийся контент
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        colorRows.forEach { row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                row.forEach { color ->
                                    val isSelected = color == selectedColor

                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                            .then(
                                                if (isSelected) Modifier.border(
                                                    3.dp,
                                                    MaterialTheme.colorScheme.surface,
                                                    CircleShape
                                                )
                                                    .border(
                                                        5.dp,
                                                        color.copy(alpha = 0.3f),
                                                        CircleShape
                                                    )
                                                else Modifier.border(
                                                    1.dp,
                                                    MaterialTheme.colorScheme.outlineVariant,
                                                    CircleShape
                                                )
                                            )
                                            .clickable {
                                                onColorSelected(color)
                                                expanded = false
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = if (color == Color.White || color == Color(0xFFFFC107))
                                                    Color.Black
                                                else
                                                    Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Кнопка "More colors"
                        FilledTonalButton(
                            onClick = onMoreColorsClick,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Palette, null, Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("More colors")
                        }
                    }
                }
            }
        }
    }
}