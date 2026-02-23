package com.saico.core.domain.usecase

import com.saico.core.domain.repository.*
import com.saico.core.model.*
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject

class SyncUserDataUseCase @Inject constructor(
    private val syncRepository: SyncRepository,
    private val userProfileUseCase: com.saico.core.domain.usecase.user_profile.UserProfileUseCase,
    private val workoutRepository: WorkoutRepository,
    private val gymExerciseRepository: GymExerciseRepository,
    private val workoutSessionRepository: WorkoutSessionRepository,
    private val stepCounterRepository: StepCounterRepository
) {
    /**
     * Sincronización inteligente: Solo sube si hay datos locales válidos.
     */
    suspend fun syncAll(uid: String): Result<Unit> {
        return try {
            val localProfile = userProfileUseCase.getUserProfileUseCase().first()
            
            // CRÍTICO: Verificamos si el perfil local es real o es un perfil nuevo/vacío.
            // Si el peso o la altura son 0, asumimos que es una instalación limpia y NO subimos nada.
            val hasValidLocalData = localProfile != null && localProfile.weightKg > 0 && localProfile.heightCm > 0

            if (hasValidLocalData) {
                val localWorkouts = workoutRepository.getWorkouts().first()
                val localSessions = workoutSessionRepository.getWorkoutSessions().first()
                val localGym = gymExerciseRepository.getGymExercises().first()

                syncRepository.uploadAllLocalData(
                    uid = uid,
                    profile = localProfile!!,
                    workouts = localWorkouts,
                    sessions = localSessions,
                    gymExercises = localGym
                ).getOrThrow()
            }

            // Después de asegurar (o saltar) la subida, descargamos lo que haya en la nube.
            restoreAllData(uid).getOrThrow()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Descarga datos de la nube y los mezcla con los locales cuidando no borrar progreso.
     */
    suspend fun restoreAllData(uid: String): Result<Unit> {
        return try {
            val cloudProfile = syncRepository.fetchUserProfile(uid).getOrNull()
            val cloudWorkouts = syncRepository.fetchWorkouts(uid).getOrDefault(emptyList())
            val cloudSessions = syncRepository.fetchWorkoutSessions(uid).getOrDefault(emptyList())
            val cloudGym = syncRepository.fetchGymExercises(uid).getOrDefault(emptyList())

            // Solo guardamos el perfil de la nube si trae datos reales.
            if (cloudProfile != null && cloudProfile.weightKg > 0) {
                userProfileUseCase.updateUserProfileUseCase(cloudProfile)
            }
            
            val todayStart = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            // Insertamos Workouts respetando la estrategia de conflicto (Indices únicos)
            cloudWorkouts.forEach { workout ->
                workoutRepository.insertWorkout(workout)
                if (workout.date >= todayStart) {
                    stepCounterRepository.synchronizeOffset(workout.steps)
                }
            }

            cloudSessions.forEach { workoutSessionRepository.insertWorkoutSession(it) }
            cloudGym.forEach { gymExerciseRepository.insertGymExercise(it) }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncProfile(uid: String, profile: UserProfile) = syncRepository.syncUserProfile(uid, profile)
    suspend fun syncWorkout(uid: String, workout: Workout) = syncRepository.syncWorkout(uid, workout)
    suspend fun syncSession(uid: String, session: WorkoutSession) = syncRepository.syncWorkoutSession(uid, session)
    suspend fun syncGymExercise(uid: String, exercise: GymExercise) = syncRepository.syncGymExercise(uid, exercise)
}
