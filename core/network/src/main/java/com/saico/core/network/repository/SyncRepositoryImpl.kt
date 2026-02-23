package com.saico.core.network.repository

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.saico.core.common.util.FitnessCalculator
import com.saico.core.domain.repository.SyncRepository
import com.saico.core.model.GymExercise
import com.saico.core.model.UserProfile
import com.saico.core.model.Workout
import com.saico.core.model.WorkoutSession
import kotlinx.coroutines.tasks.await
import java.sql.Time
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
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
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncWorkout(uid: String, workout: Workout): Result<Unit> {
        if (workout.date <= 0L) return Result.success(Unit)
        return try {
            val workoutMap = mapOf(
                "steps" to workout.steps,
                "calories" to workout.calories,
                "distance" to workout.distance,
                "time" to formatTimeMillisToDuration(workout.time.time),
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
        if (session.date <= 0L) return Result.success(Unit)
        return try {
            val sessionMap = mapOf(
                "steps" to session.steps,
                "calories" to session.calories,
                "distance" to session.distance,
                "time" to formatTimeMillisToDuration(session.time.time),
                "date" to session.date
            )
            usersRef.child(uid).child("workoutSessions").child(session.date.toString()).setValue(sessionMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncGymExercise(uid: String, exercise: GymExercise): Result<Unit> {
        if (exercise.date <= 0L) return Result.success(Unit)
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
            dataMap["workouts"] = workouts.filter { it.date > 0 }.associate { it.date.toString() to mapOf(
                "steps" to it.steps, 
                "calories" to it.calories, 
                "distance" to it.distance, 
                "time" to formatTimeMillisToDuration(it.time.time), 
                "date" to it.date, 
                "dayOfWeek" to it.dayOfWeek
            )}
            dataMap["workoutSessions"] = sessions.filter { it.date > 0 }.associate { it.date.toString() to mapOf(
                "steps" to it.steps, 
                "calories" to it.calories, 
                "distance" to it.distance, 
                "time" to formatTimeMillisToDuration(it.time.time), 
                "date" to it.date
            )}
            dataMap["gymExercises"] = gymExercises.filter { it.date > 0 }.associateBy { it.date.toString() }

            usersRef.child(uid).updateChildren(dataMap).await()
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
                    val steps = child.child("steps").value?.let { (it as? Long)?.toInt() ?: (it as? Int) } ?: 0
                    val timeString = child.child("time").getValue(String::class.java)
                    
                    val timeMillis = parseTimeDurationToMillis(timeString)

                    Workout(
                        steps = steps,
                        calories = child.child("calories").value?.let { (it as? Long)?.toInt() ?: (it as? Int) } ?: 0,
                        distance = child.child("distance").value?.let { (it as? Double) ?: (it as? Long)?.toDouble() ?: (it as? Float)?.toDouble() } ?: 0.0,
                        time = if (timeMillis > 0) Time(timeMillis) else Time(FitnessCalculator.calculateActiveTimeMinutes(steps) * 60 * 1000L),
                        date = child.child("date").getValue(Long::class.java) ?: child.key?.toLong() ?: 0L,
                        dayOfWeek = child.child("dayOfWeek").getValue(String::class.java) ?: ""
                    )
                } catch (e: Exception) {
                    Log.e("FirebaseSync", "Error parseando workout: ${e.message}")
                    null
                }
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
                    val steps = child.child("steps").value?.let { (it as? Long)?.toInt() ?: (it as? Int) } ?: 0
                    val timeString = child.child("time").getValue(String::class.java)
                    
                    val timeMillis = parseTimeDurationToMillis(timeString)

                    WorkoutSession(
                        steps = steps,
                        calories = child.child("calories").value?.let { (it as? Long)?.toInt() ?: (it as? Int) } ?: 0,
                        distance = child.child("distance").value?.let { (it as? Float) ?: (it as? Double)?.toFloat() ?: (it as? Long)?.toFloat() } ?: 0f,
                        time = if (timeMillis > 0) Time(timeMillis) else Time(FitnessCalculator.calculateActiveTimeMinutes(steps) * 60 * 1000L),
                        date = child.child("date").getValue(Long::class.java) ?: child.key?.toLong() ?: 0L
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

    /**
     * Formatea milisegundos a HH:mm:ss tratando la entrada como una DURACIÓN pura,
     * eliminando cualquier offset de zona horaria que java.sql.Time haya aplicado.
     */
    private fun formatTimeMillisToDuration(millis: Long): String {
        // Obtenemos los campos usando UTC para ignorar la zona horaria del dispositivo
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.timeInMillis = millis
        
        val hours = cal.get(Calendar.HOUR_OF_DAY)
        val minutes = cal.get(Calendar.MINUTE)
        val seconds = cal.get(Calendar.SECOND)

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun parseTimeDurationToMillis(timeStr: String?): Long {
        if (timeStr == null || !timeStr.contains(":")) return 0L
        return try {
            val parts = timeStr.split(":")
            val h = parts[0].toLong()
            val m = parts[1].toLong()
            val s = parts[2].toLong()
            (h * 3600 + m * 60 + s) * 1000
        } catch (e: Exception) {
            0L
        }
    }
}
