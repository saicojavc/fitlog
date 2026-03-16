package com.saico.feature.setting


import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.saico.core.model.LanguageConfig
import com.saico.core.model.UnitsConfig
import com.saico.core.model.UserData
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.FitlogTopAppBar
import com.saico.core.ui.components.GravityParticlesBackground
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.navigation.routes.about.AboutRoute
import com.saico.core.ui.theme.PaddingDim
import com.saico.core.ui.theme.techBlue
import com.saico.feature.setting.state.SettingUiState
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavHostController,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo de partículas dinámico
        GravityParticlesBackground()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent, // Importante para ver las partículas
            topBar = {
                FitlogTopAppBar(
                    title = stringResource(id = R.string.settings),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black.copy(alpha = 0.4f)
                    ),
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = FitlogIcons.ArrowBack,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                )
            },
            bottomBar = {
                TextButton(
                    onClick = { navController.navigate(AboutRoute.AboutScreenRoute.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = PaddingDim.VERY_HUGE)
                ) {
                    FitlogText(
                        text = stringResource(id = R.string.about_me),
                        style = MaterialTheme.typography.titleMedium,
                        color = techBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(PaddingDim.MEDIUM)
            ) {
                when (val state = uiState) {
                    is SettingUiState.Loading -> { /* Shimmer o loader azul */
                    }

                    is SettingUiState.Success -> {
                        SettingsContent(
                            settings = state.settings,
                            onLanguageChange = viewModel::updateLanguageConfig,
                            onUnitsChange = viewModel::updateUnitsConfig,
                            onTimeChange = viewModel::updateWorkoutReminderTime,
                            onEnabledChange = viewModel::updateWorkoutReminderEnabled,
                            onDaysChange = viewModel::updateWorkoutReminderDays
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SettingsContent(
    settings: UserData,
    onLanguageChange: (LanguageConfig) -> Unit,
    onUnitsChange: (UnitsConfig) -> Unit,
    onTimeChange: (Int, Int) -> Unit,
    onEnabledChange: (Boolean) -> Unit,
    onDaysChange: (Set<Int>) -> Unit
) {
    val context = LocalContext.current
    val isSystem24Hour = remember { DateFormat.is24HourFormat(context) }
    var showTimePicker by remember { mutableStateOf(false) }

    val activeColor = Color(0xFF3FB9F6) // Azul principal de tu BottomColor

    if (showTimePicker) {
        FitlogTimePickerDialog(
            initialHour = settings.workoutReminderHour,
            initialMinute = settings.workoutReminderMinute,
            initialDays = settings.workoutReminderDays,
            onDismissRequest = { showTimePicker = false },
            onConfirm = { hour, minute, days ->
                onTimeChange(hour, minute)
                onDaysChange(days)
                showTimePicker = false
            }
        )
    }

    // --- SECCIÓN NOTIFICACIONES ---
    FitlogCard(modifier = Modifier.padding(vertical = PaddingDim.SMALL)) {
        SettingSectionTitle(
            title = stringResource(id = R.string.notifications).uppercase(),
            icon = FitlogIcons.Notifications
        )
        
        // Fila del Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingDim.MEDIUM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FitlogText(
                text = stringResource(id = R.string.workout_reminder),
                color = Color.White
            )
            Switch(
                checked = settings.workoutReminderEnabled,
                onCheckedChange = onEnabledChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = activeColor,
                    checkedBorderColor = activeColor,
                    uncheckedThumbColor = Color.White.copy(alpha = 0.4f),
                    uncheckedTrackColor = Color.White.copy(alpha = 0.1f),
                    uncheckedBorderColor = Color.White.copy(alpha = 0.2f)
                )
            )
        }

        // Fila del TimePicker (solo clickeable si está habilitado)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = settings.workoutReminderEnabled) { showTimePicker = true }
                .padding(PaddingDim.MEDIUM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                FitlogText(
                    text = String.format("%02d:%02d", settings.workoutReminderHour, settings.workoutReminderMinute),
                    color = if (settings.workoutReminderEnabled) Color.White else Color.White.copy(alpha = 0.3f)
                )
                FitlogText(
                    text = if (settings.workoutReminderDays.isEmpty()) "TODOS LOS DÍAS" 
                           else getFormattedDays(settings.workoutReminderDays),
                    color = if (settings.workoutReminderEnabled) activeColor else activeColor.copy(alpha = 0.2f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(
                imageVector = FitlogIcons.Clock, 
                contentDescription = null, 
                tint = if (settings.workoutReminderEnabled) activeColor else Color.White.copy(alpha = 0.2f)
            )
        }
    }

    // --- IDIOMA Y UNIDADES ---
    FitlogCard(modifier = Modifier.padding(vertical = PaddingDim.SMALL)) {
        SettingSectionTitle(
            title = stringResource(id = R.string.language).uppercase(),
            icon = FitlogIcons.Language
        )
        FitlogSettingDropdown(
            label = stringResource(id = R.string.language),
            selectedOption = settings.languageConfig,
            options = listOf(
                LanguageConfig.FOLLOW_SYSTEM to stringResource(id = R.string.follow_system),
                LanguageConfig.ENGLISH to stringResource(id = R.string.english),
                LanguageConfig.SPANISH to stringResource(id = R.string.spanish)
            ),
            onOptionSelected = onLanguageChange
        )
    }

    FitlogCard(modifier = Modifier.padding(vertical = PaddingDim.SMALL)) {
        SettingSectionTitle(
            title = stringResource(id = R.string.measurement_units).uppercase(),
            icon = FitlogIcons.Settings
        )
        FitlogSettingDropdown(
            label = stringResource(id = R.string.measurement_units),
            selectedOption = settings.unitsConfig,
            options = listOf(
                UnitsConfig.METRIC to stringResource(id = R.string.metric_system),
                UnitsConfig.IMPERIAL to stringResource(id = R.string.imperial_system)
            ),
            onOptionSelected = onUnitsChange
        )
    }
}

private fun getFormattedDays(days: Set<Int>): String {
    val dayNames = listOf("DOM", "LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB")
    return days.sorted().joinToString(", ") { dayNames[it - 1] }
}

@Composable
fun FitlogTimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Int, Int, Set<Int>) -> Unit,
    initialHour: Int,
    initialMinute: Int,
    initialDays: Set<Int>
) {
    var selectedHour by remember { mutableIntStateOf(initialHour) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute) }
    var selectedDays by remember { mutableStateOf(initialDays) }
    
    val blueGradient = Brush.horizontalGradient(listOf(Color(0xFF3FB9F6), Color(0xFF216EE0)))

    Dialog(onDismissRequest = onDismissRequest) {
        FitlogCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(32.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FitlogText(
                    text = stringResource(id = R.string.workout_reminder).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TimeNumberColumn(
                        value = selectedHour,
                        range = 0..23,
                        onValueChange = { selectedHour = it })
                    FitlogText(
                        text = ":",
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    TimeNumberColumn(
                        value = selectedMinute,
                        range = 0..59,
                        onValueChange = { selectedMinute = it })
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Selector de días
                DaysOfWeekSelector(
                    selectedDays = selectedDays,
                    onDaysChanged = { selectedDays = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onConfirm(selectedHour, selectedMinute, selectedDays) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, CircleShape, spotColor = Color(0xFF3FB9F6)),
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
                            stringResource(id = R.string.done).uppercase(),
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DaysOfWeekSelector(
    selectedDays: Set<Int>,
    onDaysChanged: (Set<Int>) -> Unit
) {
    val days = listOf(
        Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, 
        Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY
    )
    val dayLabels = listOf("L", "M", "M", "J", "V", "S", "D")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        days.forEachIndexed { index, day ->
            val isSelected = selectedDays.contains(day)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) techBlue else Color.White.copy(alpha = 0.1f))
                    .clickable {
                        val newSelection = if (isSelected) {
                            selectedDays - day
                        } else {
                            selectedDays + day
                        }
                        onDaysChanged(newSelection)
                    },
                contentAlignment = Alignment.Center
            ) {
                FitlogText(
                    text = dayLabels[index],
                    color = if (isSelected) Color.Black else Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TimeNumberColumn(value: Int, range: IntRange, onValueChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = { if (value < range.last) onValueChange(value + 1) else onValueChange(range.first) }) {
            Icon(Icons.Default.KeyboardArrowUp, null, tint = Color(0xFF10B981))
        }
        Text(
            text = String.format("%02d", value),
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Light, // El peso ligero da elegancia
            color = Color.White
        )
        IconButton(onClick = { if (value > range.first) onValueChange(value - 1) else onValueChange(range.last) }) {
            Icon(Icons.Default.KeyboardArrowDown, null, tint = Color(0xFF10B981))
        }
    }
}

@Composable
fun SettingSectionTitle(title: String, icon: ImageVector? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = PaddingDim.MEDIUM, vertical = PaddingDim.SMALL)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF94A3B8), // Cool Gray
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = PaddingDim.SMALL)
            )
        }
        FitlogText(
            text = title,
            style = MaterialTheme.typography.titleSmall, // Un poco más pequeño para elegancia
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF94A3B8) // Cool Gray para un look sobrio
        )
    }
}

@Composable
fun SettingOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = PaddingDim.MEDIUM, vertical = PaddingDim.SMALL),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        FitlogText(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) Color.White else Color.White.copy(alpha = 0.6f)
        )
        RadioButton(
            selected = selected, onClick = onClick, colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF10B981), // Emerald Green
                unselectedColor = Color.White.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
