package com.rywent.pixelhabit.presentation.components.panels

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rywent.pixelhabit.presentation.components.customElements.ErrorPanel
import com.rywent.pixelhabit.presentation.components.customElements.PixelSliderProgress
import com.rywent.pixelhabit.presentation.screens.habits.components.QuestData
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits.ColorPickerScreen
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits.FullIconPicker
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.quests.QuestDatePickerContent
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.quests.QuickDaysContent
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.quests.StepQuestNameAndIcon
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.quests.StepNavigationBar
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.quests.StepQuestDescription
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.quests.StepQuestDuration
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.quests.StepQuestPreview
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuestPanel(
    onDismiss: () -> Unit,
    onQuestCreated: (QuestData) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()
    val currentStep = pagerState.currentPage

    var questName by remember { mutableStateOf("") }
    var questDescription by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(Icons.Outlined.Explore) }
    var selectedColor by remember { mutableStateOf(Color(0xFF6366F1)) }
    var totalDays by remember { mutableIntStateOf(7) }
    var startDate by remember { mutableStateOf(LocalDate.now()) }

    var showIconPicker by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showQuickDaysDialog by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    BackHandler(onBack = {
        if (currentStep > 0) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(currentStep - 1)
            }
        } else {
            onDismiss()
        }
    })

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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (currentStep) {
                        0 -> "New Quest"
                        1 -> "Description"
                        2 -> "Duration"
                        else -> "Ready?"
                    },
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            PixelSliderProgress(
                currentStep = currentStep,
                totalSteps = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (showError) {
                ErrorPanel(
                    message = errorMessage,
                    onDismissed = {
                        showError = false
                        errorMessage = ""
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> StepQuestNameAndIcon(
                        name = questName,
                        onNameChange = {
                            questName = it
                            if (it.isNotBlank()) {
                                showError = false
                                errorMessage = ""
                            }
                        },
                        selectedIcon = selectedIcon,
                        selectedColor = selectedColor,
                        onIconSelected = { selectedIcon = it },
                        onColorSelected = { selectedColor = it },
                        onMoreIconsClick = { showIconPicker = true },
                        onMoreColorsClick = { showColorPicker = true }
                    )

                    1 -> StepQuestDescription(
                        description = questDescription,
                        onDescriptionChange = { questDescription = it },
                        color = selectedColor
                    )

                    2 -> StepQuestDuration(
                        totalDays = totalDays,
                        onTotalDaysChange = { totalDays = it },
                        startDate = startDate,
                        onStartDateClick = { showDatePicker = true },
                        onQuickSelectClick = { showQuickDaysDialog = true },
                        color = selectedColor
                    )

                    3 -> StepQuestPreview(
                        name = questName,
                        description = questDescription,
                        icon = selectedIcon,
                        color = selectedColor,
                        totalDays = totalDays,
                        startDate = startDate,
                        onCreateClick = {
                            if (questName.isBlank()) {
                                errorMessage = "Quest name cannot be empty"
                                showError = true
                            } else {
                                val endDate = startDate.plusDays((totalDays - 1).toLong())
                                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                                val quest = QuestData(
                                    id = UUID.randomUUID().toString(),
                                    name = questName,
                                    description = questDescription,
                                    icon = selectedIcon,
                                    iconColor = selectedColor,
                                    totalDays = totalDays,
                                    currentDay = 1,
                                    daysLeft = totalDays,
                                    startDate = startDate.format(formatter),
                                    endDate = endDate.format(formatter),
                                    isCompleted = false,
                                    completionPercent = 0f
                                )
                                onQuestCreated(quest)
                                onDismiss()
                            }
                        }
                    )
                }
            }

            StepNavigationBar(
                currentStep = currentStep,
                totalSteps = 4,
                onNext = {
                    var canProceed = true

                    when (currentStep) {
                        0 -> {
                            if (questName.isBlank()) {
                                errorMessage = "Enter quest name"
                                showError = true
                                canProceed = false
                            } else {
                                showError = false
                                errorMessage = ""
                            }
                        }

                        1 -> {
                            if (questDescription.length > 200) {
                                errorMessage = "Description is too long (max 200 chars)"
                                showError = true
                                canProceed = false
                            } else {
                                showError = false
                                errorMessage = ""
                            }
                        }

                        2 -> {
                            if (totalDays < 1) {
                                errorMessage = "Duration must be at least 1 day"
                                showError = true
                                canProceed = false
                            } else {
                                showError = false
                                errorMessage = ""
                            }
                        }
                    }

                    if (canProceed && currentStep < 3) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(currentStep + 1)
                        }
                    }
                },
                onBack = {
                    if (currentStep > 0) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(currentStep - 1)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showIconPicker) {
        ModalBottomSheet(
            onDismissRequest = { showIconPicker = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
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
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
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

    if (showDatePicker) {
        ModalBottomSheet(
            onDismissRequest = { showDatePicker = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            dragHandle = null
        ) {
            QuestDatePickerContent(
                initialDate = startDate,
                onDateSelected = {
                    startDate = it
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false }
            )
        }
    }

    if (showQuickDaysDialog) {
        ModalBottomSheet(
            onDismissRequest = { showQuickDaysDialog = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            dragHandle = null
        ) {
            QuickDaysContent(
                onDaysSelected = {
                    totalDays = it
                    showQuickDaysDialog = false
                },
                onDismiss = { showQuickDaysDialog = false }
            )
        }
    }
}





