package com.saico.feature.onboarding.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import com.saico.core.ui.theme.techBlue
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
        // --- CABECERA DE ÉXITO (Look Cyber) ---
        Column(
            modifier = Modifier.padding(top = 60.dp, start = PaddingDim.LARGE, end = PaddingDim.LARGE),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(20.dp, CircleShape, spotColor = techBlue)
                    .background(techBlue.copy(alpha = 0.1f), CircleShape)
                    .border(2.dp, techBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = FitlogIcons.Check,
                    contentDescription = null,
                    tint = techBlue,
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
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        SpacerHeight(PaddingDim.EXTRA_LARGE)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = PaddingDim.MEDIUM)
        ) {
            // Título Sección Perfil
            FitlogText(
                text = stringResource(id = R.string.your_profile).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = techBlue,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
            )

            // Card de Datos Glassmorphism
            FitlogCard(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProfileInfoItem(
                        icon = FitlogIcons.Cake,
                        label = stringResource(id = R.string.age),
                        value = "${uiState.age} ${stringResource(id = R.string.years)}",
                        accentColor = techBlue
                    )
                    ProfileInfoItem(
                        icon = if (uiState.gender == "Male") FitlogIcons.Male else FitlogIcons.Female,
                        label = stringResource(id = R.string.gender),
                        value = uiState.gender,
                        accentColor = techBlue
                    )
                    ProfileInfoItem(
                        icon = FitlogIcons.Weight,
                        label = stringResource(id = R.string.weight),
                        value = "${uiState.weight} ${if (unitsConfig == UnitsConfig.METRIC) "kg" else "lb"}",
                        accentColor = techBlue
                    )
                }
            }

            SpacerHeight(PaddingDim.LARGE)

            // Título Sección Meta
            FitlogText(
                text = stringResource(id = R.string.your_main_goal).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = techBlue,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
            )

            LevelCard(levelText = levelText, accentColor = techBlue)

            Spacer(modifier = Modifier.weight(1f))

            // --- BOTÓN FINAL CON DEGRADADO ---
            Button(
                onClick = {
                    onSaveUserProfile()
                    navController.navigate(DashboardRoute.DashboardScreenRoute.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp, start = PaddingDim.LARGE, end = PaddingDim.LARGE)
                    .height(60.dp)
                    .shadow(15.dp, CircleShape, spotColor = techBlue),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(techBlue, Color(0xFF216EE0))))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.save_and_continue).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileInfoItem(icon: ImageVector, label: String, value: String, accentColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(20.dp)
        )
        SpacerHeight(6.dp)
        FitlogText(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.4f),
            fontWeight = FontWeight.Bold
        )
        FitlogText(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}

@Composable
fun LevelCard(levelText: String, accentColor: Color) {
    FitlogCard(
        modifier = Modifier.fillMaxWidth(),
        color = accentColor.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = FitlogIcons.Walk,
                    contentDescription = null,
                    tint = accentColor
                )
                Spacer(Modifier.width(12.dp))
                FitlogText(
                    text = stringResource(id = R.string.lose_weight).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
            SpacerHeight(16.dp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FitlogText(
                    text = stringResource(id = R.string.level).uppercase(),
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = accentColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
                ) {
                    FitlogText(
                        text = levelText.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = accentColor,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}