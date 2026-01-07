package com.saico.feature.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.saico.core.model.Workout
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.theme.PaddingDim
import java.util.Calendar
import java.util.Locale

@Composable
fun WeeklyActivityCard(workouts: List<Workout>) {
    FitlogCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingDim.SMALL)
    ) {
        Column(
            modifier = Modifier.padding(PaddingDim.MEDIUM)
        ) {
            Text("Actividad Semanal", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(PaddingDim.MEDIUM))

            // 1. Ponemos los workouts en un mapa para buscarlos fácilmente por fecha.
            val workoutsByDate = workouts.associateBy { workout ->
                val cal = Calendar.getInstance().apply { timeInMillis = workout.date }
                // Creamos una clave única para cada día (ej. "2023-10-27")
                "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}-${cal.get(Calendar.DAY_OF_MONTH)}"
            }

            val maxSteps = workouts.maxOfOrNull { it.steps } ?: 1

            // 2. Fila con altura fija para el gráfico.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp), // Altura fija para el área del gráfico
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // 3. Iteramos sobre los últimos 7 días, desde hace 6 días hasta hoy.
                for (i in 6 downTo 0) {
                    val calendar = Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_YEAR, -i)
                    }

                    val dateKey = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}-${calendar.get(Calendar.DAY_OF_MONTH)}"
                    val dayInitial = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())?.take(1) ?: "?"
                    
                    // Buscamos el workout del día. Si no existe, los pasos son 0.
                    val stepsForDay = workoutsByDate[dateKey]?.steps ?: 0

                    Bar(day = dayInitial, steps = stepsForDay, maxSteps = maxSteps)
                }
            }
        }
    }
}

@Composable
private fun Bar(day: String, steps: Int, maxSteps: Int) {
    // La altura se calcula en proporción al día con más pasos de la semana
    val barHeight = (steps.toFloat() / maxSteps.toFloat() * 100).coerceIn(0f, 100f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        // La altura de la columna contenedora es fija para alinear las barras desde abajo
        modifier = Modifier.height(120.dp)
    ) {
        Box(
            modifier = Modifier
                .height(barHeight.dp)
                .width(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = day,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}
