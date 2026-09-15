package com.rywent.pixelhabit.presentation.screens.focus

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fullscreen
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rywent.pixelhabit.presentation.screens.focus.components.AmbientSoundSelector
import com.rywent.pixelhabit.presentation.screens.focus.components.CelebrationOverlay
import com.rywent.pixelhabit.presentation.screens.focus.components.CircularTimer
import com.rywent.pixelhabit.presentation.screens.focus.components.HabitPickerSheet
import com.rywent.pixelhabit.presentation.screens.focus.components.ImmersiveFocusScreen
import com.rywent.pixelhabit.presentation.screens.focus.components.LinkedHabitCard
import com.rywent.pixelhabit.presentation.screens.focus.components.ModeSelector
import com.rywent.pixelhabit.presentation.screens.focus.components.SessionInterruptedOverlay
import com.rywent.pixelhabit.presentation.screens.focus.components.SessionInfoBar
import com.rywent.pixelhabit.presentation.screens.focus.components.TimerControls
import com.rywent.pixelhabit.presentation.screens.focus.components.panels.CreatePresetPanel
import com.rywent.pixelhabit.presentation.screens.focus.components.PresetListSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: FocusViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scheme = MaterialTheme.colorScheme

    if (uiState.isImmersive) {
        ImmersiveFocusScreen(
            timeText = uiState.formattedTime,
            phase = uiState.phase,
            phaseLabel = uiState.phaseLabel,
            isRunning = uiState.isRunning,
            isSilentMode = uiState.isSilentMode,
            selectedSound = uiState.selectedSound,
            onExit = { viewModel.setImmersive(false) },
            onPlayPause = {
                if (uiState.isRunning) viewModel.pause() else viewModel.startOrResume()
            },
            onToggleSilent = { viewModel.toggleSilentMode() }
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(scheme.surface)
                .padding(start = 20.dp, end = 20.dp),
            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = paddingValues.calculateBottomPadding() + 24.dp
            )
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            text = "Focus",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            ),
                            color = scheme.onSurface
                        )
                        Text(
                            text = "Deep work · Pomodoro · Flow",
                            style = MaterialTheme.typography.bodyMedium,
                            color = scheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { viewModel.showPresetSheet(true) },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(Icons.Rounded.Tune, "Presets", tint = scheme.onSurfaceVariant)
                    }
                }
            }

            item { Spacer(Modifier.height(20.dp)) }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(scheme.surfaceContainerHigh)
                            .clickable { viewModel.setImmersive(true) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Icon(Icons.Rounded.Fullscreen, null, tint = scheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Immersive", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.weight(1f))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (uiState.isSilentMode) scheme.primaryContainer
                                else scheme.surfaceContainerHigh
                            )
                            .clickable { viewModel.toggleSilentMode() }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            if (uiState.isSilentMode) Icons.Rounded.VolumeOff else Icons.Rounded.VolumeUp,
                            null,
                            tint = if (uiState.isSilentMode) scheme.onPrimaryContainer else scheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (uiState.isSilentMode) "Silent" else "Sound on",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium,
                            color = if (uiState.isSilentMode) scheme.onPrimaryContainer else scheme.onSurface
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }

            item(key = "timer_item") {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = uiState.phase,
                        transitionSpec = {
                            (fadeIn() togetherWith fadeOut()).using(
                                SizeTransform(clip = false)
                            )
                        },
                        label = "phase_transition"
                    ) { targetPhase ->
                        CircularTimer(
                            progress = uiState.progress,
                            timeText = uiState.formattedTime,
                            phaseLabel = uiState.phaseLabel,
                            phase = targetPhase,
                            isRunning = uiState.isRunning
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(20.dp)) }

            item {
                SessionInfoBar(
                    completedSessions = uiState.completedFocusSessions,
                    currentCycle = uiState.currentCycle,
                    longBreakEvery = uiState.longBreakEvery,
                    stagesCount = uiState.stages.size,
                    currentStageIndex = uiState.currentStageIndex,
                    todayFocusFormatted = uiState.todayFocusFormatted
                )
            }

            item { Spacer(Modifier.height(28.dp)) }

            item {
                ModeSelector(
                    selected = uiState.selectedMode,
                    enabled = !uiState.isRunning,
                    onSelect = { viewModel.selectMode(it) }
                )
            }

            item { Spacer(Modifier.height(28.dp)) }

            item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimerControls(
                        isRunning = uiState.isRunning,
                        onPlayPause = {
                            if (uiState.isRunning) viewModel.pause()
                            else viewModel.startOrResume()
                        },
                        onReset = { viewModel.reset() },
                        onSkip = { viewModel.skipPhase() }
                    )
                }
            }

            item { Spacer(Modifier.height(32.dp)) }

            item {
                AmbientSoundSelector(
                    selected = uiState.selectedSound,
                    isPlaying = uiState.isSoundPlaying,
                    onSelect = { viewModel.selectSound(it) }
                )
            }

            item { Spacer(Modifier.height(28.dp)) }

            item {
                LinkedHabitCard(
                    habitName = uiState.linkedHabitName,
                    habitIcon = uiState.linkedHabitIcon,
                    onLinkClick = { viewModel.showHabitPicker(true) },
                    onUnlink = { viewModel.unlinkHabit() }
                )
            }

            item { Spacer(Modifier.height(24.dp)) }
        }

        if (uiState.showCelebration) {
            CelebrationOverlay(
                message = uiState.celebrationMessage,
                subtitle = uiState.celebrationSubtitle,
                type = uiState.celebrationType,
                onDismiss = { viewModel.dismissCelebration() }
            )
        }

        if (uiState.showInterruptedDialog) {
            SessionInterruptedOverlay(
                onContinue = { viewModel.continueInterruptedSession() },
                onCancel = { viewModel.cancelInterruptedSession() }
            )
        }

        if (uiState.showPresetSheet) {
            PresetListSheet(
                title = "Timer presets",
                presets = uiState.presets,
                currentFocus = uiState.focusDurationMin,
                currentShort = uiState.shortBreakMin,
                onApply = { viewModel.applyPreset(it) },
                onDelete = { viewModel.deletePreset(it) },
                onCreate = {
                    viewModel.showPresetSheet(false)
                    viewModel.showCreatePresetPanel(true)
                },
                onDismiss = { viewModel.showPresetSheet(false) }
            )
        }

        if (uiState.showCustomPresetsSheet) {
            PresetListSheet(
                title = "Your custom presets",
                presets = uiState.customPresets,
                currentFocus = uiState.focusDurationMin,
                currentShort = uiState.shortBreakMin,
                onApply = { viewModel.applyPreset(it) },
                onDelete = { viewModel.deletePreset(it) },
                onCreate = {
                    viewModel.showCustomPresetsSheet(false)
                    viewModel.showCreatePresetPanel(true)
                },
                onDismiss = { viewModel.showCustomPresetsSheet(false) },
                emptyHint = "No custom presets yet. Create one with stages and breaks."
            )
        }

        if (uiState.showCreatePresetPanel) {
            CreatePresetPanel(
                onDismiss = { viewModel.showCreatePresetPanel(false) },
                onCreate = { name, f, s, l, e, json ->
                    viewModel.createPreset(name, f, s, l, e, json)
                }
            )
        }

        if (uiState.showHabitPicker) {
            HabitPickerSheet(
                habits = uiState.todayHabits,
                onSelect = { id, name, icon -> viewModel.linkHabit(id, name, icon) },
                onDismiss = { viewModel.showHabitPicker(false) }
            )
        }
    }
}
