package com.saico.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.saico.core.model.DarkThemeConfig
import com.saico.core.model.LanguageConfig
import com.saico.core.model.UnitsConfig
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
        val UNITS_CONFIG = stringPreferencesKey("units_config")
        val USE_DYNAMIC_COLOR = booleanPreferencesKey("use_dynamic_color")
        val WORKOUT_REMINDER_HOUR = intPreferencesKey("workout_reminder_hour")
        val WORKOUT_REMINDER_MINUTE = intPreferencesKey("workout_reminder_minute")
        
        // Notificaciones Flags
        val GOAL_REACHED_SHOWN_DATE = longPreferencesKey("goal_reached_shown_date")
        val HALF_GOAL_SHOWN_DATE = longPreferencesKey("half_goal_shown_date")
    }

    val userData: Flow<UserData> = context.userDataStore.data.map { preferences ->
        UserData(
            darkThemeConfig = DarkThemeConfig.valueOf(
                preferences[PreferencesKeys.DARK_THEME_CONFIG] ?: DarkThemeConfig.FOLLOW_SYSTEM.name
            ),
            languageConfig = LanguageConfig.valueOf(
                preferences[PreferencesKeys.LANGUAGE_CONFIG] ?: LanguageConfig.FOLLOW_SYSTEM.name
            ),
            unitsConfig = UnitsConfig.valueOf(
                preferences[PreferencesKeys.UNITS_CONFIG] ?: UnitsConfig.METRIC.name
            ),
            useDynamicColor = preferences[PreferencesKeys.USE_DYNAMIC_COLOR] ?: false,
            workoutReminderHour = preferences[PreferencesKeys.WORKOUT_REMINDER_HOUR] ?: 18,
            workoutReminderMinute = preferences[PreferencesKeys.WORKOUT_REMINDER_MINUTE] ?: 0
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

    suspend fun setUnitsConfig(unitsConfig: UnitsConfig) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.UNITS_CONFIG] = unitsConfig.name
        }
    }

    suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_DYNAMIC_COLOR] = useDynamicColor
        }
    }

    suspend fun setWorkoutReminderTime(hour: Int, minute: Int) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.WORKOUT_REMINDER_HOUR] = hour
            preferences[PreferencesKeys.WORKOUT_REMINDER_MINUTE] = minute
        }
    }

    // Lógica para notificaciones una vez al día
    val goalReachedShownDate: Flow<Long> = context.userDataStore.data.map { it[PreferencesKeys.GOAL_REACHED_SHOWN_DATE] ?: 0L }
    val halfGoalShownDate: Flow<Long> = context.userDataStore.data.map { it[PreferencesKeys.HALF_GOAL_SHOWN_DATE] ?: 0L }

    suspend fun setGoalReachedShown(timestamp: Long) {
        context.userDataStore.edit { it[PreferencesKeys.GOAL_REACHED_SHOWN_DATE] = timestamp }
    }

    suspend fun setHalfGoalShown(timestamp: Long) {
        context.userDataStore.edit { it[PreferencesKeys.HALF_GOAL_SHOWN_DATE] = timestamp }
    }
}
