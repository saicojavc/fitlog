package com.saico.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.core.datastore.StepCounterDataStore
import com.saico.core.domain.usecase.user_profile.GetUserProfileUseCase
import com.saico.feature.dashboard.service.StepCounterSensor
import com.saico.feature.dashboard.state.DashboardUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val stepCounterSensor: StepCounterSensor,
    private val stepCounterDataStore: StepCounterDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        getUserProfile()
        initStepCounter()
    }

    private fun getUserProfile() {
        viewModelScope.launch {
            getUserProfileUseCase().collectLatest {
                _uiState.update { state ->
                    state.copy(userProfile = it)
                }
            }
        }
    }

    private fun initStepCounter() {
        if (!stepCounterSensor.isSensorAvailable()) {
            return
        }

        viewModelScope.launch {
            combine(
                stepCounterSensor.steps,
                stepCounterDataStore.stepOffset,
                stepCounterDataStore.lastResetDate
            ) { totalStepsSinceReboot, offset, lastResetDate ->
                
                val currentOffset = if (stepCounterDataStore.isNewDay(lastResetDate)) {
                    stepCounterDataStore.saveStepCounterData(totalStepsSinceReboot)
                    totalStepsSinceReboot // El nuevo offset es el valor actual
                } else {
                    offset // Mantenemos el offset guardado
                }

                val dailySteps = (totalStepsSinceReboot - currentOffset).coerceAtLeast(0)

                // Devolvemos un objeto con todos los datos para actualizar el estado
                Triple(dailySteps, totalStepsSinceReboot, currentOffset)

            }.collectLatest { (daily, total, offsetValue) ->
                _uiState.update {
                    it.copy(
                        dailySteps = daily,
                        totalSteps = total,
                        stepOffset = offsetValue
                    )
                }
            }
        }
    }
}
