package com.saico.core.domain.usecase

import com.saico.core.domain.repository.*
import com.saico.core.domain.usecase.user_profile.UserProfileUseCase
import com.saico.core.model.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SyncUserDataUseCase @Inject constructor(
    private val syncRepository: SyncRepository,
    private val userProfileUseCase: UserProfileUseCase,
    private val workoutRepository: WorkoutRepository,
    private val gymExerciseRepository: GymExerciseRepository,
    private val workoutSessionRepository: WorkoutSessionRepository
) {
    suspend fun syncAll(uid: String): Result<Unit> {
        return try {
            val profile = userProfileUseCase.getUserProfileUseCase().first()
            val workouts = workoutRepository.getWorkouts().first()
            val sessions = workoutSessionRepository.getWorkoutSessions().first()
            val gymExercises = gymExerciseRepository.getGymExercises().first()

            syncRepository.uploadAllLocalData(
                uid = uid,
                profile = profile ?: UserProfile(),
                workouts = workouts,
                sessions = sessions,
                gymExercises = gymExercises
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreAllData(uid: String): Result<Unit> {
        return try {
            // 1. Descargar todo de la nube
            val cloudProfile = syncRepository.fetchUserProfile(uid).getOrNull()
            val cloudWorkouts = syncRepository.fetchWorkouts(uid).getOrDefault(emptyList())
            val cloudSessions = syncRepository.fetchWorkoutSessions(uid).getOrDefault(emptyList())
            val cloudGym = syncRepository.fetchGymExercises(uid).getOrDefault(emptyList())

            // 2. Guardar en Room localmente
            cloudProfile?.let { userProfileUseCase.updateUserProfileUseCase(it) }
            cloudWorkouts.forEach { workoutRepository.insertWorkout(it) }
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
