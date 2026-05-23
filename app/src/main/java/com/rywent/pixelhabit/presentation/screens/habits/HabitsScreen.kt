package com.rywent.pixelhabit.presentation.screens.habits

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rywent.pixelhabit.presentation.components.customElements.CustomCircularProgress
import com.rywent.pixelhabit.presentation.navigation.Screen
import com.rywent.pixelhabit.presentation.screens.habits.components.HabitsTabSwitcher
import com.rywent.pixelhabit.presentation.screens.habits.subScreens.HabitsSubScreen
import com.rywent.pixelhabit.presentation.screens.habits.subScreens.LifestyleSubScreen
import com.rywent.pixelhabit.presentation.screens.habits.subScreens.subTabTransitionSpec

@Composable
fun HabitsScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HabitsViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val tabs = listOf("HABITS", "LIFESTYLE", "QUESTS")

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 15.dp, end = 15.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "Habits",
                    fontSize = 46.sp,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            item {
                Spacer(modifier = Modifier.height(15.dp))

                HabitsTabSwitcher(
                    tabs = tabs,
                    selectedIndex = uiState.selectedTabIndex,
                    onTabSelected = { index -> viewModel.onTabSelected(index)}
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                AnimatedContent(
                    targetState = uiState.selectedTabIndex,
                    transitionSpec = {
                        subTabTransitionSpec(targetState > initialState)
                    },
                    label = "tabContentTransition",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp)
                ) { targetIndex ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(
                                animationSpec = tween(300, easing = FastOutSlowInEasing)
                            )
                    ) {
                        when (targetIndex) {
                            0 -> HabitsSubScreen(
                                navigateToHabitDetails = { habitId ->
                                    viewModel.onHabitClick(habitId)
                                },
                                uiState
                            )
                            1 -> LifestyleSubScreen(
                                navigateToLifestyleDetails = {lifestyleId ->
                                    viewModel.onLifestyleClick(lifestyleId)
                                },
                                uiState
                            )
                            2 -> Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Quests Screen",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}