package com.saico.feature.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.saico.core.common.util.FitnessCalculator
import com.saico.core.common.util.UnitsConverter
import com.saico.core.model.UnitsConfig
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.navigation.routes.stepshistory.StepsHistoryRoute
import com.saico.core.ui.theme.DarkPrimary
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.dashboard.state.DashboardUiState
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit


@Composable
fun StepsDailyCard(uiState: DashboardUiState, navController: NavHostController) {
    val userProfile = uiState.userProfile
    val dailySteps = uiState.dailySteps
    val dailyStepsGoal = (userProfile?.dailyStepsGoal ?: 1).toFloat()
    val units = uiState.userData?.unitsConfig ?: UnitsConfig.METRIC

    val haptic = LocalHapticFeedback.current

    // Cálculos de Fitness
    val calories = FitnessCalculator.calculateCaloriesBurned(dailySteps, userProfile?.weightKg ?: 0.0)
    val distance = FitnessCalculator.calculateDistanceKm(
        steps = dailySteps,
        heightCm = userProfile?.heightCm?.toInt() ?: 0,
        genderString = userProfile?.gender ?: ""
    )

    // Lógica de progreso
    val baseStepsProgress = (dailySteps / dailyStepsGoal).coerceIn(0f, 1f)
    val extraProgress = ((dailySteps - dailyStepsGoal) / dailyStepsGoal).coerceIn(0f, 1f)

    // Lógica de Confeti
    var hasCelebrated by remember { mutableStateOf(false) }
    val isGoalReached = dailySteps >= dailyStepsGoal.toInt()

    LaunchedEffect(isGoalReached) {
        if (isGoalReached && !hasCelebrated) {
            hasCelebrated = true
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }


    val activeTime = FitnessCalculator.calculateActiveTimeMinutes(dailySteps)

    // Contenedor principal para permitir superposición (Z-Index)
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {

        FitlogCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingDim.LARGE, vertical = PaddingDim.SMALL)
                .clickable {
                    navController.navigate(StepsHistoryRoute.RootRoute.route)
                },
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(32.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = PaddingDim.MEDIUM),
                    contentAlignment = Alignment.Center
                ) {
                    // Corazón de meta base (Verde principal)
                    HeartProgressIndicator(
                        progress = baseStepsProgress,
                        modifier = Modifier.size(140.dp),
                        color = Color(0xFF10B981) // Verde Brillante
                    )

                    if (dailySteps >= dailyStepsGoal) {
                        // Corazón de Pasos Extra (Verde Oscuro)
                        HeartProgressIndicator(
                            progress = extraProgress,
                            modifier = Modifier.size(140.dp),
                            color = Color(0xFF064E3B) // Verde Esmeralda Muy Oscuro
                        )
                    }


                    // Contenido central del corazón: Ahora mostramos solo el porcentaje o meta
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(baseStepsProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = stringResource(id = R.string.daily_steps).lowercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(PaddingDim.MEDIUM))

                // Fila de Estadísticas Inferiores: Ahora incluye Pasos, Calorías, Distancia y Tiempo
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = PaddingDim.SMALL),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // ESTADÍSTICA DE PASOS (Movida aquí)
                    StatInfo(
                        icon = FitlogIcons.Walk,
                        value = dailySteps.toString(),
                        unit = stringResource(R.string.steps),
                        tint = Color(0xFF10B981)
                    )

                    StatInfo(
                        icon = FitlogIcons.Fire,
                        value = calories.toString(),
                        unit = "cal",
                        tint = Color(0xFFFF6F00)
                    )

                    val formattedDistance = UnitsConverter.formatDistance(distance.toDouble(), units)
                    val distanceParts = formattedDistance.split(" ")

                    StatInfo(
                        icon = FitlogIcons.Map,
                        value = distanceParts[0],
                        unit = distanceParts[1].uppercase(),
                        tint = DarkPrimary
                    )

                    StatInfo(
                        icon = FitlogIcons.Clock,
                        value = activeTime.toString(),
                        unit = "Min",
                        tint = DarkPrimary
                    )
                }
                Spacer(modifier = Modifier.height(PaddingDim.SMALL))
            }
        }

        // CAPA DE CONFETI: Se dibuja encima de la FitlogCard
        if (isGoalReached && hasCelebrated) {
            KonfettiView(
                modifier = Modifier.matchParentSize(),
                parties = listOf(
                    Party(
                        speed = 0f,
                        maxSpeed = 30f,
                        damping = 0.9f,
                        spread = 360,
                        colors = listOf(0xFF10B981.toInt(), 0xFFFF6F00.toInt(), 0xFFFFFFFF.toInt()),
                        position = Position.Relative(0.5, 0.4), // Sale un poco arriba del centro
                        emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
                    )
                )
            )
        }
    }
}

@Composable
private fun StatInfo(
    icon: ImageVector,
    value: String,
    unit: String,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon, 
            contentDescription = null, 
            tint = tint,
            modifier = Modifier.size(24.dp) // Tamaño estándar para todos los iconos
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium, // Ajustado para que 4 items quepan bien
            fontWeight = FontWeight.Bold
        )
        Text(text = unit, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
    }
}

fun createHeartPath(size: androidx.compose.ui.geometry.Size): androidx.compose.ui.graphics.Path {
    return androidx.compose.ui.graphics.Path().apply {
        val width = size.width
        val height = size.height

        // Empezamos en la punta superior central
        moveTo(width / 2f, height * 0.25f)

        // Curva derecha (Sentido horario)
        cubicTo(width * 0.8f, height * -0.1f, width * 1.25f, height * 0.6f, width * 0.5f, height * 0.9f)

        // Curva izquierda para cerrar
        cubicTo(width * -0.25f, height * 0.6f, width * 0.2f, height * -0.1f, width * 0.5f, height * 0.25f)

        close()
    }
}

@Composable
fun HeartProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color,
    trackColor: Color = color.copy(alpha = 0.15f)
) {
    Canvas(modifier = modifier) {
        val heartPath = createHeartPath(size)

        // Fondo (Track)
        drawPath(
            path = heartPath,
            color = trackColor,
            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
        )

        // Progreso (Sentido Horario)
        val pathMeasure = android.graphics.PathMeasure(heartPath.asAndroidPath(), false)
        val pathLength = pathMeasure.length
        val stop = pathLength * progress

        val drawPath = android.graphics.Path()
        // getSegment extrae la porción del path de 0 a 'stop'
        pathMeasure.getSegment(0f, stop, drawPath, true)

        drawPath(
            path = drawPath.asComposePath(),
            color = color,
            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
