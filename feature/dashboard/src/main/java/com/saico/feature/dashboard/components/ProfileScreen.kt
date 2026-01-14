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
import com.saico.core.model.UserProfile
import com.saico.core.ui.R
import com.saico.core.ui.components.CrmAlertDialog
import com.saico.core.ui.components.FitlogButton
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.FitlogTextField
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.LightPrimary
import com.saico.core.ui.theme.LightSuccess
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.dashboard.state.DashboardUiState

@Composable
fun ProfileScreen(
    uiState: DashboardUiState,
    updateUserProfile: (UserProfile) -> Unit,
) {

    uiState.userProfile?.let { profile ->
        ProfileContent(
            profile = profile,
            onSave = updateUserProfile
        )
    }
}

@Composable
fun ProfileContent(
    profile: UserProfile,
    onSave: (UserProfile) -> Unit
) {
    var age by remember { mutableStateOf(profile.age.toString()) }
    var weight by remember { mutableStateOf(profile.weightKg.toString()) }
    var height by remember { mutableStateOf(profile.heightCm.toString()) }
    var dailyStepsGoal by remember { mutableStateOf(profile.dailyStepsGoal.toString()) }
    var caloriesGoal by remember { mutableStateOf(profile.dailyCaloriesGoal.toString()) }
    
    var showConfirmDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    if (showConfirmDialog) {
        CrmAlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { FitlogText(text = stringResource(id = R.string.update_profile_title)) },
            text = { FitlogText(text = stringResource(id = R.string.update_profile_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val updatedProfile = profile.copy(
                            age = age.toIntOrNull() ?: profile.age,
                            weightKg = weight.toDoubleOrNull() ?: profile.weightKg,
                            heightCm = height.toDoubleOrNull() ?: profile.heightCm,
                            dailyStepsGoal = dailyStepsGoal.toIntOrNull() ?: profile.dailyStepsGoal,
                            dailyCaloriesGoal = caloriesGoal.toIntOrNull() ?: profile.dailyCaloriesGoal
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

        Spacer(modifier = Modifier.height(PaddingDim.SMALL))

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
                label = stringResource(id = R.string.weight_kg),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            FitlogTextField(
                value = height,
                onValueChange = { height = it },
                label = stringResource(id = R.string.height_cm),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
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
            Divider(modifier = Modifier.padding(vertical = PaddingDim.SMALL))
            content()
        }
    }
}
