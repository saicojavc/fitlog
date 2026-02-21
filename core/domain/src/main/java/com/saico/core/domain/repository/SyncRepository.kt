package com.saico.core.domain.repository

import com.saico.core.model.UserProfile
import com.saico.core.model.Workout
import com.saico.core.model.WorkoutSession
import com.saico.core.model.GymExercise

interface SyncRepository {
    // Subida
    suspend fun syncUserProfile(uid: String, profile: UserProfile): Result<Unit>
    suspend fun syncWorkout(uid: String, workout: Workout): Result<Unit>
    suspend fun syncWorkoutSession(uid: String, session: WorkoutSession): Result<Unit>
    suspend fun syncGymExercise(uid: String, exercise: GymExercise): Result<Unit>
    suspend fun uploadAllLocalData(uid: String, profile: UserProfile, workouts: List<Workout>, sessions: List<WorkoutSession>, gymExercises: List<GymExercise>): Result<Unit>

    // Descarga (Recuperación)
    suspend fun fetchUserProfile(uid: String): Result<UserProfile?>
    suspend fun fetchWorkouts(uid: String): Result<List<Workout>>
    suspend fun fetchWorkoutSessions(uid: String): Result<List<WorkoutSession>>
    suspend fun fetchGymExercises(uid: String): Result<List<GymExercise>>
}
