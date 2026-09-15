package com.rywent.pixelhabit.presentation.screens.focus

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rywent.pixelhabit.data.local.entity.FocusPresetEntity
import com.rywent.pixelhabit.data.mapper.toTodayHabitData
import com.rywent.pixelhabit.data.repository.FocusRepository
import com.rywent.pixelhabit.data.repository.HabitRepository
import com.rywent.pixelhabit.services.FocusForegroundService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FocusViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val focusRepository: FocusRepository,
    private val habitRepository: HabitRepository,
    private val ambientPlayer: AmbientSoundPlayer
) : ViewModel() {

    private val prefs = context.getSharedPreferences("focus_session_prefs", Context.MODE_PRIVATE)
    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private val userId = "default_user"

    init {
        checkInterruptedSession()
        seedAndObservePresets()
        observeTodayStats()
        loadTodayHabits()
    }

    private fun clearSessionPrefs() {
        prefs.edit()
            .putBoolean("is_running", false)
            .putBoolean("is_paused", false)
            .putBoolean("has_active_session", false)
            .remove("target_end_time")
            .remove("remaining_seconds")
            .remove("total_seconds")
            .remove("phase")
            .apply()
    }

    private fun saveSessionPrefs(
        isRunning: Boolean,
        isPaused: Boolean,
        remainingSeconds: Int,
        totalSeconds: Int,
        phase: FocusPhase
    ) {
        prefs.edit()
            .putBoolean("is_running", isRunning)
            .putBoolean("is_paused", isPaused)
            .putBoolean("has_active_session", true)
            .putInt("remaining_seconds", remainingSeconds)
            .putInt("total_seconds", totalSeconds)
            .putString("phase", phase.name)
            .apply()
    }

    private fun checkInterruptedSession() {
        val hasActive = prefs.getBoolean("has_active_session", false)
        val wasRunning = prefs.getBoolean("is_running", false)
        val wasPaused = prefs.getBoolean("is_paused", false)

        if (!hasActive && !wasRunning && !wasPaused) return

        val total = prefs.getInt("total_seconds", 25 * 60)
        val phaseName = prefs.getString("phase", FocusPhase.FOCUS.name) ?: FocusPhase.FOCUS.name
        val phase = runCatching { FocusPhase.valueOf(phaseName) }.getOrDefault(FocusPhase.FOCUS)
        val remaining = prefs.getInt("remaining_seconds", total)

        if (remaining > 0) {
            _uiState.update {
                it.copy(
                    showInterruptedDialog = true,
                    interruptedSessionSeconds = remaining,
                    interruptedTotalSeconds = total,
                    interruptedPhase = phase
                )
            }
        } else {
            clearSessionPrefs()
        }
    }

    fun continueInterruptedSession() {
        val s = _uiState.value

        _uiState.update {
            it.copy(
                showInterruptedDialog = false,
                remainingSeconds = s.interruptedSessionSeconds,
                totalSeconds = s.interruptedTotalSeconds,
                phase = s.interruptedPhase,
                isPaused = true,
                isRunning = false
            )
        }

        saveSessionPrefs(
            isRunning = false,
            isPaused = true,
            remainingSeconds = s.interruptedSessionSeconds,
            totalSeconds = s.interruptedTotalSeconds,
            phase = s.interruptedPhase
        )
    }

    fun cancelInterruptedSession() {
        _uiState.update {
            it.copy(showInterruptedDialog = false, interruptedSessionSeconds = 0)
        }
        clearSessionPrefs()
        reset()
    }

    fun startOrResume() {
        val s = _uiState.value
        if (s.isRunning) return

        val startedAt = if (s.sessionStartedAt == 0L) System.currentTimeMillis() else s.sessionStartedAt
        _uiState.update {
            it.copy(isRunning = true, isPaused = false, sessionStartedAt = startedAt)
        }

        saveSessionPrefs(
            isRunning = true,
            isPaused = false,
            remainingSeconds = s.remainingSeconds,
            totalSeconds = s.totalSeconds,
            phase = s.phase
        )

        startFocusService(
            time = formatTime(s.remainingSeconds),
            phase = s.phase.name,
            progressMax = s.totalSeconds,
            progressCurrent = s.remainingSeconds
        )

        if (!s.isSilentMode && s.selectedSound != AmbientSound.NONE) {
            ambientPlayer.playLoop(s.selectedSound)
            _uiState.update { it.copy(isSoundPlaying = true) }
        }

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val cur = _uiState.value
                if (!cur.isRunning) break

                if (!cur.isSilentMode && cur.remainingSeconds in 2..4) {
                    ambientPlayer.playCountdownTick()
                }

                if (cur.remainingSeconds <= 1) {
                    onTimerFinished(completed = true)
                    break
                } else {
                    val nextSeconds = cur.remainingSeconds - 1
                    _uiState.update { it.copy(remainingSeconds = nextSeconds) }

                    saveSessionPrefs(
                        isRunning = true,
                        isPaused = false,
                        remainingSeconds = nextSeconds,
                        totalSeconds = cur.totalSeconds,
                        phase = cur.phase
                    )

                    updateFocusServiceTime(
                        time = formatTime(nextSeconds),
                        phase = cur.phase.name,
                        progressMax = cur.totalSeconds,
                        progressCurrent = nextSeconds
                    )
                }
            }
        }
    }

    fun pause() {
        timerJob?.cancel()
        ambientPlayer.pauseLoop()
        stopFocusService()

        val s = _uiState.value
        saveSessionPrefs(
            isRunning = false,
            isPaused = true,
            remainingSeconds = s.remainingSeconds,
            totalSeconds = s.totalSeconds,
            phase = s.phase
        )

        _uiState.update {
            it.copy(isRunning = false, isPaused = true, isSoundPlaying = false)
        }
    }

    fun reset() {
        timerJob?.cancel()
        ambientPlayer.stopLoop()
        stopFocusService()
        clearSessionPrefs()
        val s = _uiState.value

        val total = if (s.stages.isNotEmpty() && s.currentStageIndex < s.stages.size) {
            when (s.phase) {
                FocusPhase.FOCUS -> s.stages[s.currentStageIndex].focusMin * 60
                FocusPhase.SHORT_BREAK, FocusPhase.LONG_BREAK -> s.stages[s.currentStageIndex].breakMin * 60
            }
        } else {
            phaseSeconds(s.phase, s)
        }

        _uiState.update {
            it.copy(
                isRunning = false,
                isPaused = false,
                remainingSeconds = total,
                totalSeconds = total,
                sessionStartedAt = 0L,
                isSoundPlaying = false
            )
        }
    }

    fun skipPhase() {
        timerJob?.cancel()
        onTimerFinished(completed = false)
    }

    private fun onTimerFinished(completed: Boolean) {
        clearSessionPrefs()
        val s = _uiState.value
        val actual = (s.totalSeconds - s.remainingSeconds).coerceAtLeast(1)
        val started = if (s.sessionStartedAt > 0) s.sessionStartedAt
        else System.currentTimeMillis() - actual * 1000L

        viewModelScope.launch {
            if (s.phase == FocusPhase.FOCUS) {
                focusRepository.saveSession(
                    habitId = s.linkedHabitId,
                    habitName = s.linkedHabitName,
                    phase = s.phase.name,
                    plannedSeconds = s.totalSeconds,
                    actualSeconds = actual,
                    completed = completed,
                    startedAt = started
                )
            }
        }

        ambientPlayer.stopLoop()
        stopFocusService()
        if (!s.isSilentMode) {
            ambientPlayer.playCompletionChime()
        }

        val stages = s.stages

        if (stages.isNotEmpty()) {
            if (s.phase == FocusPhase.FOCUS) {
                val currentStage = stages[s.currentStageIndex]
                val nextPhase = if (currentStage.isLongBreak) FocusPhase.LONG_BREAK else FocusPhase.SHORT_BREAK
                val nextTotal = currentStage.breakMin * 60

                _uiState.update {
                    it.copy(
                        phase = nextPhase,
                        totalSeconds = nextTotal,
                        remainingSeconds = nextTotal,
                        isRunning = false,
                        isPaused = false,
                        sessionStartedAt = 0L,
                        isSoundPlaying = false,
                        showCelebration = true,
                        celebrationType = CelebrationType.CYCLE,
                        celebrationMessage = "Stage ${s.currentStageIndex + 1} finished",
                        celebrationSubtitle = "Time for a break."
                    )
                }
            } else {
                val nextIndex = s.currentStageIndex + 1
                if (nextIndex < stages.size) {
                    val nextStage = stages[nextIndex]
                    val nextTotal = nextStage.focusMin * 60
                    _uiState.update {
                        it.copy(
                            currentStageIndex = nextIndex,
                            phase = FocusPhase.FOCUS,
                            stages = stages,
                            totalSeconds = nextTotal,
                            remainingSeconds = nextTotal,
                            isRunning = false,
                            isPaused = false,
                            sessionStartedAt = 0L,
                            isSoundPlaying = false,
                            showCelebration = true,
                            celebrationType = CelebrationType.BREAK_DONE,
                            celebrationMessage = "Break finished",
                            celebrationSubtitle = "Starting stage ${nextIndex + 1}"
                        )
                    }
                } else {
                    val firstStage = stages[0]
                    val resetTotal = firstStage.focusMin * 60

                    _uiState.update {
                        it.copy(
                            currentStageIndex = 0,
                            phase = FocusPhase.FOCUS,
                            totalSeconds = resetTotal,
                            remainingSeconds = resetTotal,
                            isRunning = false,
                            isPaused = false,
                            sessionStartedAt = 0L,
                            isSoundPlaying = false,
                            showCelebration = true,
                            celebrationType = CelebrationType.FULL_SESSION,
                            celebrationMessage = "Preset Complete!",
                            celebrationSubtitle = "All custom stages finished successfully."
                        )
                    }
                }
            }
        } else {
            when (s.phase) {
                FocusPhase.FOCUS -> {
                    val newCompleted = s.completedFocusSessions + 1
                    val nextIsLong = newCompleted % s.longBreakEvery == 0
                    val nextPhase = if (nextIsLong) FocusPhase.LONG_BREAK else FocusPhase.SHORT_BREAK
                    val nextTotal = if (nextIsLong) s.longBreakMin * 60 else s.shortBreakMin * 60

                    _uiState.update {
                        it.copy(
                            phase = nextPhase,
                            completedFocusSessions = newCompleted,
                            currentCycle = if (nextIsLong) s.longBreakEvery else it.currentCycle + 1,
                            totalSeconds = nextTotal,
                            remainingSeconds = nextTotal,
                            isRunning = false,
                            isPaused = false,
                            sessionStartedAt = 0L,
                            isSoundPlaying = false,
                            showCelebration = true,
                            celebrationType = if (nextIsLong) CelebrationType.FULL_SESSION else CelebrationType.CYCLE,
                            celebrationMessage = if (nextIsLong) "Great work" else "Cycle $newCompleted / ${s.longBreakEvery} complete",
                            celebrationSubtitle = if (nextIsLong) "Full session complete." else "Short break, then the next focus block."
                        )
                    }
                }
                FocusPhase.SHORT_BREAK, FocusPhase.LONG_BREAK -> {
                    val nextTotal = s.focusDurationMin * 60
                    val isAfterLongBreak = s.phase == FocusPhase.LONG_BREAK

                    _uiState.update {
                        it.copy(
                            phase = FocusPhase.FOCUS,
                            completedFocusSessions = if (isAfterLongBreak) 0 else it.completedFocusSessions,
                            currentCycle = if (isAfterLongBreak) 1 else it.currentCycle,
                            totalSeconds = nextTotal,
                            remainingSeconds = nextTotal,
                            isRunning = false,
                            isPaused = false,
                            sessionStartedAt = 0L,
                            isSoundPlaying = false,
                            showCelebration = true,
                            celebrationType = CelebrationType.BREAK_DONE,
                            celebrationMessage = "Break finished",
                            celebrationSubtitle = if (isAfterLongBreak) "Starting a new session." else "Ready for the next focus block."
                        )
                    }
                }
            }
        }
    }

    private fun startFocusService(time: String, phase: String, progressMax: Int, progressCurrent: Int) {
        val intent = Intent(context, FocusForegroundService::class.java).apply {
            putExtra("time", time)
            putExtra("phase", phase)
            putExtra("progress_max", progressMax)
            putExtra("progress_current", progressCurrent)
        }
        try {
            context.startForegroundService(intent)
        } catch (_: Exception) {}
    }

    private fun updateFocusServiceTime(time: String, phase: String, progressMax: Int, progressCurrent: Int) {
        val intent = Intent(context, FocusForegroundService::class.java).apply {
            action = "ACTION_UPDATE_TIME"
            putExtra("time", time)
            putExtra("phase", phase)
            putExtra("progress_max", progressMax)
            putExtra("progress_current", progressCurrent)
        }
        try {
            context.startService(intent)
        } catch (_: Exception) {}
    }

    private fun stopFocusService() {
        val intent = Intent(context, FocusForegroundService::class.java)
        try {
            context.stopService(intent)
        } catch (_: Exception) {}
    }

    private fun formatTime(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return String.format(Locale.ROOT, "%02d:%02d", m, s)
    }

    fun dismissCelebration() {
        _uiState.update { it.copy(showCelebration = false) }
    }

    private fun phaseSeconds(phase: FocusPhase, s: FocusUiState): Int = when (phase) {
        FocusPhase.FOCUS -> s.focusDurationMin * 60
        FocusPhase.SHORT_BREAK -> s.shortBreakMin * 60
        FocusPhase.LONG_BREAK -> s.longBreakMin * 60
    }

    fun selectMode(mode: FocusMode) {
        if (_uiState.value.isRunning) return
        when (mode) {
            FocusMode.POMODORO -> applyDurations(25, 5, 15, 4, mode)
            FocusMode.DEEP_WORK -> applyDurations(50, 10, 20, 3, mode)
            FocusMode.FLOW -> applyDurations(90, 15, 30, 2, mode)
            FocusMode.CUSTOM -> {
                _uiState.update { it.copy(selectedMode = FocusMode.CUSTOM, showCustomPresetsSheet = true) }
            }
        }
    }

    fun applyPreset(entity: FocusPresetEntity) {
        if (_uiState.value.isRunning) return
        val stages = entity.getStages()

        if (stages.isNotEmpty()) {
            val firstStage = stages[0]
            val total = firstStage.focusMin * 60
            _uiState.update {
                it.copy(
                    stages = stages,
                    currentStageIndex = 0,
                    focusDurationMin = firstStage.focusMin,
                    shortBreakMin = firstStage.breakMin,
                    longBreakMin = firstStage.breakMin,
                    selectedMode = FocusMode.CUSTOM,
                    phase = FocusPhase.FOCUS,
                    totalSeconds = total,
                    remainingSeconds = total,
                    isRunning = false,
                    isPaused = false,
                    completedFocusSessions = 0,
                    sessionStartedAt = 0L,
                    showPresetSheet = false,
                    showCustomPresetsSheet = false
                )
            }
        } else {
            applyDurations(
                entity.focusMin, entity.shortBreakMin, entity.longBreakMin, entity.longBreakEvery,
                if (entity.isDefault) {
                    when {
                        entity.focusMin == 25 -> FocusMode.POMODORO
                        entity.focusMin == 50 -> FocusMode.DEEP_WORK
                        entity.focusMin == 90 -> FocusMode.FLOW
                        else -> FocusMode.CUSTOM
                    }
                } else FocusMode.CUSTOM
            )
            _uiState.update {
                it.copy(
                    stages = emptyList(),
                    currentStageIndex = 0,
                    showPresetSheet = false,
                    showCustomPresetsSheet = false
                )
            }
        }
    }

    private fun applyDurations(focus: Int, short: Int, long: Int, every: Int, mode: FocusMode) {
        val total = focus.coerceIn(1, 180) * 60
        _uiState.update {
            it.copy(
                stages = emptyList(),
                currentStageIndex = 0,
                focusDurationMin = focus.coerceIn(1, 180),
                shortBreakMin = short.coerceIn(1, 60),
                longBreakMin = long.coerceIn(1, 60),
                longBreakEvery = every.coerceIn(1, 10),
                selectedMode = mode,
                phase = FocusPhase.FOCUS,
                totalSeconds = total,
                remainingSeconds = total,
                isRunning = false,
                isPaused = false,
                completedFocusSessions = 0,
                currentCycle = 1,
                sessionStartedAt = 0L
            )
        }
    }

    fun createPreset(
        name: String,
        focus: Int,
        short: Int,
        long: Int,
        every: Int,
        stagesJson: String?
    ) {
        viewModelScope.launch {
            focusRepository.insertPreset(
                FocusPresetEntity(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    name = name.ifBlank { "Custom ${focus}m" },
                    focusMin = focus.coerceIn(1, 180),
                    shortBreakMin = short.coerceIn(1, 60),
                    longBreakMin = long.coerceIn(1, 60),
                    longBreakEvery = every.coerceIn(1, 10),
                    stagesJson = stagesJson,
                    isDefault = false
                )
            )
            _uiState.update {
                it.copy(
                    showCreatePresetPanel = false,
                    selectedMode = FocusMode.CUSTOM
                )
            }
        }
    }

    fun deletePreset(id: String) {
        viewModelScope.launch { focusRepository.deletePreset(id) }
    }

    fun selectSound(sound: AmbientSound) {
        val running = _uiState.value.isRunning
        val silent = _uiState.value.isSilentMode
        ambientPlayer.stopLoop()
        _uiState.update {
            it.copy(
                selectedSound = sound,
                isSoundPlaying = sound != AmbientSound.NONE && running && !silent
            )
        }
        if (sound != AmbientSound.NONE && running && !silent) {
            ambientPlayer.playLoop(sound)
        }
    }

    fun toggleSilentMode() {
        val next = !_uiState.value.isSilentMode
        _uiState.update { it.copy(isSilentMode = next) }
        if (next) {
            ambientPlayer.stopLoop()
            _uiState.update { it.copy(isSoundPlaying = false) }
        } else if (_uiState.value.isRunning && _uiState.value.selectedSound != AmbientSound.NONE) {
            ambientPlayer.playLoop(_uiState.value.selectedSound)
            _uiState.update { it.copy(isSoundPlaying = true) }
        }
    }

    fun setImmersive(enabled: Boolean) {
        FocusImmersiveHolder.isImmersive = enabled
        _uiState.update { it.copy(isImmersive = enabled) }
    }

    fun showHabitPicker(show: Boolean) {
        if (show) loadTodayHabits()
        _uiState.update { it.copy(showHabitPicker = show) }
    }

    fun linkHabit(id: String, name: String, icon: ImageVector?) {
        _uiState.update {
            it.copy(
                linkedHabitId = id,
                linkedHabitName = name,
                linkedHabitIcon = icon,
                showHabitPicker = false
            )
        }
    }

    fun unlinkHabit() {
        _uiState.update {
            it.copy(linkedHabitId = null, linkedHabitName = null, linkedHabitIcon = null)
        }
    }

    private fun loadTodayHabits() {
        viewModelScope.launch {
            try {
                val today = java.time.LocalDate.now().toString()
                habitRepository.getHabitsForToday(userId, today).collect { list ->
                    val mapped = list.map { it.toTodayHabitData() }
                    _uiState.update { it.copy(todayHabits = mapped) }
                }
            } catch (_: Exception) { }
        }
    }

    fun showPresetSheet(show: Boolean) = _uiState.update { it.copy(showPresetSheet = show) }
    fun showCustomPresetsSheet(show: Boolean) = _uiState.update { it.copy(showCustomPresetsSheet = show) }
    fun showCreatePresetPanel(show: Boolean) = _uiState.update { it.copy(showCreatePresetPanel = show) }

    private fun seedAndObservePresets() {
        viewModelScope.launch {
            focusRepository.seedDefaultPresetsIfEmpty()
            focusRepository.getPresets().collect { list ->
                _uiState.update { it.copy(presets = list) }
            }
        }
    }

    private fun observeTodayStats() {
        viewModelScope.launch {
            focusRepository.getTodayFocusSeconds().collect { sec ->
                _uiState.update { it.copy(todayFocusSeconds = sec) }
            }
        }
        viewModelScope.launch {
            focusRepository.getTodayCompletedCount().collect { count ->
                _uiState.update { it.copy(todayCompletedCount = count) }
            }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        ambientPlayer.releaseAll()
        stopFocusService()
        super.onCleared()
    }
}