package com.saico.core.domain.usecase.workout

import com.saico.core.domain.repository.AuthRepository
import com.saico.core.domain.repository.SyncRepository
import com.saico.core.domain.repository.WorkoutRepository
import com.saico.core.model.Workout
import javax.inject.Inject

class InsertWorkoutUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val authRepository: AuthRepository,
    private val syncRepository: SyncRepository
) {
    suspend operator fun invoke(workout: Workout) {
        // 1. Guardar localmente (Offline-First)
        workoutRepository.insertWorkout(workout)

        // 2. Sincronizar con Firebase en vivo si hay sesión
        authRepository.getCurrentUser()?.let { user ->
            syncRepository.syncWorkout(user.id, workout)
        }
    }
}
