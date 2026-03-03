package com.saico.feature.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.saico.core.model.Workout
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.navigation.routes.stepshistory.StepsHistoryRoute
import com.saico.core.ui.theme.PaddingDim
import com.saico.core.ui.theme.techBlue
import java.util.Calendar
import java.util.Locale

@Composable
fun WeeklyActivityCard(
    workouts: List<Workout>,
    dailySteps: Int,
    dailyStepsGoal: Int,
    navController: NavHostController
) {

    // 1. Procesar datos (Lógica optimizada)
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
        val dayName =
            cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())?.take(1)
                ?: ""

        ChartData(dayName, steps.toFloat(), isToday)
    }

    val maxSteps = weeklyData.maxOfOrNull { it.value }?.takeIf { it > 0 } ?: 1000f

    // 2. Diseño de la Card con Estética Cyber-Blue
    FitlogCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingDim.LARGE, vertical = PaddingDim.SMALL)
            .clickable { navController.navigate(StepsHistoryRoute.RootRoute.route) },
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
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
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = FitlogIcons.ChevronRight,
                    contentDescription = null,
                    tint = techBlue.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Contenedor de las barras con efecto de profundidad
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                weeklyData.forEach { data ->
                    WeeklyBarItem(
                        item = data,
                        maxValue = maxSteps,
                        accentColor = techBlue,
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
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val barHeightRatio = (item.value / maxValue).coerceIn(0.05f, 1f)

    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Valor numérico (Solo si hay datos o es hoy)
        if (item.value > 0 || item.isHighlighted) {
            Text(
                text = if (item.value >= 1000) "${(item.value / 1000).toInt()}k" else item.value.toInt()
                    .toString(),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = if (item.isHighlighted) Color.White else Color.White.copy(alpha = 0.3f),
                fontWeight = if (item.isHighlighted) FontWeight.Black else FontWeight.Normal,
                fontFamily = FontFamily.Monospace // Look digital
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Barra Estilo Cápsula Neón
        Box(
            modifier = Modifier
                .fillMaxHeight(barHeightRatio * 0.7f)
                .width(10.dp)
                .shadow(
                    elevation = if (item.isHighlighted) 8.dp else 0.dp,
                    shape = CircleShape,
                    spotColor = accentColor
                )
                .clip(CircleShape)
                .background(
                    if (item.isHighlighted)
                        Brush.verticalGradient(listOf(accentColor, accentColor.copy(alpha = 0.4f)))
                    else SolidColor(Color.White.copy(alpha = 0.08f)) // Barra inactiva muy sutil
                )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Inicial del día
        Text(
            text = item.label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = if (item.isHighlighted) accentColor else Color(0xFF94A3B8).copy(alpha = 0.5f),
            fontWeight = if (item.isHighlighted) FontWeight.Black else FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

data class ChartData(
    val label: String,      // Ej: "M", "T", "W" o "12-18"
    val value: Float,       // El valor numérico (pasos, kcal, etc.)
    val isHighlighted: Boolean = false // Para saber si es el día actual o el seleccionado
)