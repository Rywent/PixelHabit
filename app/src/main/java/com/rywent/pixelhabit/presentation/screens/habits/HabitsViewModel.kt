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
import com.rywent.pixelhabit.data.mapper.toEntity
import com.rywent.pixelhabit.data.mapper.toHabitData
import com.rywent.pixelhabit.data.mapper.toLifestyleData
import com.rywent.pixelhabit.data.repository.HabitRepository
import com.rywent.pixelhabit.data.repository.LifestyleRepository
import com.rywent.pixelhabit.presentation.components.habit.HabitData
import com.rywent.pixelhabit.presentation.screens.habits.components.LifestyleData
import com.rywent.pixelhabit.presentation.screens.habits.components.QuestData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val lifestyleRepository: LifestyleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HabitsUIState())
    val uiState: StateFlow<HabitsUIState> = _uiState.asStateFlow()

    private val userId = "default_user"

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

    fun createHabit(habit: HabitData) {
        viewModelScope.launch {
            habitRepository.insertHabit(habit.toEntity(userId))
        }
    }

    fun createLifestyle(lifestyle: LifestyleData) {
        viewModelScope.launch {
            lifestyleRepository.insertLifestyle(lifestyle.toEntity(userId))
        }
    }
    private fun loadData() {
        viewModelScope.launch {
            lifestyleRepository.getLifestylesByUserId(userId).collect { lifestyles ->
                _uiState.update { it ->
                    it.copy(lifestyles = lifestyles.map { it.toLifestyleData() })
                }
            }
        }

        viewModelScope.launch {
            val today = LocalDate.now().toString()

            combine(
                habitRepository.getAllHabits(userId),
                habitRepository.getCompletionsByDate(today)
            ) { habits, completions ->
                val completedIds = completions.filter { it.completed }.map { it.habitId }.toSet()

                habits.map { entity ->
                    entity.toHabitData().copy(isCompletedToday = entity.id in completedIds)
                }
            }.collect { allHabits ->
                _uiState.update { it.copy(allHabits = allHabits) }
                calculateStats(allHabits)
            }
        }
    }

    private fun calculateStats(habits: List<HabitData>) {
        val totalHabitCount = habits.size
        val habitsCompleted = habits.count { it.weeklyDone == it.weeklyGoal }
        val completionRate = if (totalHabitCount > 0) {
            (habits.sumOf { it.weeklyDone }.toFloat() / habits.sumOf { it.weeklyGoal }) * 100
        } else 0f
        val avgFocusTime = 0

        _uiState.update {
            it.copy(
                totalHabitCount = totalHabitCount,
                habitsCompleted = habitsCompleted,
                completionRate = completionRate,
                avgFocusTime = avgFocusTime
            )
        }
    }
}
