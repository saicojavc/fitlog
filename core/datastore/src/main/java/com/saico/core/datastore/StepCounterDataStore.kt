package com.saico.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

// Extensión para crear la instancia de DataStore a nivel de aplicación.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "step_counter_prefs")

/**
 * Gestiona la persistencia de los datos del contador de pasos, como el offset diario
 * y la fecha del último reinicio.
 *
 * @property context El contexto de la aplicación para acceder a DataStore.
 */
@Singleton
class StepCounterDataStore @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferencesKeys {
        // Key para guardar el último valor conocido del sensor (offset).
        val STEP_OFFSET_KEY = intPreferencesKey("step_offset")
        // Key para guardar la fecha (en milisegundos) en que se guardó el offset.
        val LAST_RESET_DATE_KEY = longPreferencesKey("last_reset_date")
    }

    /**
     * Guarda el offset de pasos y la fecha actual.
     *
     * @param steps El valor del contador de pasos a guardar como offset.
     */
    suspend fun saveStepCounterData(steps: Int) {
        context.dataStore.edit {
            it[PreferencesKeys.STEP_OFFSET_KEY] = steps
            it[PreferencesKeys.LAST_RESET_DATE_KEY] = System.currentTimeMillis()
        }
    }

    /**
     * Flow que emite el offset de pasos guardado. Emite 0 si no hay ningún valor.
     */
    val stepOffset: Flow<Int> = context.dataStore.data.map {
        it[PreferencesKeys.STEP_OFFSET_KEY] ?: 0
    }

    /**
     * Flow que emite la fecha del último reinicio. Emite 0L si no hay ningún valor.
     */
    val lastResetDate: Flow<Long> = context.dataStore.data.map {
        it[PreferencesKeys.LAST_RESET_DATE_KEY] ?: 0L
    }

    /**
     * Comprueba si la fecha guardada es de un día anterior al actual.
     *
     * @param lastSavedMillis La fecha del último guardado en milisegundos.
     * @return `true` si es un día nuevo, `false` en caso contrario.
     */
    fun isNewDay(lastSavedMillis: Long): Boolean {
        if (lastSavedMillis == 0L) return true // Si nunca se ha guardado, es "nuevo".

        val lastDate = Calendar.getInstance().apply { timeInMillis = lastSavedMillis }
        val currentDate = Calendar.getInstance()

        return lastDate.get(Calendar.DAY_OF_YEAR) != currentDate.get(Calendar.DAY_OF_YEAR) ||
                lastDate.get(Calendar.YEAR) != currentDate.get(Calendar.YEAR)
    }
}