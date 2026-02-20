package com.saico.feature.weighttracking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.saico.core.common.util.UnitsConverter
import com.saico.core.model.BmiStatus
import com.saico.core.model.UnitsConfig
import com.saico.core.model.WeightEntry
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.FitlogTopAppBar
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.CardBackground
import com.saico.core.ui.theme.CoolGray
import com.saico.core.ui.theme.DarkBackground
import com.saico.core.ui.theme.EmeraldGreen
import com.saico.feature.weighttracking.state.WeightTrackingUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightTrackingScreen(
    navController: NavHostController, viewModel: WeightTrackingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(), topBar = {
            FitlogTopAppBar(
                title = stringResource(id = R.string.summary),
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
                })
        }) { paddingValues ->
        WeightTrackingContent(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(DarkBackground, Color(0xFF064E3B))))
                .padding(paddingValues),
            uiState = uiState,
            onRegisterWeight = viewModel::registerNewWeight
        )
    }
}

@Composable
fun WeightTrackingContent(
    modifier: Modifier = Modifier,
    uiState: WeightTrackingUiState,
    onRegisterWeight: (String) -> Unit
) {
    var weightInput by remember { mutableStateOf("") }
    val profile = uiState.userProfile
    val units = uiState.unitsConfig

    val currentWeightDisplay = remember(profile?.weightKg, units) {
        if (profile == null) 0.0
        else if (units == UnitsConfig.METRIC) profile.weightKg
        else UnitsConverter.kgToLb(profile.weightKg)
    }

    val bmiValue = remember(profile?.weightKg, profile?.heightCm) {
        if (profile == null || profile.heightCm <= 0) 0f
        else (profile.weightKg / (profile.heightCm / 100.0).pow(2)).toFloat()
    }

    val bmiStatus = when {
        bmiValue < 18.5f -> BmiStatus.LOW_WEIGHT
        bmiValue < 25.0f -> BmiStatus.NORMAL
        bmiValue < 30.0f -> BmiStatus.OVERWEIGHT
        else -> BmiStatus.OBESE
    }

    val bodyFatValue = remember(bmiValue, profile?.age, profile?.gender) {
        if (profile == null || profile.age <= 0) 0f
        else {
            val isMale = profile.gender.lowercase().contains("male") && !profile.gender.lowercase()
                .contains("female")
            val genderFactor = if (isMale) 1 else 0
            ((1.20 * bmiValue) + (0.23 * profile.age) - (10.8 * genderFactor) - 5.4).toFloat()
        }
    }

    val weightLabel = if (units == UnitsConfig.METRIC) "kg" else "lb"

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FitlogText(
            text = stringResource(id = R.string.weight_evolution),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black, letterSpacing = 1.sp
            ),
            color = Color.White,
            modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
        )

        WeightTrackerCardReal(
            currentWeight = currentWeightDisplay.toFloat(),
            unit = weightLabel,
            bmiValue = bmiValue,
            bmiStatus = bmiStatus,
            bodyFatValue = bodyFatValue
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.weightHistory.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    FitlogText(
                        text = stringResource(id = R.string.recent_history),
                        style = MaterialTheme.typography.labelMedium,
                        color = CoolGray,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        WeightLineChartReal(uiState.weightHistory, units)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text(stringResource(id = R.string.new_weight_label, weightLabel)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = EmeraldGreen,
                        focusedBorderColor = EmeraldGreen,
                        unfocusedBorderColor = CoolGray.copy(alpha = 0.3f),
                        focusedLabelColor = EmeraldGreen,
                        unfocusedLabelColor = CoolGray
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (weightInput.isNotBlank()) {
                            onRegisterWeight(weightInput)
                            weightInput = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    FitlogText(
                        text = stringResource(id = R.string.register_weight),
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HistoryList(uiState.weightHistory, units)

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun HistoryList(history: List<WeightEntry>, units: UnitsConfig) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 8.dp)) {
            Icon(
                FitlogIcons.History,
                contentDescription = null,
                tint = CoolGray,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            FitlogText(
                text = stringResource(id = R.string.full_history),
                style = MaterialTheme.typography.labelMedium,
                color = CoolGray,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) FitlogIcons.ArrowUp else FitlogIcons.ArrowDown,
                contentDescription = null,
                tint = CoolGray,
                modifier = Modifier.size(20.dp)
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                history.forEach { entry ->
                    val displayW =
                        if (units == UnitsConfig.METRIC) entry.weight else UnitsConverter.kgToLb(
                            entry.weight
                        )
                    val unit = if (units == UnitsConfig.METRIC) "kg" else "lb"

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f))
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FitlogText(
                                text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(
                                    Date(entry.date)
                                ), color = Color.White, style = MaterialTheme.typography.bodyMedium
                            )
                            FitlogText(
                                text = "%.1f %s".format(displayW, unit),
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeightTrackerCardReal(
    currentWeight: Float, unit: String, bmiValue: Float, bmiStatus: BmiStatus, bodyFatValue: Float
) {
    val statusColors = mapOf(
        BmiStatus.LOW_WEIGHT to Color(0xFFFACC15),
        BmiStatus.NORMAL to Color(0xFF10B981),
        BmiStatus.OVERWEIGHT to Color(0xFFFF6F00),
        BmiStatus.OBESE to Color(0xFFEF4444)
    )

    val statusColor = statusColors[bmiStatus] ?: CoolGray

    val statusText = when (bmiStatus) {
        BmiStatus.LOW_WEIGHT -> stringResource(id = R.string.bmi_underweight)
        BmiStatus.NORMAL -> stringResource(id = R.string.bmi_normal)
        BmiStatus.OVERWEIGHT -> stringResource(id = R.string.bmi_overweight)
        BmiStatus.OBESE -> stringResource(id = R.string.bmi_obese)
        else -> ""
    }

    FitlogCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    FitlogText(
                        text = stringResource(id = R.string.current_weight),
                        style = MaterialTheme.typography.labelSmall,
                        color = CoolGray
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        FitlogText(
                            text = "%.1f".format(currentWeight),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        FitlogText(
                            text = " $unit",
                            style = MaterialTheme.typography.titleMedium,
                            color = CoolGray,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
                ) {
                    FitlogText(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(16.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FitlogText(
                        text = "IMC", style = MaterialTheme.typography.labelSmall, color = CoolGray
                    )
                    FitlogText(
                        text = "%.1f".format(bmiValue),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(30.dp)
                        .background(Color.White.copy(alpha = 0.1f))
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FitlogText(
                        text = stringResource(R.string.body_fat_est),
                        style = MaterialTheme.typography.labelSmall,
                        color = CoolGray
                    )
                    FitlogText(
                        text = "%.1f%%".format(bodyFatValue),
                        style = MaterialTheme.typography.titleMedium,
                        color = EmeraldGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FitlogText(
                        text = stringResource(id = R.string.bmi_status_label),
                        style = MaterialTheme.typography.bodySmall,
                        color = CoolGray
                    )
                    FitlogText(
                        text = "%.1f".format(bmiValue),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                WeightProgressBarCustom(
                    bmiValue = bmiValue, statusColors = statusColors
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FitlogText(
                        text = stringResource(R.string.bmi_underweight),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColors[BmiStatus.LOW_WEIGHT]!!
                    )
                    FitlogText(
                        text = stringResource(R.string.bmi_normal),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColors[BmiStatus.NORMAL]!!
                    )
                    FitlogText(
                        text = stringResource(R.string.bmi_overweight),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColors[BmiStatus.OVERWEIGHT]!!
                    )
                    FitlogText(
                        text = stringResource(R.string.bmi_obese),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColors[BmiStatus.OBESE]!!
                    )
                }
            }
        }
    }
}

@Composable
fun WeightProgressBarCustom(
    bmiValue: Float, statusColors: Map<BmiStatus, Color>
) {
    val minBmi = 10f
    val maxBmi = 40f

    val ranges = listOf(
        18.5f to BmiStatus.LOW_WEIGHT,
        25.0f to BmiStatus.NORMAL,
        30.0f to BmiStatus.OVERWEIGHT,
        40.0f to BmiStatus.OBESE
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp))
    ) {
        val totalRange = maxBmi - minBmi
        var lastMark = minBmi

        ranges.forEach { (mark, status) ->
            val startX = ((lastMark - minBmi) / totalRange) * size.width
            val endX = ((mark - minBmi) / totalRange) * size.width

            drawRect(
                color = statusColors[status] ?: Color.Gray,
                topLeft = Offset(startX, 0f),
                size = Size(endX - startX, size.height)
            )
            lastMark = mark
        }

        val indicatorPosition = ((bmiValue - minBmi) / totalRange).coerceIn(0f, 1f) * size.width

        drawCircle(
            color = Color.White,
            radius = 6.dp.toPx(),
            center = Offset(indicatorPosition, size.height / 2)
        )
        drawCircle(
            color = Color.Black.copy(alpha = 0.2f),
            radius = 7.dp.toPx(),
            center = Offset(indicatorPosition, size.height / 2),
            style = Stroke(1.dp.toPx())
        )
    }
}

@Composable
fun WeightLineChartReal(history: List<WeightEntry>, units: UnitsConfig) {
    val data = history.sortedBy { it.date }.map {
        val w = if (units == UnitsConfig.METRIC) it.weight else UnitsConverter.kgToLb(it.weight)
        w.toFloat() to SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(it.date))
    }

    val textPaint = remember {
        android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE; textSize = 22f; textAlign =
            android.graphics.Paint.Align.CENTER; typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
    }
    val datePaint = remember {
        android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#94A3B8"); textSize = 18f; textAlign =
            android.graphics.Paint.Align.CENTER
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        if (data.size < 2) return@Canvas
        val padding = 30.dp.toPx()
        val spacing = size.width / (data.size - 1)
        val weights = data.map { it.first }
        val maxW = weights.maxOrNull() ?: 1f
        val minW = weights.minOrNull() ?: 0f
        val range = (maxW - minW).coerceAtLeast(1f)

        val points = data.mapIndexed { index, pair ->
            val x = index * spacing
            val normalizedY = if (range == 0f) 0.5f else (pair.first - minW) / range
            val y =
                (size.height - padding) - (normalizedY * (size.height - padding * 2)) - padding / 2
            Offset(x, y)
        }

        for (i in 0 until points.size - 1) {
            drawLine(
                color = EmeraldGreen,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 2.dp.toPx()
            )
        }

        points.forEachIndexed { index, point ->
            drawCircle(color = EmeraldGreen, radius = 3.dp.toPx(), center = point)
            drawContext.canvas.nativeCanvas.drawText(
                "%.1f".format(data[index].first), point.x, point.y - 10.dp.toPx(), textPaint
            )
            drawContext.canvas.nativeCanvas.drawText(
                data[index].second, point.x, size.height, datePaint
            )
        }
    }
}

@Composable
fun ProgressIndicatorWithLabel(label: String, value: String, progress: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            FitlogText(text = label, style = MaterialTheme.typography.bodySmall, color = CoolGray)
            FitlogText(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
