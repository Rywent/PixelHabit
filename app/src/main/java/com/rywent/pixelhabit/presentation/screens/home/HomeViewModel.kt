package com.rywent.pixelhabit.presentation.screens.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.lifecycle.ViewModel
import com.rywent.pixelhabit.presentation.components.habit.TodayHabitData
import com.rywent.pixelhabit.presentation.screens.home.components.DayStat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun onSettingsClicked() {

    }

    fun onCalendarClicked() {

    }

    fun onAddHabit(){

    }

    fun onHabitClick (id: String){
        val habit = _uiState.value.todayHabits.find { it.id == id }

    }
    fun onHabitCheckboxClicked(id: String, isCompleted: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                todayHabits = currentState.todayHabits.map { habit ->
                    if (habit.id == id)
                        habit.copy(isCompleted = isCompleted)
                    else
                        habit
                }
            )
        }
    }

    private fun loadHomeData() {
        _uiState.value = HomeUiState(
            userName = getUserName(),
            currentDate = getCurrentDate(),
            currentStreak = 14,
            weekStat = getWeekStatistics(),
            todayHabits = getTodayHabits()
        )
    }

    private fun getTodayHabits() : List<TodayHabitData>{
        return emptyList()
    }

    private fun getWeekStatistics() : List<DayStat> {
        val data = listOf(
            DayStat("Mo", 12), DayStat("Tu", 4), DayStat("We", 18),
            DayStat("Th", 9), DayStat("Fr", 2), DayStat("Sa", 7), DayStat("Su", 3),
        )
        return data
    }
    private fun getUserName(): String {
        return "Rywent"
    }

    private fun getCurrentDate(): String {
        val today = java.time.LocalDate.now()
        val formatter = java.time.format.DateTimeFormatter.ofPattern(
            "EEEE, d MMMM",
            java.util.Locale.getDefault()
        )
        return "Today's ${today.format(formatter).replaceFirstChar { it.uppercase() }}"
    }
}