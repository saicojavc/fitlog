package com.saico.feature.onboarding

import androidx.lifecycle.ViewModel
import com.saico.feature.onboarding.state.OnboardingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onAgeChange(newAge: String) {
        if (newAge.all { it.isDigit() }) {
            _uiState.update { it.copy(age = newAge) }
            validateProfileConfiguration()
        }
    }

    fun onWeightChange(newWeight: String) {
        if (newWeight.all { it.isDigit() }) {
            _uiState.update { it.copy(weight = newWeight) }
            validateProfileConfiguration()
        }
    }

    fun onHeightChange(newHeight: String) {
        if (newHeight.all { it.isDigit() }) {
            _uiState.update { it.copy(height = newHeight) }
            validateProfileConfiguration()
        }
    }

    fun onGenderSelected(gender: String) {
        _uiState.update { it.copy(gender = gender) }
        validateProfileConfiguration()
    }

    fun onGenderMenuExpanded(expanded: Boolean) {
        _uiState.update { it.copy(isGenderMenuExpanded = expanded) }
    }

    fun onDailyStepsChange(newSteps: Int) {
        _uiState.update { it.copy(dailySteps = newSteps) }
    }

    fun onCaloriesToBurnChange(newCalories: Int) {
        _uiState.update { it.copy(caloriesToBurn = newCalories) }
    }

    private fun validateProfileConfiguration() {
        val state = _uiState.value
        val isConfigurationValid = state.age.isNotBlank() &&
                state.weight.isNotBlank() &&
                state.height.isNotBlank() &&
                state.gender.isNotBlank()
        _uiState.update { it.copy(isProfileConfigurationValid = isConfigurationValid) }
    }
}
