package com.rywent.pixelhabit.presentation.components.panels

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
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
import com.rywent.pixelhabit.presentation.screens.habits.components.LifestyleData
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits.ColorPickerScreen
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits.FullIconPicker
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.lifestyles.StepCategoryAndDescription
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.lifestyles.StepNameAndIcon
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.lifestyles.StepNavigationBar
import com.rywent.pixelhabit.presentation.screens.habits.creationPanels.lifestyles.StepPreview
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifestyleFormPanel(
    onDismiss: () -> Unit,
    onSave: (LifestyleData) -> Unit,
    editingLifestyle: LifestyleData? = null,
    existingLifestyles: List<LifestyleData> = emptyList()
) {
    val isEditing = editingLifestyle != null

    var lifestyleName by remember(editingLifestyle) { mutableStateOf(editingLifestyle?.name ?: "") }
    var selectedIcon by remember(editingLifestyle) { mutableStateOf(editingLifestyle?.icon ?: Icons.Default.Favorite) }
    var selectedColor by remember(editingLifestyle) { mutableStateOf(editingLifestyle?.iconColor ?: Color(0xFF4CAF50)) }
    var lifestyleCategory by remember(editingLifestyle) { mutableStateOf(editingLifestyle?.category ?: "") }
    var lifestyleDescription by remember(editingLifestyle) { mutableStateOf(editingLifestyle?.description ?: "") }

    var showIconPicker by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState(pageCount = { 3 })
    val totalSteps = 3
    val coroutineScope = rememberCoroutineScope()
    val currentStage = pagerState.currentPage

    var nameError by remember { mutableStateOf<String?>(null) }
    var isNameErrorShowing by remember { mutableStateOf(false) }

    fun isNameUnique(name: String): Boolean {
        if (name.isBlank()) return false
        return if (isEditing) {
            existingLifestyles.none { it.name.equals(name, ignoreCase = true) && it.id != editingLifestyle.id }
        } else {
            existingLifestyles.none { it.name.equals(name, ignoreCase = true) }
        }
    }

    BackHandler(onBack = onDismiss)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        contentWindowInsets = { WindowInsets.ime.only(WindowInsetsSides.Bottom) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .imePadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isEditing) "Edit Lifestyle" else "New Lifestyle",
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

            PixelSliderProgress(
                currentStep = currentStage,
                totalSteps = totalSteps,
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
                            name = lifestyleName,
                            onNameChange = { newName ->
                                lifestyleName = newName
                                if (isNameUnique(newName) && newName.isNotBlank()) {
                                    nameError = null
                                    isNameErrorShowing = false
                                }
                            },
                            selectedIcon = selectedIcon,
                            selectedColor = selectedColor,
                            onIconSelected = { selectedIcon = it },
                            onColorSelected = { selectedColor = it },
                            onMoreIconsClick = { showIconPicker = true },
                            onMoreColorsClick = { showColorPicker = true }
                        )
                    }
                    1 -> {
                        StepCategoryAndDescription(
                            name = lifestyleName,
                            selectedIcon = selectedIcon,
                            selectedColor = selectedColor,
                            category = lifestyleCategory,
                            onCategoryChange = { lifestyleCategory = it },
                            description = lifestyleDescription,
                            onDescriptionChange = { lifestyleDescription = it }
                        )
                    }
                    2 -> {
                        StepPreview(
                            name = lifestyleName,
                            selectedIcon = selectedIcon,
                            selectedColor = selectedColor,
                            category = lifestyleCategory,
                            description = lifestyleDescription,
                            onSave = {
                                if (!isNameUnique(lifestyleName)) {
                                    nameError = "Lifestyle with name '$lifestyleName' already exists"
                                    isNameErrorShowing = true
                                    return@StepPreview
                                }

                                val lifestyle = LifestyleData(
                                    id = editingLifestyle?.id ?: UUID.randomUUID().toString(),
                                    name = lifestyleName.ifBlank { "Lifestyle name" },
                                    description = lifestyleDescription,
                                    icon = selectedIcon,
                                    iconColor = selectedColor,
                                    category = lifestyleCategory,
                                    createdDate = editingLifestyle?.createdDate
                                        ?: SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                                    isActive = editingLifestyle?.isActive ?: true
                                )
                                onSave(lifestyle)
                                onDismiss()
                            },
                            isEditing = isEditing
                        )
                    }
                }
            }

            StepNavigationBar(
                currentStep = currentStage,
                totalSteps = totalSteps,
                onNext = {
                    var canProceed = true

                    when (currentStage) {
                        0 -> {
                            when {
                                lifestyleName.isBlank() -> {
                                    if (!isNameErrorShowing) {
                                        nameError = "Lifestyle name cannot be empty"
                                        isNameErrorShowing = true
                                    }
                                    canProceed = false
                                }
                                !isNameUnique(lifestyleName) -> {
                                    if (!isNameErrorShowing) {
                                        nameError = "Lifestyle with name '$lifestyleName' already exists"
                                        isNameErrorShowing = true
                                    }
                                    canProceed = false
                                }
                                else -> {
                                    nameError = null
                                    isNameErrorShowing = false
                                }
                            }
                        }
                    }

                    if (canProceed && currentStage < totalSteps - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(currentStage + 1)
                        }
                    }
                },
                onBack = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(currentStage - 1)
                    }
                },
                modifier = Modifier.padding(bottom = 20.dp)
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
fun CreateLifestylePanel(
    onDismiss: () -> Unit,
    onLifestyleCreated: (LifestyleData) -> Unit,
    existingLifestyles: List<LifestyleData> = emptyList()
) {
    LifestyleFormPanel(
        onDismiss = onDismiss,
        onSave = onLifestyleCreated,
        editingLifestyle = null,
        existingLifestyles = existingLifestyles
    )
}