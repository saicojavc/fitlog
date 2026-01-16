package com.saico.feature.stepshistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.core.datastore.StepCounterDataStore
import com.saico.core.domain.usecase.workout.WorkoutUseCase
import com.saico.core.model.Workout
import com.saico.core.domain.usecase.user_profile.UserProfileUseCase
import com.saico.feature.stepshistory.state.StepsHistoryFilter
import com.saico.feature.stepshistory.state.StepsHistoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class StepsHistoryViewModel @Inject constructor(
    private val workoutUseCase: WorkoutUseCase,
    private val stepCounterDataStore: StepCounterDataStore,
    private val userProfileUseCase: UserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(StepsHistoryUiState())
    val uiState: StateFlow<StepsHistoryUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            combine(
                workoutUseCase.getWorkoutsUseCase(),
                stepCounterDataStore.currentSteps,
                userProfileUseCase.getUserProfileUseCase()
            ) { workouts, currentSteps, userProfile ->
                Triple(workouts, currentSteps, userProfile)
            }.collectLatest { (workouts, currentSteps, userProfile) ->
                _uiState.update { 
                    it.copy(
                        workouts = workouts,
                        currentSteps = currentSteps,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onFilterSelected(filter: StepsHistoryFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }
}
