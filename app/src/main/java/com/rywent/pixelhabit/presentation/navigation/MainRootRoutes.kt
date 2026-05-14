package com.rywent.pixelhabit.presentation.navigation

internal fun isMainRoute(route: String?) : Boolean = when (route){
    Screen.Home .route,
    Screen.Habits.route,
    Screen.Focus.route,
    Screen.Journal.route -> true
    else -> false
}

internal fun mainRouteIndex(route: String?) : Int? = when(route){
    Screen.Focus.route -> 0
    Screen.Home.route -> 1
    Screen.Habits.route -> 2
    Screen.Journal.route -> 3
    else -> null
}