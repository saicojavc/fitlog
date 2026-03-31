package com.saico.feature.gymwork

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.FitlogTopAppBar
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.GradientColors
import com.saico.core.ui.theme.PaddingDim
import com.saico.core.ui.theme.techBlue
import com.saico.feature.gymwork.component.AddExerciseDialog
import com.saico.feature.gymwork.component.ExerciseCard
import com.saico.feature.gymwork.component.GymBottomBar
import com.saico.feature.gymwork.component.TimeCard
import com.saico.feature.gymwork.state.GuidedExerciseItem
import com.saico.feature.gymwork.state.GuidedSessionState
import com.saico.feature.gymwork.state.GymExerciseItem
import com.saico.feature.gymwork.state.GymWorkUiState
import com.saico.feature.gymwork.state.GymWorkoutMode
import com.saico.feature.gymwork.util.GuidedWorkoutProvider

@Composable
fun GymWorkScreen(
    navController: NavHostController,
    viewModel: GymWorkViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler(enabled = uiState.hasStarted || uiState.exercises.isNotEmpty()) {
    }

    Content(
        uiState = uiState,
        navController = navController,
        onModeChange = viewModel::setWorkoutMode,
        onStartSession = viewModel::startSession,
        onToggleTimer = viewModel::toggleTimer,
        onShowAddDialog = viewModel::showAddExerciseDialog,
        onHideAddDialog = viewModel::hideAddExerciseDialog,
        onAddExercise = viewModel::addExercise,
        onEditExercise = viewModel::onEditExercise,
        onUpdateExercise = viewModel::updateExercise,
        onToggleExpansion = viewModel::toggleExerciseExpansion,
        onRemoveExercise = viewModel::removeExercise,
        onSaveSession = viewModel::saveSession,
        onNextSet = viewModel::nextGuidedSet,
        onSkipRest = viewModel::skipRest,
        onAddRestTime = viewModel::addRestTime,
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
    onModeChange: (GymWorkoutMode) -> Unit,
    onStartSession: () -> Unit,
    onToggleTimer: () -> Unit,
    onShowAddDialog: () -> Unit,
    onHideAddDialog: () -> Unit,
    onAddExercise: (String, String, String, String) -> Unit,
    onEditExercise: (GymExerciseItem) -> Unit,
    onUpdateExercise: (String, String, String, String, String) -> Unit,
    onToggleExpansion: (String) -> Unit,
    onRemoveExercise: (String) -> Unit,
    onSaveSession: () -> Unit,
    onNextSet: () -> Unit,
    onSkipRest: () -> Unit,
    onAddRestTime: (Int) -> Unit,
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
                .background(Color(0xFF1E293B).copy(alpha = 0.95f))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(32.dp)),
            content = {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color(0xFF216EE0).copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFF216EE0).copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = FitlogIcons.Check,
                                contentDescription = null,
                                tint = Color(0xFF216EE0),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    FitlogText(
                        text = stringResource(id = R.string.workout_saved_title).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    FitlogText(
                        text = stringResource(id = R.string.workout_saved_text),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = onDismissSuccessDialog,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = CircleShape,
                                ambientColor = Color(0xFF3FB9F6),
                                spotColor = Color(0xFF3FB9F6)
                            ),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
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
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.25f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            FitlogText(
                                text = stringResource(id = android.R.string.ok).uppercase(),
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.5.sp
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
                title = stringResource(id = R.string.gym),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.3f)
                ),
                navigationIcon = {
                    if (!uiState.hasStarted && uiState.exercises.isEmpty()) {
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
        floatingActionButton = {
            if (uiState.workoutMode == GymWorkoutMode.NON_GUIDED) {
                FloatingActionButton(
                    onClick = onShowAddDialog,
                    containerColor = Color.Transparent,
                    elevation = FloatingActionButtonDefaults.elevation(0.dp),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = CircleShape,
                            ambientColor = Color(0xFF3FB9F6),
                            spotColor = Color(0xFF3FB9F6)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF3FB9F6), Color(0xFF216EE0))
                                )
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.25f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = FitlogIcons.Add,
                            contentDescription = stringResource(id = R.string.add_exercise),
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            GymBottomBar(
                uiState = uiState,
                onStartSession = onStartSession,
                onSaveSession = onSaveSession
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(GradientColors))
                .padding(paddingValues)
        ) {
            // Selector de Modo (Chips)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = PaddingDim.SMALL),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ModeChip(
                    label = stringResource(R.string.mode_solo),
                    selected = uiState.workoutMode == GymWorkoutMode.NON_GUIDED,
                    onClick = { onModeChange(GymWorkoutMode.NON_GUIDED) }
                )
                Spacer(Modifier.size(12.dp))
                ModeChip(
                    label = stringResource(R.string.mode_protocol),
                    selected = uiState.workoutMode == GymWorkoutMode.GUIDED,
                    onClick = { onModeChange(GymWorkoutMode.GUIDED) }
                )
            }

            if (uiState.workoutMode == GymWorkoutMode.GUIDED && uiState.hasStarted) {
                // EXPERIENCIA TOTALMENTE GUIADA (FOCUS MODE)
                GuidedFocusSession(
                    uiState = uiState,
                    onNextSet = onNextSet,
                    onSkipRest = onSkipRest,
                    onAddRestTime = onAddRestTime
                )
            } else {
                // VISTA ESTÁNDAR (LISTA)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = PaddingDim.MEDIUM),
                    verticalArrangement = Arrangement.spacedBy(PaddingDim.MEDIUM)
                ) {
                    // Quitamos la card del contador si es vista guiada
                    if (uiState.workoutMode == GymWorkoutMode.NON_GUIDED) {
                        item {
                            TimeCard(
                                uiState = uiState,
                                onToggleTimer = onToggleTimer
                            )
                        }
                    }

                    item {
                        Text(
                            text = if (uiState.workoutMode == GymWorkoutMode.NON_GUIDED)
                                stringResource(id = R.string.exercises)
                            else
                                stringResource(id = GuidedWorkoutProvider.getDayNameRes()).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = techBlue,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(vertical = PaddingDim.SMALL)
                        )
                    }

                    if (uiState.workoutMode == GymWorkoutMode.NON_GUIDED) {
                        items(uiState.exercises, key = { it.id }) { exercise ->
                            ExerciseCard(
                                exercise = exercise,
                                onToggleExpansion = { onToggleExpansion(exercise.id) },
                                onRemove = { onRemoveExercise(exercise.id) },
                                onEdit = { onEditExercise(exercise) }
                            )
                        }
                    } else {
                        items(uiState.guidedExercises) { exercise ->
                            GuidedExerciseCard(exercise)
                        }
                    }

                    item { Spacer(Modifier.height(PaddingDim.MEDIUM)) }
                }
            }
        }
    }
}

