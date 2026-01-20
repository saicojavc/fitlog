package com.saico.feature.onboarding.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.SpacerHeight
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.navigation.routes.dashboard.DashboardRoute
import com.saico.core.ui.theme.CornerDim
import com.saico.core.ui.theme.LightPrimary
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.onboarding.state.OnboardingUiState

@Composable
fun OnboardingFinish(
    uiState: OnboardingUiState,
    onSaveUserProfile: () -> Unit,
    navController: NavHostController,
) {
    val userLevel = remember(uiState.dailySteps, uiState.caloriesToBurn) {
        when {
            uiState.dailySteps > 19000 || uiState.caloriesToBurn > 1500 -> "Professional"
            uiState.dailySteps > 10000 || uiState.caloriesToBurn > 500 -> "Intermediate"
            else -> "Beginner"
        }
    }

    val levelText = when(userLevel) {
        "Professional" -> stringResource(id = R.string.professional)
        "Intermediate" -> stringResource(id = R.string.intermediate)
        else -> stringResource(id = R.string.beginner)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.padding(top = PaddingDim.LARGE, start = PaddingDim.LARGE, end = PaddingDim.LARGE),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FitlogIcon(
                modifier = Modifier
                    .padding(horizontal = PaddingDim.MEDIUM)
                    .size(PaddingDim.EXTRA_HUGE)
                    .border(1.dp, Color.White, CircleShape),
                imageVector = FitlogIcons.Check,
                background = Color.Transparent,
                tint = Color.White,
            )
            SpacerHeight(PaddingDim.SMALL)
            FitlogText(text = stringResource(id = R.string.all_set), style = MaterialTheme.typography.headlineMedium, color = Color.White)
            SpacerHeight(PaddingDim.SMALL)
            FitlogText(text = stringResource(id = R.string.review_your_data), style = MaterialTheme.typography.bodyLarge, color = Color.White)
        }

        SpacerHeight(PaddingDim.LARGE)

        FitlogCard(
            modifier = Modifier.fillMaxWidth().weight(1f),
            shape = RoundedCornerShape(topStart = CornerDim.HUGE, topEnd = CornerDim.HUGE, bottomStart = CornerDim.ZERO, bottomEnd = CornerDim.ZERO)
        ) {
            SpacerHeight(PaddingDim.MEDIUM)
            Column(modifier = Modifier.fillMaxSize().padding(PaddingDim.MEDIUM)) {
                FitlogText(modifier = Modifier.padding(PaddingDim.MEDIUM), text = stringResource(id = R.string.your_profile), style = MaterialTheme.typography.headlineSmall)
                
                FitlogCard(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Transparent,
                    shape = RoundedCornerShape(CornerDim.MEDIUM),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(PaddingDim.MEDIUM), horizontalArrangement = Arrangement.SpaceAround) {
                        ProfileInfoItem(icon = FitlogIcons.Cake, label = stringResource(id = R.string.age), value = "${uiState.age} ${stringResource(id = R.string.years)}")
                        ProfileInfoItem(
                            icon = if (uiState.gender == "Male") FitlogIcons.Male else FitlogIcons.Female,
                            label = stringResource(id = R.string.gender),
                            value = uiState.gender
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(PaddingDim.MEDIUM), horizontalArrangement = Arrangement.SpaceAround) {
                        ProfileInfoItem(icon = FitlogIcons.Weight, label = stringResource(id = R.string.weight), value = "${uiState.weight} kg")
                        ProfileInfoItem(icon = FitlogIcons.Height, label = stringResource(id = R.string.height), value = "${uiState.height} cm")
                    }
                }

                SpacerHeight(PaddingDim.MEDIUM)
                FitlogText(modifier = Modifier.padding(PaddingDim.MEDIUM), text = stringResource(id = R.string.your_main_goal), style = MaterialTheme.typography.headlineSmall)

                // TARJETA DE NIVEL DINÁMICA
                LevelCard(levelText = levelText)

                Button(
                    onClick = {
                        onSaveUserProfile()
                        navController.navigate(DashboardRoute.DashboardScreenRoute.route)
                    },
                    modifier = Modifier.fillMaxWidth().padding(PaddingDim.LARGE)
                ) {
                    Text(text = stringResource(id = R.string.save_and_continue))
                }
            }
        }
    }
}

@Composable
fun ProfileInfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FitlogIcon(imageVector = icon, contentDescription = null, background = Color.Transparent)
            FitlogText(text = label)
        }
        FitlogText(text = value)
    }
}

@Composable
fun LevelCard(levelText: String) {
    FitlogCard(
        modifier = Modifier.fillMaxWidth(),
        color = LightPrimary,
        shape = RoundedCornerShape(CornerDim.MEDIUM),
    ) {
        Column(modifier = Modifier.padding(PaddingDim.MEDIUM)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FitlogIcon(imageVector = FitlogIcons.Walk, contentDescription = null, background = Color.Transparent, tint = Color.White)
                FitlogText(text = stringResource(id = R.string.lose_weight), color = Color.White)
            }
            SpacerHeight(PaddingDim.SMALL)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FitlogText(text = stringResource(id = R.string.running), color = Color.White)
                FitlogText(text = stringResource(id = R.string.times_a_week), color = Color.White)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = PaddingDim.SMALL), color = Color.White, thickness = 1.dp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FitlogText(text = stringResource(id = R.string.level), color = Color.White)
                FitlogText(text = levelText, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
