package com.saico.feature.dashboard.screen


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import java.time.format.TextStyle

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
        FitlogAlertDialog(
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
        // --- HEADER CON AVATAR REFINADO ---
        Box(
            modifier = Modifier
                .size(100.dp)
                .border(2.dp, Color(0xFF10B981), CircleShape) // Borde esmeralda fino
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

        // --- TARJETA DE NIVEL (Estilo Dark) ---
        LevelCard(levelText = levelText)

        // --- SECCIÓN: INFORMACIÓN PERSONAL ---
        ProfileSectionCard(title = stringResource(id = R.string.personal_info)) {
            // Editores de fila en lugar de TextFields grandes
            EditableInfoRow(
                icon = FitlogIcons.Cake, // Asumiendo iconos existentes
                label = stringResource(id = R.string.age),
                value = age,
                onValueChange = { age = it }
            )
            EditableInfoRow(
                icon = FitlogIcons.Scale,
                label = if (units == UnitsConfig.METRIC) "kg" else "lb",
                value = weight,
                onValueChange = { weight = it }
            )
            EditableInfoRow(
                icon = FitlogIcons.Height,
                label = if (units == UnitsConfig.METRIC) "cm" else "ft/in",
                value = height,
                onValueChange = { height = it }
            )
        }

        // --- SECCIÓN: METAS DIARIAS ---
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

        // --- BOTÓN PRINCIPAL (Emerald Green) ---
        Button(
            onClick = { showConfirmDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF10B981), // Emerald Green
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
    // Usamos FitlogCard pero sobreescribimos con el nuevo estilo Dark
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B) // Slate Gray Dark para consistencia
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)) // Efecto cristal
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila Superior: Título de la sección
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = FitlogIcons.Star, // Cambié Walk por Star para que se sienta como "Nivel"
                    contentDescription = null,
                    tint = Color(0xFF10B981), // Acento esmeralda
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FitlogText(
                    text = stringResource(id = R.string.your_main_goal),
                    color = Color(0xFF94A3B8), // Cool Gray para que sea un subtítulo
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fila Central: Actividad principal
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

                // Un "Badge" para la frecuencia
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

            // Fila Inferior: Nivel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FitlogText(
                    text = stringResource(id = R.string.level),
                    color = Color(0xFF94A3B8)
                )

                // Resaltamos el nivel con el color de acento
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
                tint = Color(0xFF94A3B8), // Cool Gray
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            FitlogText(text = label, color = Color(0xFF94A3B8))
        }

        // Input minimalista a la derecha
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E293B) // Slate Gray Dark
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)) // Efecto cristal
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
