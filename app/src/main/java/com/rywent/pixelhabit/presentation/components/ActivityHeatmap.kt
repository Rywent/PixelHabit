package com.rywent.pixelhabit.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.rywent.pixelhabit.data.local.entity.HabitCompletionEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private const val DAYS_IN_WEEK = 7

@Composable
fun ActivityHeatmap(
    completions: List<HabitCompletionEntity>,
    modifier: Modifier = Modifier,
    onDayClick: ((LocalDate, Boolean) -> Unit)? = null,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    todayBorderColor: Color = MaterialTheme.colorScheme.primary
) {
    val today = LocalDate.now()
    val startDate = today.minusMonths(6).withDayOfMonth(1)
    val firstVisibleDate = startDate.with(DayOfWeek.MONDAY)

    val completionMap = remember(completions) {
        completions.associateBy { LocalDate.parse(it.date) }
    }

    val weeks = remember(firstVisibleDate, today) {
        buildWeeks(firstVisibleDate, today)
    }

    val monthSpans = remember(weeks, today) {
        buildMonthSpans(weeks, startDate, today)
    }

    var tooltipDate by remember { mutableStateOf<LocalDate?>(null) }
    var animatedCell by remember { mutableStateOf<LocalDate?>(null) }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(weeks) {
        delay(50)
        coroutineScope.launch {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Box(modifier = modifier) {
        Column(modifier = Modifier.horizontalScroll(scrollState)) {
            MonthHeaderRow(
                monthSpans = monthSpans,
                activeColor = activeColor
            )
            Spacer(modifier = Modifier.height(8.dp))

            val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            dayLabels.forEachIndexed { index, label ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.width(40.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        weeks.forEach { week ->
                            val date = week.getOrNull(index)
                            if (date != null && date in firstVisibleDate..today) {
                                val isDone = completionMap[date]?.completed == true
                                val isToday = date == today
                                val isAnimating = animatedCell == date

                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .scale(if (isAnimating) 0.85f else 1f)
                                        .animateContentSize()
                                        .background(
                                            when {
                                                isDone -> activeColor
                                                isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                                else -> MaterialTheme.colorScheme.surfaceVariant
                                            }
                                        )
                                        .then(
                                            if (isToday) {
                                                Modifier.border(
                                                    width = 2.dp,
                                                    color = todayBorderColor,
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                            } else Modifier
                                        )
                                        .clickable {
                                            animatedCell = date
                                            tooltipDate = date
                                            onDayClick?.invoke(date, isDone)
                                        }
                                )
                            } else {
                                Spacer(modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        if (tooltipDate != null) {
            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(0, -70),
                onDismissRequest = { }
            ) {
                AnimatedVisibility(
                    visible = tooltipDate != null,
                    enter = fadeIn(animationSpec = tween(200)) +
                            scaleIn(initialScale = 0.7f, animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)),
                    exit = fadeOut(animationSpec = tween(150)) +
                            scaleOut(targetScale = 0.7f, animationSpec = tween(150))
                ) {
                    tooltipDate?.let { date ->
                        val isDone = completionMap[date]?.completed == true
                        Surface(
                            modifier = Modifier.shadow(8.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            tonalElevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isDone) "✓" else "○",
                                    fontSize = 20.sp,
                                    color = if (isDone) activeColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = date.format(DateTimeFormatter.ofPattern("EEE, d MMM", Locale.ENGLISH)),
                                    fontSize = 15.sp,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isDone) "Done" else "Not done",
                                    fontSize = 13.sp,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isDone) activeColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(tooltipDate) {
        if (tooltipDate != null) {
            delay(1800)
            tooltipDate = null
        }
    }

    LaunchedEffect(animatedCell) {
        if (animatedCell != null) {
            delay(100)
            animatedCell = null
        }
    }
}

@Composable
private fun MonthHeaderRow(
    monthSpans: List<MonthSpan>,
    activeColor: Color
) {
    val cellSize = 16.dp
    val spacing = 4.dp
    val weekStep = cellSize + spacing

    Row(modifier = Modifier.padding(start = 40.dp, bottom = 8.dp)) {
        var currentWeekIndex = 0

        monthSpans.forEachIndexed { spanIndex, span ->
            val emptyWeeks = span.startWeekIndex - currentWeekIndex
            if (emptyWeeks > 0) {
                Spacer(modifier = Modifier.width(weekStep * emptyWeeks))
            }

            val monthLabel = span.month.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
            val isLastSpan = spanIndex == monthSpans.lastIndex
            val baseWidth = weekStep * span.weekCount

            Text(
                text = monthLabel,
                modifier = Modifier
                    .then(
                        if (isLastSpan) {
                            Modifier.widthIn(min = baseWidth, max = baseWidth * 2)
                        } else {
                            Modifier.width(baseWidth)
                        }
                    ),
                style = MaterialTheme.typography.labelMedium,
                color = activeColor,
                maxLines = 1
            )

            currentWeekIndex = span.startWeekIndex + span.weekCount
        }
    }
}

private data class MonthSpan(
    val month: YearMonth,
    val startWeekIndex: Int,
    val weekCount: Int
)

private fun buildWeeks(firstVisibleDate: LocalDate, lastVisibleDate: LocalDate): List<List<LocalDate>> {
    val start = firstVisibleDate.minusDays(dayOfWeekOffset(firstVisibleDate).toLong())
    val end = lastVisibleDate.plusDays((6 - dayOfWeekOffset(lastVisibleDate)).toLong())

    val weeks = mutableListOf<List<LocalDate>>()
    var current = start
    while (current <= end) {
        weeks.add((0 until DAYS_IN_WEEK).map { current.plusDays(it.toLong()) })
        current = current.plusDays(DAYS_IN_WEEK.toLong())
    }
    return weeks
}

private fun buildMonthSpans(
    weeks: List<List<LocalDate>>,
    realStartDate: LocalDate,
    today: LocalDate
): List<MonthSpan> {
    if (weeks.isEmpty()) return emptyList()

    val spans = mutableListOf<MonthSpan>()
    var currentMonth: YearMonth? = null
    var startIndex = 0

    weeks.forEachIndexed { index, week ->
        val visibleDays = week.filter { it in realStartDate..today }
        if (visibleDays.isEmpty()) return@forEachIndexed

        val monthOfWeek = YearMonth.from(visibleDays.first())

        if (currentMonth == null) {
            currentMonth = monthOfWeek
            startIndex = index
        } else if (monthOfWeek != currentMonth) {
            spans.add(MonthSpan(currentMonth, startIndex, index - startIndex))
            currentMonth = monthOfWeek
            startIndex = index
        }
    }

    currentMonth?.let {
        spans.add(MonthSpan(it, startIndex, weeks.size - startIndex))
    }

    return spans
}

private fun dayOfWeekOffset(date: LocalDate): Int {
    return date.dayOfWeek.value - DayOfWeek.MONDAY.value
}