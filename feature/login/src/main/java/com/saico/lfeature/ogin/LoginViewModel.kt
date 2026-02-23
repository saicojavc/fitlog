package com.saico.lfeature.ogin

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
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
import com.saico.core.ui.R

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

    private val _error = MutableStateFlow<UiText?>(null)
    val error = _error.asStateFlow()

    fun loginWithGoogle(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            loginWithGoogleUseCase(idToken).onSuccess { user ->
                // Al iniciar sesión, usamos syncAll para SUBIR datos locales primero y luego descargar.
                // Esto evita que el progreso offline se pierda al ser sobreescrito por la nube.
                syncUserDataUseCase.syncAll(user.id).onSuccess {
                    setOnboardingCompletedUseCase(true)
                    onSuccess()
                }.onFailure {
                    // Si falla la sincronización completa, intentamos al menos restaurar el perfil
                    syncUserDataUseCase.restoreAllData(user.id).onSuccess {
                        setOnboardingCompletedUseCase(true)
                        onSuccess()
                    }.onFailure {
                        _error.value = UiText.StringResource(R.string.error_restoring_data)
                    }
                }
                _isLoading.value = false
            }.onFailure {
                _error.value = UiText.StringResource(R.string.error_auth_failed)
                _isLoading.value = false
            }
        }
    }
}

/**
 * Clase para manejar textos que pueden ser Strings directos o Recursos de ID (para multi-idioma)
 */
sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : UiText()

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args)
        }
    }
}
