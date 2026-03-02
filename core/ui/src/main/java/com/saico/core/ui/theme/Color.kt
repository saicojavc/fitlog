package com.saico.core.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Light Mode
val LightPrimary = Color(0xFF0F172A)
val LightPrimaryVariant = Color(0xFF002851)
val LightSuccess = Color(0xFF10B981)
val LightBackground = Color(0xFFF0F0F0)
val LightSurface = Color(0xFFFFFFFF)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightOnBackground = Color(0xFF000000)
val LightSecondaryText = Color(0xFF666666)
val LightInfo = Color(0xFFE3F2FD) // A light blue color for info cards

val DarkBackground = Color(0xFF0F172A)
val CardBackground = Color(0xFF1E293B).copy(alpha = 0.6f)
val EmeraldGreen = Color(0xFF10B981)
val CoolGray = Color(0xFF94A3B8)

val GradientColors = listOf( Color(0xFF020509), Color(0xFF0D1424), Color(0xFF16223B))
val BottomColor = Brush.horizontalGradient(listOf(Color(0xFF3FB9F6), Color(0xFF216EE0)))

//val NightBlueGradient = Brush.verticalGradient(
//    colors = listOf(
//        Color(0xFF020509), // Negro azulado profundo
//        Color(0xFF0D1424), // Azul Oxford oscuro
//        Color(0xFF16223B)  // Azul medianoche sutil
//    )
//)

// Dark Mode
val DarkPrimary = Color(0xFF004C99)
val DarkPrimaryVariant = Color(0xFF003366)
val DarkSuccess = Color(0xFF00A351)
//val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkOnPrimary = Color(0xFFFFFFFF)
val DarkOnBackground = Color(0xFFE0E0E0)
val DarkSecondaryText = Color(0xFFAAAAAA)
val DarkInfo = Color(0xFF2C3E50) // A dark blue-gray color for info cards

// Common Colors
val CommonError = Color(0xFFFF4444)
val CommonWarning = Color(0xFFFFBB33)
