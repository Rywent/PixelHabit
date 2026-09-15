package com.rywent.pixelhabit.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rywent.pixelhabit.presentation.screens.home.HomeScreen
import com.rywent.pixelhabit.presentation.screens.focus.FocusScreen
import com.rywent.pixelhabit.presentation.screens.habits.HabitsScreen
import com.rywent.pixelhabit.presentation.screens.journal.JournalScreen
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.rywent.pixelhabit.presentation.screens.home.panels.weekStatistics.WeekStatisticsPanel
import com.rywent.pixelhabit.presentation.screens.settings.SettingsScreen

private const val BOTTOM_NAV_TRANSITION_DURATION = 350

private val MAIN_ROOT_TRANSITION_SPEC =
    tween<IntOffset>(durationMillis = BOTTOM_NAV_TRANSITION_DURATION, easing = FastOutSlowInEasing)

private val MAIN_ROOT_FADE_SPEC =
    tween<Float>(durationMillis = BOTTOM_NAV_TRANSITION_DURATION, easing = FastOutSlowInEasing)

private fun mainRootDirection(
    fromRoute: String?,
    toRoute: String?
): MainRootDirection? {
    val fromIndex = mainRouteIndex(fromRoute) ?: return null
    val toIndex = mainRouteIndex(toRoute) ?: return null
    if (fromIndex == toIndex) return null
    return if (toIndex > fromIndex) MainRootDirection.FORWARD else MainRootDirection.BACKWARD
}

private enum class MainRootDirection {
    FORWARD,
    BACKWARD
}

private fun mainRootEnterTransition(
    fromRoute: String?,
    toRoute: String?
): EnterTransition = when (mainRootDirection(fromRoute, toRoute)) {
    MainRootDirection.FORWARD -> {
        slideInHorizontally(
            animationSpec = MAIN_ROOT_TRANSITION_SPEC,
            initialOffsetX = { it }
        ) + fadeIn(animationSpec = MAIN_ROOT_FADE_SPEC)
    }
    MainRootDirection.BACKWARD -> {
        slideInHorizontally(
            animationSpec = MAIN_ROOT_TRANSITION_SPEC,
            initialOffsetX = { -it }
        ) + fadeIn(animationSpec = MAIN_ROOT_FADE_SPEC)
    }
    null -> fadeIn(animationSpec = MAIN_ROOT_FADE_SPEC)
}

private fun mainRootExitTransition(
    fromRoute: String?,
    toRoute: String?
): ExitTransition = when (mainRootDirection(fromRoute, toRoute)) {
    MainRootDirection.FORWARD -> {
        slideOutHorizontally(
            animationSpec = MAIN_ROOT_TRANSITION_SPEC,
            targetOffsetX = { -it }
        ) + fadeOut(animationSpec = MAIN_ROOT_FADE_SPEC)
    }
    MainRootDirection.BACKWARD -> {
        slideOutHorizontally(
            animationSpec = MAIN_ROOT_TRANSITION_SPEC,
            targetOffsetX = { it }
        ) + fadeOut(animationSpec = MAIN_ROOT_FADE_SPEC)
    }
    null -> fadeOut(animationSpec = MAIN_ROOT_FADE_SPEC)
}

private const val SETTINGS_TRANSITION_DURATION = 450

private val EmphasizedEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

private fun settingsEnterTransition(): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { (it * 0.25).toInt() },
        animationSpec = tween(SETTINGS_TRANSITION_DURATION, easing = EmphasizedEasing)
    ) + fadeIn(tween(SETTINGS_TRANSITION_DURATION, easing = EmphasizedEasing)) +
            scaleIn(
                initialScale = 0.92f,
                transformOrigin = TransformOrigin(0.5f, 0.5f),
                animationSpec = tween(SETTINGS_TRANSITION_DURATION, easing = EmphasizedEasing)
            )

private fun settingsExitTransition(): ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { -(it * 0.25).toInt() },
        animationSpec = tween(SETTINGS_TRANSITION_DURATION, easing = EmphasizedEasing)
    ) + fadeOut(tween(SETTINGS_TRANSITION_DURATION, easing = EmphasizedEasing)) +
            scaleOut(
                targetScale = 0.95f,
                transformOrigin = TransformOrigin(0.5f, 0.5f),
                animationSpec = tween(SETTINGS_TRANSITION_DURATION, easing = EmphasizedEasing)
            )

