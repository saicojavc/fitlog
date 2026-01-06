package com.saico.feature.onboarding.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogIcon
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
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FitlogText(
            text = stringResource(id = R.string.daily_goals),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
        )

        SpacerHeight(PaddingDim.SMALL)

        FitlogText(
            text = stringResource(id = R.string.set_goals_to_motivate_yourself),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
        )

        Spacer(modifier = Modifier.height(PaddingDim.MEDIUM))
        FitlogCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingDim.SMALL),
        ) {
            Column(
                modifier = Modifier.padding(PaddingDim.SMALL),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = PaddingDim.LARGE, horizontal = PaddingDim.SMALL),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FitlogIcon(
                        modifier = Modifier.padding(end = PaddingDim.SMALL),
                        imageVector = FitlogIcons.Walk,
                        background = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    )
                    FitlogText(
                        text = stringResource(id = R.string.daily_steps),
                        style = MaterialTheme.typography.bodyLarge
                    )

                }

                FitlogText(
                    text = "$dailySteps steps",
                    style = MaterialTheme.typography.bodyLarge
                )

                Slider(
                    value = dailySteps.toFloat(),
                    onValueChange = { onDailyStepsChange(it.toInt()) },
                    valueRange = 1000f..20000f
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FitlogText(
                        text = "1k",
                        style = MaterialTheme.typography.bodySmall
                    )
                    FitlogText(
                        text = "20k",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

        }

        SpacerHeight(PaddingDim.LARGE)

        FitlogCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingDim.SMALL),
        ) {
            Column(
                modifier = Modifier.padding(PaddingDim.SMALL),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = PaddingDim.LARGE, horizontal = PaddingDim.SMALL),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FitlogIcon(
                        modifier = Modifier.padding(end = PaddingDim.SMALL),
                        imageVector = FitlogIcons.Fire,
                        background = MaterialTheme.colorScheme.tertiaryContainer,
                        tint = Color(0xFFFF6F00),
                        shape = CircleShape
                    )
                    FitlogText(
                        text = stringResource(id = R.string.calories_to_burn),
                        style = MaterialTheme.typography.bodyLarge
                    )

                }

                FitlogText(
                    text = "$caloriesToBurn Kcal",
                    style = MaterialTheme.typography.bodyLarge
                )

                Slider(
                    value = caloriesToBurn.toFloat(),
                    onValueChange = { onCaloriesToBurnChange(it.toInt()) },
                    valueRange = 100f..2000f
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FitlogText(
                        text = "100",
                        style = MaterialTheme.typography.bodySmall
                    )
                    FitlogText(
                        text = "2k",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

            }
        }
    }
}