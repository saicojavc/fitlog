package com.saico.feature.weighttracking

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.saico.core.ui.components.FitlogTopAppBar
import com.saico.core.ui.icon.FitlogIcons
import com.saico.feature.weighttracking.state.WeightTrackingUiState
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

// Colores de la App (Consistentes con Dashboard)
val DarkBackground = Color(0xFF0F172A)
val CardBackground = Color(0xFF1E293B).copy(alpha = 0.6f)
val EmeraldGreen = Color(0xFF10B981)
val CoolGray = Color(0xFF94A3B8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightTrackingScreen(
    navController: NavHostController,
    viewModel: WeightTrackingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
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
                }
            )
        }
    ) { paddingValues ->
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

    // Cálculos de salud reales
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

    val weightLabel = if (units == UnitsConfig.METRIC) "kg" else "lb"

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Evolución de Peso",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            ),
            color = Color.White,
            modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
        )

        // Card Real
        WeightTrackerCardReal(
            currentWeight = currentWeightDisplay.toFloat(),
            unit = weightLabel,
            bmiValue = bmiValue,
            bmiStatus = bmiStatus
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Gráfico Real
        if (uiState.weightHistory.size >= 2) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "HISTORIAL RECIENTE",
                        style = MaterialTheme.typography.labelMedium,
                        color = CoolGray,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                        WeightLineChartReal(uiState.weightHistory.takeLast(7), units)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Entrada de Datos
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Nuevo peso ($weightLabel)") },
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
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("REGISTRAR PESO", fontWeight = FontWeight.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de Historial
        HistoryList(uiState.weightHistory, units)
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun HistoryList(history: List<WeightEntry>, units: UnitsConfig) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(FitlogIcons.History, contentDescription = null, tint = CoolGray, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("HISTORIAL COMPLETO", style = MaterialTheme.typography.labelMedium, color = CoolGray)
        }
        
        history.forEach { entry ->
            val displayW = if (units == UnitsConfig.METRIC) entry.weight else UnitsConverter.kgToLb(entry.weight)
            val unit = if (units == UnitsConfig.METRIC) "kg" else "lb"
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(entry.date)),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
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

@Composable
fun WeightTrackerCardReal(
    currentWeight: Float,
    unit: String,
    bmiValue: Float,
    bmiStatus: BmiStatus
) {
    val statusColor = when (bmiStatus) {
        BmiStatus.LOW_WEIGHT -> Color(0xFFFACC15)
        BmiStatus.NORMAL -> Color(0xFF10B981)
        BmiStatus.OVERWEIGHT -> Color(0xFFFF6F00)
        BmiStatus.OBESE -> Color(0xFFEF4444)
        else -> CoolGray
    }

    FitlogCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("PESO ACTUAL", style = MaterialTheme.typography.labelSmall, color = CoolGray)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("%.1f".format(currentWeight), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, color = Color.White)
                        Text(" $unit", style = MaterialTheme.typography.titleMedium, color = CoolGray, modifier = Modifier.padding(bottom = 6.dp))
                    }
                }
                Surface(color = statusColor.copy(alpha = 0.15f), shape = CircleShape, border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))) {
                    Text(bmiStatus.name.replace("_", " "), modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium, color = statusColor, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            ProgressIndicatorWithLabel(label = "BMI", value = "%.1f".format(bmiValue), progress = (bmiValue / 40f).coerceIn(0f, 1f), color = statusColor)
        }
    }
}

@Composable
fun WeightLineChartReal(history: List<WeightEntry>, units: UnitsConfig) {
    // Ordenamos cronológicamente para el gráfico (más antiguo a la izquierda)
    val data = history.sortedBy { it.date }.map {
        val w = if (units == UnitsConfig.METRIC) it.weight else UnitsConverter.kgToLb(it.weight)
        w.toFloat() to SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(it.date))
    }
    
    val textPaint = remember { android.graphics.Paint().apply { color = android.graphics.Color.WHITE; textSize = 24f; textAlign = android.graphics.Paint.Align.CENTER; typeface = android.graphics.Typeface.DEFAULT_BOLD } }
    val datePaint = remember { android.graphics.Paint().apply { color = android.graphics.Color.parseColor("#94A3B8"); textSize = 20f; textAlign = android.graphics.Paint.Align.CENTER } }

    Canvas(modifier = Modifier.fillMaxSize()) {
        if (data.size < 2) return@Canvas
        val padding = 40.dp.toPx()
        val spacing = size.width / (data.size - 1)
        val weights = data.map { it.first }
        val maxW = weights.maxOrNull() ?: 1f
        val minW = weights.minOrNull() ?: 0f
        val range = (maxW - minW).coerceAtLeast(1f)

        val points = data.mapIndexed { index, pair ->
            val x = index * spacing
            val normalizedY = if (range == 0f) 0.5f else (pair.first - minW) / range
            val y = (size.height - padding) - (normalizedY * (size.height - padding * 2)) - padding/2
            Offset(x, y)
        }

        for (i in 0 until points.size - 1) {
            drawLine(color = EmeraldGreen, start = points[i], end = points[i + 1], strokeWidth = 3.dp.toPx())
        }

        points.forEachIndexed { index, point ->
            drawCircle(color = EmeraldGreen, radius = 4.dp.toPx(), center = point)
            drawContext.canvas.nativeCanvas.drawText("%.1f".format(data[index].first), point.x, point.y - 10.dp.toPx(), textPaint)
            drawContext.canvas.nativeCanvas.drawText(data[index].second, point.x, size.height, datePaint)
        }
    }
}

@Composable
fun ProgressIndicatorWithLabel(label: String, value: String, progress: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = CoolGray)
            Text(value, style = MaterialTheme.typography.bodySmall, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.05f))) {
            Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().clip(CircleShape).background(color))
        }
    }
}
