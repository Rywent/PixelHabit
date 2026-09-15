package com.rywent.pixelhabit.presentation.screens.habits.creationPanels.quests

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RemoveDone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StepQuestFailureMode(
    selectedMode: FailureMode,
    onModeSelected: (FailureMode) -> Unit,
    color: Color
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "What happens if you miss a day?",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onModeSelected(FailureMode.FAIL) },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedMode == FailureMode.FAIL)
                    color.copy(alpha = 0.12f)
                else
                    MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            border = if (selectedMode == FailureMode.FAIL)
                BorderStroke(2.dp, color)
            else
                null
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.RemoveDone,
                    contentDescription = null,
                    tint = if (selectedMode == FailureMode.FAIL) color else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Strict Mode",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (selectedMode == FailureMode.FAIL) color else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "if you miss a day the quest failed. No second chances.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (selectedMode == FailureMode.FAIL) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Selected",
                        tint = color
                    )
                }
            }
        }


        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onModeSelected(FailureMode.SHIFT) },
            shape = RoundedCornerShape(50.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedMode == FailureMode.SHIFT)
                    color.copy(alpha = 0.12f)
                else
                    MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            border = if (selectedMode == FailureMode.SHIFT)
                BorderStroke(2.dp, color)
            else
                null
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = null,
                    tint = if (selectedMode == FailureMode.SHIFT) color else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Flexible Mode",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (selectedMode == FailureMode.SHIFT) color else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "if you miss a day, the quest shifts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (selectedMode == FailureMode.SHIFT) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Selected",
                        tint = color
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))


        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "You can change this later in Quest Settings",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

enum class FailureMode {
    FAIL,
    SHIFT
}