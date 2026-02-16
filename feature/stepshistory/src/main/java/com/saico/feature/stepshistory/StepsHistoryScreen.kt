package com.saico.feature.stepshistory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.saico.core.common.util.FitnessCalculator
import com.saico.core.common.util.UnitsConverter
import com.saico.core.model.Workout
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogTopAppBar
import com.saico.core.ui.components.SpacerHeight
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.GradientColors
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.stepshistory.state.ChartData
import com.saico.feature.stepshistory.state.StepsHistoryFilter
import com.saico.feature.stepshistory.state.StepsHistoryUiState
import java.util.Calendar
import java.util.Locale

@Composable
fun StepsHistoryScreen(
    navController: NavHostController,
    viewModel: StepsHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Content(
        uiState = uiState,
        onBackClick = { navController.popBackStack() },
        onFilterSelected = viewModel::onFilterSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content(
    uiState: StepsHistoryUiState,
    onBackClick: () -> Unit,
    onFilterSelected: (StepsHistoryFilter) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            FitlogTopAppBar(
                title = stringResource(id = R.string.steps_history),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.3f) // Siempre oscuro para consistencia
                ),
                navigationIcon = {
                    FitlogIcon(
                        modifier = Modifier.clickable { onBackClick() },
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
                .background(Brush.verticalGradient(GradientColors)) // Fondo unificado
                .padding(paddingValues)
                .padding(horizontal = PaddingDim.MEDIUM)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SpacerHeight(PaddingDim.MEDIUM)

            // Selector de Filtro Minimalista
            FilterSelector(
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = onFilterSelected
            )

            SpacerHeight(PaddingDim.LARGE)

            val chartData = processData(uiState)

            // Fila de Estadísticas con diseño Glass
            SummaryStatsRow(
                distance = UnitsConverter.formatDistance(chartData.totalDistanceKm.toDouble(), uiState.unitsConfig),
                time = formatMinutes(chartData.totalTimeMinutes)
            )

            SpacerHeight(PaddingDim.LARGE)

            // Gráficos Estilizados
            ChartCard(
                title = stringResource(id = R.string.steps).uppercase(),
                data = chartData.stepsData,
                unit = "",
                accentColor = Color(0xFF10B981) // Esmeralda
            )

            SpacerHeight(PaddingDim.MEDIUM)

            ChartCard(
                title = stringResource(id = R.string.calories).uppercase(),
                data = chartData.caloriesData,
                unit = "kcal",
                accentColor = Color(0xFFFF6F00) // Naranja
            )

            SpacerHeight(PaddingDim.MEDIUM)
        }
    }
}

@Composable
fun SummaryStatsRow(
    distance: String,
    time: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(PaddingDim.MEDIUM)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = stringResource(id = R.string.distance),
            value = distance,
            icon = FitlogIcons.Location
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = stringResource(id = R.string.time),
            value = time,
            icon = FitlogIcons.History
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector
) {
    FitlogCard(
        modifier = modifier,
        color = Color(0xFF1E293B).copy(alpha = 0.6f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(PaddingDim.MEDIUM),
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF10B981)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Light, // Elegancia
                color = Color.White
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSelector(
    selectedFilter: StepsHistoryFilter,
    onFilterSelected: (StepsHistoryFilter) -> Unit
) {
    val options = StepsHistoryFilter.values()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.05f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEach { filter ->
            val isSelected = filter == selectedFilter
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color(0xFF10B981) else Color.Transparent)
                    .clickable { onFilterSelected(filter) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (filter) {
                        StepsHistoryFilter.WEEKLY -> stringResource(R.string.weekly)
                        StepsHistoryFilter.MONTHLY -> stringResource(R.string.monthly)
                        StepsHistoryFilter.YEARLY -> stringResource(R.string.yearly)
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) Color.White else Color(0xFF94A3B8),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun ChartCard(
    title: String,
    data: List<ChartData>,
    unit: String,
    accentColor: Color
) {
    FitlogCard(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1E293B).copy(alpha = 0.6f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(PaddingDim.MEDIUM)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF94A3B8),
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(PaddingDim.LARGE))

            val maxValue = data.maxOfOrNull { it.value } ?: 1f

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                data.forEach { item ->
                    BarItem(
                        item = item,
                        maxValue = maxValue,
                        accentColor = accentColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun BarItem(
    item: ChartData,
    maxValue: Float,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val barHeightRatio = (item.value / maxValue).coerceIn(0.05f, 1f)

    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        if (item.isHighlighted) {
            Text(
                text = if (item.value >= 1000) "${(item.value / 1000).toInt()}k" else item.value.toInt().toString(),
                style = MaterialTheme.typography.labelSmall,
                color = accentColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxHeight(barHeightRatio * 0.7f)
                .width(8.dp) // Más delgadas para ser elegantes
                .clip(CircleShape) // Cápsula completa
                .background(
                    if (item.isHighlighted) accentColor
                    else accentColor.copy(alpha = 0.2f)
                )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (item.isHighlighted) Color.White else Color(0xFF94A3B8),
            fontSize = 10.sp
        )
    }
}

@Composable
fun BarItem(
    item: ChartData,
    maxValue: Float,
    modifier: Modifier = Modifier
) {
    val barHeightRatio = (item.value / maxValue).coerceIn(0.05f, 1f)

    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = if (item.value >= 1000) "${(item.value / 1000).toInt()}k" else item.value.toInt().toString(),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 8.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxHeight(barHeightRatio * 0.8f)
                .width(12.dp)
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(
                    if (item.isHighlighted) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
            fontSize = 8.sp
        )
    }
}

data class ProcessedChartData(
    val stepsData: List<ChartData>,
    val caloriesData: List<ChartData>,
    val totalDistanceKm: Float = 0f,
    val totalTimeMinutes: Int = 0
)

private fun processData(uiState: StepsHistoryUiState): ProcessedChartData {
    return when (uiState.selectedFilter) {
        StepsHistoryFilter.WEEKLY -> processWeekly(uiState)
        StepsHistoryFilter.MONTHLY -> processMonthly(uiState)
        StepsHistoryFilter.YEARLY -> processYearly(uiState)
    }
}

private fun processWeekly(uiState: StepsHistoryUiState): ProcessedChartData {
    val steps = mutableListOf<ChartData>()
    val calories = mutableListOf<ChartData>()
    var totalDistance = 0f
    var totalTime = 0
    
    val today = Calendar.getInstance()
    val startOfWeek = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val uniqueWorkouts = uiState.workouts.associateBy { 
        val cal = Calendar.getInstance().apply { timeInMillis = it.date }
        "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
    }

    for (i in 0..6) {
        val cal = Calendar.getInstance().apply {
            timeInMillis = startOfWeek.timeInMillis
            add(Calendar.DAY_OF_WEEK, i)
        }
        val dateKey = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
        val isToday = cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)
        
        val workout = uniqueWorkouts[dateKey]
        val stepCount = if (isToday) uiState.currentSteps else (workout?.steps ?: 0)
        
        val calorieCount = if (isToday) {
            FitnessCalculator.calculateCaloriesBurned(uiState.currentSteps, uiState.userProfile?.weightKg ?: 0.0)
        } else {
            workout?.calories ?: 0
        }

        val distanceKm = if (isToday) {
            FitnessCalculator.calculateDistanceKm(uiState.currentSteps, uiState.userProfile?.heightCm?.toInt() ?: 170, uiState.userProfile?.gender ?: "male")
        } else {
            workout?.distance?.toFloat() ?: 0f
        }

        val timeMinutes = if (isToday) {
            FitnessCalculator.calculateActiveTimeMinutes(uiState.currentSteps)
        } else {
            FitnessCalculator.calculateActiveTimeMinutes(workout?.steps ?: 0)
        }

        totalDistance += distanceKm
        totalTime += timeMinutes

        val label = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()) ?: ""
        steps.add(ChartData(label.take(1), stepCount.toFloat(), isToday))
        calories.add(ChartData(label.take(1), calorieCount.toFloat(), isToday))
    }
    
    return ProcessedChartData(steps, calories, totalDistance, totalTime)
}

private fun processMonthly(uiState: StepsHistoryUiState): ProcessedChartData {
    val steps = mutableListOf<ChartData>()
    val calories = mutableListOf<ChartData>()
    var totalDistance = 0f
    var totalTime = 0
    
    val cal = Calendar.getInstance()
    val currentMonth = cal.get(Calendar.MONTH)
    val currentYear = cal.get(Calendar.YEAR)
    val todayDay = cal.get(Calendar.DAY_OF_YEAR)

    val workoutsByDay = uiState.workouts
        .filter { 
            val wCal = Calendar.getInstance().apply { timeInMillis = it.date }
            wCal.get(Calendar.MONTH) == currentMonth && wCal.get(Calendar.YEAR) == currentYear
        }
        .associateBy { 
            val wCal = Calendar.getInstance().apply { timeInMillis = it.date }
            wCal.get(Calendar.DAY_OF_YEAR)
        }

    val lastDayOfMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    var d = 1
    while (d <= lastDayOfMonth) {
        val weekCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth)
            set(Calendar.DAY_OF_MONTH, d)
        }
        
        val startDay = d
        val daysUntilSunday = (Calendar.SUNDAY - weekCal.get(Calendar.DAY_OF_WEEK) + 7) % 7
        val endDay = (startDay + daysUntilSunday).coerceAtMost(lastDayOfMonth)
        
        var weekSteps = 0f
        var weekCals = 0f
        var weekDist = 0f
        var weekT = 0
        var isCurrentWeek = false

        for (day in startDay..endDay) {
            val dateCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, currentYear)
                set(Calendar.MONTH, currentMonth)
                set(Calendar.DAY_OF_MONTH, day)
            }
            val dayOfYear = dateCal.get(Calendar.DAY_OF_YEAR)
            val isToday = dayOfYear == todayDay && dateCal.get(Calendar.YEAR) == currentYear

            if (isToday) isCurrentWeek = true

            val workout = workoutsByDay[dayOfYear]
            val s = if (isToday) uiState.currentSteps else (workout?.steps ?: 0)
            val c = if (isToday) FitnessCalculator.calculateCaloriesBurned(s, uiState.userProfile?.weightKg ?: 70.0) else (workout?.calories ?: 0)
            val dist = if (isToday) FitnessCalculator.calculateDistanceKm(s, uiState.userProfile?.heightCm?.toInt() ?: 170, uiState.userProfile?.gender ?: "male") else (workout?.distance?.toFloat() ?: 0f)
            val t = if (isToday) FitnessCalculator.calculateActiveTimeMinutes(s) else FitnessCalculator.calculateActiveTimeMinutes(workout?.steps ?: 0)

            weekSteps += s
            weekCals += c
            weekDist += dist
            weekT += t
        }

        totalDistance += weekDist
        totalTime += weekT

        steps.add(ChartData("$startDay-$endDay", weekSteps, isCurrentWeek))
        calories.add(ChartData("$startDay-$endDay", weekCals, isCurrentWeek))

        d = endDay + 1
    }
    
    return ProcessedChartData(steps, calories, totalDistance, totalTime)
}

