package com.saico.core.database.repository

import com.saico.core.database.dao.CustomRoutineDao
import com.saico.core.database.dao.WgerExerciseDao
import com.saico.core.database.entity.CustomRoutineEntity
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
    private val dao: WgerExerciseDao,
    private val customRoutineDao: CustomRoutineDao
) : WgerRepository {

    override fun getExercises(): Flow<List<WgerExercise>> {
        return dao.getAllExercises().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun preloadExercises() {
        val totalCount = dao.getExerciseCount()
        val nameFailures = dao.getExerciseCountByNamePrefix("Ejercicio #")
        Log.d("WgerPreload", "DB Status: Total=$totalCount, NameFailures=$nameFailures")

        // Force a total refresh if names are bad or count is low
        if (totalCount > 400 && nameFailures == 0) return 

        try {
            Log.d("WgerPreload", "CRITICAL REFRESH: Clearing DB and fetching from EXERCISEINFO...")
            dao.deleteAllExercises() // Clear old bad data
            
            // 1. Fetch English baseline (Best name availability and all images)
            val englishResults = apiService.getExercisesInfo(language = 1, limit = 500).results
            Log.d("WgerPreload", "Fetched ${englishResults.size} English exercises")
            
            // 2. Fetch Spanish translations (Overwrite names where available)
            val spanishResults = apiService.getExercisesInfo(language = 2, limit = 500).results
            Log.d("WgerPreload", "Fetched ${spanishResults.size} Spanish exercises")
            
            val spanishNamesMap = spanishResults.associateBy({ it.id }, { it.name })

            val entities = englishResults.map { dto ->
                // Priority: Spanish Translated Name -> English Name -> Generic
                val name = spanishNamesMap[dto.id]?.takeIf { it.isNotBlank() } ?: dto.name ?: "Ejercicio #${dto.id}"
                
                // Images: wger provides images in exerciseinfo objects
                var imageUrl = dto.images?.firstOrNull { it.isMain }?.image 
                    ?: dto.images?.firstOrNull()?.image
                
                // Fallback for missing images: Generic high-quality gym photo (Unsplash)
                if (imageUrl == null) {
                    imageUrl = "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?auto=format&fit=crop&w=500"
                    Log.d("WgerPreload", "Image fallback for $name")
                }

                val rawCategory = dto.category?.name ?: "Otros"
                val categoryName = when {
                    rawCategory.contains("Chest", true) || rawCategory.contains("Pecho", true) -> "Pecho"
                    rawCategory.contains("Back", true) || rawCategory.contains("Espalda", true) -> "Espalda"
                    rawCategory.contains("Shoulders", true) || rawCategory.contains("Hombros", true) -> "Hombros"
                    rawCategory.contains("Triceps", true) || rawCategory.contains("Tríceps", true) -> "Tríceps"
                    rawCategory.contains("Biceps", true) || rawCategory.contains("Bíceps", true) -> "Bíceps"
                    rawCategory.contains("Abs", true) || rawCategory.contains("Abdominales", true) -> "Abdominales"
                    rawCategory.contains("Legs", true) || rawCategory.contains("Piernas", true) -> "Piernas"
                    else -> rawCategory
                }
                
                WgerExerciseEntity(
                    id = dto.id,
                    baseId = dto.id,
                    name = name,
                    category = categoryName,
                    description = (dto.description ?: "").replace(Regex("<[^>]*>"), ""), 
                    imageUrl = imageUrl,
                    equipment = null
                )
            }
            
            Log.d("WgerPreload", "Saving ${entities.size} finalized exercises to DB.")
            dao.insertExercises(entities)
        } catch (e: Exception) {
            Log.e("WgerPreload", "Preload failed: ${e.message}", e)
        }
    }

    override suspend fun getRoutineForDay(day: WorkoutDay): DayRoutine {
        val customRoutine = getCustomRoutine(day)
        if (customRoutine != null) return customRoutine

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

        val enrichedExercises = routine.exercises.map { exercise ->
            val localExercise = dao.getExerciseById(exercise.exerciseId) 
                ?: dao.getExerciseByBaseId(exercise.exerciseId)
                
            exercise.copy(
                name = localExercise?.name ?: exercise.name,
                imageUrl = localExercise?.imageUrl
            )
        }

        return routine.copy(exercises = enrichedExercises)
    }

    override suspend fun saveCustomRoutine(routine: DayRoutine) {
        customRoutineDao.insertRoutine(
            CustomRoutineEntity(
                dayOfWeek = routine.dayOfWeek.name,
                title = routine.title,
                exercises = routine.exercises
            )
        )
    }

    override suspend fun getCustomRoutine(day: WorkoutDay): DayRoutine? {
        val entity = customRoutineDao.getRoutineForDay(day.name) ?: return null
        
        val enrichedExercises = entity.exercises.map { exercise ->
            val localExercise = dao.getExerciseById(exercise.exerciseId) 
                ?: dao.getExerciseByBaseId(exercise.exerciseId)
                
            exercise.copy(
                name = localExercise?.name ?: exercise.name,
                imageUrl = localExercise?.imageUrl
            )
        }

        return DayRoutine(
            dayOfWeek = day,
            title = entity.title,
            exercises = enrichedExercises
        )
    }

    override suspend fun deleteCustomRoutine(day: WorkoutDay) {
        customRoutineDao.deleteRoutineForDay(day.name)
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
