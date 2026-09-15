package com.rywent.pixelhabit.presentation.screens.focus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rywent.pixelhabit.ui.theme.adaptiveShadowColor

@Composable
fun SessionInfoBar(
    completedSessions: Int,
    currentCycle: Int,
    longBreakEvery: Int,
    stagesCount: Int = 0,
    currentStageIndex: Int = 0,
    todayFocusFormatted: String,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val shadow = adaptiveShadowColor()

    val isCustom = stagesCount > 0
    val labelText = if (isCustom) "Stage ${currentStageIndex + 1}/$stagesCount" else "$completedSessions done"
    val totalDots = if (isCustom) stagesCount else longBreakEvery

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp), ambientColor = shadow, spotColor = shadow)
            .clip(RoundedCornerShape(20.dp))
            .background(scheme.surfaceContainerHigh)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.LocalFireDepartment,
                contentDescription = null,
                tint = scheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = labelText,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = scheme.onSurface
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(totalDots) { index ->
                val filled = if (isCustom) index <= currentStageIndex else index < currentCycle
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(
                            if (filled) scheme.primary
                            else scheme.outlineVariant.copy(alpha = 0.45f)
                        )
                )
            }
        }

        Text(
            text = todayFocusFormatted,
            style = MaterialTheme.typography.labelMedium,
            color = scheme.onSurfaceVariant,
            fontSize = 12.sp
        )
    }
}