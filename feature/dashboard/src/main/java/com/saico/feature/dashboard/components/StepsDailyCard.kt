package com.saico.feature.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saico.core.common.util.FitnessCalculator
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.dashboard.state.DashboardUiState

@Composable
fun StepsDailyCard(uiState: DashboardUiState) {
    val userProfile = uiState.userProfile
    val dailySteps = uiState.dailySteps
    val dailyStepsGoal = userProfile?.dailyStepsGoal ?: 1
    val progress = (dailySteps.toFloat() / dailyStepsGoal.toFloat()).coerceIn(0f, 1f)


    val calories = FitnessCalculator.calculateCaloriesBurned(dailySteps, userProfile?.weightKg ?: 0.0)
    val distance = FitnessCalculator.calculateDistanceKm(
        steps = dailySteps, 
        heightCm = userProfile?.heightCm?.toInt() ?: 0, 
        genderString = userProfile?.gender ?: ""
    )
    val activeTime = FitnessCalculator.calculateActiveTimeMinutes(dailySteps)

    FitlogCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingDim.SMALL),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = PaddingDim.MEDIUM),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(150.dp),
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = FitlogIcons.Walk,
                        contentDescription = "Icono de pasos",
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(PaddingDim.SMALL))
                    Text(
                        text = "$dailySteps / $dailyStepsGoal",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(PaddingDim.MEDIUM))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PaddingDim.SMALL),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatInfo(
                    icon = FitlogIcons.Fire,
                    value = calories.toString(),
                    unit = "Kcal",
                    tint = Color(0xFFFF6F00) // Naranja fuego
                )
                StatInfo(
                    icon = FitlogIcons.Map,
                    value = "%.2f".format(distance),
                    unit = "KM"
                )
                StatInfo(
                    icon = FitlogIcons.Clock,
                    value = activeTime.toString(),
                    unit = "Min"
                )
            }
            
            Spacer(modifier = Modifier.height(PaddingDim.SMALL))
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
        Icon(imageVector = icon, contentDescription = null, tint = tint)
        Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text = unit, style = MaterialTheme.typography.bodySmall, fontSize = 12.sp)
    }
}