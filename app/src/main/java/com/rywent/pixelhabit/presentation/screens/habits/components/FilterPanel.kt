package com.rywent.pixelhabit.presentation.screens.habits.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPanel(
    currentFilter: HabitsFilter,
    lifestyles: List<LifestyleData>,
    onFilterChange: (HabitsFilter) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter habits",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row {
                    if (currentFilter.activeCount > 0) {
                        TextButton(onClick = { onFilterChange(HabitsFilter.all()) }) {
                            Text("Clear all")
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick filters section
            Text(
                text = "Quick filters",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChipItem(
                    text = "All",
                    icon = Icons.Default.Apps,
                    isSelected = currentFilter.isAllActive,
                    onClick = { onFilterChange(HabitsFilter.all()) },
                    modifier = Modifier.weight(1f)
                )

                FilterChipItem(
                    text = "Today",
                    icon = Icons.Default.Today,
                    isSelected = currentFilter.isTodayActive && !currentFilter.isAllActive,
                    onClick = {
                        val newFilter = if (currentFilter.isAllActive) {
                            HabitsFilter.today()
                        } else {
                            currentFilter.toggleFilter(FilterType.Today)
                        }
                        onFilterChange(newFilter)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChipItem(
                    text = "Completed",
                    icon = Icons.Default.CheckCircle,
                    isSelected = currentFilter.isCompletedActive,
                    onClick = {
                        val newFilter = if (currentFilter.isAllActive) {
                            HabitsFilter.completed()
                        } else {
                            currentFilter.toggleFilter(FilterType.Completed)
                        }
                        onFilterChange(newFilter)
                    },
                    modifier = Modifier.weight(1f)
                )

                FilterChipItem(
                    text = "Not completed",
                    icon = Icons.Default.RadioButtonUnchecked,
                    isSelected = currentFilter.isNotCompletedActive,
                    onClick = {
                        val newFilter = if (currentFilter.isAllActive) {
                            HabitsFilter.notCompleted()
                        } else {
                            currentFilter.toggleFilter(FilterType.NotCompleted)
                        }
                        onFilterChange(newFilter)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Day of week section
            Text(
                text = "Day of week",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                days.forEach { day ->
                    FilterChipSmall(
                        text = day,
                        isSelected = currentFilter.activeDays.contains(day),
                        onClick = {
                            val newFilter = if (currentFilter.isAllActive) {
                                HabitsFilter.byDay(day)
                            } else {
                                currentFilter.toggleFilter(FilterType.ByDayOfWeek(day))
                            }
                            onFilterChange(newFilter)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lifestyles section
            if (lifestyles.isNotEmpty()) {
                Text(
                    text = "Lifestyles",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(lifestyles) { lifestyle ->
                        FilterLifestyleItem(
                            lifestyle = lifestyle,
                            isSelected = currentFilter.activeLifestyles.any { it.id == lifestyle.id },
                            onClick = {
                                val newFilter = if (currentFilter.isAllActive) {
                                    HabitsFilter.byLifestyle(lifestyle)
                                } else {
                                    currentFilter.toggleFilter(FilterType.ByLifestyle(lifestyle))
                                }
                                onFilterChange(newFilter)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FilterChipItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected)
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        else
            MaterialTheme.colorScheme.surfaceContainerHigh,
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
        else
            null
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FilterChipSmall(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun FilterLifestyleItem(
    lifestyle: LifestyleData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected)
            lifestyle.iconColor.copy(alpha = 0.15f)
        else
            MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(lifestyle.iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = lifestyle.icon,
                    contentDescription = null,
                    tint = lifestyle.iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = lifestyle.name,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = lifestyle.category,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = lifestyle.iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


@Composable
fun FilterButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    activeFilterCount: Int = 0
) {
    val shape = RoundedCornerShape(
        topStart = 8.dp,
        topEnd = 24.dp,
        bottomEnd = 24.dp,
        bottomStart = 8.dp
    )

    Row(
        modifier = modifier
            .height(48.dp)
            .clip(shape)
            .background(
                color = if (activeFilterCount > 0)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else
                    MaterialTheme.colorScheme.surfaceContainerHigh
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = "Filter",
            tint = if (activeFilterCount > 0)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )

        Text(
            text = "Filter",
            fontSize = 15.sp,
            color = if (activeFilterCount > 0)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

sealed class FilterType {
    data object All : FilterType()
    data object Today : FilterType()
    data object Completed : FilterType()
    data object NotCompleted : FilterType()
    data class ByLifestyle(val lifestyle: LifestyleData) : FilterType()
    data class ByDayOfWeek(val day: String) : FilterType()
}

data class HabitsFilter(
    val types: Set<FilterType> = setOf(FilterType.All)
) {
    val isAllActive: Boolean get() = types.contains(FilterType.All)
    val isTodayActive: Boolean get() = types.contains(FilterType.Today)
    val isCompletedActive: Boolean get() = types.contains(FilterType.Completed)
    val isNotCompletedActive: Boolean get() = types.contains(FilterType.NotCompleted)
    val activeLifestyles: List<LifestyleData> get() = types.filterIsInstance<FilterType.ByLifestyle>().map { it.lifestyle }
    val activeDays: List<String> get() = types.filterIsInstance<FilterType.ByDayOfWeek>().map { it.day }

    val activeCount: Int get() = when {
        isAllActive -> 0
        else -> types.size
    }

    companion object {
        fun all() = HabitsFilter(setOf(FilterType.All))
        fun today() = HabitsFilter(setOf(FilterType.Today))
        fun completed() = HabitsFilter(setOf(FilterType.Completed))
        fun notCompleted() = HabitsFilter(setOf(FilterType.NotCompleted))
        fun byLifestyle(lifestyle: LifestyleData) = HabitsFilter(setOf(FilterType.ByLifestyle(lifestyle)))
        fun byDay(day: String) = HabitsFilter(setOf(FilterType.ByDayOfWeek(day)))
    }
}

fun HabitsFilter.toggleFilter(filter: FilterType): HabitsFilter {
    return when {
        filter is FilterType.All -> HabitsFilter.all()
        types.contains(FilterType.All) -> HabitsFilter(setOf(filter))
        types.contains(filter) -> HabitsFilter(types - filter)
        else -> HabitsFilter(types + filter)
    }
}