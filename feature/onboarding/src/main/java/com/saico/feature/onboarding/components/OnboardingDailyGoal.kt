package com.saico.feature.onboarding.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.SpacerHeight
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.PaddingDim

@Composable
fun OnboardingDailyGoal(
    dailySteps: Int,
    onDailyStepsChange: (Int) -> Unit,
    caloriesToBurn: Int,
    onCaloriesToBurnChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = PaddingDim.MEDIUM),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        FitlogText(
            text = stringResource(id = R.string.daily_goals),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )

        SpacerHeight(PaddingDim.SMALL)

        FitlogText(
            text = stringResource(id = R.string.set_goals_to_motivate_yourself),
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF94A3B8),
        )

        Spacer(modifier = Modifier.height(PaddingDim.LARGE))

        // Meta de Pasos
        GoalSliderCard(
            title = stringResource(id = R.string.daily_steps),
            value = dailySteps,
            unit = "steps",
            valueRange = 1000f..20000f,
            minLabel = "1k",
            maxLabel = "20k",
            icon = FitlogIcons.Walk,
            accentColor = Color(0xFF10B981),
            onValueChange = onDailyStepsChange
        )

        SpacerHeight(PaddingDim.MEDIUM)

        // Meta de Calorías
        GoalSliderCard(
            title = stringResource(id = R.string.calories_to_burn),
            value = caloriesToBurn,
            unit = "Kcal",
            valueRange = 100f..2000f,
            minLabel = "100",
            maxLabel = "2k",
            icon = FitlogIcons.Fire,
            accentColor = Color(0xFFFF6F00),
            onValueChange = onCaloriesToBurnChange
        )
    }
}

@Composable
fun GoalSliderCard(
    title: String,
    value: Int,
    unit: String,
    valueRange: ClosedFloatingPointRange<Float>,
    minLabel: String,
    maxLabel: String,
    icon: ImageVector,
    accentColor: Color,
    onValueChange: (Int) -> Unit
) {
    FitlogCard(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1E293B).copy(alpha = 0.6f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(PaddingDim.MEDIUM),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                FitlogText(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
            }

            SpacerHeight(PaddingDim.MEDIUM)


            Row(verticalAlignment = Alignment.Bottom) {
                FitlogText(
                    text = value.toString(),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Light,
                    color = Color.White
                )
                FitlogText(
                    text = " $unit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = valueRange,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = accentColor,
                    inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FitlogText(text = minLabel, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.4f))
                FitlogText(text = maxLabel, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.4f))
            }
        }
    }
}