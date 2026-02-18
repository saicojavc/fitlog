package com.saico.feature.weighttracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.core.common.util.UnitsConverter
import com.saico.core.datastore.UserSettingsDataStore
import com.saico.core.domain.usecase.user_profile.UserProfileUseCase
import com.saico.core.model.UnitsConfig
import com.saico.core.model.WeightEntry
import com.saico.feature.weighttracking.state.WeightTrackingUiState
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
class WeightTrackingViewModel @Inject constructor(
    private val userProfileUseCase: UserProfileUseCase,
    private val userDataStore: UserSettingsDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeightTrackingUiState())
    val uiState: StateFlow<WeightTrackingUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            combine(
                userProfileUseCase.getUserProfileUseCase(),
                userDataStore.userData
            ) { profile, userData ->
                profile to userData.unitsConfig
            }.collectLatest { (profile, units) ->
                _uiState.update { it.copy(
                    userProfile = profile,
                    unitsConfig = units,
                    weightHistory = profile?.weightHistory?.sortedByDescending { it.date } ?: emptyList(),
                    isLoading = false
                ) }
            }
        }
    }

    fun registerNewWeight(weightStr: String) {
        val weightValue = weightStr.toDoubleOrNull() ?: return
        val currentProfile = _uiState.value.userProfile ?: return
        val units = _uiState.value.unitsConfig

        // Siempre convertimos a KG antes de guardar en la DB
        val weightInKg = if (units == UnitsConfig.METRIC) weightValue else UnitsConverter.lbToKg(weightValue)
        
        val newEntry = WeightEntry(weight = weightInKg, date = System.currentTimeMillis())
        val updatedHistory = currentProfile.weightHistory + newEntry

        viewModelScope.launch {
            userProfileUseCase.updateUserProfileUseCase(
                currentProfile.copy(
                    weightKg = weightInKg,
                    weightHistory = updatedHistory
                )
            )
        }
    }
}
