package com.saico.feature.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saico.core.model.Workout
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.theme.PaddingDim
import java.util.Calendar
import java.util.Locale

@Composable
fun WeeklyActivityCard(
    workouts: List<Workout>,
    dailySteps: Int,
    dailyStepsGoal: Int) {
    val targetSteps = if (dailyStepsGoal > 0) dailyStepsGoal else 10000
    val maxScaleSteps = targetSteps * 2

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

            val workoutsByDate = workouts.associateBy { workout ->
                val cal = Calendar.getInstance().apply { timeInMillis = workout.date }
                "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}-${cal.get(Calendar.DAY_OF_MONTH)}"
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                // Background Scale Lines
                ScaleBackground(targetSteps = targetSteps)

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
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
    val lineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    val textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasHeight = size.height - 30.dp.toPx() // Account for day label height
            
            // Positions: 200% (top), 100% (middle), 50% (quarter from bottom)
            val heights = listOf(0f, 0.5f, 0.75f) 
            
            heights.forEach { hRatio ->
                val y = hRatio * canvasHeight
                drawLine(
                    color = lineColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            }
        }
        
        // Labels
        Column(
            modifier = Modifier.fillMaxHeight().padding(bottom = 30.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "${targetSteps * 2}", fontSize = 8.sp, color = textColor)
            Text(text = "$targetSteps", fontSize = 8.sp, color = textColor)
            Text(text = "${targetSteps / 2}", fontSize = 8.sp, color = textColor)
        }
    }
}

@Composable
private fun Bar(day: String, steps: Int, maxScaleSteps: Int, isToday: Boolean) {
    val barMaxHeight = 120.dp
    val barHeightRatio = (steps.toFloat() / maxScaleSteps.toFloat()).coerceIn(0.05f, 1f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxHeight()
    ) {
        Box(
            modifier = Modifier
                .height(barMaxHeight * barHeightRatio)
                .width(18.dp)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(
                    if (isToday) MaterialTheme.colorScheme.primary 
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
        )
        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .size(22.dp)
                .background(
                    color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day,
                style = MaterialTheme.typography.labelSmall,
                color = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}
