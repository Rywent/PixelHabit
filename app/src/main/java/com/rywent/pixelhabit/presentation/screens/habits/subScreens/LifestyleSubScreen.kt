package com.rywent.pixelhabit.presentation.screens.habits.subScreens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rywent.pixelhabit.presentation.screens.habits.HabitsUIState
import com.rywent.pixelhabit.presentation.screens.habits.components.LifestyleCard
import com.rywent.pixelhabit.presentation.screens.habits.components.NoLifestyle

@Composable
fun LifestyleSubScreen(
    navigateToLifestyleDetails: (String) -> Unit,
    uiState: HabitsUIState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = 8.dp)
    ) {
        if (uiState.lifestyles.isEmpty()) {
            NoLifestyle()
        } else {
            val chunkedLifestyles = uiState.lifestyles.chunked(2)

            chunkedLifestyles.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // First card in row
                    LifestyleCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(220.dp),
                        lifestyle = row[0],
                        onClick = {
                            navigateToLifestyleDetails(row[0].id)
                        }
                    )

                    // Second card (if exists) or spacer for symmetry
                    if (row.size > 1) {
                        LifestyleCard(
                            modifier = Modifier
                                .weight(1f)
                                .height(220.dp),
                            lifestyle = row[1],
                            onClick = {
                                navigateToLifestyleDetails(row[1].id)
                            }
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(80.dp))
    }
}