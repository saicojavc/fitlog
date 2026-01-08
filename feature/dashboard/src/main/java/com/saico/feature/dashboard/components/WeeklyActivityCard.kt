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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.saico.core.model.Workout
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.theme.PaddingDim
import java.util.Calendar
import java.util.Locale

@Composable
fun WeeklyActivityCard(workouts: List<Workout>, dailySteps: Int) {
    FitlogCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingDim.SMALL),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier.padding(PaddingDim.MEDIUM)
        ) {
            Text(
                text = stringResource(id = R.string.weekly_activity),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(PaddingDim.MEDIUM))

            val maxSteps = (workouts.map { it.steps } + dailySteps).maxOrNull()?.coerceAtLeast(1) ?: 1

            val workoutsByDate = workouts.associateBy { workout ->
                val cal = Calendar.getInstance().apply { timeInMillis = workout.date }
                "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}-${cal.get(Calendar.DAY_OF_MONTH)}"
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
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

                    Bar(day = dayInitial, steps = stepsForDay, maxSteps = maxSteps, isToday = isToday)
                }
            }
        }
    }
}

@Composable
private fun Bar(day: String, steps: Int, maxSteps: Int, isToday: Boolean) {
    val barHeight = (steps.toFloat() / maxSteps.toFloat() * 100).coerceIn(0f, 100f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
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

        // --- CORRECCIÓN AQUÍ ---
        Box(
            modifier = Modifier
                .size(24.dp) // 1. Damos un tamaño fijo al contenedor
                .background( // 2. Aplicamos el fondo condicionalmente
                    color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center // 3. Centramos el texto dentro
        ) {
            Text(
                text = day,
                style = MaterialTheme.typography.bodySmall,
                color = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}
