package com.saico.feature.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.saico.core.common.util.UnitsConverter
import com.saico.core.model.UnitsConfig
import com.saico.core.model.UserProfile
import com.saico.core.ui.R
import com.saico.core.ui.components.CrmAlertDialog
import com.saico.core.ui.components.FitlogButton
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.FitlogTextField
import com.saico.core.ui.components.SpacerHeight
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.CornerDim
import com.saico.core.ui.theme.LightPrimary
import com.saico.core.ui.theme.LightSuccess
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.dashboard.state.DashboardUiState

@Composable
fun ProfileScreen(
    uiState: DashboardUiState,
    updateUserProfile: (UserProfile) -> Unit,
) {
    val units = uiState.userData?.unitsConfig ?: UnitsConfig.METRIC

    uiState.userProfile?.let { profile ->
        ProfileContent(
            profile = profile,
            units = units,
            onSave = updateUserProfile
        )
    }
}

@Composable
fun ProfileContent(
    profile: UserProfile,
    units: UnitsConfig,
    onSave: (UserProfile) -> Unit
) {
    var age by remember { mutableStateOf(profile.age.toString()) }
    var weight by remember { mutableStateOf(profile.weightKg.toString()) }
    var height by remember { mutableStateOf(profile.heightCm.toString()) }
    var dailyStepsGoal by remember { mutableStateOf(profile.dailyStepsGoal.toString()) }
    var caloriesGoal by remember { mutableStateOf(profile.dailyCaloriesGoal.toString()) }
    
    var showConfirmDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val levelText = when(profile.level) {
        "Professional" -> stringResource(id = R.string.professional)
        "Intermediate" -> stringResource(id = R.string.intermediate)
        else -> stringResource(id = R.string.beginner)
    }

    if (showConfirmDialog) {
        CrmAlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { FitlogText(text = stringResource(id = R.string.update_profile_title)) },
            text = { FitlogText(text = stringResource(id = R.string.update_profile_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val steps = dailyStepsGoal.toIntOrNull() ?: profile.dailyStepsGoal
                        val cals = caloriesGoal.toIntOrNull() ?: profile.dailyCaloriesGoal
                        
                        val newLevel = when {
                            steps > 19000 || cals > 1500 -> "Professional"
                            steps > 10000 || cals > 500 -> "Intermediate"
                            else -> "Beginner"
                        }

                        val updatedProfile = profile.copy(
                            age = age.toIntOrNull() ?: profile.age,
                            weightKg = weight.toDoubleOrNull() ?: profile.weightKg,
                            heightCm = height.toDoubleOrNull() ?: profile.heightCm,
                            dailyStepsGoal = steps,
                            dailyCaloriesGoal = cals,
                            level = newLevel
                        )
                        onSave(updatedProfile)
                        showConfirmDialog = false
                    }
                ) {
                    FitlogText(text = stringResource(id = R.string.accept))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    FitlogText(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(PaddingDim.MEDIUM),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PaddingDim.MEDIUM)
    ) {
        // Header con Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(LightPrimary, LightSuccess))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = FitlogIcons.Person,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = Color.White
            )
        }

        FitlogText(
            text = stringResource(id = R.string.your_profile),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // TARJETA DE NIVEL DEL USUARIO (Igual que en Onboarding)
        LevelCard(levelText = levelText)

        // Card de Información Personal
        ProfileSectionCard(title = stringResource(id = R.string.personal_info)) {
            FitlogTextField(
                value = age,
                onValueChange = { age = it },
                label = stringResource(id = R.string.age),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            FitlogTextField(
                value = weight,
                onValueChange = { weight = it },
                label = if (units == UnitsConfig.METRIC) stringResource(id = R.string.weight_kg) else "Peso (lb)",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            FitlogTextField(
                value = height,
                onValueChange = { height = it },
                label = if (units == UnitsConfig.METRIC) stringResource(id = R.string.height_cm) else "Altura (ft/in)",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                FitlogText(
                    text = "Actual: ${UnitsConverter.formatWeight(profile.weightKg, units)} / ${UnitsConverter.formatHeight(profile.heightCm, units)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Card de Metas
        ProfileSectionCard(title = stringResource(id = R.string.daily_goals)) {
            FitlogTextField(
                value = dailyStepsGoal,
                onValueChange = { dailyStepsGoal = it },
                label = stringResource(id = R.string.daily_steps),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            FitlogTextField(
                value = caloriesGoal,
                onValueChange = { caloriesGoal = it },
                label = stringResource(id = R.string.calories_to_burn),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Spacer(modifier = Modifier.height(PaddingDim.MEDIUM))

        FitlogButton(
            onClick = { showConfirmDialog = true },
            modifier = Modifier.fillMaxWidth(),
            content = {
                FitlogText(text = stringResource(id = R.string.save_and_continue))
            }
        )

        Spacer(modifier = Modifier.height(PaddingDim.LARGE))
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
                FitlogText(text = stringResource(id = R.string.your_main_goal), color = Color.White)
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

@Composable
fun ProfileSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(PaddingDim.MEDIUM),
            verticalArrangement = Arrangement.spacedBy(PaddingDim.SMALL)
        ) {
            FitlogText(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = PaddingDim.SMALL))
            content()
        }
    }
}
