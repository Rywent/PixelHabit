package com.rywent.pixelhabit.presentation.components.panels

import android.widget.Space
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits.StepFrequency
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits.StepNameAndIcon
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits.StepNavigationBar
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits.StepPreviewAndCreate
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits.StepTimeOfDay
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHabitPanel(
    onDismiss: () -> Unit,
    lifestyles: List<LifestyleData>,
    onHabitCreated: (HabitData) -> Unit
) {
    val defaultCategory = lifestyles.firstOrNull() ?: LifestyleData(
        id = "", name = "Other", description = "", icon = Icons.Default.Favorite,
        iconColor = Color(0xFF4CAF50), category = "", createdDate = "", isActive = true
    )

    var habitName by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(Icons.Default.Favorite) }
    var selectedColor by remember { mutableStateOf(Color(0xFF4CAF50)) }
    var selectedCategory by remember { mutableStateOf(defaultCategory) }

    var selectedFrequency by remember { mutableStateOf("every_day") }
    var selectedCustomDays by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedTimeOfDay by remember { mutableStateOf("morning") }
    var selectedSpecificTime by remember { mutableStateOf<String?>(null) }

    var showIconPicker by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = { 5 })
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
                    text = "New Habit",
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
                totalSteps = 5,
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

                    1 -> StepCategory(
                        lifestyles = lifestyles,
                        selectedCategoryName = selectedCategory.name,
                        onCategorySelected = {
                            selectedCategory = it
                            selectedColor = it.iconColor
                        }
                    )

                    2 -> StepFrequency(
                        selectedFrequency = selectedFrequency,
                        selectedCustomDays = selectedCustomDays,
                        onFrequencySelected = { selectedFrequency = it },
                        onCustomDaysSelected = { selectedCustomDays = it }
                    )

                    3 -> StepTimeOfDay(
                        selectedTimeOfDay = selectedTimeOfDay,
                        selectedSpecificTime = selectedSpecificTime,
                        onTimeOfDaySelected = { selectedTimeOfDay = it },
                        onSpecificTimeSelected = { selectedSpecificTime = it }
                    )

                    4 -> StepPreviewAndCreate(
                        habitName = habitName,
                        selectedIcon = selectedIcon,
                        selectedColor = selectedColor,
                        selectedCategory = selectedCategory.name,
                        selectedFrequency = selectedFrequency,
                        selectedTimeOfDay = selectedTimeOfDay,
                        selectedSpecificTime = selectedSpecificTime,
                        onCreateHabit = {
                            val habit = HabitData(
                                id = UUID.randomUUID().toString(),
                                name = habitName.ifBlank { "New Habit" },
                                icon = selectedIcon,
                                frequency = when (selectedFrequency) {
                                    "every_day" -> "Every day"
                                    "weekdays" -> "Weekdays"
                                    "weekends" -> "Weekends"
                                    "every_other_day" -> "Every other day"
                                    else -> "Custom"
                                },
                                timeOfDay = selectedTimeOfDay.replaceFirstChar { it.uppercase() },
                                timeOfDayIcon = when (selectedTimeOfDay) {
                                    "morning" -> Icons.Default.WbSunny
                                    "afternoon" -> Icons.Default.WbTwilight
                                    "evening" -> Icons.Default.NightlightRound
                                    else -> Icons.Default.Schedule
                                },
                                lifestyleName = selectedCategory.name,
                                lifestyleColor = selectedColor,
                                lifestyleIcon = selectedCategory.icon,
                                weeklyProgress = 0f,
                                weeklyDone = 0,
                                weeklyGoal = 7,
                                currentStreak = 0,
                                bestStreak = 0
                            )
                            onHabitCreated(habit)
                            onDismiss()
                        }
                    )
                }
            }

            StepNavigationBar(
                currentStep = currentStage,
                totalSteps = 5,
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
                        2 -> {
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

                    if (canProceed && currentStage < 4) {
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