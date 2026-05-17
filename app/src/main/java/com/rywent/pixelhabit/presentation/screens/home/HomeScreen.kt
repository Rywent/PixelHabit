package com.rywent.pixelhabit.presentation.screens.home


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.EmojiObjects
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rywent.pixelhabit.R
import com.rywent.pixelhabit.presentation.components.habit.HabitTodayCard
import com.rywent.pixelhabit.presentation.screens.home.components.AddHabitButton
import com.rywent.pixelhabit.presentation.screens.home.components.HeaderButtons
import com.rywent.pixelhabit.presentation.screens.home.components.NoHabitsToday
import com.rywent.pixelhabit.presentation.screens.home.components.StreakCard
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
                        onAddHabit = {}
                    )
                }
            }
            item {

                Spacer(modifier = Modifier.height(20.dp))
                WeekStatistics(data = uiState.weekStat, onWeekClick = {})
            }
            item {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "Today Habits",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = FontFamily(
                            Font(
                                resId = R.font.gflex_variable,
                                variationSettings = FontVariation.Settings(
                                    FontVariation.weight(750),
                                    FontVariation.width(155f)
                                )
                            )
                        ),
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        lineHeight = 10.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            if (uiState.todayHabits.isEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(60.dp))
                    NoHabitsToday()
                }
            } else {
                items(
                    items = uiState.todayHabits,
                    key = { it.id }
                ) { habit ->
                    HabitTodayCard(
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
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        HeaderButtons(
            onClickSettings = {},
            onClickAppVersion = {},
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(start = 10.dp, end = 10.dp, top = 8.dp)
        )
    }
}