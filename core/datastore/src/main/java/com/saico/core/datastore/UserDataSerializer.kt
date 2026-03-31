package com.saico.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
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
        val WORKOUT_REMINDER_ENABLED = booleanPreferencesKey("workout_reminder_enabled")
        val WORKOUT_REMINDER_DAYS = stringSetPreferencesKey("workout_reminder_days")

        // Notificaciones Flags
        val GOAL_REACHED_SHOWN_DATE = longPreferencesKey("goal_reached_shown_date")
        val MILESTONE_50_SHOWN_DATE = longPreferencesKey("milestone_50_shown_date")
        val MILESTONE_90_SHOWN_DATE = longPreferencesKey("milestone_90_shown_date")
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
            workoutReminderMinute = preferences[PreferencesKeys.WORKOUT_REMINDER_MINUTE] ?: 0,
            workoutReminderEnabled = preferences[PreferencesKeys.WORKOUT_REMINDER_ENABLED] ?: false,
            workoutReminderDays = preferences[PreferencesKeys.WORKOUT_REMINDER_DAYS]?.map { it.toInt() }
                ?.toSet() ?: emptySet())
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

    suspend fun setWorkoutReminderEnabled(enabled: Boolean) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.WORKOUT_REMINDER_ENABLED] = enabled
        }
    }

    suspend fun setWorkoutReminderDays(days: Set<Int>) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.WORKOUT_REMINDER_DAYS] = days.map { it.toString() }.toSet()
        }
    }

    // Lógica para notificaciones una vez al día
    val goalReachedShownDate: Flow<Long> =
        context.userDataStore.data.map { it[PreferencesKeys.GOAL_REACHED_SHOWN_DATE] ?: 0L }
    val milestone50ShownDate: Flow<Long> =
        context.userDataStore.data.map { it[PreferencesKeys.MILESTONE_50_SHOWN_DATE] ?: 0L }
    val milestone90ShownDate: Flow<Long> =
        context.userDataStore.data.map { it[PreferencesKeys.MILESTONE_90_SHOWN_DATE] ?: 0L }

    suspend fun setGoalReachedShown(timestamp: Long) {
        context.userDataStore.edit { it[PreferencesKeys.GOAL_REACHED_SHOWN_DATE] = timestamp }
    }

    suspend fun setMilestone50Shown(timestamp: Long) {
        context.userDataStore.edit { it[PreferencesKeys.MILESTONE_50_SHOWN_DATE] = timestamp }
    }

    suspend fun setMilestone90Shown(timestamp: Long) {
        context.userDataStore.edit { it[PreferencesKeys.MILESTONE_90_SHOWN_DATE] = timestamp }
    }
}