fun <T> FitlogSettingDropdown(
    label: String,
    options: List<Pair<T, String>>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currentLabel = options.find { it.first == selectedOption }?.second ?: ""


    Column(modifier = Modifier.padding(PaddingDim.MEDIUM)) {
        FitlogText(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.7f), // Un poco más sutil
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box {
            // "Botón" del selector estilo Glassmorphism
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.05f)) // Fondo traslúcido muy leve
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { expanded = true }
                    .padding(horizontal = PaddingDim.MEDIUM),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FitlogText(
                    text = currentLabel,
                    color = techBlue, // Ahora en azul tecnológico
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (expanded) FitlogIcons.KeyboardArrowUp else FitlogIcons.KeyboardArrowDown,
                    contentDescription = null,
                    tint = techBlue // Icono también en azul
                )
            }

            // El menú desplegable con estética oscura
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(Color(0xFF0D1424).copy(alpha = 0.95f)) // Azul profundo casi opaco para lectura
                    .border(
                        width = 1.dp,
                        color = techBlue.copy(alpha = 0.3f), // Borde azul sutil
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                options.forEach { (value, text) ->
                    DropdownMenuItem(
                        text = {
                            FitlogText(
                                text = text,
                                color = if (value == selectedOption) techBlue else Color.White,
                                fontWeight = if (value == selectedOption) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            onOptionSelected(value)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
