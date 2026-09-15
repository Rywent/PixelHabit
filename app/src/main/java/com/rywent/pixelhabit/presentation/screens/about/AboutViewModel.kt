package com.rywent.pixelhabit.presentation.screens.about

import android.content.Context
import androidx.lifecycle.ViewModel
import com.rywent.pixelhabit.R
import com.rywent.pixelhabit.presentation.screens.about.components.ChangelogItem
import com.rywent.pixelhabit.presentation.screens.about.data.ChangelogRoot
import com.rywent.pixelhabit.presentation.screens.about.data.toImageVector
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(AboutUiState())
    val uiState: StateFlow<AboutUiState> = _uiState.asStateFlow()

    init {
        loadChangelog()
    }

    private fun loadChangelog() {
        _uiState.value = getChangelogFromJson()
    }

    private fun getChangelogFromJson(): AboutUiState {
        return try {
            val jsonString = context.resources.openRawResource(R.raw.changelog)
                .bufferedReader()
                .use { it.readText() }

            val changelogRoot = Json.decodeFromString<ChangelogRoot>(jsonString)

            AboutUiState(
                versions = changelogRoot.versions.map { version ->
                    VersionUiState(
                        version = version.version,
                        releaseDate = version.releaseDate,
                        sections = version.sections.map { section ->
                            ChangelogSection(
                                header = section.header,
                                icon = section.icon.toImageVector(),
                                items = section.items.map { item ->
                                    ChangelogItem(item.title, item.description)
                                }
                            )
                        }
                    )
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            println("ERROR loading changelog: ${e.message}")
            println("Error type: ${e.javaClass.simpleName}")

            AboutUiState(
                versions = listOf(
                    VersionUiState(
                        version = "0.1.0",
                        releaseDate = "June 13, 2026",
                        sections = emptyList()
                    )
                )
            )
        }
    }
}