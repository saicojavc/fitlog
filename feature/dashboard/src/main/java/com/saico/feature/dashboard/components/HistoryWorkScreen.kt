package com.saico.feature.dashboard.components

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saico.core.model.GymExercise
import com.saico.core.model.WorkoutSession
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.LightPrimary
import com.saico.core.ui.theme.LightSuccess
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.dashboard.state.DashboardUiState
import com.saico.feature.dashboard.state.HistoryFilter
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryWorkScreen(
    uiState: DashboardUiState,
    onFilterSelected: (HistoryFilter) -> Unit
) {

    HistoryContent(
        uiState = uiState,
        onFilterSelected = onFilterSelected
    )
}

@Composable
fun HistoryContent(
    uiState: DashboardUiState,
    onFilterSelected: (HistoryFilter) -> Unit
) {
    val filteredGymExercises = remember(uiState.gymExercises, uiState.selectedFilter) {
        filterData(uiState.gymExercises, uiState.selectedFilter) { it.date }
    }

    val filteredWorkoutSessions = remember(uiState.workoutSessions, uiState.selectedFilter) {
        filterData(uiState.workoutSessions, uiState.selectedFilter) { it.date }
    }

    val totalCalories = remember(filteredGymExercises, filteredWorkoutSessions) {
        filteredGymExercises.sumOf { it.totalCalories } + filteredWorkoutSessions.sumOf { it.calories }
    }

    val totalTimeSeconds = remember(filteredGymExercises, filteredWorkoutSessions) {
        filteredGymExercises.sumOf { it.elapsedTime } + filteredWorkoutSessions.sumOf { it.time.time / 1000 }
    }

    val combinedHistory = remember(filteredGymExercises, filteredWorkoutSessions) {
        (filteredGymExercises.map { HistoryItem.Gym(it) } +
                filteredWorkoutSessions.map { HistoryItem.Session(it) })
            .sortedByDescending { it.date }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        FilterRow(
            selectedFilter = uiState.selectedFilter,
            onFilterSelected = onFilterSelected
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(PaddingDim.MEDIUM),
            verticalArrangement = Arrangement.spacedBy(PaddingDim.MEDIUM)
        ) {
            item {
                SummaryCard(
                    filter = uiState.selectedFilter,
                    totalCalories = totalCalories,
                    totalTimeSeconds = totalTimeSeconds
                )
            }

            items(combinedHistory) { item ->
                when (item) {
                    is HistoryItem.Gym -> GymExerciseCard(gymExercise = item.exercise)
                    is HistoryItem.Session -> WorkoutSessionCard(session = item.session)
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    filter: HistoryFilter,
    totalCalories: Int,
    totalTimeSeconds: Long
) {
    val filterText = when (filter) {
        HistoryFilter.TODAY -> "Hoy"
        HistoryFilter.LAST_WEEK -> "Esta Semana"
        HistoryFilter.LAST_MONTH -> "Este Mes"
        HistoryFilter.ALL -> "Historial Total"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(LightPrimary, LightSuccess)
                    )
                )
                .padding(PaddingDim.MEDIUM)
        ) {
            Column {
                Text(
                    text = "Resumen $filterText",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(PaddingDim.MEDIUM))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryStat(
                        label = "Calorías Totales",
                        value = "$totalCalories",
                        unit = "kcal",
                        icon = FitlogIcons.Fire,
                        tint = Color(0xFFFF6F00)
                    )
                    SummaryStat(
                        label = "Tiempo Activo",
                        value = formatElapsedTime(totalTimeSeconds),
                        unit = "",
                        icon = FitlogIcons.Clock,
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryStat(
    label: String,
    value: String,
    unit: String,
    icon: ImageVector,
    tint: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = tint
        )
        Spacer(modifier = Modifier.width(PaddingDim.SMALL))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )
                if (unit.isNotEmpty()) {
                    Text(
                        text = " $unit",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FilterRow(
    selectedFilter: HistoryFilter,
    onFilterSelected: (HistoryFilter) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = PaddingDim.SMALL),
        contentPadding = PaddingValues(horizontal = PaddingDim.MEDIUM),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(HistoryFilter.values()) { filter ->
            FilterChip(
                modifier = Modifier.padding(horizontal = 4.dp),
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = when (filter) {
                            HistoryFilter.TODAY -> "Hoy"
                            HistoryFilter.LAST_WEEK -> "Semana"
                            HistoryFilter.LAST_MONTH -> "Mes"
                            HistoryFilter.ALL -> "Todos"
                        }
                    )
                }
            )
        }
    }
}

@Composable
fun GymExerciseCard(gymExercise: GymExercise) {
    var expanded by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(PaddingDim.MEDIUM)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Entrenamiento Gym",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${gymExercise.dayOfWeek}, ${dateFormat.format(Date(gymExercise.date))}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Icon(
                    imageVector = if (expanded) FitlogIcons.ArrowUp else FitlogIcons.ArrowDown,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.height(PaddingDim.SMALL))

            Row(modifier = Modifier.fillMaxWidth()) {
                StatItem(label = "Tiempo", value = formatElapsedTime(gymExercise.elapsedTime))
                Spacer(modifier = Modifier.width(PaddingDim.MEDIUM))
                StatItem(label = "Calorías", value = "${gymExercise.totalCalories} kcal")
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = PaddingDim.MEDIUM)) {
                    Divider(modifier = Modifier.padding(vertical = PaddingDim.SMALL))
                    gymExercise.exercises.forEach { exercise ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = exercise.name, fontWeight = FontWeight.Medium)
                            Text(text = "${exercise.sets}x${exercise.reps} - ${exercise.weightKg}kg")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutSessionCard(session: WorkoutSession) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val calendar = remember { Calendar.getInstance().apply { timeInMillis = session.date } }
    val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) ?: ""

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(PaddingDim.MEDIUM)) {
            Text(
                text = "Sesión de Cardio/Pasos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$dayOfWeek, ${dateFormat.format(Date(session.date))}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(PaddingDim.SMALL))
            Row(modifier = Modifier.fillMaxWidth()) {
                StatItem(label = "Pasos", value = session.steps.toString())
                Spacer(modifier = Modifier.width(PaddingDim.MEDIUM))
                StatItem(label = "Distancia", value = String.format("%.2f km", session.distance))
                Spacer(modifier = Modifier.width(PaddingDim.MEDIUM))
                StatItem(label = "Calorías", value = "${session.calories} kcal")
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

sealed class HistoryItem(val date: Long) {
    data class Gym(val exercise: GymExercise) : HistoryItem(exercise.date)
    data class Session(val session: WorkoutSession) : HistoryItem(session.date)
}

private fun <T> filterData(data: List<T>, filter: HistoryFilter, dateSelector: (T) -> Long): List<T> {
    val now = Calendar.getInstance()
    val today = now.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    return when (filter) {
        HistoryFilter.TODAY -> data.filter { dateSelector(it) >= today }
        HistoryFilter.LAST_WEEK -> {
            val weekAgo = today - (7 * 24 * 60 * 60 * 1000L)
            data.filter { dateSelector(it) >= weekAgo }
        }
        HistoryFilter.LAST_MONTH -> {
            val monthAgo = today - (30 * 24 * 60 * 60 * 1000L)
            data.filter { dateSelector(it) >= monthAgo }
        }
        HistoryFilter.ALL -> data
    }
}

private fun formatElapsedTime(seconds: Long): String {
    return DateUtils.formatElapsedTime(seconds)
}
