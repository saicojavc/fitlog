package com.saico.feature.onboarding.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.saico.core.model.UnitsConfig
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
    unitsConfig: UnitsConfig,
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
        // Cabecera de Éxito
        Column(
            modifier = Modifier.padding(top = 40.dp, start = PaddingDim.LARGE, end = PaddingDim.LARGE),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono de Check con brillo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFF10B981).copy(alpha = 0.1f), CircleShape)
                    .border(2.dp, Color(0xFF10B981), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = FitlogIcons.Check,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(40.dp)
                )
            }

            SpacerHeight(PaddingDim.MEDIUM)
            FitlogText(
                text = stringResource(id = R.string.all_set).uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = Color.White
            )
            FitlogText(
                text = stringResource(id = R.string.review_your_data),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF94A3B8)
            )
        }

        SpacerHeight(PaddingDim.EXTRA_LARGE)


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = PaddingDim.MEDIUM)
        ) {
            FitlogText(
                text = stringResource(id = R.string.your_profile).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )


            FitlogCard(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF1E293B).copy(alpha = 0.6f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(PaddingDim.MEDIUM).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProfileInfoItem(icon = FitlogIcons.Cake, label = stringResource(id = R.string.age), value = "${uiState.age} ${stringResource(id = R.string.years)}")
                    ProfileInfoItem(icon = if (uiState.gender == "Male") FitlogIcons.Male else FitlogIcons.Female, label = stringResource(id = R.string.gender), value = uiState.gender)
                    ProfileInfoItem(icon = FitlogIcons.Weight, label = stringResource(id = R.string.weight), value = "${uiState.weight} ${if (unitsConfig == UnitsConfig.METRIC) "kg" else "lb"}  ")
                }
            }

            SpacerHeight(PaddingDim.LARGE)

            FitlogText(
                text = stringResource(id = R.string.your_main_goal).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )


            LevelCard(levelText = levelText)

            Spacer(modifier = Modifier.weight(1f))


            Button(
                onClick = {
                    onSaveUserProfile()
                    navController.navigate(DashboardRoute.DashboardScreenRoute.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = PaddingDim.VERY_HUGE, start = PaddingDim.LARGE, end = PaddingDim.LARGE)
                    .height(60.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Text(
                    text = stringResource(id = R.string.save_and_continue).uppercase(),
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun ProfileInfoItem(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
        SpacerHeight(4.dp)
        FitlogText(text = label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
        FitlogText(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun LevelCard(levelText: String) {
    FitlogCard(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF10B981).copy(alpha = 0.15f), // Verde traslúcido
        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f)),
    ) {
        Column(modifier = Modifier.padding(PaddingDim.MEDIUM)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = FitlogIcons.Walk, contentDescription = null, tint = Color(0xFF10B981))
                Spacer(Modifier.width(8.dp))
                FitlogText(text = stringResource(id = R.string.lose_weight), color = Color.White, fontWeight = FontWeight.Bold)
            }
            SpacerHeight(PaddingDim.MEDIUM)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FitlogText(text = stringResource(id = R.string.level), color = Color(0xFF94A3B8))
                Text(
                    text = levelText,
                    color = Color(0xFF10B981),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}