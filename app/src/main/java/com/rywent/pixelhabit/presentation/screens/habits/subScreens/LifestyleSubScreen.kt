package com.rywent.pixelhabit.presentation.screens.habits.subScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rywent.pixelhabit.presentation.screens.habits.HabitsUISate
import com.rywent.pixelhabit.presentation.screens.habits.components.LifestyleCard


@Composable
fun LifestyleSubScreen(
    navigateToLifestyleDetails: (String) -> Unit,
    uiState: HabitsUISate,
    modifier: Modifier = Modifier
) {
    Column {
        uiState.lifestyles.forEach { lifestyle ->
            LifestyleCard(
                modifier = modifier.padding(bottom = 12.dp),
                lifestyle = lifestyle,
                {
                    navigateToLifestyleDetails(lifestyle.id)
                },
                {}
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}