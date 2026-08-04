package com.saico.feature.gymwork.routineconfig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.core.domain.repository.WgerRepository
import com.saico.core.model.DayRoutine
import com.saico.core.model.RoutineExercise
import com.saico.core.model.WgerExercise
import com.saico.core.model.WorkoutDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineConfigViewModel @Inject constructor(
    private val wgerRepository: WgerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutineConfigUiState())
    val uiState: StateFlow<RoutineConfigUiState> = _uiState.asStateFlow()

    private var allExercises = emptyList<WgerExercise>()
    private val PAGE_SIZE = 30
    private var currentPage = 0

    init {
        loadExercises()
        loadCurrentRoutine()
    }

    private fun loadExercises() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            wgerRepository.getExercises().collectLatest { exercises ->
                allExercises = exercises
                currentPage = 1
                updateDisplayedExercises()
            }
        }
    }

    private fun loadCurrentRoutine() {
        viewModelScope.launch {
            val routine = wgerRepository.getCustomRoutine(_uiState.value.selectedDay)
            val selectedIds = routine?.exercises?.map { it.exerciseId } ?: emptyList()
            
            // Reconstruct WgerExercise list from IDs
            wgerRepository.getExercises().collectLatest { exercises ->
                val selected = exercises.filter { it.id in selectedIds }
                _uiState.update { it.copy(selectedExercises = selected) }
            }
        }
    }

    private fun updateDisplayedExercises() {
        val query = _uiState.value.searchQuery.lowercase().trim()
        val category = _uiState.value.selectedCategory

        val filtered = allExercises.filter { exercise ->
            val matchesQuery = exercise.name.lowercase().contains(query)
            val matchesCategory = category == null || 
                exercise.category.contains(category, ignoreCase = true) ||
                (category == "Pecho" && exercise.category.contains("Chest", ignoreCase = true))

            matchesQuery && matchesCategory
        }

        val paginated = filtered.take(currentPage * PAGE_SIZE)
        _uiState.update { it.copy(
            availableExercises = paginated, 
            isLoading = false 
        ) }
    }

    fun loadNextPage() {
        if (_uiState.value.isLoading) return
        currentPage++
        updateDisplayedExercises()
    }

    fun onDaySelected(day: WorkoutDay) {
        _uiState.update { it.copy(selectedDay = day) }
        loadCurrentRoutine()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        currentPage = 1
        updateDisplayedExercises()
    }

    fun onCategorySelected(category: String?) {
        val newCategory = if (_uiState.value.selectedCategory == category) null else category
        _uiState.update { it.copy(selectedCategory = newCategory) }
        currentPage = 1
        updateDisplayedExercises()
    }

    fun toggleExerciseSelection(exercise: WgerExercise) {
        _uiState.update { state ->
            val current = state.selectedExercises.toMutableList()
            if (current.any { it.id == exercise.id }) {
                current.removeAll { it.id == exercise.id }
            } else {
                current.add(exercise)
            }
            state.copy(selectedExercises = current)
        }
    }

    fun saveRoutine() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val state = _uiState.value
            
            val routineExercises = state.selectedExercises.map { exercise ->
                RoutineExercise(
                    exerciseId = exercise.id,
                    name = exercise.name,
                    imageUrl = exercise.imageUrl,
                    targetSets = 3, // Default values
                    targetReps = "12",
                    restTimeSeconds = 90
                )
            }

            val routine = DayRoutine(
                dayOfWeek = state.selectedDay,
                title = "Rutina de ${state.selectedDay.name.lowercase().replaceFirstChar { it.uppercase() }}",
                exercises = routineExercises
            )

            wgerRepository.saveCustomRoutine(routine)
            _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }
}
