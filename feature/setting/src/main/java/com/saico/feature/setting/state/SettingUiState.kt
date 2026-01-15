package com.saico.feature.setting.state

import com.saico.core.model.UserData

sealed interface SettingUiState {
    object Loading : SettingUiState
    data class Success(val settings: UserData) : SettingUiState
}
