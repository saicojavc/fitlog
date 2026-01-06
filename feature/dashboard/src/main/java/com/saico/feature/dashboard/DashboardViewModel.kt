package com.saico.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.core.domain.usecase.user_profile.GetUserProfileUseCase
import com.saico.feature.dashboard.state.DashboardUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        getUserProfile()
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
}
