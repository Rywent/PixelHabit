package com.rywent.pixelhabit.presentation.screens.habits.subScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rywent.pixelhabit.presentation.screens.habits.HabitsUIState
import com.rywent.pixelhabit.presentation.screens.habits.components.NoQuests
import com.rywent.pixelhabit.presentation.screens.habits.components.QuestCard


@Composable
fun QuestsSubScreen(
    navigateToQuestDetails: (String) -> Unit,
    uiState: HabitsUIState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier
        .padding(top = 8.dp)) {

        if (uiState.quests.isEmpty()) {
            NoQuests()
        } else {
            // active quests
            uiState.quests
                .filter { !it.isCompleted }
                .forEach { quest ->
                    QuestCard(
                        quest = quest,
                        onClick = { navigateToQuestDetails(quest.id) },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

            // completed quests
            val completedQuests = uiState.quests.filter { it.isCompleted }
            if (completedQuests.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Completed",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                completedQuests.forEach { quest ->
                    QuestCard(
                        quest = quest,
                        onClick = { navigateToQuestDetails(quest.id) },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}