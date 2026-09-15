package com.rywent.pixelhabit.presentation.screens.home.panels.weekStatistics.components

import androidx.compose.runtime.Composable
import com.rywent.pixelhabit.presentation.screens.home.components.DayStat
import com.rywent.pixelhabit.presentation.screens.home.components.WeekStatistics

@Composable
fun ThisWeekStatistics(
    daysStat: List<DayStat>
){
    WeekStatistics(data = daysStat, onWeekClick = {}, "This Week", false)
}