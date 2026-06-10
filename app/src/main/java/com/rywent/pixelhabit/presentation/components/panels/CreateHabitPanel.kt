package com.rywent.pixelhabit.presentation.components.panels

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rywent.pixelhabit.presentation.components.customElements.ErrorPanel
import com.rywent.pixelhabit.presentation.components.customElements.PixelSliderProgress
import com.rywent.pixelhabit.presentation.components.habit.HabitData
import com.rywent.pixelhabit.presentation.screens.habits.components.LifestyleData
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits.ColorPickerScreen
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits.FullIconPicker
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits.StepCategory
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits.StepDescription
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits.StepFrequency
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits.StepNameAndIcon
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits.StepNavigationBar
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits.StepPreviewAndCreate
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits.StepTimeOfDay
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitFormPanel(
    onDismiss: () -> Unit,
    lifestyles: List<LifestyleData>,
    onSave: (HabitData) -> Unit,
    editingHabit: HabitData? = null
) {
    val isEditing = editingHabit != null
    val defaultCategory = lifestyles.firstOrNull { it.id == editingHabit?.lifestyleId }
        ?: lifestyles.firstOrNull()
        ?: LifestyleData(
            id = "", name = "Other", description = "", icon = Icons.Default.Favorite,
            iconColor = Color(0xFF4CAF50), category = "", createdDate = "", isActive = true
        )


    var habitName by remember(editingHabit) { mutableStateOf(editingHabit?.name ?: "") }
    var habitDescription by remember(editingHabit) { mutableStateOf(editingHabit?.description ?: "") }
    var selectedIcon by remember(editingHabit) { mutableStateOf(editingHabit?.icon ?: Icons.Default.Favorite) }
    var selectedColor by remember(editingHabit) { mutableStateOf(editingHabit?.habitColor ?: Color(0xFF4CAF50)) }
    var selectedCategory by remember(editingHabit, defaultCategory) {
        mutableStateOf(
            lifestyles.firstOrNull { it.name == editingHabit?.lifestyleName } ?: defaultCategory
        )
    }

    val initialFrequency = when {
        editingHabit?.customDays != null && editingHabit.customDays.contains(",") -> {
            "custom"
        }
        editingHabit?.frequency == "Every day" -> "every_day"
        editingHabit?.frequency == "Weekdays" -> "weekdays"
        editingHabit?.frequency == "Weekends" -> "weekends"
        editingHabit?.frequency == "Every other day" -> "every_other_day"
        editingHabit?.frequency?.contains(",") == true -> "custom"
        else -> "every_day"
    }

    var selectedFrequency by remember(editingHabit) { mutableStateOf(initialFrequency) }
    var selectedCustomDays by remember(editingHabit) {
        mutableStateOf(
            editingHabit?.customDays?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
        )
    }

    val initialTimeOfDay = editingHabit?.timeOfDay?.lowercase() ?: "morning"
    var selectedTimeOfDay by remember(editingHabit) { mutableStateOf(initialTimeOfDay) }
    var selectedSpecificTime by remember(editingHabit) { mutableStateOf(editingHabit?.specificTime) }

    var showIconPicker by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = { 6 })
    val coroutineScope = rememberCoroutineScope()
    val currentStage = pagerState.currentPage

    var nameError by remember { mutableStateOf<String?>(null) }
    var frequencyError by remember { mutableStateOf<String?>(null) }
    var isNameErrorShowing by remember { mutableStateOf(false) }
    var isFrequencyErrorShowing by remember { mutableStateOf(false) }

    BackHandler(onBack = onDismiss)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isEditing) "Edit Habit" else "New Habit",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            nameError?.let { error ->
                ErrorPanel(
                    message = error,
                    onDismissed = {
                        nameError = null
                        isNameErrorShowing = false
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            frequencyError?.let { error ->
                ErrorPanel(
                    message = error,
                    onDismissed = {
                        frequencyError = null
                        isFrequencyErrorShowing = false
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            PixelSliderProgress(
                currentStep = currentStage,
                totalSteps = 6,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> {
                        StepNameAndIcon(
                            name = habitName,
                            onNameChange = {
                                habitName = it
                                if (it.isNotBlank()) nameError = null
                            },
                            selectedIcon = selectedIcon,
                            selectedColor = selectedColor,
                            onIconSelected = { selectedIcon = it },
                            onColorSelected = { selectedColor = it },
                            onMoreIconsClick = { showIconPicker = true },
                            onMoreColorsClick = { showColorPicker = true }
                        )
                    }
                    1 -> StepDescription(
                        selectedColor = selectedColor,
                        description = habitDescription,
                        onDescriptionChange = { habitDescription = it }
                    )

                    2 -> StepCategory(
                        lifestyles = lifestyles,
                        selectedCategoryName = selectedCategory.name,
                        onCategorySelected = {
                            selectedCategory = it
                        }
                    )

                    3 -> StepFrequency(
                        selectedFrequency = selectedFrequency,
                        selectedCustomDays = selectedCustomDays,
                        onFrequencySelected = { selectedFrequency = it },
                        onCustomDaysSelected = { selectedCustomDays = it }
                    )

                    4 -> StepTimeOfDay(
                        selectedTimeOfDay = selectedTimeOfDay,
                        selectedSpecificTime = selectedSpecificTime,
                        onTimeOfDaySelected = {
                            selectedTimeOfDay = it
                            if (it == "anytime") {
                                selectedSpecificTime = null
                            }
                        },
                        onSpecificTimeSelected = { selectedSpecificTime = it }
                    )

                    5 -> StepPreviewAndCreate(
                        habitName = habitName,
                        description = habitDescription,
                        selectedIcon = selectedIcon,
                        selectedColor = selectedColor,
                        selectedCategory = selectedCategory.name,
                        selectedCategoryColor = selectedCategory.iconColor,
                        selectedCategoryIcon = selectedCategory.icon,
                        selectedFrequency = selectedFrequency,
                        selectedTimeOfDay = selectedTimeOfDay,
                        selectedCustomDays = selectedCustomDays,
                        selectedSpecificTime = selectedSpecificTime,
                        isEditing = isEditing,
                        onCreateHabit = {
                            val weeklyGoal = when (selectedFrequency) {
                                "every_day" -> 7
                                "weekdays" -> 5
                                "weekends" -> 2
                                "every_other_day" -> 4
                                "custom" -> selectedCustomDays.size
                                else -> 7
                            }

                            val customDaysString = when (selectedFrequency) {
                                "every_day" -> listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                                "weekdays" -> listOf("Mon", "Tue", "Wed", "Thu", "Fri")
                                "weekends" -> listOf("Sat", "Sun")
                                "every_other_day" -> listOf("Mon", "Wed", "Fri", "Sun")
                                "custom" -> selectedCustomDays
                                else -> listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                            }.joinToString(",")

                            val habit = HabitData(
                                id = editingHabit?.id ?: UUID.randomUUID().toString(),
                                description = habitDescription,
                                name = habitName.ifBlank { "New Habit" },
                                icon = selectedIcon,
                                frequency = when (selectedFrequency) {
                                    "every_day" -> "Every day"
                                    "weekdays" -> "Weekdays"
                                    "weekends" -> "Weekends"
                                    "every_other_day" -> "Every other day"
                                    "custom" -> {
                                        val dayShortcuts = mapOf(
                                            "Monday" to "Mn", "Tuesday" to "Tu", "Wednesday" to "Wd",
                                            "Thursday" to "Th", "Friday" to "Fr", "Saturday" to "St", "Sunday" to "Sn"
                                        )
                                        selectedCustomDays.take(3).joinToString(",") { dayShortcuts[it] ?: it.take(2) } +
                                                if (selectedCustomDays.size > 3) ", +${selectedCustomDays.size - 3}d" else ""
                                    }
                                    else -> "Every day"
                                },
                                timeOfDay = selectedTimeOfDay.replaceFirstChar { it.uppercase() },
                                timeOfDayIcon = when (selectedTimeOfDay) {
                                    "morning" -> Icons.Default.WbSunny
                                    "afternoon" -> Icons.Default.WbTwilight
                                    "evening" -> Icons.Default.NightlightRound
                                    else -> Icons.Default.Schedule
                                },
                                specificTime = selectedSpecificTime,
                                customDays = customDaysString,
                                lifestyleName = selectedCategory.name,
                                lifestyleId = selectedCategory.id,
                                lifestyleColor = selectedCategory.iconColor,
                                habitColor = selectedColor,
                                lifestyleIcon = selectedCategory.icon,
                                weeklyProgress = editingHabit?.weeklyProgress ?: 0f,
                                weeklyDone = editingHabit?.weeklyDone ?: 0,
                                weeklyGoal = weeklyGoal,
                                currentStreak = editingHabit?.currentStreak ?: 0,
                                bestStreak = editingHabit?.bestStreak ?: 0
                            )
                            onSave(habit)
                            onDismiss()
                        }
                    )
                }
            }

            StepNavigationBar(
                currentStep = currentStage,
                totalSteps = 6,
                onNext = {
                    var canProceed = true

                    when (currentStage) {
                        0 -> {
                            if (habitName.isBlank()) {
                                if (!isNameErrorShowing) {
                                    nameError = "Habit name cannot be empty"
                                    isNameErrorShowing = true
                                }
                                canProceed = false
                            } else {
                                nameError = null
                                isNameErrorShowing = false
                            }
                        }
                        3 -> {
                            if (selectedFrequency == "custom" && selectedCustomDays.isEmpty()) {
                                if (!isFrequencyErrorShowing) {
                                    frequencyError = "Select at least one day"
                                    isFrequencyErrorShowing = true
                                }
                                canProceed = false
                            } else {
                                frequencyError = null
                                isFrequencyErrorShowing = false
                            }
                        }
                    }

                    if (canProceed && currentStage < 5) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(currentStage + 1)
                        }
                    }
                },
                onBack = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(currentStage - 1)
                    }
                }
            )
        }
    }

    if (showIconPicker) {
        ModalBottomSheet(
            onDismissRequest = { showIconPicker = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            dragHandle = null
        ) {
            FullIconPicker(
                selectedIcon = selectedIcon,
                selectedColor = selectedColor,
                onIconSelected = {
                    selectedIcon = it
                    showIconPicker = false
                },
                onDismiss = { showIconPicker = false }
            )
        }
    }

    if (showColorPicker) {
        ModalBottomSheet(
            onDismissRequest = { showColorPicker = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            dragHandle = null
        ) {
            ColorPickerScreen(
                selectedColor = selectedColor,
                onColorSelected = {
                    selectedColor = it
                    showColorPicker = false
                },
                onDismiss = { showColorPicker = false }
            )
        }
    }
}

@Composable
fun CreateHabitPanel(
    onDismiss: () -> Unit,
    lifestyles: List<LifestyleData>,
    onHabitCreated: (HabitData) -> Unit
) {
    HabitFormPanel(
        onDismiss = onDismiss,
        lifestyles = lifestyles,
        onSave = onHabitCreated,
        editingHabit = null
    )
}