package com.saico.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.core.common.util.UnitsConverter
import com.saico.core.datastore.UserSettingsDataStore
import com.saico.core.domain.usecase.onboarding.SetOnboardingCompletedUseCase
import com.saico.core.domain.usecase.user_profile.UserProfileUseCase
import com.saico.core.model.UnitsConfig
import com.saico.core.model.UserProfile
import com.saico.core.model.WeightEntry
import com.saico.feature.onboarding.state.OnboardingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userProfileUseCase: UserProfileUseCase,
    private val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase,
    private val userSettingsDataStore: UserSettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onAgeChange(newAge: String) {
        if (newAge.all { it.isDigit() }) {
            _uiState.update { it.copy(age = newAge) }
            validateProfileConfiguration()
        }
    }

    fun onWeightChange(newWeight: String) {
        if (newWeight.all { it.isDigit() || it == '.' }) {
            _uiState.update { it.copy(weight = newWeight) }
            validateProfileConfiguration()
        }
    }

    fun onHeightChange(newHeight: String) {
        if (newHeight.all { it.isDigit() || it == '.' }) {
            _uiState.update { it.copy(height = newHeight) }
            validateProfileConfiguration()
        }
    }

    fun onHeightFtChange(newFt: String) {
        if (newFt.all { it.isDigit() }) {
            _uiState.update { it.copy(heightFt = newFt) }
            validateProfileConfiguration()
        }
    }

    fun onHeightInChange(newIn: String) {
        if (newIn.all { it.isDigit() }) {
            _uiState.update { it.copy(heightIn = newIn) }
            validateProfileConfiguration()
        }
    }

    fun onGenderSelected(gender: String) {
        _uiState.update { it.copy(gender = gender) }
        validateProfileConfiguration()
    }

    fun onUnitsConfigSelected(unitsConfig: UnitsConfig) {
        _uiState.update { it.copy(unitsConfig = unitsConfig) }
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

    fun saveUserProfile() {
        viewModelScope.launch {
            val state = _uiState.value
            val steps = state.dailySteps
            val cals = state.caloriesToBurn
            
            val calculatedLevel = when {
                steps > 19000 || cals > 1500 -> "Professional"
                steps > 10000 || cals > 500 -> "Intermediate"
                else -> "Beginner"
            }

            val weightInputValue = state.weight.toDoubleOrNull() ?: 0.0
            
            val weightKg = if (state.unitsConfig == UnitsConfig.METRIC) {
                weightInputValue
            } else {
                UnitsConverter.lbToKg(weightInputValue)
            }

            val heightCm = if (state.unitsConfig == UnitsConfig.METRIC) {
                state.height.toDoubleOrNull() ?: 0.0
            } else {
                UnitsConverter.ftInToCm(
                    state.heightFt.toIntOrNull() ?: 0,
                    state.heightIn.toIntOrNull() ?: 0
                )
            }

            val initialWeightEntry = WeightEntry(
                weight = weightKg,
                date = System.currentTimeMillis()
            )

            val userProfile = UserProfile(
                age = state.age.toIntOrNull() ?: 0,
                weightKg = weightKg,
                heightCm = heightCm,
                gender = state.gender,
                dailyStepsGoal = state.dailySteps,
                dailyCaloriesGoal = state.caloriesToBurn,
                level = calculatedLevel,
                weightHistory = listOf(initialWeightEntry)
            )
            
            userProfileUseCase.insertUserProfileUseCase(userProfile)
            userSettingsDataStore.setUnitsConfig(state.unitsConfig)
            setOnboardingCompletedUseCase(true)
        }
    }

    private fun validateProfileConfiguration() {
        val state = _uiState.value
        val isHeightValid = if (state.unitsConfig == UnitsConfig.METRIC) {
            state.height.isNotBlank()
        } else {
            state.heightFt.isNotBlank() && state.heightIn.isNotBlank()
        }

        val isConfigurationValid = state.age.isNotBlank() &&
                state.weight.isNotBlank() &&
                isHeightValid &&
                state.gender.isNotBlank()
        _uiState.update { it.copy(isProfileConfigurationValid = isConfigurationValid) }
    }
}
