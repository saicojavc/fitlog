package com.saico.feature.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.saico.core.common.util.UnitsConverter
import com.saico.core.model.BmiStatus
import com.saico.core.model.UnitsConfig
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.navigation.routes.weighttracking.WeightTrackingRoute
import com.saico.core.ui.theme.BottomColor
import com.saico.core.ui.theme.PaddingDim
import com.saico.core.ui.theme.techBlue
import com.saico.feature.dashboard.state.DashboardUiState
import kotlin.math.pow

@Composable
fun WeightTrackerCard(
    uiState: DashboardUiState,
    navController: NavHostController
) {
    val profile = uiState.userProfile ?: return
    val units = uiState.userData?.unitsConfig ?: UnitsConfig.METRIC

    val bmi = remember(profile.weightKg, profile.heightCm) {
        val heightMeters = profile.heightCm / 100.0
        if (heightMeters > 0) (profile.weightKg / heightMeters.pow(2)).toFloat() else 0f
    }

    val bmiStatus = remember(bmi) {
        when {
            bmi < 18.5f -> BmiStatus.LOW_WEIGHT
            bmi < 25.0f -> BmiStatus.NORMAL
            bmi < 30.0f -> BmiStatus.OVERWEIGHT
            else -> BmiStatus.OBESE
        }
    }

    val displayWeight = remember(profile.weightKg, units) {
        if (units == UnitsConfig.METRIC) profile.weightKg else UnitsConverter.kgToLb(profile.weightKg)
    }

    // Mantenemos el recurso de la unidad según tu lógica
    val weightUnit = if (units == UnitsConfig.METRIC) "kg" else "lb"

    val statusColors = remember {
        mapOf(
            BmiStatus.LOW_WEIGHT to Color(0xFFFACC15),
            BmiStatus.NORMAL to Color(0xFF3FB9F6),
            BmiStatus.OVERWEIGHT to Color(0xFFFF9F1C),
            BmiStatus.OBESE to Color(0xFFFF4550)
        )
    }

    val currentStatusColor = statusColors[bmiStatus] ?: Color.Gray

    FitlogCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingDim.LARGE, vertical = PaddingDim.SMALL),
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    FitlogText(
                        text = "%.1f".format(displayWeight),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(Modifier.width(4.dp))
                    FitlogText(
                        text = weightUnit.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }

                // USO DE STRING RESOURCES PARA EL STATUS
                Surface(
                    color = currentStatusColor.copy(alpha = 0.1f),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, currentStatusColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = when (bmiStatus) {
                            BmiStatus.LOW_WEIGHT -> stringResource(R.string.bmi_underweight)
                            BmiStatus.NORMAL -> stringResource(R.string.bmi_normal)
                            BmiStatus.OVERWEIGHT -> stringResource(R.string.bmi_overweight)
                            BmiStatus.OBESE -> stringResource(R.string.bmi_obese)
                            else -> "N/A"
                        }.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = currentStatusColor,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Column(modifier = Modifier.fillMaxWidth()) {

                BmiProgressBar(
                    bmi = bmi,
                    statusColors = statusColors
                )

                Spacer(Modifier.height(8.dp))

                // LABELS DE LA BARRA USANDO STRING RESOURCES
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BmiLabel(
                        stringResource(R.string.bmi_underweight),
                        statusColors[BmiStatus.LOW_WEIGHT]!!
                    )
                    BmiLabel(stringResource(R.string.bmi_normal), statusColors[BmiStatus.NORMAL]!!)
                    BmiLabel(
                        stringResource(R.string.bmi_overweight),
                        statusColors[BmiStatus.OVERWEIGHT]!!
                    )
                    BmiLabel(stringResource(R.string.bmi_obese), statusColors[BmiStatus.OBESE]!!)
                }
            }

            Spacer(Modifier.height(32.dp))

            // BOTÓN CON GRADIENTE AZUL Y STRING RESOURCE
            Button(
                onClick = { navController.navigate(WeightTrackingRoute.RootRoute.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(12.dp, CircleShape, spotColor = techBlue),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BottomColor)
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    FitlogText(
                        text = stringResource(R.string.enter).uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                }
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
            .height(16.dp)
            .clip(RoundedCornerShape(10.dp))
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
