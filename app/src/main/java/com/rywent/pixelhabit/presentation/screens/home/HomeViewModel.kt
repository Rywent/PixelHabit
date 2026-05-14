package com.rywent.pixelhabit.presentation.screens.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        _uiState.value = HomeUiState(
            userName = getUserName(),
            currentDate = getCurrentDate()
        )
    }

    private fun getUserName(): String {

        return "Rywent"
    }

    private fun getCurrentDate(): String {
        val today = java.time.LocalDate.now()
        val formatter = java.time.format.DateTimeFormatter.ofPattern(
            "EEEE, d MMMM",
            java.util.Locale.getDefault()
        )
        return "Today's ${today.format(formatter).replaceFirstChar { it.uppercase() }}"
    }
}