package com.rywent.pixelhabit.presentation.screens.home

data class HomeUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val currentDate: String = ""
)