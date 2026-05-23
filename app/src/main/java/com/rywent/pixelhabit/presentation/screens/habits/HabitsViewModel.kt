package com.rywent.pixelhabit.presentation.screens.habits

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.NightlightRound
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rywent.pixelhabit.presentation.components.habit.HabitData
import com.rywent.pixelhabit.presentation.screens.habits.components.LifestyleData
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
    fun onLifestyleClick(lifestyleId: String) {
        _uiState.update { it.copy(selectedLifestyleId = lifestyleId) }
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
                    lifestyleName = "Mindfulness",
                    lifestyleColor = Color(0xFF8B5CF6),
                    lifestyleIcon = Icons.Rounded.Spa,
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
                    lifestyleName = "Health",
                    lifestyleColor = Color(0xFFEF4444),
                    lifestyleIcon = Icons.Rounded.Favorite,
                    weeklyProgress = 0.6f,
                    weeklyDone = 3,
                    weeklyGoal = 5,
                    currentStreak = 8,
                    bestStreak = 19
                )
            )

            val sampleLifestyles = listOf(
                LifestyleData(
                    id = "1",
                    name = "Self-Development",
                    description = "Books, courses, meditation, and learning new skills",
                    icon = Icons.Rounded.Psychology,
                    iconColor = Color(0xFF8B5CF6),
                    category = "Growth",
                    createdDate = "Active since Jan 2024",
                    isActive = true
                ),
                LifestyleData(
                    id = "2",
                    name = "Health & Fitness",
                    description = "Workouts, nutrition, sleep tracking, and wellness",
                    icon = Icons.Rounded.Favorite,
                    iconColor = Color(0xFFEF4444),
                    category = "Body",
                    createdDate = "Active since Mar 2024",
                    isActive = true
                ),
                LifestyleData(
                    id = "3",
                    name = "Career & Work",
                    description = "Projects, networking, skills, and professional growth",
                    icon = Icons.Rounded.Work,
                    iconColor = Color(0xFF3B82F6), // Blue
                    category = "Professional",
                    createdDate = "Active since Feb 2024",
                    isActive = true
                ),
                LifestyleData(
                    id = "4",
                    name = "Relationships",
                    description = "Family time, friends, dating, and social connections",
                    icon = Icons.Rounded.People,
                    iconColor = Color(0xFFF59E0B),
                    category = "Social",
                    createdDate = "Active since Apr 2024",
                    isActive = true
                )
            )

            _uiState.update {
                it.copy(
                    allHabits = sampleHabits,
                    lifestyles = sampleLifestyles
                )
            }
        }
    }
}
