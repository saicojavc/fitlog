package com.saico.feature.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.saico.core.model.Workout
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.navigation.routes.stepshistory.StepsHistoryRoute
import com.saico.core.ui.theme.PaddingDim
import java.util.Calendar
import java.util.Locale

@Composable
fun WeeklyActivityCard(
    workouts: List<Workout>,
    dailySteps: Int,
    dailyStepsGoal: Int,
    navController: NavHostController
) {
    val targetSteps = if (dailyStepsGoal > 0) dailyStepsGoal else 10000
    val maxScaleSteps = targetSteps * 2

    FitlogCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp) // PaddingDim.SMALL
            .clickable {
                navController.navigate(StepsHistoryRoute.RootRoute.route)
            },
        shape = RoundedCornerShape(24.dp) // Bordes más redondeados
    ) {
        Column(
            modifier = Modifier.padding(16.dp) // PaddingDim.MEDIUM
        ) {
            Text(
                text = stringResource(id = R.string.weekly_activity),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White // Texto principal siempre blanco
            )
            Spacer(modifier = Modifier.height(16.dp))

            val workoutsByDate = workouts.associateBy { workout ->
                val cal = Calendar.getInstance().apply { timeInMillis = workout.date }
                "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}-${cal.get(Calendar.DAY_OF_MONTH)}"
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp) // Un poco más alto para mejor visualización
            ) {
                // Líneas de escala de fondo
                ScaleBackground(targetSteps = targetSteps)

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    for (i in 6 downTo 0) {
                        val calendar = Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_YEAR, -i)
                        }

                        val dayInitial = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())?.take(1) ?: "?"
                        val isToday = i == 0

                        val stepsForDay = if (isToday) {
                            dailySteps
                        } else {
                            val dateKey = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}-${calendar.get(Calendar.DAY_OF_MONTH)}"
                            workoutsByDate[dateKey]?.steps ?: 0
                        }

                        Bar(
                            day = dayInitial,
                            steps = stepsForDay,
                            maxScaleSteps = maxScaleSteps,
                            isToday = isToday
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScaleBackground(targetSteps: Int) {
    // Usamos el Cool Gray para las líneas con baja opacidad
    val lineColor = Color(0xFF94A3B8).copy(alpha = 0.15f)
    val textColor = Color(0xFF94A3B8)

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasHeight = size.height - 40.dp.toPx()

            // Dibujamos líneas en 0%, 50% y 100% de la escala
            val heights = listOf(0f, 0.5f, 0.75f)

            heights.forEach { hRatio ->
                val y = hRatio * canvasHeight
                drawLine(
                    color = lineColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxHeight().padding(bottom = 40.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "${targetSteps * 2}", fontSize = 9.sp, color = textColor)
            Text(text = "$targetSteps", fontSize = 9.sp, color = textColor)
            Text(text = "0", fontSize = 9.sp, color = textColor)
        }
    }
}

@Composable
private fun Bar(day: String, steps: Int, maxScaleSteps: Int, isToday: Boolean) {
    val barMaxHeight = 140.dp
    // Calculamos la altura. CoerceIn asegura que siempre haya una pequeña marca aunque sea 0
    val barHeightRatio = (steps.toFloat() / maxScaleSteps.toFloat()).coerceIn(0.02f, 1f)

    // Color Emerald Green para hoy, y una versión más apagada para los otros días
    val activeColor = Color(0xFF10B981)
    val inactiveColor = Color(0xFF10B981).copy(alpha = 0.3f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxHeight()
    ) {
        Box(
            modifier = Modifier
                .height(barMaxHeight * barHeightRatio)
                .width(28.dp) // Más anchas para look moderno
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                .background(if (isToday) activeColor else inactiveColor)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    color = if (isToday) activeColor else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (isToday) Color(0xFF0F172A) else Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )
        }
    }
}