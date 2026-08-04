package com.saico.feature.gymwork

import android.text.format.DateUtils
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.FitlogTopAppBar
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.GradientColors
import com.saico.feature.gymwork.component.*
import com.saico.feature.gymwork.state.GymWorkUiState

import androidx.compose.ui.platform.LocalContext
import android.os.Vibrator
import android.os.VibrationEffect
import android.content.Context

@Composable
fun GymWorkScreen(
    navController: NavHostController,
    viewModel: GymWorkViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showExitDialog by remember { mutableStateOf(false) }

    // Feedback háptico al terminar el descanso
    LaunchedEffect(uiState.restTimerSeconds) {
        if (uiState.restTimerSeconds == 0 && uiState.isSessionActive) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(500)
            }
        }
    }

    BackHandler(enabled = uiState.isSessionActive) {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("¿Abandonar entrenamiento?") },
            text = { Text("Si sales ahora, perderás el progreso de la sesión actual.") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    viewModel.resetState()
                    navController.popBackStack()
                }) {
                    Text("ABANDONAR", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("CONTINUAR")
                }
            }
        )
    }

    Content(
        uiState = uiState,
        navController = navController,
        onDaySelected = viewModel::onDaySelected,
        onStartSession = viewModel::startSession,
        onFinishSession = viewModel::finishSession,
        onSetToggled = viewModel::onSetToggled,
        onSkipRest = viewModel::skipRest,
        onDismissSuccessDialog = {
            viewModel.resetState()
            navController.popBackStack()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content(
    uiState: GymWorkUiState,
    navController: NavHostController,
    onDaySelected: (com.saico.core.model.WorkoutDay) -> Unit,
    onStartSession: () -> Unit,
    onFinishSession: () -> Unit,
    onSetToggled: (Int, Int, String, String) -> Unit,
    onSkipRest: () -> Unit,
    onDismissSuccessDialog: () -> Unit
) {
    if (uiState.showSessionSavedDialog) {
        // Success dialog similar to previous implementation
        AlertDialog(
            onDismissRequest = onDismissSuccessDialog,
            confirmButton = {
                Button(onClick = onDismissSuccessDialog) { Text("OK") }
            },
            title = { Text("¡Entrenamiento Guardado!") },
            text = { Text("Tu sesión ha sido registrada correctamente.") }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            FitlogTopAppBar(
                title = if (uiState.isSessionActive) "ENTRENAMIENTO" else "GIMNASIO",
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.3f)
                ),
                navigationIcon = {
                    if (!uiState.isSessionActive) {
                        FitlogIcon(
                            modifier = Modifier.clickable { navController.popBackStack() },
                            imageVector = FitlogIcons.ArrowBack,
                            background = Color.Transparent,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        bottomBar = {
            WorkoutBottomBar(
                uiState = uiState,
                onStartSession = onStartSession,
                onFinishSession = onFinishSession
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(GradientColors))
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (!uiState.isSessionActive) {
                    DaySelectorTabs(
                        selectedDay = uiState.selectedDay,
                        onDaySelected = onDaySelected,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                AnimatedContent(
                    targetState = uiState.isSessionActive,
                    label = "SessionTransition"
                ) { isActive ->
                    if (isActive) {
                        LiveWorkoutView(
                            uiState = uiState,
                            onSetToggled = onSetToggled
                        )
                    } else {
                        PlanningView(uiState = uiState)
                    }
                }
            }
            
            RestTimerOverlay(
                remainingSeconds = uiState.restTimerSeconds,
                onSkip = onSkipRest
            )
        }
    }
}

@Composable
fun WorkoutBottomBar(
    uiState: GymWorkUiState,
    onStartSession: () -> Unit,
    onFinishSession: () -> Unit
) {
    val blueGradient = Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color(0xFF216EE0)))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(Color(0xFF0D1424).copy(alpha = 0.9f)),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            if (uiState.isSessionActive) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("TIEMPO", color = Color.Gray, fontSize = 10.sp)
                        Text(
                            DateUtils.formatElapsedTime(uiState.elapsedTimeSeconds),
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("CALORÍAS", color = Color.Gray, fontSize = 10.sp)
                        Text(
                            "${uiState.totalCalories.toInt()} KCAL",
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Button(
                onClick = if (uiState.isSessionActive) onFinishSession else onStartSession,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(12.dp, CircleShape, spotColor = Color(0xFF38BDF8)),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(blueGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (uiState.isSessionActive) "FINALIZAR ENTRENAMIENTO" else "INICIAR SESIÓN",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
