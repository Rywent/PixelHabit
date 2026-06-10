package com.rywent.pixelhabit.presentation.screens.habits.subScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rywent.pixelhabit.presentation.components.habit.HabitCard
import com.rywent.pixelhabit.presentation.components.habit.HabitData
import com.rywent.pixelhabit.presentation.screens.habits.HabitsUIState
import com.rywent.pixelhabit.presentation.screens.habits.components.FilterButton
import com.rywent.pixelhabit.presentation.screens.habits.components.HabitsFilter
import com.rywent.pixelhabit.presentation.screens.habits.components.NoHabits
import com.rywent.pixelhabit.presentation.screens.habits.components.StatisticsSection
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HabitsSubScreen(
    navigateToHabitDetails: (String) -> Unit,
    uiState: HabitsUIState,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val filteredHabits = remember(uiState.allHabits, uiState.currentFilter) {
        filterHabits(uiState.allHabits, uiState.currentFilter)
    }

    val activeFilterCount = uiState.currentFilter.activeCount

    Column(
        modifier = modifier
            .padding(top = 8.dp)
    ) {
        StatisticsSection(
            completionRate = uiState.completionRate,
            totalHabitCount = uiState.totalHabitCount,
            habitsCompleted = uiState.habitsCompleted,
            avgFocusTime = uiState.avgFocusTime
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterTitle(
                currentFilter = uiState.currentFilter,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            )

            FilterButton(
                onClick = onFilterClick,
                activeFilterCount = activeFilterCount
            )
        }

        if (filteredHabits.isEmpty()) {
            val message = when {
                uiState.currentFilter.isTodayActive -> "No habits scheduled for today"
                uiState.currentFilter.activeLifestyles.isNotEmpty() -> "No habits in this lifestyle"
                uiState.currentFilter.activeDays.isNotEmpty() -> "No habits scheduled on these days"
                uiState.currentFilter.isCompletedActive -> "No completed habits"
                uiState.currentFilter.isNotCompletedActive -> "No uncompleted habits"
                else -> "No habits yet"
            }
            NoHabits(message = message)
        } else {
            filteredHabits.forEach { habit ->
                HabitCard(
                    habit = habit,
                    onClick = {
                        navigateToHabitDetails(habit.id)
                    },
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun FilterTitle(
    currentFilter: HabitsFilter,
    modifier: Modifier = Modifier
) {
    val title = when {
        currentFilter.isAllActive -> "All habits"
        currentFilter.isTodayActive && currentFilter.activeLifestyles.isNotEmpty() ->
            "Today • ${currentFilter.activeLifestyles.first().name}"
        currentFilter.isTodayActive -> "Today's habits"
        currentFilter.activeLifestyles.isNotEmpty() && currentFilter.activeDays.isNotEmpty() ->
            "${currentFilter.activeLifestyles.first().name} • ${currentFilter.activeDays.joinToString(", ")}"
        currentFilter.activeLifestyles.isNotEmpty() ->
            currentFilter.activeLifestyles.first().name
        currentFilter.activeDays.isNotEmpty() ->
            currentFilter.activeDays.joinToString(", ")
        currentFilter.isCompletedActive -> "Completed"
        currentFilter.isNotCompletedActive -> "Not completed"
        else -> "Filtered"
    }

    val subtitle = if (currentFilter.activeCount > 1) {
        " +${currentFilter.activeCount - 1} more"
    } else null

    val isLongTitle = title.length > 25 || (subtitle != null && currentFilter.activeCount > 2)

    Column(
        modifier = modifier,
        verticalArrangement = if (isLongTitle) Arrangement.Top else Arrangement.Center
    ) {
        Text(
            text = title,
            fontSize = if (isLongTitle) 18.sp else 26.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = if (isLongTitle) 2 else 1,
            minLines = 1,
            lineHeight = if (isLongTitle) 24.sp else 28.sp
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                fontSize = if (isLongTitle) 11.sp else 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
private fun filterHabits(
    habits: List<HabitData>,
    filter: HabitsFilter
): List<HabitData> {
    if (filter.isAllActive) return habits

    val today = LocalDate.now()
    val todayDayOfWeek = today.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US)

    return habits.filter { habit ->
        var matches = true

        if (filter.isTodayActive) {
            val isTodayScheduled = habit.customDays?.contains(todayDayOfWeek) == true ||
                    when (habit.frequency) {
                        "Every day" -> true
                        "Weekdays" -> todayDayOfWeek in listOf("Mon", "Tue", "Wed", "Thu", "Fri")
                        "Weekends" -> todayDayOfWeek in listOf("Sat", "Sun")
                        else -> false
                    }
            matches = true && isTodayScheduled
        }

        if (filter.isCompletedActive) {
            matches = matches && habit.isCompletedToday
        }
        if (filter.isNotCompletedActive) {
            matches = matches && !habit.isCompletedToday
        }

        if (filter.activeDays.isNotEmpty()) {
            val matchesDay = filter.activeDays.any { day ->
                habit.customDays?.contains(day) == true ||
                        when (habit.frequency) {
                            "Every day" -> true
                            "Weekdays" -> day in listOf("Mon", "Tue", "Wed", "Thu", "Fri")
                            "Weekends" -> day in listOf("Sat", "Sun")
                            else -> false
                        }
            }
            matches = matches && matchesDay
        }

        if (filter.activeLifestyles.isNotEmpty()) {
            val matchesLifestyle = filter.activeLifestyles.any { it.id == habit.lifestyleId }
            matches = matches && matchesLifestyle
        }

        matches
    }
}