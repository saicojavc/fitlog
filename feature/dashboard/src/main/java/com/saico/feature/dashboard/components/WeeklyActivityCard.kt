package com.saico.feature.dashboard.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
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
import com.saico.core.ui.icon.FitlogIcons
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
    // 1. Procesar datos para obtener los pasos de los últimos 7 días
    val calendar = Calendar.getInstance()
    val workoutsByDate = workouts.associateBy { workout ->
        val cal = Calendar.getInstance().apply { timeInMillis = workout.date }
        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
    }

    val weeklyData = (6 downTo 0).map { i ->
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
        val dateKey = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
        val isToday = i == 0
        val steps = if (isToday) dailySteps else workoutsByDate[dateKey]?.steps ?: 0
        val dayName = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())?.take(1) ?: ""

        ChartData(dayName, steps.toFloat(), isToday)
    }

    val maxSteps = weeklyData.maxOfOrNull { it.value }?.takeIf { it > 0 } ?: 1000f

    // 2. Diseño de la Card con Estética Premium
    FitlogCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingDim.LARGE, vertical = PaddingDim.SMALL)
            .clickable { navController.navigate(StepsHistoryRoute.RootRoute.route) },
        color = Color(0xFF1E293B).copy(alpha = 0.6f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.weekly_activity).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.5.sp
                )
                Icon(
                    imageVector = FitlogIcons.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Contenedor de las barras
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                weeklyData.forEach { data ->
                    WeeklyBarItem(
                        item = data,
                        maxValue = maxSteps,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyBarItem(
    item: ChartData,
    maxValue: Float,
    modifier: Modifier = Modifier
) {
    val barHeightRatio = (item.value / maxValue).coerceIn(0.05f, 1f)
    val accentColor = Color(0xFF10B981)

    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Valor numérico encima de la barra (Solo si hay pasos o es hoy)
        if (item.value > 0 || item.isHighlighted) {
            Text(
                text = if (item.value >= 1000) "${(item.value / 1000).toInt()}k" else item.value.toInt().toString(),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = if (item.isHighlighted) Color.White else Color(0xFF94A3B8),
                fontWeight = if (item.isHighlighted) FontWeight.Bold else FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Barra Estilo Cápsula
        Box(
            modifier = Modifier
                .fillMaxHeight(barHeightRatio * 0.75f)
                .width(10.dp) // Grosor elegante
                .clip(CircleShape) // Cápsula total
                .background(
                    if (item.isHighlighted) accentColor
                    else accentColor.copy(alpha = 0.2f)
                )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Inicial del día
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (item.isHighlighted) accentColor else Color(0xFF94A3B8).copy(alpha = 0.6f),
            fontWeight = if (item.isHighlighted) FontWeight.Black else FontWeight.Normal
        )
    }
}
data class ChartData(
    val label: String,      // Ej: "M", "T", "W" o "12-18"
    val value: Float,       // El valor numérico (pasos, kcal, etc.)
    val isHighlighted: Boolean = false // Para saber si es el día actual o el seleccionado
)