package com.rywent.pixelhabit.presentation.screens.habits

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rywent.pixelhabit.presentation.components.customElements.AddItemButton
import com.rywent.pixelhabit.presentation.components.panels.CreateHabitPanel
import com.rywent.pixelhabit.presentation.screens.habits.components.HabitsTabSwitcher
import com.rywent.pixelhabit.presentation.screens.habits.subScreens.HabitsSubScreen
import com.rywent.pixelhabit.presentation.screens.habits.subScreens.LifestyleSubScreen
import com.rywent.pixelhabit.presentation.screens.habits.subScreens.QuestsSubScreen
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitsScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: HabitsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tabs = listOf("HABITS", "LIFESTYLE", "QUESTS")

    val pagerState = rememberPagerState(
        initialPage = uiState.selectedTabIndex,
        pageCount = { tabs.size }
    )

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                if (page != uiState.selectedTabIndex) {
                    viewModel.onTabSelected(page)
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(start = 15.dp, end = 15.dp)) {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Habits",
                    fontSize = 46.sp,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(15.dp))

                HabitsTabSwitcher(
                    tabs = tabs,
                    selectedIndex = uiState.selectedTabIndex,
                    onTabSelected = { index ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                page = index,
                                animationSpec = tween(
                                    durationMillis = 400,
                                    easing = FastOutSlowInEasing
                                )
                            )
                            viewModel.onTabSelected(index)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                pageSpacing = 0.dp,
                beyondViewportPageCount = 2,
                key = { it }
            ) { page ->
                val scrollState = rememberScrollState()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 15.dp)
                ) {
                    when (page) {
                        0 -> HabitsSubScreen(
                            navigateToHabitDetails = { viewModel.onHabitClick(it) },
                            uiState = uiState
                        )
                        1 -> LifestyleSubScreen(
                            navigateToLifestyleDetails = { viewModel.onLifestyleClick(it) },
                            uiState = uiState
                        )
                        2 -> QuestsSubScreen(
                            navigateToQuestDetails = { viewModel.onQuestClick(it) },
                            uiState = uiState
                        )
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        if (uiState.showCreateHabitPanel) {
            CreateHabitPanel(
                onDismiss = { viewModel.onDismissCreateHabitPanel() }
            )
        }

        AddItemButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 25.dp, end = 15.dp),
            onClick = {
                when (uiState.selectedTabIndex) {
                    0 -> viewModel.onHabitCreateClick()
                    1 -> viewModel.onLifestyleCreateClick()
                    2 -> viewModel.onQuestCreateClick()
                }
            }
        )
    }
}