package com.rywent.pixelhabit.presentation.screens.habits.creationPanels.lifestyles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rywent.pixelhabit.presentation.screens.habits.components.LifestyleCard
import com.rywent.pixelhabit.presentation.screens.habits.components.LifestyleData

@Composable
fun StepPreview(
    name: String,
    selectedIcon: ImageVector,
    selectedColor: Color,
    category: String,
    description: String,
    onSave: () -> Unit,
    isEditing: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Preview",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "This is how your lifestyle will look",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LifestyleCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            lifestyle = LifestyleData(
                id = "",
                name = name.ifBlank { "Lifestyle name" },
                description = description,
                icon = selectedIcon,
                iconColor = selectedColor,
                category = category,
                createdDate = "Today",
                isActive = true
            ),
            onClick = { }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = selectedColor,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Icon(
                imageVector = if (isEditing) Icons.Default.Edit else Icons.Default.Check,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isEditing) "Save Changes" else "Create Lifestyle",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}