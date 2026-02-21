package com.saico.fitlog.ui.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.core.datastore.UserSettingsDataStore
import com.saico.core.domain.repository.AuthRepository
import com.saico.core.domain.usecase.onboarding.GetOnboardingCompletedUseCase
import com.saico.core.model.UserData
import com.saico.core.ui.navigation.routes.dashboard.DashboardRoute
import com.saico.core.ui.navigation.routes.login.LoginRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getOnboardingCompletedUseCase: GetOnboardingCompletedUseCase,
    private val userDataStore: UserSettingsDataStore,
    private val authRepository: AuthRepository
) : ViewModel() {

    var isLoading by mutableStateOf(true)
        private set

    var firstScreen by mutableStateOf(LoginRoute.RootRoute.route)
        private set

    val userData: StateFlow<UserData?> = userDataStore.userData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    init {
        viewModelScope.launch {
            getOnboardingCompletedUseCase().collectLatest { onboardingCompleted ->
                // Lógica de decisión: 
                // Si está logueado en Firebase O ha completado el onboarding local -> Dashboard
                firstScreen = if (authRepository.isUserLoggedIn || onboardingCompleted) {
                    DashboardRoute.RootRoute.route
                } else {
                    LoginRoute.RootRoute.route
                }
                isLoading = false
            }
        }
    }
}
