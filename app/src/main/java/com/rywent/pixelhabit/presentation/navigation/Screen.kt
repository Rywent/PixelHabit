package com.rywent.pixelhabit.presentation.navigation


// all routes in the app
sealed class Screen(val route: String){

    object Focus : Screen("focus")
    object Home : Screen("home")
    object Habits : Screen("habits")
    object Journal : Screen("journal")

    object Settings : Screen("settings")
    object AboutVersions : Screen("about")

    object HabitDetail : Screen("habit_detail")

}