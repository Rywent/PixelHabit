package com.rywent.pixelhabit.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rywent.pixelhabit.data.local.entity.HabitCompletionEntity
import com.rywent.pixelhabit.data.local.entity.UserEntity
import com.rywent.pixelhabit.data.mapper.toEntity
import com.rywent.pixelhabit.data.mapper.toLifestyleData
import com.rywent.pixelhabit.data.mapper.toTodayHabitData
import com.rywent.pixelhabit.data.repository.HabitRepository
import com.rywent.pixelhabit.data.repository.LifestyleRepository
import com.rywent.pixelhabit.data.repository.UserRepository
import com.rywent.pixelhabit.data.utils.isHabitScheduledForDate
import com.rywent.pixelhabit.presentation.components.habit.HabitData
import com.rywent.pixelhabit.presentation.screens.home.components.DayStat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val habitRepository: HabitRepository,
    private val lifestyleRepository: LifestyleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val userId = "default_user"

    init {
        initializeUser()
        collectLifestyles()
        collectUserStreak()
        loadTodayHabits()
    }

    private fun initializeUser() {
        viewModelScope.launch {
            val user = userRepository.ensureDefaultUser()
            _uiState.update {
                it.copy(
                    userName = getUserName(),
                    currentDate = getCurrentDate(),
                    currentStreak = user.currentStreak,
                )
            }
        }
    }

    private fun collectLifestyles() {
        viewModelScope.launch {
            lifestyleRepository.getLifestylesByUserId(userId).collect { lifestyles ->
                _uiState.update {
                    it.copy(lifestyles = lifestyles.map { lifestyle -> lifestyle.toLifestyleData() })
                }
            }
        }
    }
    private fun collectUserStreak() {
        viewModelScope.launch {
            userRepository.getUserFlow(userId).collect { user ->
                user?.let {
                    _uiState.update { state ->
                        state.copy(
                            currentStreak = it.currentStreak,
                            bestStreak = it.bestStreak
                        )
                    }
                }
            }
        }
    }

    private fun loadTodayHabits() {
        viewModelScope.launch {
            habitRepository.resetWeeklyProgressIfNeeded(userId)
            habitRepository.checkAndResetStreaks(userId)

            updateGlobalStreak()

            val todayDateString = LocalDate.now().toString()
            val dayOfWeek = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)

            val today = LocalDate.now()
            val startOfWeek = today.with(java.time.DayOfWeek.MONDAY).toString()
            val endOfWeek = today.with(java.time.DayOfWeek.SUNDAY).toString()

            launch {
                habitRepository.getWeekCompletionsFlow(startOfWeek, endOfWeek)
                    .collect { completions ->
                        val weekStats = calculateWeekStats(completions)
                        _uiState.update { it.copy(weekStat = weekStats) }
                    }
            }

            habitRepository.getHabitsForToday(userId, todayDateString)
                .collect { habitWithCompletions ->
                    val todayHabits = habitWithCompletions
                        .filter { isHabitScheduledForDate(it.habit, todayDateString, dayOfWeek) }
                        .map { it.toTodayHabitData() }

                    _uiState.update { state ->
                        state.copy(todayHabits = todayHabits)
                    }
                }
        }
    }



    fun onHabitCheckboxClicked(id: String, isCompleted: Boolean) {
        viewModelScope.launch {
            habitRepository.toggleCompletion(id, LocalDate.now().toString(), isCompleted)
            updateGlobalStreak()
        }
    }

    fun createHabit(habit: HabitData) {
        viewModelScope.launch {
            habitRepository.insertHabit(habit.toEntity(userId))
        }
    }

    fun onHabitClick(id: String) {
        val habit = _uiState.value.todayHabits.find { it.id == id }
    }



    fun onSettingsClicked() {

    }

    fun onAboutClicked() {
        _uiState.update { it.copy(showAboutSheet = true) }
    }

    fun onDismissAbout() {
        _uiState.update { it.copy(showAboutSheet = false) }
    }

    fun onAddHabit() {
        _uiState.update { it.copy(showCreateHabitPanel = true) }
    }

    fun onDismissCreateHabitPanel() {
        _uiState.update { it.copy(showCreateHabitPanel = false) }
    }

    fun onToggleExpandTodayHabits() {
        _uiState.update { it.copy(isTodayHabitsExpanded = !it.isTodayHabitsExpanded) }
    }



    private fun getUserName(): String = "Rywent"

    private fun getCurrentDate(): String {
        val today = LocalDate.now()
        val formatter = java.time.format.DateTimeFormatter.ofPattern(
            "EEEE, d MMMM", java.util.Locale.getDefault()
        )
        return "Today's ${today.format(formatter).replaceFirstChar { it.uppercase() }}"
    }

    private suspend fun loadWeekStatistics() {
        val completions = habitRepository.getWeekCompletions(userId)
        val weekStats = calculateWeekStats(completions)
        _uiState.update { it.copy(weekStat = weekStats) }
    }
    private fun calculateWeekStats(completions: List<HabitCompletionEntity>): List<DayStat> {
        val today = LocalDate.now()
        val startOfWeek = today.with(java.time.DayOfWeek.MONDAY)

        val dayNames = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")

        return dayNames.mapIndexed { index, shortName ->
            val date = startOfWeek.plusDays(index.toLong())

            val value = if (date.isAfter(today)) {
                -1
            } else {
                completions.count { it.date == date.toString() }
            }

            DayStat(shortName = shortName, value = value)
        }
    }

    private suspend fun updateGlobalStreak() {
        val streak = habitRepository.calculateAndUpdateGlobalStreak(userId)
        val user = userRepository.getUserById(userId)
        val bestStreak = maxOf(streak, user?.bestStreak ?: 0)
        userRepository.updateStreak(userId, streak, bestStreak)
    }
}

