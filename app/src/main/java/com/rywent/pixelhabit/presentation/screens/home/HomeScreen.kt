package com.rywent.pixelhabit.presentation.screens.home


import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rywent.pixelhabit.presentation.components.habit.HabitTodayCard
import com.rywent.pixelhabit.presentation.components.habit.TodayHabitData
import com.rywent.pixelhabit.presentation.components.panels.CreateHabitPanel
import com.rywent.pixelhabit.presentation.screens.about.AboutBottomSheet
import com.rywent.pixelhabit.presentation.screens.home.components.AddHabitButton
import com.rywent.pixelhabit.presentation.screens.home.components.HeaderButtons
import com.rywent.pixelhabit.presentation.screens.home.components.NoHabitsToday
import com.rywent.pixelhabit.presentation.screens.home.components.StreakCard
import com.rywent.pixelhabit.presentation.screens.home.components.TodayHabitsHeader
import com.rywent.pixelhabit.presentation.screens.home.components.WeekStatistics
import com.rywent.pixelhabit.presentation.screens.home.components.WelcomeHeader

@OptIn(ExperimentalTextApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(135.dp))
                WelcomeHeader(
                    name = uiState.userName,
                    subtitle = uiState.currentDate
                )
            }
            item {
                Spacer(modifier = Modifier.height(70.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    StreakCard(
                        currentStreak = uiState.currentStreak,
                        modifier = Modifier.offset(x = (-20).dp),
                        onStreakClick = {}
                    )
                    AddHabitButton(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 0.dp, y = (-88).dp),
                        onAddHabit = { viewModel.onAddHabit() }
                    )
                }
            }
            item {

                Spacer(modifier = Modifier.height(20.dp))
                WeekStatistics(data = uiState.weekStat, onWeekClick = {})
            }
            item {
                Spacer(modifier = Modifier.height(40.dp))
                TodayHabitsHeader(
                    totalCount = uiState.todayHabits.size,
                    isExpanded = uiState.isTodayHabitsExpanded,
                    onToggleClick = { viewModel.onToggleExpandTodayHabits() }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            if (uiState.todayHabits.isEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(60.dp))
                    NoHabitsToday()
                }
            } else {
                val sorted = uiState.todayHabits.sortedWith(
                    compareBy<TodayHabitData> { it.isCompleted }
                        .thenBy { it.name.lowercase() }
                )
                val displayHabits = if (uiState.isTodayHabitsExpanded) sorted else sorted.take(5)

                items(
                    items = displayHabits,
                    key = { it.id }
                ) { habit ->
                    HabitTodayCard(
                        modifier = Modifier.animateItem(
                            fadeInSpec = tween(300),
                            fadeOutSpec = tween(300)
                        ),
                        name = habit.name,
                        description = habit.description,
                        streak = habit.streak,
                        icon = habit.icon,
                        isCompleted = habit.isCompleted,
                        onCheckedChange = { completed ->
                            viewModel.onHabitCheckboxClicked(habit.id, completed)
                        },
                        onTodayHabitClick = {
                            viewModel.onHabitClick(habit.id)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        HeaderButtons(
            onClickSettings = {},
            onClickAppVersion = {viewModel.onAboutClicked()},
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(start = 10.dp, end = 10.dp, top = 8.dp)
        )

        if(uiState.showAboutSheet){
            AboutBottomSheet(true, onDismiss = {viewModel.onDismissAbout()})
        }

        if (uiState.showCreateHabitPanel) {
            CreateHabitPanel(
                onDismiss = { viewModel.onDismissCreateHabitPanel() },
                lifestyles = uiState.lifestyles,
                onHabitCreated = { habit ->
                    viewModel.createHabit(habit)
                }
            )
        }
    }
}