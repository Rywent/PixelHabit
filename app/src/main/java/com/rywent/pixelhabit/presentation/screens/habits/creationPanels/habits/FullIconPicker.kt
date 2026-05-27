package com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class IconCategory(
    val title: String,
    val icons: List<ImageVector>
)

@Composable
fun FullIconPicker(
    selectedIcon: ImageVector,
    selectedColor: Color,
    onIconSelected: (ImageVector) -> Unit,
    onDismiss: () -> Unit
) {
    val categories = remember {
        listOf(
            IconCategory("Health & Fitness", listOf(
                Icons.Default.Favorite, Icons.Default.FitnessCenter, Icons.Default.WaterDrop,
                Icons.Default.RunCircle, Icons.Default.SelfImprovement, Icons.Default.Bedtime,
                Icons.Default.LocalHospital, Icons.Default.MonitorHeart, Icons.Default.EmojiFoodBeverage,
                Icons.Default.DirectionsRun, Icons.Default.Pool, Icons.Default.SportsGymnastics,
                Icons.Default.Cake, Icons.Default.Restaurant, Icons.Default.NoFood,
                Icons.Default.Vaccines, Icons.Default.Sick, Icons.Default.Healing,
                Icons.Default.AccessibilityNew, Icons.Default.SportsGymnastics
            )),
            IconCategory("Mindfulness & Productivity", listOf(
                Icons.Default.Psychology, Icons.Default.Lightbulb, Icons.Default.Edit,
                Icons.Default.Book, Icons.Default.MenuBook, Icons.Default.CalendarToday,
                Icons.Default.Timer, Icons.Default.Alarm, Icons.Default.CheckCircle,
                Icons.Default.Task, Icons.Default.Work, Icons.Default.Balance,
                Icons.Default.EmojiObjects, Icons.Default.WbSunny, Icons.Default.Nightlight,
                Icons.Default.Pending, Icons.Default.ThumbUp
            )),
            IconCategory("Learning & Growth", listOf(
                Icons.Default.School, Icons.Default.Language, Icons.Default.Code,
                Icons.Default.Palette, Icons.Default.MusicNote, Icons.Default.SportsEsports,
                Icons.Default.Create, Icons.Default.AutoStories, Icons.Default.Quiz,
                Icons.Default.Translate, Icons.Default.DeveloperMode, Icons.Default.Science,
                Icons.Default.Public, Icons.Default.HistoryEdu, Icons.Default.TheaterComedy,
                Icons.Default.Brush, Icons.Default.Audiotrack, Icons.Default.VideoLibrary,
                Icons.Default.LocalLibrary
            )),
            IconCategory("Lifestyle & Daily", listOf(
                Icons.Default.Home, Icons.Default.ShoppingCart, Icons.Default.DirectionsCar,
                Icons.Default.Train, Icons.Default.Flight, Icons.Default.Coffee,
                Icons.Default.LocalLaundryService, Icons.Default.Kitchen, Icons.Default.Pets,
                Icons.Default.ChildCare, Icons.Default.Group, Icons.Default.FamilyRestroom,
                Icons.Default.Shower, Icons.Default.Bathtub, Icons.Default.Wc,
                Icons.Default.Phone, Icons.Default.Email, Icons.Default.Chat,
                Icons.Default.Notifications, Icons.Default.Star
            )),
            IconCategory("Nature & Environment", listOf(
                Icons.Default.Eco, Icons.Default.Park, Icons.Default.Forest,
                Icons.Default.Water, Icons.Default.WbCloudy, Icons.Default.WbSunny,
                Icons.Default.SevereCold, Icons.Default.Thermostat, Icons.Default.EnergySavingsLeaf,
                Icons.Default.LocalFlorist, Icons.Default.Pets, Icons.Default.BugReport,
                Icons.Default.Spa, Icons.Default.Cloud, Icons.Default.Thunderstorm,
                Icons.Default.BeachAccess, Icons.Default.Hiking, Icons.Default.Surfing,
                Icons.Default.Sailing, Icons.Default.DownhillSkiing
            ))
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Choose Icon",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            categories.forEach { category ->
                item(key = "${category.title}_header") {
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                val icons = category.icons
                val rows = icons.chunked(5)

                rows.forEachIndexed { rowIndex, rowIcons ->
                    item(key = "${category.title}_row_$rowIndex") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            repeat(5) { index ->
                                if (index < rowIcons.size) {
                                    val icon = rowIcons[index]
                                    val isSelected = icon == selectedIcon

                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) selectedColor.copy(alpha = 0.15f)
                                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                            )
                                            .clickable { onIconSelected(icon) }
                                            .border(
                                                if (isSelected) 3.dp else 0.dp,
                                                if (isSelected) selectedColor else Color.Transparent,
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = if (isSelected) selectedColor
                                            else selectedColor.copy(alpha = 0.7f), // ← Все иконки в выбранном цвете
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.size(64.dp))
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}