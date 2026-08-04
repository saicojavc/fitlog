package com.saico.core.database.repository

import com.saico.core.database.dao.WgerExerciseDao
import com.saico.core.database.entity.WgerExerciseEntity
import com.saico.core.domain.repository.WgerRepository
import com.saico.core.model.DayRoutine
import com.saico.core.model.RoutineExercise
import com.saico.core.model.WgerExercise
import com.saico.core.model.WorkoutDay
import com.saico.core.network.api.WgerApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

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
        if (dao.getExerciseCount() > 0) return

        coroutineScope {
            val exercisesDeferred = async { apiService.getExercises(limit = 100) }
            val imagesDeferred = async { apiService.getImages(limit = 100) }
            val categoriesDeferred = async { apiService.getCategories() }

            try {
                val exerciseResults = exercisesDeferred.await().results
                val imageResults = imagesDeferred.await().results
                val categoryResults = categoriesDeferred.await().results

                val entities = exerciseResults.mapNotNull { dto ->
                    val name = dto.name ?: return@mapNotNull null
                    val imageUrl = imageResults.find { it.exerciseBase == dto.id }?.image
                    val categoryName = categoryResults.find { it.id == dto.category }?.name ?: "Otros"
                    
                    WgerExerciseEntity(
                        id = dto.id,
                        name = name,
                        category = categoryName,
                        description = (dto.description ?: "").replace(Regex("<[^>]*>"), ""), // Clean HTML
                        imageUrl = imageUrl,
                        equipment = dto.equipment?.joinToString(", ")
                    )
                }
                dao.insertExercises(entities)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getRoutineForDay(day: WorkoutDay): DayRoutine {
        return when (day) {
            WorkoutDay.MONDAY -> DayRoutine(day, "Empuje (Pecho, Hombro, Tríceps)", listOf(
                RoutineExercise(1, "Press de Banca", null, 4, "10-12"),
                RoutineExercise(2, "Press Militar", null, 3, "12"),
                RoutineExercise(3, "Extensiones de Tríceps", null, 3, "15")
            ))
            WorkoutDay.TUESDAY -> DayRoutine(day, "Jale (Espalda, Biceps)", listOf(
                RoutineExercise(4, "Dominadas", null, 4, "Fallo"),
                RoutineExercise(5, "Remo con Barra", null, 3, "10"),
                RoutineExercise(6, "Curl de Biceps", null, 3, "12")
            ))
            WorkoutDay.WEDNESDAY -> DayRoutine(day, "Piernas y Abdomen", listOf(
                RoutineExercise(7, "Sentadillas", null, 4, "8-10"),
                RoutineExercise(8, "Prensa de Piernas", null, 3, "12"),
                RoutineExercise(9, "Elevación de Piernas", null, 3, "20")
            ))
            WorkoutDay.THURSDAY -> DayRoutine(day, "Push B (Enfoque Hombro)", listOf(
                RoutineExercise(10, "Press Inclinado", null, 4, "10"),
                RoutineExercise(11, "Elevaciones Laterales", null, 4, "15"),
                RoutineExercise(12, "Fondos", null, 3, "12")
            ))
            WorkoutDay.FRIDAY -> DayRoutine(day, "Pull B (Enfoque Espalda)", listOf(
                RoutineExercise(13, "Peso Muerto", null, 3, "5"),
                RoutineExercise(14, "Jalón al Pecho", null, 4, "12"),
                RoutineExercise(15, "Martillo", null, 3, "12")
            ))
            else -> DayRoutine(day, "Descanso Activo / Cardio", listOf(
                RoutineExercise(16, "Caminata Ligera", null, 1, "30 min"),
                RoutineExercise(17, "Estiramientos", null, 1, "15 min")
            ))
        }
    }

    private fun WgerExerciseEntity.toDomain() = WgerExercise(
        id = id,
        name = name,
        category = category,
        description = description,
        imageUrl = imageUrl,
        equipment = equipment
    )
}
