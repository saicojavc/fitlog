package com.saico.feature.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.saico.core.common.util.UnitsConverter
import com.saico.core.model.UnitsConfig
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.navigation.routes.weighttracking.WeightTrackingRoute
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.dashboard.model.BmiStatus
import com.saico.feature.dashboard.state.DashboardUiState
import kotlin.math.pow

@Composable
fun WeightTrackerCard(
    uiState: DashboardUiState,
    navController: NavHostController
) {
    val profile = uiState.userProfile ?: return
    val units = uiState.userData?.unitsConfig ?: UnitsConfig.METRIC

    // 1. Cálculo del IMC (BMI)
    // Fórmula: peso (kg) / [estatura (m)]^2
    val bmi = remember(profile.weightKg, profile.heightCm) {
        val heightMeters = profile.heightCm / 100.0
        if (heightMeters > 0) {
            (profile.weightKg / heightMeters.pow(2)).toFloat()
        } else 0f
    }

    // 2. Determinar el Estado según el IMC
    val bmiStatus = remember(bmi) {
        when {
            bmi < 18.5f -> BmiStatus.LOW_WEIGHT
            bmi < 25.0f -> BmiStatus.NORMAL
            bmi < 30.0f -> BmiStatus.OVERWEIGHT
            else -> BmiStatus.OBESE
        }
    }

    // 3. Preparar visualización del peso según unidades
    val displayWeight = remember(profile.weightKg, units) {
        if (units == UnitsConfig.METRIC) profile.weightKg else UnitsConverter.kgToLb(profile.weightKg)
    }
    val weightUnit = if (units == UnitsConfig.METRIC) "kg" else "lb"

    val statusColors = remember {
        mapOf(
            BmiStatus.LOW_WEIGHT to Color(0xFFFACC15), // Amarillo
            BmiStatus.NORMAL to Color(0xFF10B981),    // Verde Esmeralda
            BmiStatus.OVERWEIGHT to Color(0xFFFF6F00), // Naranja
            BmiStatus.OBESE to Color(0xFFEF4444)      // Rojo
        )
    }

    val currentStatusColor = statusColors[bmiStatus] ?: Color.Gray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingDim.LARGE, vertical = PaddingDim.SMALL),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B).copy(alpha = 0.6f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(
            modifier = Modifier.padding(PaddingDim.LARGE)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = FitlogIcons.Scale,
                        contentDescription = "Weight",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(PaddingDim.SMALL))
                    FitlogText(
                        text = "%.1f ".format(displayWeight),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Light,
                        color = Color.White
                    )
                    FitlogText(
                        text = weightUnit,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF94A3B8)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(currentStatusColor.copy(alpha = 0.2f))
                        .padding(horizontal = PaddingDim.MEDIUM, vertical = PaddingDim.SMALL)
                ) {
                    FitlogText(
                        text = when (bmiStatus) {
                            BmiStatus.LOW_WEIGHT -> stringResource(R.string.bmi_underweight)
                            BmiStatus.NORMAL -> stringResource(R.string.bmi_normal)
                            BmiStatus.OVERWEIGHT -> stringResource(R.string.bmi_overweight)
                            BmiStatus.OBESE -> stringResource(R.string.bmi_obese)
                            else -> "N/A"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = currentStatusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(PaddingDim.LARGE))

            Column(modifier = Modifier.fillMaxWidth()) {
                BmiProgressBar(
                    bmi = bmi,
                    statusColors = statusColors
                )

                Spacer(Modifier.height(PaddingDim.SMALL))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BmiLabel(stringResource(R.string.bmi_underweight), Color(0xFFFACC15))
                    BmiLabel(stringResource(R.string.bmi_normal), Color(0xFF10B981))
                    BmiLabel(stringResource(R.string.bmi_overweight), Color(0xFFFF6F00))
                    BmiLabel(stringResource(R.string.bmi_obese), Color(0xFFEF4444))
                }
            }

            Spacer(Modifier.height(PaddingDim.EXTRA_LARGE))

            Button(
                onClick = {
                    navController.navigate(WeightTrackingRoute.RootRoute.route)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                FitlogText(
                    text = stringResource(R.string.enter),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun BmiLabel(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.Medium
    )
}

@Composable
fun BmiProgressBar(
    bmi: Float,
    statusColors: Map<BmiStatus, Color>
) {
    val minBmiScale = 10f
    val maxBmiScale = 40f

    // Definición de rangos estándar de la OMS
    val ranges = listOf(
        18.5f to BmiStatus.LOW_WEIGHT,
        25.0f to BmiStatus.NORMAL,
        30.0f to BmiStatus.OVERWEIGHT,
        40.0f to BmiStatus.OBESE
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp))
    ) {
        val totalRange = maxBmiScale - minBmiScale
        var lastMark = minBmiScale

        ranges.forEach { (mark, status) ->
            val startX = ((lastMark - minBmiScale) / totalRange) * size.width
            val endX = ((mark - minBmiScale) / totalRange) * size.width
            
            drawRect(
                color = statusColors[status] ?: Color.Gray,
                topLeft = Offset(startX, 0f),
                size = Size(endX - startX, size.height)
            )
            lastMark = mark
        }

        // Indicador del BMI actual
        val indicatorPosition = ((bmi - minBmiScale) / totalRange)
            .coerceIn(0f, 1f) * size.width

        drawCircle(
            color = Color.White,
            radius = 6.dp.toPx(),
            center = Offset(indicatorPosition, size.height / 2)
        )
        
        // Borde del indicador para contraste
        drawCircle(
            color = Color.Black.copy(alpha = 0.2f),
            radius = 7.dp.toPx(),
            center = Offset(indicatorPosition, size.height / 2),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
        )
    }
}
