package com.rywent.pixelhabit.presentation.screens.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rywent.pixelhabit.data.local.entity.HabitEntity
import com.rywent.pixelhabit.data.local.entity.UserEntity
import com.rywent.pixelhabit.data.mapper.toEntity
import com.rywent.pixelhabit.data.mapper.toLifestyleData
import com.rywent.pixelhabit.data.mapper.toTodayHabitData
import com.rywent.pixelhabit.data.repository.HabitRepository
import com.rywent.pixelhabit.data.repository.LifestyleRepository
import com.rywent.pixelhabit.data.repository.UserRepository
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
        ensureUserExists()
        viewModelScope.launch {
            habitRepository.checkAndResetStreaks(userId)
            habitRepository.resetWeeklyProgressIfNeeded(userId)
        }
        loadHomeData()
        loadTodayHabits()
    }

    fun onSettingsClicked() {

    }

    fun onAboutClicked() {
        _uiState.update { it.copy(showAboutSheet = true) }
    }

    fun onDismissAbout() {
        _uiState.update { it.copy(showAboutSheet = false) }
    }

    fun onAddHabit(){
        _uiState.update { it.copy(showCreateHabitPanel = true) }
    }

    fun onDismissCreateHabitPanel() {
        _uiState.update { it.copy(showCreateHabitPanel = false) }
    }
    fun onHabitClick (id: String){
        val habit = _uiState.value.todayHabits.find { it.id == id }

    }
    fun createHabit(habit: HabitData) {
        viewModelScope.launch {
            habitRepository.insertHabit(habit.toEntity(userId))
        }
    }

    fun onToggleExpandTodayHabits() {
        _uiState.update { it.copy(isTodayHabitsExpanded = !it.isTodayHabitsExpanded) }
    }

    fun onHabitCheckboxClicked(id: String, isCompleted: Boolean) {
        val todayDateString = LocalDate.now().toString()

        viewModelScope.launch {
            habitRepository.toggleCompletion(id, todayDateString, isCompleted)
        }
    }
    private fun loadHomeData() {
        _uiState.value = HomeUiState(
            userName = getUserName(),
            currentDate = getCurrentDate(),
            currentStreak = 14,
            weekStat = getWeekStatistics(),
        )

        viewModelScope.launch {
            lifestyleRepository.getLifestylesByUserId(userId).collect { lifestyles ->
                _uiState.update { it ->
                    it.copy(lifestyles = lifestyles.map { it.toLifestyleData() })
                }
            }
        }
    }

    private fun loadTodayHabits() {
        val todayDateString = LocalDate.now().toString()
        val dayOfWeek = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)

        viewModelScope.launch {
            habitRepository.getHabitsForToday(userId, todayDateString)
                .collect { habitWithCompletions ->

                    val todayHabits = habitWithCompletions
                        .filter { isHabitForToday(it.habit, dayOfWeek) }
                        .map { it.toTodayHabitData() }

                    _uiState.update { it.copy(todayHabits = todayHabits) }
                }
        }
    }

    private fun isHabitForToday(habit: HabitEntity, dayOfWeek: String): Boolean {
        return when (habit.frequency) {
            "Every day" -> true
            "Weekdays" -> dayOfWeek !in listOf("Sat", "Sun")
            "Weekends" -> dayOfWeek in listOf("Sat", "Sun")
            "Every other day" -> {
                val dayIndex = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").indexOf(dayOfWeek)
                dayIndex % 2 == 0
            }
            "Custom" -> {
                habit.customDays?.split(",")?.contains(dayOfWeek) == true
            }
            else -> true
        }
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

    private fun ensureUserExists() {
        viewModelScope.launch {
            val existingUser = userRepository.getUserById(userId)
            if (existingUser == null) {
                val newUser = UserEntity(
                    id = userId,
                    name = "Rywent"
                )
                userRepository.insertUser(newUser)
            }
        }
    }
}