package com.saico.feature.dashboard.screen


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saico.core.common.util.UnitsConverter
import com.saico.core.model.UnitsConfig
import com.saico.core.model.UserProfile
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogAlertDialog
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.LightPrimary
import com.saico.core.ui.theme.LightSuccess
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.dashboard.state.DashboardUiState
import androidx.compose.foundation.text.BasicTextField

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
    // Estados para la edición
    var age by remember(profile) { mutableStateOf(profile.age.toString()) }
    
    // Peso: Se convierte si es imperial
    var weight by remember(profile, units) { 
        val displayWeight = if (units == UnitsConfig.METRIC) {
            profile.weightKg
        } else {
            UnitsConverter.kgToLb(profile.weightKg)
        }
        mutableStateOf("%.1f".format(displayWeight).replace(",", "."))
    }
    
    // Altura: Se maneja cm o ft/in
    var heightCm by remember(profile, units) { 
        mutableStateOf("%.1f".format(profile.heightCm).replace(",", "."))
    }
    var heightFt by remember(profile, units) { 
        val (ft, _) = UnitsConverter.cmToFtIn(profile.heightCm)
        mutableStateOf(ft.toString())
    }
    var heightIn by remember(profile, units) { 
        val (_, inc) = UnitsConverter.cmToFtIn(profile.heightCm)
        mutableStateOf(inc.toString())
    }
    
    var dailyStepsGoal by remember(profile) { mutableStateOf(profile.dailyStepsGoal.toString()) }
    var caloriesGoal by remember(profile) { mutableStateOf(profile.dailyCaloriesGoal.toString()) }

    var showConfirmDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val levelText = when(profile.level) {
        "Professional" -> stringResource(id = R.string.professional)
        "Intermediate" -> stringResource(id = R.string.intermediate)
        else -> stringResource(id = R.string.beginner)
    }

    if (showConfirmDialog) {
        FitlogAlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { FitlogText(text = stringResource(id = R.string.update_profile_title)) },
            text = { FitlogText(text = stringResource(id = R.string.update_profile_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val steps = dailyStepsGoal.toIntOrNull() ?: profile.dailyStepsGoal
                        val cals = caloriesGoal.toIntOrNull() ?: profile.dailyCaloriesGoal

                        // Conversión inversa para guardar en base de datos (siempre métrico)
                        val weightValue = weight.toDoubleOrNull() ?: profile.weightKg
                        val weightKg = if (units == UnitsConfig.METRIC) weightValue else UnitsConverter.lbToKg(weightValue)
                        
                        val finalHeightCm = if (units == UnitsConfig.METRIC) {
                            heightCm.toDoubleOrNull() ?: profile.heightCm
                        } else {
                            UnitsConverter.ftInToCm(
                                heightFt.toIntOrNull() ?: 0,
                                heightIn.toIntOrNull() ?: 0
                            )
                        }

                        val newLevel = when {
                            steps > 19000 || cals > 1500 -> "Professional"
                            steps > 10000 || cals > 500 -> "Intermediate"
                            else -> "Beginner"
                        }

                        val updatedProfile = profile.copy(
                            age = age.toIntOrNull() ?: profile.age,
                            weightKg = weightKg,
                            heightCm = finalHeightCm,
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
        // Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .border(2.dp, Color(0xFF10B981), CircleShape)
                .padding(4.dp),
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
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        LevelCard(levelText = levelText)

        // Sección: Información Personal
        ProfileSectionCard(title = stringResource(id = R.string.personal_info)) {
            EditableInfoRow(
                icon = FitlogIcons.Cake,
                label = stringResource(id = R.string.age),
                value = age,
                onValueChange = { age = it }
            )
            EditableInfoRow(
                icon = FitlogIcons.Weight,
                label = if (units == UnitsConfig.METRIC) "kg" else "lb",
                value = weight,
                onValueChange = { weight = it }
            )
            
            // Altura adaptativa
            if (units == UnitsConfig.METRIC) {
                EditableInfoRow(
                    icon = FitlogIcons.Height,
                    label = "cm",
                    value = heightCm,
                    onValueChange = { heightCm = it }
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = FitlogIcons.Height, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        FitlogText(text = "Height", color = Color(0xFF94A3B8))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BasicTextField(
                            value = heightFt,
                            onValueChange = { heightFt = it },
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, fontSize = 16.sp),
                            modifier = Modifier.width(30.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            cursorBrush = SolidColor(Color(0xFF10B981))
                        )
                        FitlogText(text = " ft ", color = Color.White, style = MaterialTheme.typography.bodySmall)
                        BasicTextField(
                            value = heightIn,
                            onValueChange = { heightIn = it },
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, fontSize = 16.sp),
                            modifier = Modifier.width(30.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            cursorBrush = SolidColor(Color(0xFF10B981))
                        )
                        FitlogText(text = " in", color = Color.White, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // Sección: Metas Diarias
        ProfileSectionCard(title = stringResource(id = R.string.daily_goals)) {
            EditableInfoRow(
                icon = FitlogIcons.Walk,
                label = stringResource(id = R.string.daily_steps),
                value = dailyStepsGoal,
                onValueChange = { dailyStepsGoal = it }
            )
            EditableInfoRow(
                icon = FitlogIcons.Fire,
                label = stringResource(id = R.string.calories_to_burn),
                value = caloriesGoal,
                onValueChange = { caloriesGoal = it }
            )
        }

        Spacer(modifier = Modifier.height(PaddingDim.MEDIUM))

        Button(
            onClick = { showConfirmDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF10B981),
                contentColor = Color.White
            )
        ) {
            FitlogText(
                text = stringResource(id = R.string.save_and_continue),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(PaddingDim.LARGE))
    }
}

@Composable
fun LevelCard(levelText: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = FitlogIcons.Star,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FitlogText(
                    text = stringResource(id = R.string.your_main_goal),
                    color = Color(0xFF94A3B8),
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FitlogText(
                    text = stringResource(id = R.string.running),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    FitlogText(
                        text = stringResource(id = R.string.times_a_week),
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.White.copy(alpha = 0.05f),
                thickness = 1.dp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FitlogText(
                    text = stringResource(id = R.string.level),
                    color = Color(0xFF94A3B8)
                )

                FitlogText(
                    text = levelText.uppercase(),
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun EditableInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            FitlogText(text = label, color = Color(0xFF94A3B8))
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                fontSize = 16.sp
            ),
            modifier = Modifier.width(100.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            cursorBrush = SolidColor(Color(0xFF10B981))
        )
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FitlogText(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
