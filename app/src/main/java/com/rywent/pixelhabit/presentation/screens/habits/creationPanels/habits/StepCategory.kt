package com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rywent.pixelhabit.presentation.screens.habits.components.LifestyleData

@Composable
fun StepCategory(
    lifestyles: List<LifestyleData>,
    selectedCategoryName: String,
    onCategorySelected: (LifestyleData) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding()
    ) {
        Text(
            text = "Choose category",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            text = "This will define the main color and style of your habit",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(lifestyles) { lifestyle ->
                val isSelected = lifestyle.name == selectedCategoryName

                CategoryChoiceCard(
                    lifestyle = lifestyle,
                    isSelected = isSelected,
                    onClick = { onCategorySelected(lifestyle) }
                )
            }
        }
    }
}