private fun processYearly(uiState: StepsHistoryUiState): ProcessedChartData {
    val steps = mutableListOf<ChartData>()
    val calories = mutableListOf<ChartData>()
    var totalDistance = 0f
    var totalTime = 0
    
    val cal = Calendar.getInstance()
    val currentYear = cal.get(Calendar.YEAR)
    val currentMonth = cal.get(Calendar.MONTH)

    val workoutsByMonth = uiState.workouts
        .filter { 
            val wCal = Calendar.getInstance().apply { timeInMillis = it.date }
            wCal.get(Calendar.YEAR) == currentYear
        }
        .groupBy { 
            val wCal = Calendar.getInstance().apply { timeInMillis = it.date }
            wCal.get(Calendar.MONTH)
        }

    for (month in 0..11) {
        val monthWorkouts = workoutsByMonth[month] ?: emptyList()
        val uniqueMonthWorkouts = monthWorkouts.associateBy { 
            val wCal = Calendar.getInstance().apply { timeInMillis = it.date }
            wCal.get(Calendar.DAY_OF_YEAR)
        }.values

        var mSteps = uniqueMonthWorkouts.sumOf { it.steps }.toFloat()
        var mCals = uniqueMonthWorkouts.sumOf { it.calories }.toFloat()
        var mDist = uniqueMonthWorkouts.sumOf { it.distance }.toFloat()
        var mT = uniqueMonthWorkouts.sumOf { FitnessCalculator.calculateActiveTimeMinutes(it.steps) }

        if (month == currentMonth) {
            val todayDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            if (uniqueMonthWorkouts.none { 
                val wCal = Calendar.getInstance().apply { timeInMillis = it.date }
                wCal.get(Calendar.DAY_OF_YEAR) == todayDayOfYear
            }) {
                mSteps += uiState.currentSteps
                mCals += FitnessCalculator.calculateCaloriesBurned(uiState.currentSteps, uiState.userProfile?.weightKg ?: 70.0)
                mDist += FitnessCalculator.calculateDistanceKm(uiState.currentSteps, uiState.userProfile?.heightCm?.toInt() ?: 170, uiState.userProfile?.gender ?: "male")
                mT += FitnessCalculator.calculateActiveTimeMinutes(uiState.currentSteps)
            }
        }

        totalDistance += mDist
        totalTime += mT

        cal.set(Calendar.MONTH, month)
        val label = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) ?: ""
        steps.add(ChartData(label.take(1), mSteps, month == currentMonth))
        calories.add(ChartData(label.take(1), mCals, month == currentMonth))
    }
    
    return ProcessedChartData(steps, calories, totalDistance, totalTime)
}

private fun formatMinutes(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}
