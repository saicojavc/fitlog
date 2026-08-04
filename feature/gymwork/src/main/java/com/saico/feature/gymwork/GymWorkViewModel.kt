package com.saico.feature.gymwork

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.core.domain.repository.WgerRepository
import com.saico.core.domain.usecase.gym_exercise.InsertGymExerciseUseCase
import com.saico.core.model.*
import com.saico.feature.gymwork.state.GymWorkUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class GymWorkViewModel @Inject constructor(
    private val wgerRepository: WgerRepository,
    private val insertGymExerciseUseCase: InsertGymExerciseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GymWorkUiState())
    val uiState: StateFlow<GymWorkUiState> = _uiState.asStateFlow()

    private var sessionTimerJob: Job? = null
    private var restTimerJob: Job? = null
    
    private var sessionStartTime: Long = 0
    private var restEndTime: Long = 0

    private val LB_TO_KG = 0.453592

    init {
        viewModelScope.launch {
            wgerRepository.preloadExercises()
            updateRoutineForDay(_uiState.value.selectedDay)
        }
    }

    fun onDaySelected(day: WorkoutDay) {
        if (_uiState.value.isSessionActive) return
        _uiState.update { it.copy(selectedDay = day) }
        updateRoutineForDay(day)
    }

    private fun updateRoutineForDay(day: WorkoutDay) {
        val routine = wgerRepository.getRoutineForDay(day)
        _uiState.update { it.copy(routine = routine) }
    }

    fun startSession() {
        sessionStartTime = System.currentTimeMillis()
        _uiState.update { state ->
            val initialSetsMap = state.routine?.exercises?.mapIndexed { index, exercise ->
                val targetSets = try { exercise.targetSets } catch(e: Exception) { 3 }
                index to List(targetSets) { setIdx -> ExerciseSetEntry(setIdx) }
            }?.toMap() ?: emptyMap()
            
            state.copy(
                isSessionActive = true,
                elapsedTimeSeconds = 0,
                completedSetsMap = initialSetsMap
            )
        }
        startSessionTimer()
    }

    private fun startSessionTimer() {
        sessionTimerJob?.cancel()
        sessionTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val elapsed = (System.currentTimeMillis() - sessionStartTime) / 1000
                _uiState.update { it.copy(elapsedTimeSeconds = elapsed) }
            }
        }
    }

    fun onSetToggled(exerciseIndex: Int, setIndex: Int, weight: String, reps: String) {
        _uiState.update { state ->
            val currentSets = state.completedSetsMap[exerciseIndex] ?: return@update state
            val updatedSets = currentSets.map { set ->
                if (set.setIndex == setIndex) {
                    val wasCompleted = set.isCompleted
                    val newCompleted = !wasCompleted
                    if (newCompleted) {
                        startRestTimer(state.routine?.exercises?.getOrNull(exerciseIndex)?.restTimeSeconds ?: 90)
                    }
                    set.copy(
                        weightLb = weight.toDoubleOrNull() ?: 0.0,
                        reps = reps.toIntOrNull() ?: 0,
                        isCompleted = newCompleted
                    )
                } else set
            }
            
            val newMap = state.completedSetsMap.toMutableMap()
            newMap[exerciseIndex] = updatedSets
            
            // Check if all sets of current exercise are completed to auto-advance
            var nextExerciseIndex = state.activeExerciseIndex
            if (updatedSets.all { it.isCompleted } && exerciseIndex == state.activeExerciseIndex) {
                if (state.activeExerciseIndex < (state.routine?.exercises?.size ?: 0) - 1) {
                    nextExerciseIndex++
                }
            }

            state.copy(
                completedSetsMap = newMap,
                activeExerciseIndex = nextExerciseIndex,
                totalCalories = calculateTotalCalories(newMap)
            )
        }
    }

    private fun startRestTimer(seconds: Int) {
        restEndTime = System.currentTimeMillis() + (seconds * 1000)
        restTimerJob?.cancel()
        restTimerJob = viewModelScope.launch {
            while (System.currentTimeMillis() < restEndTime) {
                val remaining = ((restEndTime - System.currentTimeMillis()) / 1000).toInt().coerceAtLeast(0)
                _uiState.update { it.copy(restTimerSeconds = remaining) }
                delay(500)
            }
            _uiState.update { it.copy(restTimerSeconds = 0) }
            // TODO: Trigger Haptic feedback via UI side effect if possible or just vibration
        }
    }

    fun skipRest() {
        restTimerJob?.cancel()
        _uiState.update { it.copy(restTimerSeconds = 0) }
    }

    private fun calculateTotalCalories(setsMap: Map<Int, List<ExerciseSetEntry>>): Double {
        var total = 0.0
        setsMap.values.forEach { sets ->
            val completed = sets.filter { it.isCompleted }
            completed.forEach { set ->
                val weightKg = set.weightLb * LB_TO_KG
                val volumeKg = set.reps * weightKg
                total += (volumeKg * 0.005) + (set.reps * 0.1) + 2.0
            }
        }
        return total
    }

    fun finishSession() {
        viewModelScope.launch {
            val state = _uiState.value
            val calendar = Calendar.getInstance()
            val dayOfWeekStr = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) ?: ""
            
            val domainExercises = state.routine?.exercises?.mapIndexed { index, exercise ->
                val sets = state.completedSetsMap[index] ?: emptyList()
                GymExerciseItem(
                    id = exercise.exerciseId.toString(),
                    name = exercise.name,
                    sets = sets.size,
                    reps = sets.firstOrNull()?.reps ?: 0, // Simplified for old model
                    weightKg = (sets.firstOrNull()?.weightLb ?: 0.0) * LB_TO_KG
                )
            } ?: emptyList()

            val gymExercise = GymExercise(
                id = 0,
                exercises = domainExercises,
                elapsedTime = state.elapsedTimeSeconds,
                totalCalories = state.totalCalories.roundToInt(),
                date = System.currentTimeMillis(),
                dayOfWeek = dayOfWeekStr
            )
            
            insertGymExerciseUseCase(gymExercise)
            
            sessionTimerJob?.cancel()
            restTimerJob?.cancel()
            _uiState.update { it.copy(showSessionSavedDialog = true, isSessionActive = false) }
        }
    }

    fun resetState() {
        sessionTimerJob?.cancel()
        restTimerJob?.cancel()
        _uiState.value = GymWorkUiState()
        updateRoutineForDay(_uiState.value.selectedDay)
    }
}
