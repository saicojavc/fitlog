package com.saico.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.saico.core.model.DarkThemeConfig
import com.saico.core.model.LanguageConfig
import com.saico.core.model.UserData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

@Singleton
class UserSettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val DARK_THEME_CONFIG = stringPreferencesKey("dark_theme_config")
        val LANGUAGE_CONFIG = stringPreferencesKey("language_config")
        val USE_DYNAMIC_COLOR = booleanPreferencesKey("use_dynamic_color")
    }

    val userData: Flow<UserData> = context.userDataStore.data.map { preferences ->
        UserData(
            darkThemeConfig = DarkThemeConfig.valueOf(
                preferences[PreferencesKeys.DARK_THEME_CONFIG] ?: DarkThemeConfig.FOLLOW_SYSTEM.name
            ),
            languageConfig = LanguageConfig.valueOf(
                preferences[PreferencesKeys.LANGUAGE_CONFIG] ?: LanguageConfig.FOLLOW_SYSTEM.name
            ),
            useDynamicColor = preferences[PreferencesKeys.USE_DYNAMIC_COLOR] ?: false
        )
    }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_THEME_CONFIG] = darkThemeConfig.name
        }
    }

    suspend fun setLanguageConfig(languageConfig: LanguageConfig) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.LANGUAGE_CONFIG] = languageConfig.name
        }
    }

    suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_DYNAMIC_COLOR] = useDynamicColor
        }
    }
}
