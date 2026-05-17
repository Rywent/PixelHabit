package com.rywent.pixelhabit.presentation.screens.about

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.Upgrade
import androidx.lifecycle.ViewModel
import com.rywent.pixelhabit.presentation.screens.about.components.ChangelogItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(AboutUiState())
    val uiState: StateFlow<AboutUiState> = _uiState.asStateFlow()

    init {
        loadChangelog()
    }

    private fun loadChangelog() {
        _uiState.value = AboutUiState(
            appVersion = "0.1.0",
            releaseDate = "May 17, 2026",
            changelogSections = listOf(
                ChangelogSection(
                    header = "Added",
                    icon = Icons.Rounded.AddCircleOutline,
                    items = listOf(
                        ChangelogItem(
                            "Swipe to complete",
                            "Swipe left on habit card to mark as done"
                        ),
                        ChangelogItem("Today habits", "List of habits for today"),
                        ChangelogItem("About screen", "Redesigned info screen with changelog cards"),
                        ChangelogItem("Collapsible habits", "Today habits expand/collapse with 4 visible by default")
                    )
                ),
                ChangelogSection(
                    header = "Improved",
                    icon = Icons.Rounded.Upgrade,
                    items = listOf(
                        ChangelogItem("Card animations", "Smoother transitions when marking habits"),
                        ChangelogItem("Theme colors", "Better adaptive colors for light and dark themes"),
                        ChangelogItem("Collapsible habits", "Today habits expand/collapse with 4 visible by default")
                    )
                ),
            )
        )
    }
}
