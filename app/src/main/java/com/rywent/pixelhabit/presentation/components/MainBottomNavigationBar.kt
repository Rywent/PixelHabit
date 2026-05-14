package com.rywent.pixelhabit.presentation.components

import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.rywent.pixelhabit.presentation.navigation.Screen
import com.rywent.pixelhabit.presentation.navigation.mainRouteIndex
import com.rywent.pixelhabit.presentation.navigation.navigateToTopLevelSafely

@Composable
fun MainBottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomTabs = listOf(
        BottomBarItem(
            screen = Screen.Focus,
            icon = Icons.Outlined.Timer,
            selectedIcon = Icons.Filled.Timer,
            label = "Focus"
        ),
        BottomBarItem(
            screen = Screen.Home,
            icon = Icons.Outlined.Home,
            selectedIcon = Icons.Rounded.Home,
            label = "Home"
        ),
        BottomBarItem(
            screen = Screen.Habits,
            icon = Icons.Outlined.Checklist,
            selectedIcon = Icons.Filled.Checklist,
            label = "Habits"
        ),
        BottomBarItem(
            screen = Screen.Journal,
            icon = Icons.Outlined.CollectionsBookmark,
            selectedIcon = Icons.Filled.CollectionsBookmark,
            label = "Journal"
        ),
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(bottom = 30.dp, start = 10.dp, end = 10.dp),
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth(),
            windowInsets = NavigationBarDefaults.windowInsets.only(WindowInsetsSides.Horizontal)
        ) {
            bottomTabs.forEach { item ->
                val selected = currentRoute == item.screen.route

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        navController.navigateToTopLevelSafely(item.screen.route)
                    },
                    icon = {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(text = item.label, style = MaterialTheme.typography.labelMedium)
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                    )
                )
            }
        }
    }
}

data class BottomBarItem(
    val screen: Screen,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String
)