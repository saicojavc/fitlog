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
        }
    }

    fun onWeightChange(newWeight: String) {
        if (newWeight.all { it.isDigit() }) {
            _uiState.update { it.copy(weight = newWeight) }
        }
    }

    fun onHeightChange(newHeight: String) {
        if (newHeight.all { it.isDigit() }) {
            _uiState.update { it.copy(height = newHeight) }
        }
    }

    fun onGenderSelected(gender: String) {
        _uiState.update { it.copy(gender = gender) }
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
}
