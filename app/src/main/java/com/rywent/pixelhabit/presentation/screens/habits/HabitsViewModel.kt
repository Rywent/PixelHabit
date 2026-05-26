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
import com.rywent.pixelhabit.presentation.screens.habits.components.QuestData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HabitsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(HabitsUIState())
    val uiState: StateFlow<HabitsUIState> = _uiState.asStateFlow()

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

    fun onQuestClick(questId: String) {
        _uiState.update { it.copy(selectedQuestId = questId) }

    }

    fun onHabitDetailDismiss() {
        _uiState.update { it.copy(selectedHabitId = null) }
    }

    fun onHabitCreateClick() {
        _uiState.update { it.copy(showCreateHabitPanel = true) }
    }

    fun onLifestyleCreateClick() {
        _uiState.update { it.copy(showCreateLifestylePanel = true) }
    }

    fun onQuestCreateClick() {
        _uiState.update { it.copy(showCreateQuestPanel = true) }
    }

    fun onDismissCreateHabitPanel() {
        _uiState.update { it.copy(showCreateHabitPanel = false) }
    }

    fun onDismissCreateLifestylePanel() {
        _uiState.update { it.copy(showCreateLifestylePanel = false) }
    }

    fun onDismissCreateQuestPanel() {
        _uiState.update { it.copy(showCreateQuestPanel = false) }
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

            val sampleQuests = listOf(
                QuestData(
                    id = "1",
                    name = "30 days of meditation",
                    description = "10 minutes every morning, no skipping",
                    icon = Icons.Rounded.SelfImprovement,
                    iconColor = Color(0xFF8B5CF6),
                    totalDays = 30,
                    currentDay = 18,
                    daysLeft = 12,
                    startDate = "12 May",
                    endDate = "11 Jun",
                    isCompleted = false,
                    completionPercent = 0.6f,
                ),
                QuestData(
                    id = "2",
                    name = "No sugar for 2 weeks",
                    description = "Cut out added sugar completely",
                    icon = Icons.Rounded.Favorite,
                    iconColor = Color(0xFFEF4444),
                    totalDays = 14,
                    currentDay = 14,
                    daysLeft = 0,
                    startDate = "1 May",
                    endDate = "15 May",
                    isCompleted = true,
                    completionPercent = 1f,
                ),
                QuestData(
                    id = "3",
                    name = "Read 3 books this month",
                    description = "At least 20 pages a day",
                    icon = Icons.Rounded.Psychology,
                    iconColor = Color(0xFF8B5CF6),
                    totalDays = 31,
                    currentDay = 9,
                    daysLeft = 22,
                    startDate = "1 May",
                    endDate = "31 May",
                    isCompleted = false,
                    completionPercent = 0.29f,
                ),
                QuestData(
                    id = "4",
                    name = "10K steps every day",
                    description = "Hit the goal no matter what",
                    icon = Icons.Rounded.FitnessCenter,
                    iconColor = Color(0xFFEF4444),
                    totalDays = 10,
                    currentDay = 7,
                    daysLeft = 3,
                    startDate = "17 May",
                    endDate = "27 May",
                    isCompleted = false,
                    completionPercent = 0.7f,
                ),
                QuestData(
                    id = "5",
                    name = "Deep work sprints",
                    description = "2 hours of focused work every weekday",
                    icon = Icons.Rounded.Work,
                    iconColor = Color(0xFF3B82F6),
                    totalDays = 21,
                    currentDay = 5,
                    daysLeft = 16,
                    startDate = "20 May",
                    endDate = "10 Jun",
                    isCompleted = false,
                    completionPercent = 0.24f,
                ),
                QuestData(
                    id = "6",
                    name = "Call a friend every day",
                    description = "Reconnect with people that matter",
                    icon = Icons.Rounded.People,
                    iconColor = Color(0xFFF59E0B),
                    totalDays = 7,
                    currentDay = 4,
                    daysLeft = 3,
                    startDate = "20 May",
                    endDate = "27 May",
                    isCompleted = false,
                    completionPercent = 0.57f,
                ),
                QuestData(
                    id = "7",
                    name = "Sleep before 11 PM",
                    description = "Wind down by 10:30, no screens after",
                    icon = Icons.Rounded.NightlightRound,
                    iconColor = Color(0xFF6366F1),
                    totalDays = 14,
                    currentDay = 0,
                    daysLeft = 14,
                    startDate = "26 May",
                    endDate = "9 Jun",
                    isCompleted = false,
                    completionPercent = 0f,
                ),
                QuestData(
                    id = "8",
                    name = "Morning pages",
                    description = "Write 500 words every morning",
                    icon = Icons.Rounded.Spa,
                    iconColor = Color(0xFF8B5CF6),
                    totalDays = 30,
                    currentDay = 30,
                    daysLeft = 0,
                    startDate = "10 Apr",
                    endDate = "10 May",
                    isCompleted = true,
                    completionPercent = 1f,
                )
            )

            _uiState.update {
                it.copy(
                    allHabits = sampleHabits,
                    lifestyles = sampleLifestyles,
                    quests = sampleQuests
                )
            }
        }
    }
}
