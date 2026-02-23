package com.saico.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface StepCounterRepository {
    val currentSteps: Flow<Int>
    suspend fun updateCurrentSteps(steps: Int)
    /**
     * Ajusta el offset del sensor para preservar los pasos restaurados de la nube.
     */
    suspend fun synchronizeOffset(stepsToPreserve: Int)
}
