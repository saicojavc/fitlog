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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.SpacerHeight
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.PaddingDim
import com.saico.core.ui.theme.techBlue

@Composable
fun OnboardingDailyGoal(
    dailySteps: Int,
    onDailyStepsChange: (Int) -> Unit,
    caloriesToBurn: Int,
    onCaloriesToBurnChange: (Int) -> Unit
) {
    val fireOrange = Color(0xFFFF9F1C)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = PaddingDim.MEDIUM),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FitlogText(
            text = stringResource(id = R.string.daily_goals).uppercase(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 1.sp
        )

        SpacerHeight(PaddingDim.SMALL)

        FitlogText(
            text = stringResource(id = R.string.set_goals_to_motivate_yourself),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Meta de Pasos (Azul Tech)
        GoalSliderCard(
            title = stringResource(id = R.string.daily_steps),
            value = dailySteps,
            unit = "steps",
            valueRange = 1000f..20000f,
            minLabel = "1k",
            maxLabel = "20k",
            icon = FitlogIcons.Walk,
            accentColor = techBlue,
            onValueChange = onDailyStepsChange
        )

        SpacerHeight(PaddingDim.LARGE)

        // Meta de Calorías (Naranja Fire)
        GoalSliderCard(
            title = stringResource(id = R.string.calories_to_burn),
            value = caloriesToBurn,
            unit = "kcal",
            valueRange = 100f..2000f,
            minLabel = "100",
            maxLabel = "2k",
            icon = FitlogIcons.Fire,
            accentColor = fireOrange,
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
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
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
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(10.dp))
                FitlogText(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f),
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            SpacerHeight(PaddingDim.MEDIUM)

            // Valor con efecto Glow dinámico
            Row(verticalAlignment = Alignment.Bottom) {
                FitlogText(
                    text = value.toString(),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier.shadow(
                        elevation = 8.dp,
                        spotColor = accentColor,
                        ambientColor = accentColor
                    )
                )
                FitlogText(
                    text = " $unit".uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = accentColor,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Slider con estética Premium
            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = valueRange,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = accentColor,
                    inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = minLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(0.3f),
                    fontFamily = FontFamily.Monospace
                )
              Text(
                    text = maxLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(0.3f),
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}