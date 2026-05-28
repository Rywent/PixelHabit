package com.rywent.pixelhabit.presentation.screens.habits.creationPanels.lifestyles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StepCategoryAndDescription(
    name: String,
    selectedIcon: ImageVector,
    selectedColor: Color,
    category: String,
    onCategoryChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Details",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Add category and description to your lifestyle",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LiveCardPreview(
            name = name,
            icon = selectedIcon,
            color = selectedColor,
            category = category,
            description = description
        )

        CategoryInput(
            category = category,
            onCategoryChange = onCategoryChange,
            selectedColor = selectedColor
        )

        DescriptionInput(
            description = description,
            onDescriptionChange = onDescriptionChange,
            selectedColor = selectedColor
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}




