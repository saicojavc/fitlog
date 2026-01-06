package com.saico.fitlog.ui.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.core.domain.usecase.onboarding.GetOnboardingCompletedUseCase
import com.saico.core.ui.navigation.routes.dashboard.DashboardRoute
import com.saico.core.ui.navigation.routes.login.LoginRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getOnboardingCompletedUseCase: GetOnboardingCompletedUseCase
) : ViewModel() {

    var isLoading by mutableStateOf(true)
        private set

    var firstScreen by mutableStateOf(LoginRoute.RootRoute.route)
        private set

    init {
        viewModelScope.launch {
            getOnboardingCompletedUseCase().collectLatest {
                firstScreen = if (it) {
                    DashboardRoute.RootRoute.route
                } else {
                    LoginRoute.RootRoute.route
                }
                isLoading = false
            }
        }
    }
}