@Composable
fun GuidedFocusSession(
    uiState: GymWorkUiState,
    onNextSet: () -> Unit,
    onSkipRest: () -> Unit,
    onAddRestTime: (Int) -> Unit
) {
    val currentExercise = uiState.guidedExercises.getOrNull(uiState.currentGuidedExerciseIndex) ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingDim.MEDIUM),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PaddingDim.LARGE)
    ) {
        // INDICADOR DE PROGRESO
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            FitlogText(
                text = "EXERCISE ${uiState.currentGuidedExerciseIndex + 1} OF ${uiState.guidedExercises.size}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f)
            )
            FitlogText(
                text = "${uiState.currentSet} OF ${currentExercise.sets} SETS",
                style = MaterialTheme.typography.labelSmall,
                color = techBlue
            )
        }

        // TARJETA DE EJERCICIO EN FOCO
        AnimatedContent(
            targetState = uiState.guidedSessionState,
            transitionSpec = {
                (slideInVertically { height -> height } + fadeIn())
                    .togetherWith(slideOutVertically { height -> -height } + fadeOut())
            }, label = "GuidedState"
        ) { state ->
            when (state) {
                GuidedSessionState.EXERCISING -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FitlogText(
                            text = stringResource(currentExercise.nameRes).uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        FitlogText(
                            text = "${currentExercise.reps} REPS",
                            style = MaterialTheme.typography.displayMedium,
                            color = techBlue,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(Modifier.height(24.dp))
                        FitlogCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = stringResource(currentExercise.descriptionRes),
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(Modifier.height(40.dp))
                        Button(
                            onClick = onNextSet,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .shadow(12.dp, CircleShape, spotColor = techBlue),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = techBlue)
                        ) {
                            Text(stringResource(R.string.finish_set).uppercase(), fontWeight = FontWeight.Black)
                        }
                    }
                }

                GuidedSessionState.RESTING -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxHeight(0.7f)
                    ) {
                        FitlogText(
                            text = stringResource(R.string.rest).uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            color = techBlue,
                            letterSpacing = 4.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        
                        Box(contentAlignment = Alignment.Center) {
                            FitlogText(
                                text = String.format("%02d", uiState.restTimeRemaining),
                                style = MaterialTheme.typography.displayLarge,
                                fontSize = 100.sp,
                                fontWeight = FontWeight.Light
                            )
                            
                            // Botón para añadir 15 segundos
                            IconButton(
                                onClick = { onAddRestTime(15) },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(start = 150.dp, bottom = 20.dp)
                                    .size(48.dp)
                                    .background(techBlue.copy(alpha = 0.1f), CircleShape)
                                    .border(1.dp, techBlue.copy(alpha = 0.3f), CircleShape)
                            ) {
                                Text(
                                    text = "+15",
                                    color = techBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(Modifier.height(40.dp))
                        TextButton(onClick = onSkipRest) {
                            Text(stringResource(R.string.skip_rest).uppercase(), color = techBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                GuidedSessionState.FINISHED -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(FitlogIcons.Check, null, modifier = Modifier.size(100.dp), tint = techBlue)
                        Spacer(Modifier.height(24.dp))
                        FitlogText(
                            text = stringResource(R.string.workout_complete).uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun ModeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) techBlue.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f))
            .border(
                width = 1.dp,
                color = if (selected) techBlue else Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) techBlue else Color.White.copy(alpha = 0.6f),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun GuidedExerciseCard(exercise: GuidedExerciseItem) {
    FitlogCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(PaddingDim.MEDIUM)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(techBlue.copy(alpha = 0.1f), CircleShape)
                        .border(1.dp, techBlue.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(exercise.categoryRes).take(1),
                        color = techBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(exercise.nameRes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${exercise.sets} SERIES x ${exercise.reps}",
                        style = MaterialTheme.typography.bodySmall,
                        color = techBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(exercise.descriptionRes),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
                lineHeight = 16.sp
            )
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
