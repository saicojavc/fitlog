package com.saico.core.common.util

import com.saico.core.model.UnitsConfig
import java.util.Locale

/**
 * Utilidad para la conversión y formateo de unidades de medida (Métrico vs Imperial).
 */
object UnitsConverter {

    // Factores de conversión
    private const val KM_TO_MI = 0.621371
    private const val KG_TO_LB = 2.20462
    private const val CM_TO_IN = 0.393701
    private const val FT_TO_IN = 12

    // --- Métodos de Conversión Pura ---

    fun kmToMi(km: Double): Double = km * KM_TO_MI
    fun miToKm(mi: Double): Double = mi / KM_TO_MI

    fun kgToLb(kg: Double): Double = kg * KG_TO_LB
    fun lbToKg(lb: Double): Double = lb / KG_TO_LB

    fun cmToIn(cm: Double): Double = cm * CM_TO_IN
    fun inToCm(inches: Double): Double = inches / CM_TO_IN

    /**
     * Convierte CM a Pies y Pulgadas.
     */
    fun cmToFtIn(cm: Double): Pair<Int, Int> {
        val totalInches = cmToIn(cm)
        val feet = (totalInches / FT_TO_IN).toInt()
        val inches = (totalInches % FT_TO_IN).toInt()
        return Pair(feet, inches)
    }

    /**
     * Convierte Pies y Pulgadas a CM.
     */
    fun ftInToCm(feet: Int, inches: Int): Double {
        val totalInches = (feet * FT_TO_IN) + inches
        return inToCm(totalInches.toDouble())
    }

    /**
     * Convierte y formatea la distancia.
     * @param km Distancia en kilómetros.
     * @param units Sistema de unidades actual.
     * @return String formateado (ej: "5.20 km" o "3.23 mi").
     */
    fun formatDistance(km: Double, units: UnitsConfig): String {
        return if (units == UnitsConfig.METRIC) {
            String.format(Locale.getDefault(), "%.2f km", km)
        } else {
            val mi = kmToMi(km)
            String.format(Locale.getDefault(), "%.2f mi", mi)
        }
    }

    /**
     * Convierte y formatea el peso.
     * @param kg Peso en kilogramos.
     * @param units Sistema de unidades actual.
     * @return String formateado (ej: "70.5 kg" o "155.4 lb").
     */
    fun formatWeight(kg: Double, units: UnitsConfig): String {
        return if (units == UnitsConfig.METRIC) {
            String.format(Locale.getDefault(), "%.1f kg", kg)
        } else {
            val lb = kgToLb(kg)
            String.format(Locale.getDefault(), "%.1f lb", lb)
        }
    }

    /**
     * Convierte y formatea la altura.
     * @param cm Altura en centímetros.
     * @param units Sistema de unidades actual.
     * @return String formateado (ej: "175 cm" o "5 ft 9 in").
     */
    fun formatHeight(cm: Double, units: UnitsConfig): String {
        return if (units == UnitsConfig.METRIC) {
            String.format(Locale.getDefault(), "%.0f cm", cm)
        } else {
            val (feet, inches) = cmToFtIn(cm)
            "$feet ft $inches in"
        }
    }

    /**
     * Convierte y formatea el ritmo (pace).
     * @param minPerKm Ritmo en minutos por kilómetro.
     * @param units Sistema de unidades actual.
     * @return String formateado (ej: "5:30 min/km" o "8:51 min/mi").
     */
    fun formatPace(minPerKm: Double, units: UnitsConfig): String {
        if (minPerKm <= 0) return "--:--"
        
        return if (units == UnitsConfig.METRIC) {
            val minutes = minPerKm.toInt()
            val seconds = ((minPerKm - minutes) * 60).toInt()
            String.format(Locale.getDefault(), "%d:%02d min/km", minutes, seconds)
        } else {
            val minPerMi = minPerKm * (1 / KM_TO_MI)
            val minutes = minPerMi.toInt()
            val seconds = ((minPerMi - minutes) * 60).toInt()
            String.format(Locale.getDefault(), "%d:%02d min/mi", minutes, seconds)
        }
    }
}
