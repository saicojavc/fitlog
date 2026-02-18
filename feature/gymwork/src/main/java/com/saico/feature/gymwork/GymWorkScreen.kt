package com.saico.feature.gymwork

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.FitlogTopAppBar
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.GradientColors
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.gymwork.component.AddExerciseDialog
import com.saico.feature.gymwork.component.ExerciseCard
import com.saico.feature.gymwork.component.GymBottomBar
import com.saico.feature.gymwork.component.TimeCard
import com.saico.feature.gymwork.state.GymExerciseItem
import com.saico.feature.gymwork.state.GymWorkUiState

@Composable
fun GymWorkScreen(
    navController: NavHostController,
    viewModel: GymWorkViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        uiState = uiState,
        navController = navController,
        onToggleTimer = viewModel::toggleTimer,
        onShowAddDialog = viewModel::showAddExerciseDialog,
        onHideAddDialog = viewModel::hideAddExerciseDialog,
        onAddExercise = viewModel::addExercise,
        onEditExercise = viewModel::onEditExercise,
        onUpdateExercise = viewModel::updateExercise,
        onToggleExpansion = viewModel::toggleExerciseExpansion,
        onRemoveExercise = viewModel::removeExercise,
        onSaveSession = viewModel::saveSession,
        onDismissSuccessDialog = {
            viewModel.onDialogDismissed()
            navController.popBackStack()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content(
    uiState: GymWorkUiState,
    navController: NavHostController,
    onToggleTimer: () -> Unit,
    onShowAddDialog: () -> Unit,
    onHideAddDialog: () -> Unit,
    onAddExercise: (String, String, String, String) -> Unit,
    onEditExercise: (GymExerciseItem) -> Unit,
    onUpdateExercise: (String, String, String, String, String) -> Unit,
    onToggleExpansion: (String) -> Unit,
    onRemoveExercise: (String) -> Unit,
    onSaveSession: () -> Unit,
    onDismissSuccessDialog: () -> Unit
) {


    if (uiState.showAddExerciseDialog) {
        AddExerciseDialog(
            onDismiss = onHideAddDialog,
            onConfirm = { name, sets, reps, weight ->
                onAddExercise(name, sets, reps, weight)
            }
        )
    }

    uiState.editingExercise?.let { exercise ->
        AddExerciseDialog(
            initialExercise = exercise,
            onDismiss = onHideAddDialog,
            onConfirm = { name, sets, reps, weight ->
                onUpdateExercise(exercise.id, name, sets, reps, weight)
            }
        )
    }

    if (uiState.showSessionSavedDialog) {
        AlertDialog(
            onDismissRequest = onDismissSuccessDialog,
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
                    // --- ICONO DE ÉXITO ESTILO "CELEBRACIÓN" ---
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color(0xFF10B981).copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Círculo interno más brillante
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFF10B981).copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = FitlogIcons.Check,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
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
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    // --- TEXTO ---
                    FitlogText(
                        text = stringResource(id = R.string.workout_saved_text),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8), // CoolGray
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(Modifier.height(32.dp))

                    // --- BOTÓN DE CIERRE (EMERALD) ---
                    Button(
                        onClick = onDismissSuccessDialog,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        FitlogText(
                            text = stringResource(id = android.R.string.ok).uppercase(),
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
                title = stringResource(id = R.string.gym),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.3f)
                ),
                navigationIcon = {
                    FitlogIcon(
                        modifier = Modifier.clickable { navController.popBackStack() },
                        imageVector = FitlogIcons.ArrowBack,
                        background = Color.Transparent,
                        contentDescription = null
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onShowAddDialog,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(imageVector = FitlogIcons.Add, contentDescription = stringResource(id = R.string.add_exercise))
            }
        },
        bottomBar = {
            GymBottomBar(
                uiState = uiState,
                onSaveSession = onSaveSession
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(GradientColors))
                .padding(paddingValues)
                .padding(PaddingDim.MEDIUM),
            verticalArrangement = Arrangement.spacedBy(PaddingDim.MEDIUM)
        ) {
            item {
                TimeCard(
                    uiState = uiState,
                    onToggleTimer = onToggleTimer
                )
            }

            item {
                Text(
                    text = stringResource(id = R.string.exercises),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = PaddingDim.SMALL)
                )
            }

            items(uiState.exercises, key = { it.id }) { exercise ->
                ExerciseCard(
                    exercise = exercise,
                    onToggleExpansion = { onToggleExpansion(exercise.id) },
                    onRemove = { onRemoveExercise(exercise.id) },
                    onEdit = { onEditExercise(exercise) }
                )
            }
        }
    }
}



@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatElapsedTime(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}
