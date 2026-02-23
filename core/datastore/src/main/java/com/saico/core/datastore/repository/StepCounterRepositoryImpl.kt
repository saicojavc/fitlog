package com.saico.core.datastore.repository

import com.saico.core.common.util.StepCounterSensor
import com.saico.core.datastore.StepCounterDataStore
import com.saico.core.domain.repository.StepCounterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class StepCounterRepositoryImpl @Inject constructor(
    private val stepCounterDataStore: StepCounterDataStore,
    private val stepCounterSensor: StepCounterSensor
) : StepCounterRepository {
    override val currentSteps: Flow<Int> = stepCounterDataStore.currentSteps

    override suspend fun updateCurrentSteps(steps: Int) {
        stepCounterDataStore.updateCurrentSteps(steps)
    }

    override suspend fun synchronizeOffset(stepsToPreserve: Int) {
        // 1. Obtenemos el valor bruto actual del sensor
        val sensorValue = stepCounterSensor.steps.first()
        
        // 2. Calculamos el nuevo offset: Offset = Sensor - Pasos_a_Preservar
        // Ejemplo: Si el sensor marca 10,000 y tengo 5,000 de la nube, el offset es 5,000.
        // Al caminar, el sensor marcará 10,001 y 10,001 - 5,000 = 5,001. ¡Funciona!
        val newOffset = (sensorValue - stepsToPreserve).coerceAtLeast(0)
        
        // 3. Guardamos el nuevo offset y actualizamos los pasos actuales
        stepCounterDataStore.saveStepCounterData(newOffset)
        stepCounterDataStore.updateCurrentSteps(stepsToPreserve)
    }
}
