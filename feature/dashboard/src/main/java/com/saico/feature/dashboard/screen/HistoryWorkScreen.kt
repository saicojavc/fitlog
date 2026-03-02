package com.saico.feature.dashboard.screen

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.saico.core.common.util.UnitsConverter
import com.saico.core.model.GymExercise
import com.saico.core.model.UnitsConfig
import com.saico.core.model.WorkoutSession
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.LightBackground
import com.saico.core.ui.theme.LightPrimary
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.dashboard.state.DashboardUiState
import com.saico.feature.dashboard.state.HistoryFilter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HistoryWorkScreen(
    uiState: DashboardUiState,
    onFilterSelected: (HistoryFilter) -> Unit,
    onExportPdf: () -> Unit
) {
    val units = uiState.userData?.unitsConfig ?: UnitsConfig.METRIC


    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onExportPdf,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = FitlogIcons.Download,
                    contentDescription = null
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
        ) {
            HistoryContent(
                uiState = uiState,
                units = units,
                onFilterSelected = onFilterSelected
            )
        }
    }
}

@Composable
fun HistoryContent(
    uiState: DashboardUiState,
    units: UnitsConfig,
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

    val groupedHistory = remember(combinedHistory, uiState.selectedFilter) {
        val locale = Locale.getDefault()
        combinedHistory.groupBy { item ->
            getHeaderLabelString(item.date, uiState.selectedFilter, locale)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        FilterRow(
            selectedFilter = uiState.selectedFilter,
            onFilterSelected = onFilterSelected
        )
        if (combinedHistory.isEmpty()) {
            EmptyHistoryState(filter = uiState.selectedFilter)
        } else {
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

                groupedHistory.forEach { (header, items) ->
                    if (header != null) {
                        item(key = header) {
                            FitlogText(
                                text = header,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 4.dp)
                            )
                        }
                    }

                    items(items) { item ->
                        when (item) {
                            is HistoryItem.Gym -> GymExerciseCard(
                                gymExercise = item.exercise,
                                units = units
                            )

                            is HistoryItem.Session -> WorkoutSessionCard(
                                session = item.session,
                                units = units
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getHeaderLabelString(date: Long, filter: HistoryFilter, locale: Locale): String? {
    val cal = Calendar.getInstance().apply { timeInMillis = date }
    return when (filter) {
        HistoryFilter.TODAY -> {
            // "Lunes"
            SimpleDateFormat("EEEE", locale).format(Date(date)).replaceFirstChar { it.uppercase() }
        }

        HistoryFilter.LAST_WEEK -> {
            // "Lunes 12"
            SimpleDateFormat("EEEE d", locale).format(Date(date))
                .replaceFirstChar { it.uppercase() }
        }

        HistoryFilter.LAST_MONTH -> {
            // "Lunes 12 - Semana X de Mes"
            val dayInfo = SimpleDateFormat("EEEE d", locale).format(Date(date))
                .replaceFirstChar { it.uppercase() }
            val weekOfMonth = cal.get(Calendar.WEEK_OF_MONTH)
            val monthName = SimpleDateFormat("MMMM", locale).format(Date(date))
                .replaceFirstChar { it.uppercase() }
            "$dayInfo - Semana $weekOfMonth de $monthName"
        }

        HistoryFilter.ALL -> {
            // "Enero 2026"
            SimpleDateFormat("MMMM yyyy", locale).format(Date(date))
                .replaceFirstChar { it.uppercase() }
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
        HistoryFilter.TODAY -> stringResource(id = R.string.today)
        HistoryFilter.LAST_WEEK -> stringResource(id = R.string.this_week)
        HistoryFilter.LAST_MONTH -> stringResource(id = R.string.this_month)
        HistoryFilter.ALL -> stringResource(id = R.string.total_history)
    }

    // Usamos la FitlogCard traslúcida que ya configuramos
    FitlogCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        // Mantenemos el radio de la web para consistencia
        shape = RoundedCornerShape(24.dp),
        // El borde sutil le da el toque "Premium" contra las partículas
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingDim.MEDIUM)
        ) {
            // Título con un pequeño indicador de color (Cian de la web)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(4.dp, 16.dp)
                        .background(Color(0xFF3FB9F6), RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                FitlogText(
                    text = "${stringResource(id = R.string.summary_resumen)} $filterText",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(PaddingDim.MEDIUM))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Calorías con tono naranja neón
                SummaryStat(
                    label = stringResource(id = R.string.total_calories),
                    value = "$totalCalories",
                    unit = "kcal",
                    icon = FitlogIcons.Fire,
                    tint = Color(0xFFFF6F00).copy(alpha = 0.9f)
                )

                // Divisor vertical sutil entre stats
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(30.dp)
                        .background(Color.White.copy(alpha = 0.1f))
                )

                // Tiempo con tono blanco/azul claro
                SummaryStat(
                    label = stringResource(id = R.string.active_time),
                    value = formatElapsedTime(totalTimeSeconds),
                    unit = "",
                    icon = FitlogIcons.Clock,
                    tint = Color(0xFF3FB9F6).copy(alpha = 0.8f)
                )
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
        FitlogIcon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(46.dp),
            tint = tint,
            background = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(PaddingDim.SMALL))
        Column {
            FitlogText(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            Row(verticalAlignment = Alignment.Bottom) {
                FitlogText(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )
                if (unit.isNotEmpty()) {
                    FitlogText(
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
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.Black.copy(alpha = 0.3f),
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                ),
                label = {
                    FitlogText(
                        text = when (filter) {
                            HistoryFilter.TODAY -> stringResource(id = R.string.today)
                            HistoryFilter.LAST_WEEK -> stringResource(id = R.string.week)
                            HistoryFilter.LAST_MONTH -> stringResource(id = R.string.month)
                            HistoryFilter.ALL -> stringResource(id = R.string.all)
                        }
                    )
                }
            )
        }
    }
}

@Composable
fun GymExerciseCard(gymExercise: GymExercise, units: UnitsConfig) {
    var expanded by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    FitlogCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, LightBackground.copy(alpha = 0.7f))
    ) {
        Column(modifier = Modifier.padding(PaddingDim.MEDIUM)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    FitlogText(
                        text = stringResource(id = R.string.gym_workout),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    FitlogText(
                        text = "${gymExercise.dayOfWeek}, ${dateFormat.format(Date(gymExercise.date))}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                FitlogIcon(
                    imageVector = if (expanded) FitlogIcons.ArrowUp else FitlogIcons.ArrowDown,
                    contentDescription = null,
                    background = Color.Unspecified,
                )
            }

            Spacer(modifier = Modifier.height(PaddingDim.SMALL))

            Row(modifier = Modifier.fillMaxWidth()) {
                StatItem(
                    label = stringResource(id = R.string.elapsed_time),
                    value = formatElapsedTime(gymExercise.elapsedTime)
                )
                Spacer(modifier = Modifier.width(PaddingDim.MEDIUM))
                StatItem(
                    label = stringResource(id = R.string.calories),
                    value = "${gymExercise.totalCalories} kcal"
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = PaddingDim.MEDIUM)) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = PaddingDim.SMALL))
                    gymExercise.exercises.forEach { exercise ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Nombre del ejercicio con peso para permitir salto de línea
                            FitlogText(
                                text = exercise.name,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(PaddingDim.MEDIUM))
                            // Detalles técnicos alineados a la derecha
                            FitlogText(
                                text = "${exercise.sets}x${exercise.reps} - ${
                                    UnitsConverter.formatWeight(
                                        exercise.weightKg,
                                        units
                                    )
                                }",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutSessionCard(session: WorkoutSession, units: UnitsConfig) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val calendar = remember { Calendar.getInstance().apply { timeInMillis = session.date } }
    val dayOfWeek =
        calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) ?: ""

    FitlogCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, LightBackground.copy(alpha = 0.7f))
    ) {
        Column(modifier = Modifier.padding(PaddingDim.MEDIUM)) {
            FitlogText(
                text = stringResource(id = R.string.cardio_session),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            FitlogText(
                text = "$dayOfWeek, ${dateFormat.format(Date(session.date))}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(PaddingDim.SMALL))
            Row(modifier = Modifier.fillMaxWidth()) {
                StatItem(
                    label = stringResource(id = R.string.daily_steps),
                    value = session.steps.toString()
                )
                Spacer(modifier = Modifier.width(PaddingDim.MEDIUM))
                StatItem(
                    label = stringResource(id = R.string.distance),
                    value = UnitsConverter.formatDistance(session.distance.toDouble(), units)
                )
                Spacer(modifier = Modifier.width(PaddingDim.MEDIUM))
                StatItem(
                    label = stringResource(id = R.string.calories),
                    value = "${session.calories} kcal"
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column {
        FitlogText(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FitlogText(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

sealed class HistoryItem(val date: Long) {
    data class Gym(val exercise: GymExercise) : HistoryItem(exercise.date)
    data class Session(val session: WorkoutSession) : HistoryItem(session.date)
}

private fun <T> filterData(
    data: List<T>,
    filter: HistoryFilter,
    dateSelector: (T) -> Long
): List<T> {
    val cal = Calendar.getInstance()
    // Resetear a 00:00:00.000 de hoy
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    return when (filter) {
        HistoryFilter.TODAY -> {
            data.filter { dateSelector(it) >= cal.timeInMillis }
        }

        HistoryFilter.LAST_WEEK -> {
            // Ajustar al lunes de la semana actual
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            // Si hoy es domingo, retroceder 7 días para estar en la semana que acaba
            if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                cal.add(Calendar.DAY_OF_YEAR, -7)
            }
            data.filter { dateSelector(it) >= cal.timeInMillis }
        }

        HistoryFilter.LAST_MONTH -> {
            // Ajustar al día 1 del mes actual
            cal.set(Calendar.DAY_OF_MONTH, 1)
            data.filter { dateSelector(it) >= cal.timeInMillis }
        }

        HistoryFilter.ALL -> data
    }
}

private fun formatElapsedTime(seconds: Long): String {
    return DateUtils.formatElapsedTime(seconds)
}

@Composable
fun EmptyHistoryState(filter: HistoryFilter) {
    val message = when (filter) {
        HistoryFilter.TODAY -> stringResource(R.string.today_message)
        HistoryFilter.LAST_WEEK -> stringResource(R.string.week_message)
        HistoryFilter.LAST_MONTH -> stringResource(R.string.month_message)
        HistoryFilter.ALL -> stringResource(R.string.all_message)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingDim.EXTRA_LARGE),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icono con efecto de profundidad y gradiente
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(LightPrimary.copy(alpha = 0.2f), Color.Transparent)
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = FitlogIcons.History, // O un icono de "Fitness"
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.White.copy(alpha = 0.4f)
            )
        }

        Spacer(modifier = Modifier.height(PaddingDim.LARGE))

        FitlogText(
            text = stringResource(R.string.no_logs),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(PaddingDim.SMALL))

        FitlogText(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}
