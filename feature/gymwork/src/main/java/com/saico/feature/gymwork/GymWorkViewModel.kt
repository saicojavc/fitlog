package com.saico.feature.gymwork

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.core.domain.usecase.gym_exercise.InsertGymExerciseUseCase
import com.saico.core.model.GymExercise
import com.saico.core.model.GymExerciseItem as DomainGymExerciseItem
import com.saico.feature.gymwork.state.GymExerciseItem
import com.saico.feature.gymwork.state.GymWorkUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class GymWorkViewModel @Inject constructor(
    private val insertGymExerciseUseCase: InsertGymExerciseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GymWorkUiState())
    val uiState: StateFlow<GymWorkUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    private val LB_TO_KG = 0.453592
    
    private val CALORIES_PER_KG_VOL = 0.005 
    private val CALORIES_BASE_PER_REP = 0.1 
    private val CALORIES_BASE_PER_SET = 2.0 

    private fun calculateExerciseCalories(sets: Int, reps: Int, weightLb: Double): Int {
        if (sets <= 0 || reps <= 0) return 0
        
        val weightKg = weightLb * LB_TO_KG
        val totalReps = sets * reps 
        val volumeKg = totalReps * weightKg
        
        val caloriesFromVolume = volumeKg * CALORIES_PER_KG_VOL
        val caloriesFromReps = totalReps * CALORIES_BASE_PER_REP
        val caloriesFromSets = sets * CALORIES_BASE_PER_SET
        
        return (caloriesFromVolume + caloriesFromReps + caloriesFromSets).roundToInt()
    }

    private fun recalculateTotalCalories(exercises: List<GymExerciseItem>): Int {
        return exercises.sumOf { calculateExerciseCalories(it.sets, it.reps, it.weightLb) }
    }

    fun toggleTimer() {
        if (_uiState.value.isTimerRunning) {
            timerJob?.cancel()
            _uiState.update { it.copy(isTimerRunning = false) }
        } else {
            _uiState.update { it.copy(isTimerRunning = true) }
            timerJob = viewModelScope.launch {
                while (true) {
                    delay(1000)
                    _uiState.update { it.copy(elapsedTime = it.elapsedTime + 1) }
                }
            }
        }
    }

    fun showAddExerciseDialog() {
        _uiState.update { it.copy(showAddExerciseDialog = true) }
    }

    fun hideAddExerciseDialog() {
        _uiState.update { it.copy(showAddExerciseDialog = false, editingExercise = null) }
    }

    fun onEditExercise(exercise: GymExerciseItem) {
        _uiState.update { it.copy(editingExercise = exercise) }
    }

    fun addExercise(name: String, sets: String, reps: String, weight: String) {
        val newItem = GymExerciseItem(
            name = name,
            sets = sets.toIntOrNull() ?: 0,
            reps = reps.toIntOrNull() ?: 0,
            weightLb = weight.toDoubleOrNull() ?: 0.0
        )
        _uiState.update { state ->
            val updatedExercises = state.exercises + newItem
            state.copy(
                exercises = updatedExercises,
                showAddExerciseDialog = false,
                totalCalories = recalculateTotalCalories(updatedExercises)
            )
        }
    }

    fun updateExercise(id: String, name: String, sets: String, reps: String, weight: String) {
        _uiState.update { state ->
            val updatedExercises = state.exercises.map {
                if (it.id == id) {
                    it.copy(
                        name = name,
                        sets = sets.toIntOrNull() ?: 0,
                        reps = reps.toIntOrNull() ?: 0,
                        weightLb = weight.toDoubleOrNull() ?: 0.0
                    )
                } else it
            }
            state.copy(
                exercises = updatedExercises,
                editingExercise = null,
                totalCalories = recalculateTotalCalories(updatedExercises)
            )
        }
    }

    fun toggleExerciseExpansion(id: String) {
        _uiState.update { state ->
            state.copy(
                exercises = state.exercises.map {
                    if (it.id == id) it.copy(isExpanded = !it.isExpanded) else it
                }
            )
        }
    }

    fun removeExercise(id: String) {
        _uiState.update { state ->
            val updatedExercises = state.exercises.filter { it.id != id }
            state.copy(
                exercises = updatedExercises,
                totalCalories = recalculateTotalCalories(updatedExercises)
            )
        }
    }

    fun saveSession() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val calendar = Calendar.getInstance()
            val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) ?: ""
            
            val gymExercise = GymExercise(
                id = 0,
                exercises = currentState.exercises.map { item ->
                    DomainGymExerciseItem(
                        id = item.id,
                        name = item.name,
                        sets = item.sets,
                        reps = item.reps,
                        weightKg = item.weightLb * LB_TO_KG
                    )
                },
                elapsedTime = currentState.elapsedTime,
                totalCalories = currentState.totalCalories,
                date = Date().time,
                dayOfWeek = dayOfWeek
            )
            
            insertGymExerciseUseCase(gymExercise)
            
            _uiState.update { it.copy(showSessionSavedDialog = true) }
        }
    }

    fun onDialogDismissed() {
        timerJob?.cancel()
        _uiState.value = GymWorkUiState()
    }
}
