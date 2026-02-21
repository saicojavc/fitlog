package com.saico.core.network.repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.saico.core.domain.repository.SyncRepository
import com.saico.core.model.GymExercise
import com.saico.core.model.UserProfile
import com.saico.core.model.Workout
import com.saico.core.model.WorkoutSession
import kotlinx.coroutines.tasks.await
import java.sql.Time
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : SyncRepository {

    private val usersRef = database.getReference("users")

    override suspend fun syncUserProfile(uid: String, profile: UserProfile): Result<Unit> {
        return try {
            usersRef.child(uid).child("profile").setValue(profile).await()
            Log.d("FirebaseSync", "Perfil sincronizado")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncWorkout(uid: String, workout: Workout): Result<Unit> {
        return try {
            val workoutMap = mapOf(
                "steps" to workout.steps,
                "calories" to workout.calories,
                "distance" to workout.distance,
                "time" to workout.time.toString(),
                "date" to workout.date,
                "dayOfWeek" to workout.dayOfWeek
            )
            usersRef.child(uid).child("workouts").child(workout.date.toString()).setValue(workoutMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncWorkoutSession(uid: String, session: WorkoutSession): Result<Unit> {
        return try {
            val sessionMap = mapOf(
                "steps" to session.steps,
                "calories" to session.calories,
                "distance" to session.distance,
                "time" to session.time.toString(),
                "date" to session.date
            )
            usersRef.child(uid).child("workoutSessions").child(session.date.toString()).setValue(sessionMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncGymExercise(uid: String, exercise: GymExercise): Result<Unit> {
        return try {
            usersRef.child(uid).child("gymExercises").child(exercise.date.toString()).setValue(exercise).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadAllLocalData(
        uid: String,
        profile: UserProfile,
        workouts: List<Workout>,
        sessions: List<WorkoutSession>,
        gymExercises: List<GymExercise>
    ): Result<Unit> {
        return try {
            val dataMap = mutableMapOf<String, Any>()
            dataMap["profile"] = profile
            dataMap["workouts"] = workouts.associate { it.date.toString() to mapOf(
                "steps" to it.steps, "calories" to it.calories, "distance" to it.distance, "time" to it.time.toString(), "date" to it.date, "dayOfWeek" to it.dayOfWeek
            )}
            dataMap["workoutSessions"] = sessions.associate { it.date.toString() to mapOf(
                "steps" to it.steps, "calories" to it.calories, "distance" to it.distance, "time" to it.time.toString(), "date" to it.date
            )}
            dataMap["gymExercises"] = gymExercises.associateBy { it.date.toString() }

            usersRef.child(uid).updateChildren(dataMap).await()
            Log.d("FirebaseSync", "Sincronización masiva exitosa")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchUserProfile(uid: String): Result<UserProfile?> {
        return try {
            val snapshot = usersRef.child(uid).child("profile").get().await()
            Result.success(snapshot.getValue(UserProfile::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchWorkouts(uid: String): Result<List<Workout>> {
        return try {
            val snapshot = usersRef.child(uid).child("workouts").get().await()
            val list = snapshot.children.mapNotNull { child ->
                try {
                    Workout(
                        steps = child.child("steps").getValue(Int::class.java) ?: 0,
                        calories = child.child("calories").getValue(Int::class.java) ?: 0,
                        distance = child.child("distance").getValue(Double::class.java) ?: 0.0,
                        time = Time.valueOf(child.child("time").getValue(String::class.java) ?: "00:00:00"),
                        date = child.child("date").getValue(Long::class.java) ?: 0L,
                        dayOfWeek = child.child("dayOfWeek").getValue(String::class.java) ?: ""
                    )
                } catch (e: Exception) { null }
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchWorkoutSessions(uid: String): Result<List<WorkoutSession>> {
        return try {
            val snapshot = usersRef.child(uid).child("workoutSessions").get().await()
            val list = snapshot.children.mapNotNull { child ->
                try {
                    WorkoutSession(
                        steps = child.child("steps").getValue(Int::class.java) ?: 0,
                        calories = child.child("calories").getValue(Int::class.java) ?: 0,
                        distance = child.child("distance").getValue(Float::class.java) ?: 0f,
                        time = Time.valueOf(child.child("time").getValue(String::class.java) ?: "00:00:00"),
                        date = child.child("date").getValue(Long::class.java) ?: 0L
                    )
                } catch (e: Exception) { null }
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchGymExercises(uid: String): Result<List<GymExercise>> {
        return try {
            val snapshot = usersRef.child(uid).child("gymExercises").get().await()
            val list = snapshot.children.mapNotNull { it.getValue(GymExercise::class.java) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
