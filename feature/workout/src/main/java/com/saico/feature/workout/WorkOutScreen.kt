package com.saico.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.LightBackground
import com.saico.core.ui.theme.LightPrimary
import com.saico.core.ui.theme.LightSuccess
import com.saico.core.ui.theme.PaddingDim

enum class WorkoutState { IDLE, RUNNING, PAUSED }

@Composable
fun WorkoutScreen(
    viewModel: WorkoutViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Content(uiState, onStart = viewModel::startWorkout, onPause = viewModel::pauseWorkout, onStop = viewModel::stopWorkout)
}

@Composable
fun Content(
    uiState: com.saico.feature.workout.state.WorkoutUiState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit
) {
    val elapsedTime = formatElapsedTime(uiState.elapsedTimeInSeconds)

    val gradientColors = if (isSystemInDarkTheme()) {
        listOf(LightPrimary, LightSuccess)
    } else {
        listOf(LightPrimary, LightSuccess, LightBackground)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors))
                .padding(paddingValues)
                .padding(PaddingDim.MEDIUM),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // -- TIEMPO --
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = FitlogIcons.Clock, contentDescription = "Tiempo transcurrido")
                Spacer(modifier = Modifier.width(PaddingDim.SMALL))
                Text(text = "Tiempo transcurrido", style = MaterialTheme.typography.titleMedium)
            }
            Text(
                text = elapsedTime,
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.padding(vertical = PaddingDim.SMALL)
            )

            Spacer(modifier = Modifier.height(PaddingDim.LARGE))

            // -- ESTADÍSTICAS PRINCIPALES --
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WorkoutStat(icon = FitlogIcons.Map, value = "%.2f".format(uiState.distance), unit = "Km")
                WorkoutStat(icon = FitlogIcons.Fire, value = uiState.calories.toString(), unit = "Calorías")
            }

            Spacer(modifier = Modifier.height(PaddingDim.LARGE))

            // -- RITMO MEDIO --
            WorkoutStat(icon = FitlogIcons.Speed, value = "%.1f".format(uiState.averagePace), unit = "Ritmo medio (Km/h)")

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
                    size = 80.dp // Botón principal más grande
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

@Composable
private fun WorkoutStat(icon: ImageVector, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = unit, modifier = Modifier.size(32.dp))
        Spacer(modifier = Modifier.height(PaddingDim.SMALL))
        Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(text = unit, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
    }
}

@Composable
private fun CircularControlButton(
    icon: ImageVector, 
    onClick: () -> Unit,
    enabled: Boolean = true,
    size: Dp = 64.dp
) {
    val backgroundColor = if (enabled) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f)
    
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // La función del botón es visual
            tint = Color.White,
            modifier = Modifier.size(size * 0.5f)
        )
    }
}