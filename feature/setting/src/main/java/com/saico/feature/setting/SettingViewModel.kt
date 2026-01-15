package com.saico.feature.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saico.core.datastore.UserSettingsDataStore
import com.saico.core.model.DarkThemeConfig
import com.saico.core.model.LanguageConfig
import com.saico.core.model.UnitsConfig
import com.saico.feature.setting.state.SettingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userDataStore: UserSettingsDataStore
) : ViewModel() {

    val uiState: StateFlow<SettingUiState> = userDataStore.userData
        .map { SettingUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingUiState.Loading
        )

    fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            userDataStore.setDarkThemeConfig(darkThemeConfig)
        }
    }

    fun updateLanguageConfig(languageConfig: LanguageConfig) {
        viewModelScope.launch {
            userDataStore.setLanguageConfig(languageConfig)
        }
    }

    fun updateUnitsConfig(unitsConfig: UnitsConfig) {
        viewModelScope.launch {
            userDataStore.setUnitsConfig(unitsConfig)
        }
    }

    fun updateDynamicColorPreference(useDynamicColor: Boolean) {
        viewModelScope.launch {
            userDataStore.setDynamicColorPreference(useDynamicColor)
        }
    }

    fun updateWorkoutReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            userDataStore.setWorkoutReminderTime(hour, minute)
        }
    }
}
