package com.rywent.pixelhabit.presentation.screens.habits

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.NightlightRound
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rywent.pixelhabit.presentation.components.habit.HabitData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HabitsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(HabitsUISate())
    val uiState: StateFlow<HabitsUISate> = _uiState.asStateFlow()

    init {
        loadData()
    }


    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    fun onHabitClick(habitId: String) {
        _uiState.update { it.copy(selectedHabitId = habitId) }
    }

    fun onHabitDetailDismiss() {
        _uiState.update { it.copy(selectedHabitId = null) }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val sampleHabits = listOf(
                HabitData(
                    id = "1",
                    name = "Morning Meditation",
                    icon = Icons.Rounded.SelfImprovement,
                    frequency = "Daily",
                    timeOfDay = "Morning",
                    timeOfDayIcon = Icons.Rounded.WbSunny,
                    tagName = "Mindfulness",
                    tagColor = androidx.compose.ui.graphics.Color(0xFF8B5CF6),
                    tagIcon = Icons.Rounded.Spa,
                    weeklyProgress = 0.85f,
                    weeklyDone = 6,
                    weeklyGoal = 7,
                    currentStreak = 14,
                    bestStreak = 31
                ),
                HabitData(
                    id = "2",
                    name = "Gym Workout",
                    icon = Icons.Rounded.FitnessCenter,
                    frequency = "5x per week",
                    timeOfDay = "Evening",
                    timeOfDayIcon = Icons.Rounded.NightlightRound,
                    tagName = "Health",
                    tagColor = androidx.compose.ui.graphics.Color(0xFFEF4444),
                    tagIcon = Icons.Rounded.Favorite,
                    weeklyProgress = 0.6f,
                    weeklyDone = 3,
                    weeklyGoal = 5,
                    currentStreak = 8,
                    bestStreak = 19
                )
            )

            _uiState.update {
                it.copy(
                    allHabits = sampleHabits
                )
            }
        }
    }
}
