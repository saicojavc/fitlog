package com.saico.feature.workout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.saico.core.model.UnitsConfig
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.FitlogTopAppBar
import com.saico.core.ui.components.SpacerHeight
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.workout.component.WorkoutStat
import com.saico.feature.workout.state.WorkoutUiState

enum class WorkoutState { IDLE, RUNNING, PAUSED }


@Composable
fun WorkoutScreen(
    navController: NavHostController,
    viewModel: WorkoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Content(
        navController = navController,
        uiState = uiState,
        onStart = viewModel::startWorkout,
        onPause = viewModel::pauseWorkout,
        onStop = viewModel::stopWorkout,
        onDialogDismissed = viewModel::onDialogDismissed
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content(
    navController: NavHostController,
    uiState: WorkoutUiState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onDialogDismissed: () -> Unit
) {
    val elapsedTime = formatElapsedTime(uiState.elapsedTimeInSeconds)

    // Lógica de conversión de unidades para la visualización
    val isMetric = uiState.unitsConfig == UnitsConfig.METRIC

    val displayDistance = remember(uiState.distance, uiState.unitsConfig) {
        if (isMetric) uiState.distance else uiState.distance * 0.621371f
    }

    val displaySpeed = remember(uiState.averagePace, uiState.unitsConfig) {
        if (isMetric) uiState.averagePace else uiState.averagePace * 0.621371f
    }

    val distanceUnit =
        if (isMetric) stringResource(id = R.string.km) else stringResource(id = R.string.mi)
    val speedUnit =
        if (isMetric) stringResource(id = R.string.km_h) else stringResource(id = R.string.mph)

    if (uiState.showWorkoutSavedDialog) {
        AlertDialog(
            onDismissRequest = onDialogDismissed,
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFF0D1424).copy(alpha = 0.95f)) // Fondo azul profundo
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(32.dp)),
            content = {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- ICONO DE CELEBRACIÓN (GLOW AZUL) ---
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .shadow(20.dp, CircleShape, spotColor = Color(0xFF3FB9F6))
                            .background(Color(0xFF3FB9F6).copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Halo intermedio
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color(0xFF3FB9F6).copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            // Círculo central brillante
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        brush = Brush.linearGradient(
                                            listOf(Color(0xFF3FB9F6), Color(0xFF216EE0))
                                        ),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = FitlogIcons.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    // --- TÍTULO ---
                    FitlogText(
                        text = stringResource(id = R.string.workout_saved_title).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    // --- MENSAJE ---
                    FitlogText(
                        text = stringResource(id = R.string.workout_saved_text),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8).copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(Modifier.height(36.dp))

                    // --- BOTÓN DE CIERRE (DEGRADADO AZUL) ---
                    Button(
                        onClick = onDialogDismissed,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(12.dp, CircleShape, spotColor = Color(0xFF3FB9F6)),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        listOf(Color(0xFF3FB9F6), Color(0xFF216EE0))
                                    )
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            FitlogText(
                                text = stringResource(id = android.R.string.ok).uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            FitlogTopAppBar(
                title = stringResource(id = R.string.register_session),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.3f)
                ),
                navigationIcon = {
                    FitlogIcon(
                        modifier = Modifier.clickable {
                            navController.popBackStack()
                        },
                        imageVector = FitlogIcons.ArrowBack,
                        background = Color.Transparent,
                        contentDescription = null
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(PaddingDim.MEDIUM),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SpacerHeight(PaddingDim.EXTRA_HUGE)

                // -- TIEMPO (Estilo Cronómetro Digital Pro) --
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.alpha(0.7f) // Un poco más sutil para que el tiempo destaque
                ) {
                    Icon(
                        imageVector = FitlogIcons.Clock,
                        contentDescription = null,
                        tint = Color(0xFF3FB9F6),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    FitlogText(
                        text = stringResource(id = R.string.elapsed_time).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 2.sp,
                        color = Color.White
                    )
                }

                FitlogText(
                    text = elapsedTime,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.ExtraLight, // Elegancia pura
                        letterSpacing = (-2).sp
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(vertical = PaddingDim.SMALL)
                )

                SpacerHeight(PaddingDim.LARGE)

                // -- TARJETA DE ESTADÍSTICAS (Holográfica) --
                FitlogCard(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF0D1424).copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(32.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            WorkoutStat(
                                icon = FitlogIcons.Map,
                                value = "%.2f".format(displayDistance),
                                unit = distanceUnit.uppercase(),
                                tint = Color(0xFF3FB9F6) // Azul Fitlog
                            )
                            WorkoutStat(
                                icon = FitlogIcons.Fire,
                                value = uiState.calories.toString(),
                                unit = "KCAL",
                                tint = Color(0xFFFF4550) // Rojo neón para quemar calorías
                            )
                        }

                        SpacerHeight(PaddingDim.LARGE)

                        // Divisor sutil
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(1.dp)
                                .background(Color.White.copy(0.05f))
                        )
                        SpacerHeight(PaddingDim.LARGE)

                        WorkoutStat(
                            icon = FitlogIcons.Speed,
                            value = "%.1f".format(displaySpeed),
                            unit = speedUnit.uppercase(),
                            tint = Color.White // Blanco puro para la velocidad
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // -- CONTROLES (Batería de botones Glow) --
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = PaddingDim.LARGE),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // PAUSA
                    CircularControlBlue(
                        icon = FitlogIcons.Pause,
                        onClick = onPause,
                        enabled = uiState.workoutState == WorkoutState.RUNNING,
                        isSecondary = true
                    )

                    // STOP (Botón central dominante)
                    CircularControlBlue(
                        icon = FitlogIcons.Stop,
                        onClick = onStop,
                        enabled = uiState.workoutState != WorkoutState.IDLE,
                        size = 84.dp,
                        isMain = true
                    )

                    // PLAY
                    CircularControlBlue(
                        icon = if (uiState.workoutState == WorkoutState.PAUSED) FitlogIcons.Play else FitlogIcons.Play,
                        onClick = onStart,
                        enabled = uiState.workoutState == WorkoutState.IDLE || uiState.workoutState == WorkoutState.PAUSED,
                        isSecondary = true
                    )
                }
            }
        }


    }
}

@Composable
fun CircularControlBlue(
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean,
    size: Dp = 64.dp,
    isMain: Boolean = false,
    isSecondary: Boolean = false
) {
    val techBlue = Color(0xFF3FB9F6)

    Box(
        modifier = Modifier
            .size(size)
            .shadow(
                elevation = if (enabled && (isMain || !isSecondary)) 15.dp else 0.dp,
                shape = CircleShape,
                spotColor = techBlue
            )
            .clip(CircleShape)
            .background(
                if (enabled) {
                    if (isMain) Brush.linearGradient(listOf(techBlue, Color(0xFF216EE0)))
                    else SolidColor(Color.White.copy(alpha = 0.1f))
                } else SolidColor(Color.White.copy(alpha = 0.05f))
            )
            .border(
                1.dp,
                if (enabled) techBlue.copy(0.4f) else Color.White.copy(0.1f),
                CircleShape
            )
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) (if (isMain) Color.White else techBlue) else Color.White.copy(0.2f),
            modifier = Modifier.size(if (isMain) 36.dp else 28.dp)
        )
    }
}

private fun formatElapsedTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}