private fun settingsPopEnterTransition(): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { -(it * 0.25).toInt() },
        animationSpec = tween(SETTINGS_TRANSITION_DURATION, easing = EmphasizedEasing)
    ) + fadeIn(tween(SETTINGS_TRANSITION_DURATION, easing = EmphasizedEasing)) +
            scaleIn(
                initialScale = 0.95f,
                transformOrigin = TransformOrigin(0.5f, 0.5f),
                animationSpec = tween(SETTINGS_TRANSITION_DURATION, easing = EmphasizedEasing)
            )

private fun settingsPopExitTransition(): ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { (it * 0.25).toInt() },
        animationSpec = tween(SETTINGS_TRANSITION_DURATION, easing = EmphasizedEasing)
    ) + fadeOut(tween(SETTINGS_TRANSITION_DURATION, easing = EmphasizedEasing)) +
            scaleOut(
                targetScale = 0.92f,
                transformOrigin = TransformOrigin(0.5f, 0.5f),
                animationSpec = tween(SETTINGS_TRANSITION_DURATION, easing = EmphasizedEasing)
            )

@Composable
fun AppNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        // Home
        composable(
            route = Screen.Home.route,
            enterTransition = {
                mainRootEnterTransition(
                    fromRoute = initialState.destination.route,
                    toRoute = targetState.destination.route
                )
            },
            exitTransition = {
                mainRootExitTransition(
                    fromRoute = initialState.destination.route,
                    toRoute = targetState.destination.route
                )
            },
            popEnterTransition = {
                mainRootEnterTransition(
                    fromRoute = initialState.destination.route,
                    toRoute = targetState.destination.route
                )
            },
            popExitTransition = {
                mainRootExitTransition(
                    fromRoute = initialState.destination.route,
                    toRoute = targetState.destination.route
                )
            }
        ) {
            HomeScreen(navController, paddingValues)
        }

        // Focus
        composable(
            route = Screen.Focus.route
        ) {
            FocusScreen(navController, paddingValues)
        }

        // Habits
        composable(
            route = Screen.Habits.route,
            enterTransition = {
                mainRootEnterTransition(
                    fromRoute = initialState.destination.route,
                    toRoute = targetState.destination.route
                )
            },
            exitTransition = {
                mainRootExitTransition(
                    fromRoute = initialState.destination.route,
                    toRoute = targetState.destination.route
                )
            },
            popEnterTransition = {
                mainRootEnterTransition(
                    fromRoute = initialState.destination.route,
                    toRoute = targetState.destination.route
                )
            },
            popExitTransition = {
                mainRootExitTransition(
                    fromRoute = initialState.destination.route,
                    toRoute = targetState.destination.route
                )
            }
        ) {
            HabitsScreen(navController, paddingValues)
        }

        // Journal
        composable(
            route = Screen.Journal.route,
            enterTransition = {
                mainRootEnterTransition(
                    fromRoute = initialState.destination.route,
                    toRoute = targetState.destination.route
                )
            },
            exitTransition = {
                mainRootExitTransition(
                    fromRoute = initialState.destination.route,
                    toRoute = targetState.destination.route
                )
            },
            popEnterTransition = {
                mainRootEnterTransition(
                    fromRoute = initialState.destination.route,
                    toRoute = targetState.destination.route
                )
            },
            popExitTransition = {
                mainRootExitTransition(
                    fromRoute = initialState.destination.route,
                    toRoute = targetState.destination.route
                )
            }
        ) {
            JournalScreen(navController, paddingValues)
        }

        composable(
            route = Screen.Settings.route,
            enterTransition = { settingsEnterTransition() },
            exitTransition = { settingsExitTransition() },
            popEnterTransition = { settingsPopEnterTransition() },
            popExitTransition = { settingsPopExitTransition() }
        ) {
            SettingsScreen(navController, paddingValues)
        }

        composable(
            route = Screen.WeekStatistics.route,
            enterTransition = { settingsEnterTransition() },
            exitTransition = { settingsExitTransition() },
            popEnterTransition = { settingsPopEnterTransition() },
            popExitTransition = { settingsPopExitTransition() }
        ) {
            WeekStatisticsPanel(
                navController = navController,
                paddingValues = paddingValues,
                viewModel = hiltViewModel()
            )
        }


    }
}