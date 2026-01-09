package com.saico.feature.gymwork

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogTopAppBar
import com.saico.core.ui.components.InfoDialog
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.LightBackground
import com.saico.core.ui.theme.LightPrimary
import com.saico.core.ui.theme.LightSuccess
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
    val gradientColors = if (isSystemInDarkTheme()) {
        listOf(LightPrimary, LightSuccess)
    } else {
        listOf(LightPrimary, LightSuccess, LightBackground)
    }

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
        InfoDialog(
            title = R.string.workout_saved_title,
            text = R.string.workout_saved_text,
            onDismiss = onDismissSuccessDialog
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            FitlogTopAppBar(
                title = "Gimnasio",
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isSystemInDarkTheme()) Color.Black.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
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
                Icon(imageVector = FitlogIcons.Add, contentDescription = "Agregar ejercicio")
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
                .background(Brush.verticalGradient(gradientColors))
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
                    text = "Ejercicios",
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
