package com.saico.feature.workout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.saico.core.ui.theme.DarkSurface
import com.saico.core.ui.theme.GradientColors
import com.saico.core.ui.theme.LightSuccess
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.workout.component.CircularControlButton
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

    val distanceUnit = if (isMetric) stringResource(id = R.string.km) else stringResource(id = R.string.mi)
    val speedUnit = if (isMetric) stringResource(id = R.string.km_h) else stringResource(id = R.string.mph)

    if (uiState.showWorkoutSavedDialog) {
        AlertDialog(
            onDismissRequest = onDialogDismissed,
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFF1E293B).copy(alpha = 0.95f)) // CardBackground
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(32.dp)),
            content = {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- ICONO DE CELEBRACIÓN ---
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color(0xFF10B981).copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Halo interno
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFF10B981).copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = FitlogIcons.Check,
                                contentDescription = null,
                                tint = Color(0xFF10B981), // EmeraldGreen
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // --- TÍTULO ---
                    FitlogText(
                        text = stringResource(id = R.string.workout_saved_title).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.5.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    // --- MENSAJE ---
                    FitlogText(
                        text = stringResource(id = R.string.workout_saved_text),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8), // CoolGray
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(Modifier.height(32.dp))

                    // --- BOTÓN DE CIERRE ---
                    Button(
                        onClick = onDialogDismissed,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981)
                        )
                    ) {
                        FitlogText(
                            text = stringResource(id = android.R.string.ok).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(GradientColors))
                .padding(paddingValues)
                .padding(PaddingDim.MEDIUM),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpacerHeight(PaddingDim.EXTRA_HUGE)
            // -- TIEMPO --
            Row(verticalAlignment = Alignment.CenterVertically) {
                FitlogIcon(
                    imageVector = FitlogIcons.Clock,
                    background = Color.Transparent,
                    contentDescription = stringResource(id = R.string.elapsed_time)
                )

                FitlogText(
                    text = stringResource(id = R.string.elapsed_time),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            SpacerHeight(PaddingDim.MEDIUM)

            FitlogText(
                text = elapsedTime,
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.padding(vertical = PaddingDim.SMALL)
            )

            SpacerHeight(PaddingDim.LARGE)

            // -- ESTADÍSTICAS PRINCIPALES --
            FitlogCard(
                color = Color(0xFF1E293B).copy(alpha = 0.6f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(32.dp),
            ) {
                Column(
                    modifier = Modifier
                        .padding(PaddingDim.MEDIUM),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        WorkoutStat(
                            icon = FitlogIcons.Map,
                            value = "%.2f".format(displayDistance),
                            unit = distanceUnit,
                            tint = LightSuccess
                        )
                        WorkoutStat(
                            icon = FitlogIcons.Fire,
                            value = uiState.calories.toString(),
                            unit = stringResource(id = R.string.calories),
                            tint = Color(0xFFFF6F00)
                        )
                    }

                    SpacerHeight(PaddingDim.LARGE)

                    // -- RITMO MEDIO --
                    WorkoutStat(
                        icon = FitlogIcons.Speed,
                        value = "%.1f".format(displaySpeed),
                        unit = speedUnit,
                        tint = DarkSurface
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Empuja los botones hacia abajo
            // -- CONTROLES --
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularControlButton(
                    icon = FitlogIcons.Pause,
                    onClick = onPause,
                    enabled = uiState.workoutState == WorkoutState.RUNNING
                )
                CircularControlButton(
                    icon = FitlogIcons.Stop,
                    onClick = onStop,
                    enabled = uiState.workoutState != WorkoutState.IDLE,
                    size = 80.dp
                )
                CircularControlButton(
                    icon = if (uiState.workoutState == WorkoutState.PAUSED) FitlogIcons.Play else FitlogIcons.Play,
                    onClick = onStart,
                    enabled = uiState.workoutState == WorkoutState.IDLE || uiState.workoutState == WorkoutState.PAUSED
                )
            }
            Spacer(modifier = Modifier.height(PaddingDim.MEDIUM))
        }
    }
}

private fun formatElapsedTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}
