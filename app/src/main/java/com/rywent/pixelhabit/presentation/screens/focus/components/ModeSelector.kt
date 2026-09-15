package com.rywent.pixelhabit.presentation.screens.focus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.rywent.pixelhabit.presentation.screens.focus.FocusMode
import com.rywent.pixelhabit.ui.theme.adaptiveShadowColor

@Composable
fun ModeSelector(
    selected: FocusMode,
    enabled: Boolean = true,
    onSelect: (FocusMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val shadow = adaptiveShadowColor()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FocusMode.entries.forEach { mode ->
            val isSelected = mode == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .shadow(
                        elevation = if (isSelected) 6.dp else 2.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = shadow,
                        spotColor = shadow
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isSelected) scheme.primaryContainer
                        else scheme.surfaceContainerHigh
                    )
                    .then(
                        if (isSelected) Modifier.border(
                            1.5.dp,
                            scheme.primary.copy(alpha = 0.5f),
                            RoundedCornerShape(16.dp)
                        ) else Modifier
                    )
                    .clickable(enabled = enabled) { onSelect(mode) }
                    .padding(vertical = 12.dp, horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = mode.title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) scheme.onPrimaryContainer else scheme.onSurface,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = mode.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected)
                            scheme.onPrimaryContainer.copy(alpha = 0.75f)
                        else
                            scheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
