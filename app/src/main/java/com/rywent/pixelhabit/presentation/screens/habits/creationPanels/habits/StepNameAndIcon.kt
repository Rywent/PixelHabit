package com.rywent.pixelhabit.presentation.screens.habits.creationPanels.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StepNameAndIcon(
    name: String,
    onNameChange: (String) -> Unit,
    selectedIcon: ImageVector,
    selectedColor: Color,
    onIconSelected: (ImageVector) -> Unit,
    onColorSelected: (Color) -> Unit,
    onMoreIconsClick: () -> Unit,
    onMoreColorsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(128.dp)
                .shadow(12.dp, CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape)
                .border(8.dp, selectedColor.copy(alpha = 0.25f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = selectedIcon,
                contentDescription = null,
                tint = selectedColor,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = { Text("Habit name") },
            textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Icon",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            TextButton(onClick = onMoreIconsClick) {
                Text("More icons")
                Icon(Icons.Default.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        IconSelectionGrid(selectedIcon, selectedColor, onIconSelected)

        Spacer(modifier = Modifier.height(28.dp))

        // color
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Color",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            TextButton(onClick = onMoreColorsClick) {
                Text("More colors")
                Icon(Icons.Default.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ColorPalette(selectedColor, onColorSelected)
    }
}