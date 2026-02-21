package com.saico.lfeature.ogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.core.domain.repository.AuthRepository
import com.saico.core.domain.repository.SyncRepository
import com.saico.core.domain.usecase.SyncUserDataUseCase
import com.saico.core.domain.usecase.onboarding.SetOnboardingCompletedUseCase
import com.saico.core.network.usecase.LoginWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val syncUserDataUseCase: SyncUserDataUseCase,
    private val syncRepository: SyncRepository,
    private val authRepository: AuthRepository,
    private val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loginWithGoogle(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            loginWithGoogleUseCase(idToken).onSuccess { user ->
                syncRepository.fetchUserProfile(user.id).onSuccess { profile ->
                    if (profile != null) {
                        syncUserDataUseCase.restoreAllData(user.id).onSuccess {
                            // IMPORTANTE: Esperamos a que se guarde en DataStore antes de navegar
                            setOnboardingCompletedUseCase(true)
                            onSuccess()
                        }.onFailure {
                            _error.value = "Error al restaurar los datos."
                        }
                    } else {
                        authRepository.logout()
                        _error.value = "No se encontró una cuenta sincronizada. Por favor, crea una nueva."
                    }
                    _isLoading.value = false
                }.onFailure {
                    authRepository.logout()
                    _error.value = "Error al verificar la cuenta."
                    _isLoading.value = false
                }
            }.onFailure {
                _error.value = "Error de autenticación."
                _isLoading.value = false
            }
        }
    }
}
