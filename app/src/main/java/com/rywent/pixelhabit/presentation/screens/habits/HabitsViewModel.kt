package com.rywent.pixelhabit.presentation.screens.habits

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rywent.pixelhabit.data.local.entity.LifestyleEntity
import com.rywent.pixelhabit.data.mapper.toEntity
import com.rywent.pixelhabit.data.mapper.toHabitData
import com.rywent.pixelhabit.data.mapper.toLifestyleData
import com.rywent.pixelhabit.data.mapper.toPath
import com.rywent.pixelhabit.data.mapper.toQuestData
import com.rywent.pixelhabit.data.repository.HabitRepository
import com.rywent.pixelhabit.data.repository.LifestyleRepository
import com.rywent.pixelhabit.data.repository.QuestRepository
import com.rywent.pixelhabit.data.utils.LifestyleStatsUtils
import com.rywent.pixelhabit.notifications.habit.HabitNotificationScheduler
import com.rywent.pixelhabit.presentation.components.habit.HabitData
import com.rywent.pixelhabit.presentation.screens.habits.components.HabitsFilter
import com.rywent.pixelhabit.presentation.screens.habits.components.LifestyleData
import com.rywent.pixelhabit.presentation.screens.habits.components.QuestData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val lifestyleRepository: LifestyleRepository,
    private val questRepository: QuestRepository,
    private val notificationScheduler: HabitNotificationScheduler
) : ViewModel() {
    private val _uiState = MutableStateFlow(HabitsUIState())
    val uiState: StateFlow<HabitsUIState> = _uiState.asStateFlow()

    private val userId = "default_user"
    private var currentOpenLifestyleId: String? = null
    private var lifestyleStatsJob: Job? = null

    init {
        loadData()
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    // habits
    fun onHabitClick(habitId: String) {
        viewModelScope.launch {
            val habit = habitRepository.getHabitByIdByUserId(habitId, userId)
            val completions = habitRepository.getCompletionsForHabit(habitId).first()

            _uiState.update {
                it.copy(
                    selectedHabitId = habitId,
                    selectedHabit = habit?.toHabitData(),
                    selectedHabitCompletions = completions,
                    showHabitDetailsPanel = true
                )
            }
        }
    }

    fun onHabitDelete(habit: HabitData) {
        viewModelScope.launch {
            notificationScheduler.cancelHabitReminders(habit.id)
            habitRepository.deleteHabit(habit.id)
            onHabitDetailDismiss()
        }
    }

    fun onHabitDetailDismiss() {
        _uiState.update {
            it.copy(
                showHabitDetailsPanel = false,
                selectedHabitId = null,
                selectedHabit = null,
                selectedHabitCompletions = emptyList()
            )
        }
    }

    fun onHabitEditClick(habit: HabitData) {
        _uiState.update { it.copy(
            showEditHabitPanel = true,
            editingHabit = habit
        )}
    }

    fun onDismissEditHabitPanel() {
        _uiState.update { it.copy(
            showEditHabitPanel = false,
            editingHabit = null
        )}
    }

    fun updateHabit(updatedHabit: HabitData) {
        viewModelScope.launch {
            val habitEntity = updatedHabit.toEntity(userId)
            habitRepository.updateHabit(habitEntity)

            notificationScheduler.cancelHabitReminders(habitEntity.id)
            notificationScheduler.scheduleHabitReminder(habitEntity)

            onDismissEditHabitPanel()

            if (_uiState.value.showHabitDetailsPanel && _uiState.value.selectedHabitId != null) {
                val freshHabit = habitRepository.getHabitByIdByUserId(
                    _uiState.value.selectedHabitId!!,
                    userId
                )
                val completions = habitRepository.getCompletionsForHabit(
                    _uiState.value.selectedHabitId!!
                ).first()

                _uiState.update { state ->
                    state.copy(
                        selectedHabit = freshHabit?.toHabitData(),
                        selectedHabitCompletions = completions
                    )
                }
            }
        }
    }

    // filter
    fun onFilterClick() {
        _uiState.update { it.copy(showFilterPanel = true) }
    }

    fun onFilterDismiss() {
        _uiState.update { it.copy(showFilterPanel = false) }
    }

    fun onFilterSelected(filter: HabitsFilter) {
        _uiState.update { it.copy(
            currentFilter = filter,
            showFilterPanel = false
        )}
    }

    // lifestyles
    fun onLifestyleClick(lifestyleId: String) {
        currentOpenLifestyleId = lifestyleId

        viewModelScope.launch {
            val lifestyle = lifestyleRepository.getLifestyleById(lifestyleId)

            if (lifestyle != null) {
                val lifestyleData = lifestyle.toLifestyleData()

                _uiState.update {
                    it.copy(
                        selectedLifestyleId = lifestyleId,
                        selectedLifestyle = lifestyleData,
                        showLifestyleDetailsPanel = true,
                        isLoading = true
                    )
                }

                loadLifestyleStats(lifestyleId)

                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onLifestyleEditClick(lifestyle: LifestyleData) {
        _uiState.update { it.copy(
            showEditLifestylePanel = true,
            editingLifestyle = lifestyle
        )}
    }

    fun onDismissEditLifestylePanel() {
        _uiState.update { it.copy(
            showEditLifestylePanel = false,
            editingLifestyle = null
        )}
    }

    private suspend fun loadLifestyleStats(lifestyleId: String) {
        try {
            val habits = habitRepository.getHabitsByLifestyleIdOnce(lifestyleId, userId)

            if (habits.isEmpty()) {
                _uiState.update { state ->
                    state.copy(
                        totalLifestyleHabitCount = 0,
                        lifestyleHabitsCompletedToday = 0,
                        lifestyleWeeklyActivity = 0,
                        lifestyleMonthlyProgress = 0
                    )
                }
                return
            }

            val today = LocalDate.now()
            val startOfMonth = today.withDayOfMonth(1)
            val endOfMonth = today.withDayOfMonth(today.lengthOfMonth())

            val completions = habitRepository.getCompletionsBetween(
                startDate = startOfMonth.toString(),
                endDate = endOfMonth.toString()
            )

            val stats = LifestyleStatsUtils.calculateLifestyleStats(
                habits = habits,
                completions = completions
            )

            _uiState.update { state ->
                state.copy(
                    totalLifestyleHabitCount = stats.totalHabitsCount,
                    lifestyleHabitsCompletedToday = stats.completedHabitsToday,
                    lifestyleWeeklyActivity = stats.weeklyActivity,
                    lifestyleMonthlyGoal = stats.monthlyGoal,
                    lifestyleMonthlyProgress = stats.monthlyProgress
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.update { state ->
                state.copy(
                    error = e.message
                )
            }
        }
    }


    fun onLifestyleDelete(lifestyle: LifestyleData) {
        viewModelScope.launch {
            var otherLifestyle = lifestyleRepository.getDefaultOtherLifestyle(userId)

            if (otherLifestyle == null) {
                val defaultLifestyle = LifestyleEntity(
                    id = "default_other",
                    name = "Other",
                    description = "Default category for uncategorized habits",
                    iconPath = Icons.Default.QuestionMark.toPath(),
                    iconColorArgb = Color(0xFF9E9E9E).value.toLong(),
                    category = "General",
                    createdDate = System.currentTimeMillis(),
                    isActive = true,
                    userId = userId
                )
                lifestyleRepository.insertLifestyle(defaultLifestyle)
                otherLifestyle = lifestyleRepository.getDefaultOtherLifestyle(userId)
            }

            val habitsToUpdate = habitRepository.getHabitsByLifestyleIdOnce(lifestyle.id, userId)
            habitsToUpdate.forEach { habitEntity ->
                val updatedHabit = habitEntity.copy(
                    lifestyleId = otherLifestyle?.id,
                    lifestyleName = otherLifestyle?.name ?: "Other",
                    lifestyleColorArgb = otherLifestyle?.iconColorArgb ?: Color(0xFF9E9E9E).value.toLong(),
                    lifestyleIconPath = otherLifestyle?.iconPath ?: Icons.Default.QuestionMark.toPath()
                )
                habitRepository.updateHabit(updatedHabit)
            }

            lifestyleRepository.deleteLifestyle(lifestyle.id)

            onLifestyleDetailDismiss()
            refreshAllHabits()
        }
    }

    fun onLifestyleDetailDismiss() {
        lifestyleStatsJob?.cancel()
        currentOpenLifestyleId = null
        _uiState.update {
            it.copy(
                showLifestyleDetailsPanel = false,
                selectedLifestyleId = null,
                selectedLifestyle = null,
                totalLifestyleHabitCount = 0,
                lifestyleHabitsCompletedToday = 0,
                lifestyleWeeklyActivity = 0,
                lifestyleMonthlyGoal = 0,
                lifestyleMonthlyProgress = 0
            )
        }
    }
    fun updateLifestyle(updatedLifestyle: LifestyleData) {
        viewModelScope.launch {
            lifestyleRepository.updateLifestyle(updatedLifestyle.toEntity(userId))

            val habitsToUpdate = habitRepository.getHabitsByLifestyleIdOnce(updatedLifestyle.id, userId)

            habitsToUpdate.forEach { habitEntity ->
                val habitData = habitEntity.toHabitData()
                val updatedHabitData = habitData.copy(
                    lifestyleName = updatedLifestyle.name,
                    lifestyleColor = updatedLifestyle.iconColor,
                    lifestyleIcon = updatedLifestyle.icon
                )
                val updatedHabit = updatedHabitData.toEntity(userId)
                habitRepository.updateHabit(updatedHabit)
            }

            onDismissEditLifestylePanel()

            if (_uiState.value.showLifestyleDetailsPanel && _uiState.value.selectedLifestyleId == updatedLifestyle.id) {
                val freshLifestyle = lifestyleRepository.getLifestyleById(updatedLifestyle.id)
                _uiState.update { state ->
                    state.copy(
                        selectedLifestyle = freshLifestyle?.toLifestyleData()
                    )
                }
            }

            refreshAllHabits()
        }
    }

    private fun refreshAllHabits() {
        viewModelScope.launch {
            val today = LocalDate.now().toString()

            val habits = habitRepository.getAllHabits(userId).firstOrNull() ?: return@launch
            val completions = habitRepository.getCompletionsByDate(today).firstOrNull() ?: return@launch
            val completedIds = completions.filter { it.completed }.map { it.habitId }.toSet()

            val updatedHabits = habits.map { entity ->
                entity.toHabitData().copy(isCompletedToday = entity.id in completedIds)
            }

            _uiState.update { state ->
                state.copy(allHabits = updatedHabits)
            }
            calculateStats(updatedHabits)
        }
    }



    fun onQuestClick(questId: String) {
        _uiState.update { it.copy(selectedQuestId = questId) }
    }

    // create
    fun onHabitCreateClick() {
        _uiState.update { it.copy(showCreateHabitPanel = true) }
    }

    fun onLifestyleCreateClick() {
        _uiState.update { it.copy(showCreateLifestylePanel = true) }
    }

    fun onQuestCreateClick() {
        _uiState.update { it.copy(showCreateQuestPanel = true) }
    }

    // dismiss create
    fun onDismissCreateHabitPanel() {
        _uiState.update { it.copy(showCreateHabitPanel = false) }
    }

    fun onDismissCreateLifestylePanel() {
        _uiState.update { it.copy(showCreateLifestylePanel = false) }
    }

    fun onDismissCreateQuestPanel() {
        _uiState.update { it.copy(showCreateQuestPanel = false) }
    }

    // create logic
    fun createHabit(habit: HabitData) {
        viewModelScope.launch {
            val habitEntity = habit.toEntity(userId)
            habitRepository.insertHabit(habitEntity)

            notificationScheduler.scheduleHabitReminder(habitEntity)
        }
    }

    fun createLifestyle(lifestyle: LifestyleData) {
        viewModelScope.launch {
            lifestyleRepository.insertLifestyle(lifestyle.toEntity(userId))
        }
    }

    fun createQuest(quest: QuestData) {
        viewModelScope.launch {
            questRepository.insertQuest(quest.toEntity(userId))
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

        viewModelScope.launch {
            questRepository.getAllQuests(userId).collect { quests ->
                _uiState.update { it ->
                    it.copy(quests = quests.map { it.toQuestData() })
                }
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