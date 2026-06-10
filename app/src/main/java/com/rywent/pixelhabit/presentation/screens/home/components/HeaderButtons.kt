package com.rywent.pixelhabit.presentation.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.rywent.pixelhabit.ui.theme.adaptiveShadowColor


@Composable
fun HeaderButtons(
    onClickSettings: () -> Unit,
    onClickAppVersion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val shadowColor = adaptiveShadowColor()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledTonalButton(
            onClick = onClickAppVersion,
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(50.dp),
                clip = false,
                ambientColor = shadowColor,
                spotColor = shadowColor
            ),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = scheme.surfaceContainer,
                contentColor = scheme.onSurfaceVariant
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Newspaper,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("v0.1.0")
        }

        FilledTonalButton(
            onClick = onClickSettings,
            shape = RoundedCornerShape(50.dp),
            contentPadding = PaddingValues(12.dp),
            modifier = Modifier.shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(50.dp),
                clip = false,
                ambientColor = shadowColor,
                spotColor = shadowColor
            ),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = scheme.surfaceContainer,
                contentColor = scheme.onSurfaceVariant
            )
        ) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}