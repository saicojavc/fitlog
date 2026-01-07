package com.saico.feature.dashboard.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestiona la comunicación con el sensor de contador de pasos del dispositivo.
 *
 * Expone un Flow con el número total de pasos registrados por el sensor desde el último reinicio
 * del dispositivo. La emisión de datos se detiene cuando no hay un colector para ahorrar batería.
 *
 * @property context El contexto de la aplicación para acceder a los servicios del sistema.
 */
@Singleton
class StepCounterSensor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private val stepCounterSensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    /**
     * Un Flow que emite el valor bruto del contador de pasos cada vez que el sensor notifica un cambio.
     * Si el sensor no está disponible, el Flow no emitirá ningún valor.
     */
    val steps: Flow<Int> = callbackFlow {
        if (stepCounterSensor == null) {
            // Si no hay sensor, cierra el flow inmediatamente.
            close()
            return@callbackFlow
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                        // El valor de los pasos es un float, lo convertimos a Int.
                        val totalStepsSinceReboot = it.values[0].toInt()
                        // Ofrecemos el último valor al flow.
                        launch { send(totalStepsSinceReboot) }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // No es necesario para esta implementación.
            }
        }

        // Registra el listener cuando el Flow es colectado.
        sensorManager.registerListener(listener, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL)

        // Desregistra el listener cuando el Flow es cancelado o cerrado.
        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    /**
     * Comprueba si el sensor de contador de pasos está disponible en el dispositivo.
     */
    fun isSensorAvailable(): Boolean = stepCounterSensor != null
}
