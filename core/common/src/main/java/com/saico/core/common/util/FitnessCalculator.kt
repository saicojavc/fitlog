package com.saico.core.common.util


import kotlin.math.roundToInt

object FitnessCalculator {

    private const val AVG_STEPS_PER_MINUTE = 100.0
    private const val MET_WALKING = 3.5 // Metabolic Equivalent of Task for walking

    /**
     * Calcula las calorías quemadas durante una caminata.
     *
     * @param steps El número de pasos dados.
     * @param weightKg El peso del usuario en kilogramos.
     * @return Las calorías estimadas quemadas (Kcal).
     */
    fun calculateCaloriesBurned(steps: Int, weightKg: Double): Int {
        if (steps <= 0 || weightKg <= 0) return 0
        val durationMinutes = steps / AVG_STEPS_PER_MINUTE
        val caloriesPerMinute = (MET_WALKING * weightKg * 3.5) / 200
        return (caloriesPerMinute * durationMinutes).roundToInt()
    }

    /**
     * Estima la distancia recorrida en kilómetros.
     *
     * @param steps El número de pasos dados.
     * @param heightCm La altura del usuario en centímetros.
     * @param genderString El género del usuario como String (ej. "Male", "Female").
     * @return La distancia estimada en kilómetros (KM).
     */
    fun calculateDistanceKm(steps: Int, heightCm: Int, genderString: String): Float {
        if (steps <= 0 || heightCm <= 0) return 0f
        val strideLengthCm = when (genderString.lowercase()) {
            "male" -> heightCm * 0.415
            "female" -> heightCm * 0.413
            else -> heightCm * 0.414 // Un promedio si no se especifica o es "Other"
        }
        val distanceMeters = (steps * strideLengthCm) / 100
        return (distanceMeters / 1000).toFloat()
    }

    /**
     * Estima el tiempo de actividad en minutos.
     *
     * @param steps El número de pasos dados.
     * @return El tiempo estimado de actividad en minutos.
     */
    fun calculateActiveTimeMinutes(steps: Int): Int {
        if (steps <= 0) return 0
        return (steps / AVG_STEPS_PER_MINUTE).roundToInt()
    }
}
