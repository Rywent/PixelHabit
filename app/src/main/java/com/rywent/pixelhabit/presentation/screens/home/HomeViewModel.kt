package com.rywent.pixelhabit.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rywent.pixelhabit.data.local.entity.HabitCompletionEntity
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
    private var previousStreak: Int? = null
    private var streakPanelJob: Job? = null
    private var isFirstLoad = true // Флаг первой загрузки
    private var isAppInForeground = true // Приложение активно

    companion object {
        private const val STREAK_PANEL_DISPLAY_DURATION = 4000L
        private const val STREAK_PANEL_EXIT_ANIMATION_DURATION = 500L
        private const val STREAK_RESET_PANEL_DURATION = 7000L
    }

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
                    currentStreak = user.currentStreak
                )
            }
        }
    }

    private fun collectLifestyles() {
        viewModelScope.launch {
            lifestyleRepository.getLifestylesByUserId(userId).collect { lifestyles ->
                _uiState.update {
                    it.copy(lifestyles = lifestyles.map { it.toLifestyleData() })
                }
            }
        }
    }

    private fun collectUserStreak() {
        viewModelScope.launch {
            userRepository.getUserFlow(userId).collect { user ->
                user?.let { handleStreakUpdate(it.currentStreak) }
            }
        }
    }

    private fun handleStreakUpdate(newStreak: Int) {
        val isIncrease = previousStreak != null && newStreak > previousStreak!!
        val isReset = previousStreak != null && newStreak < previousStreak!!
        val previousValue = previousStreak

        previousStreak = newStreak

        _uiState.update { state ->
            state.copy(
                currentStreak = newStreak,
                bestStreak = maxOf(newStreak, state.bestStreak)
            )
        }

        when {
            // Стрик увеличился - всегда показываем
            isIncrease -> {
                isFirstLoad = false
                showStreakPanel(newStreak, isResetMode = false)
            }
            // Стрик сбросился только при первой загрузке приложения
            isReset && isFirstLoad -> {
                isFirstLoad = false
                showStreakPanel(previousValue!!, isResetMode = true)
            }
            // Стрик сбросился во время использования - игнорируем
            isReset && !isFirstLoad -> {
                // Ничего не делаем
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
            val (startOfWeek, endOfWeek) = getWeekRange()

            launch { collectWeekCompletions(startOfWeek, endOfWeek) }
            collectTodayHabits(todayDateString, dayOfWeek)
        }
    }

    private fun getWeekRange(): Pair<String, String> {
        val today = LocalDate.now()
        val startOfWeek = today.with(java.time.DayOfWeek.MONDAY)
        val endOfWeek = today.with(java.time.DayOfWeek.SUNDAY)
        return startOfWeek.toString() to endOfWeek.toString()
    }

    private suspend fun collectWeekCompletions(startOfWeek: String, endOfWeek: String) {
        habitRepository.getWeekCompletionsFlow(startOfWeek, endOfWeek).collect { completions ->
            _uiState.update { it.copy(weekStat = calculateWeekStats(completions)) }
        }
    }

    private suspend fun collectTodayHabits(todayDateString: String, dayOfWeek: String) {
        habitRepository.getHabitsForToday(userId, todayDateString).collect { habits ->
            val todayHabits = habits
                .filter { isHabitScheduledForDate(it.habit, todayDateString, dayOfWeek) }
                .map { it.toTodayHabitData() }

            _uiState.update { it.copy(todayHabits = todayHabits) }
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

    private fun showStreakPanel(streak: Int, isResetMode: Boolean) {
        streakPanelJob?.cancel()

        streakPanelJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showStreakPanel = true,
                    streakPanelValue = streak,
                    streakPanelVisible = true,
                    isStreakReset = isResetMode
                )
            }

            val duration = if (isResetMode) STREAK_RESET_PANEL_DURATION else STREAK_PANEL_DISPLAY_DURATION
            delay(duration)

            _uiState.update { it.copy(streakPanelVisible = false) }
            delay(STREAK_PANEL_EXIT_ANIMATION_DURATION)

            _uiState.update {
                it.copy(
                    showStreakPanel = false,
                    isStreakReset = false
                )
            }
        }
    }

    private fun getUserName(): String = "Rywent"

    private fun getCurrentDate(): String {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault())
        return "Today's ${today.format(formatter).replaceFirstChar { it.uppercase() }}"
    }

    private fun calculateWeekStats(completions: List<HabitCompletionEntity>): List<DayStat> {
        val today = LocalDate.now()
        val startOfWeek = today.with(java.time.DayOfWeek.MONDAY)
        val dayNames = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")

        return dayNames.mapIndexed { index, shortName ->
            val date = startOfWeek.plusDays(index.toLong())
            val value = when {
                date.isAfter(today) -> -1
                else -> completions.count { it.date == date.toString() }
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