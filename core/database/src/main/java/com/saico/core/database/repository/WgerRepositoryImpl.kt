package com.saico.core.database.repository

import com.saico.core.database.dao.WgerExerciseDao
import com.saico.core.database.entity.WgerExerciseEntity
import com.saico.core.domain.repository.WgerRepository
import com.saico.core.model.DayRoutine
import com.saico.core.model.RoutineExercise
import com.saico.core.model.WgerExercise
import com.saico.core.model.WorkoutDay
import com.saico.core.network.api.WgerApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

import android.util.Log

@Singleton
class WgerRepositoryImpl @Inject constructor(
    private val apiService: WgerApiService,
    private val dao: WgerExerciseDao
) : WgerRepository {

    override fun getExercises(): Flow<List<WgerExercise>> {
        return dao.getAllExercises().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun preloadExercises() {
        val totalCount = dao.getExerciseCount()
        val withImages = dao.getExercisesWithImagesCount()
        Log.d("WgerPreload", "DB Status: Total=$totalCount, WithImages=$withImages")

        if (totalCount > 100 && withImages > 20) return // Already have some data

        try {
            Log.d("WgerPreload", "Starting API preloading...")
            // Fetch everything sequentially to be safer with timeouts and memory
            val exerciseResults = apiService.getExercises(limit = 200).results
            Log.d("WgerPreload", "Fetched ${exerciseResults.size} exercises")
            
            val imageResults = apiService.getImages(limit = 500).results
            Log.d("WgerPreload", "Fetched ${imageResults.size} images")
            
            val categoryResults = apiService.getCategories().results
            Log.d("WgerPreload", "Fetched ${categoryResults.size} categories")

            val entities = exerciseResults.map { dto ->
                val name = dto.name ?: "Ejercicio #${dto.id}"
                val matchId = dto.exerciseBase ?: dto.id
                val imageEntry = imageResults.find { it.exercise == matchId }
                val imageUrl = imageEntry?.image
                
                val categoryName = categoryResults.find { it.id == dto.category }?.name ?: "Otros"
                
                WgerExerciseEntity(
                    id = dto.id,
                    baseId = dto.exerciseBase,
                    name = name,
                    category = categoryName,
                    description = (dto.description ?: "").replace(Regex("<[^>]*>"), ""), 
                    imageUrl = imageUrl,
                    equipment = dto.equipment?.joinToString(", ")
                )
            }
            
            Log.d("WgerPreload", "Saving ${entities.size} exercises to DB. With images: ${entities.count { it.imageUrl != null }}")
            dao.insertExercises(entities)
        } catch (e: Exception) {
            Log.e("WgerPreload", "Preload failed: ${e.message}", e)
            // No crash, just log it. We'll try again next time.
        }
    }

    override suspend fun getRoutineForDay(day: WorkoutDay): DayRoutine {
        val routine = when (day) {
            WorkoutDay.MONDAY -> DayRoutine(day, "Empuje (Pecho, Hombro, Tríceps)", listOf(
                createRoutineExercise(73, "Press de Banca", 4, "10-12"),
                createRoutineExercise(567, "Press Militar", 3, "12"),
                createRoutineExercise(197, "Fondos / Dips", 3, "15")
            ))
            WorkoutDay.TUESDAY -> DayRoutine(day, "Jale (Espalda, Biceps)", listOf(
                createRoutineExercise(84, "Remo con Barra", 4, "10"),
                createRoutineExercise(394, "Remo Sentado", 3, "12"),
                createRoutineExercise(92, "Curl de Biceps", 3, "12")
            ))
            WorkoutDay.WEDNESDAY -> DayRoutine(day, "Piernas y Abdomen", listOf(
                createRoutineExercise(191, "Sentadillas", 4, "8-10"),
                createRoutineExercise(366, "Leg Curl", 3, "12"),
                createRoutineExercise(167, "Abdominales", 3, "20")
            ))
            WorkoutDay.THURSDAY -> DayRoutine(day, "Push B (Enfoque Pecho)", listOf(
                createRoutineExercise(105, "Press Inclinado", 4, "10"),
                createRoutineExercise(127, "Cruce de Poleas", 4, "15"),
                createRoutineExercise(246, "Tríceps Polea", 3, "12")
            ))
            WorkoutDay.FRIDAY -> DayRoutine(day, "Pull B (Enfoque Espalda)", listOf(
                createRoutineExercise(484, "Peso Muerto", 3, "5"),
                createRoutineExercise(513, "Remo T", 4, "12"),
                createRoutineExercise(272, "Martillo", 3, "12")
            ))
            else -> DayRoutine(day, "Descanso Activo / Cardio", listOf(
                createRoutineExercise(377, "Elevación de Piernas", 3, "15"),
                createRoutineExercise(171, "Crunch Declinado", 3, "20")
            ))
        }

        // Enrich with real images from database
        val enrichedExercises = routine.exercises.map { exercise ->
            // Try by ID first, then by Base ID (since routines might use either)
            val localExercise = dao.getExerciseById(exercise.exerciseId) 
                ?: dao.getExerciseByBaseId(exercise.exerciseId)
                
            exercise.copy(
                name = localExercise?.name ?: exercise.name, // Use translated name if available
                imageUrl = localExercise?.imageUrl
            )
        }

        return routine.copy(exercises = enrichedExercises)
    }

    private fun createRoutineExercise(id: Int, name: String, sets: Int, reps: String) = RoutineExercise(
        exerciseId = id,
        name = name,
        imageUrl = null,
        targetSets = sets,
        targetReps = reps
    )

    private fun WgerExerciseEntity.toDomain() = WgerExercise(
        id = id,
        name = name,
        category = category,
        description = description,
        imageUrl = imageUrl,
        equipment = equipment
    )
}
