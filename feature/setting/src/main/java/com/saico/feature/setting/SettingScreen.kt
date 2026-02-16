package com.saico.feature.setting


import android.annotation.SuppressLint
import android.text.format.DateFormat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
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
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.FitlogTopAppBar
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.navigation.routes.about.AboutRoute
import com.saico.core.ui.theme.GradientColors
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.setting.state.SettingUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavHostController,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            FitlogTopAppBar(
                title = stringResource(id = R.string.settings),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(GradientColors))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(PaddingDim.MEDIUM)
        ) {
            when (val state = uiState) {
                is SettingUiState.Loading -> { /* Loading indicator */
                }

                is SettingUiState.Success -> {
                    SettingsContent(
                        settings = state.settings,
                        onLanguageChange = viewModel::updateLanguageConfig,
                        onUnitsChange = viewModel::updateUnitsConfig,
                        onTimeChange = viewModel::updateWorkoutReminderTime,
                        navController = navController
                    )
                }
            }
        }
    }
}


@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    settings: UserData,
    onLanguageChange: (LanguageConfig) -> Unit,
    onUnitsChange: (UnitsConfig) -> Unit,
    onTimeChange: (Int, Int) -> Unit,
    navController: NavHostController
) {
    val context = LocalContext.current
    val isSystem24Hour = remember { DateFormat.is24HourFormat(context) }
    var showTimePicker by remember { mutableStateOf(false) }


    if (showTimePicker) {
        FitlogTimePickerDialog(
            initialHour = settings.workoutReminderHour,
            initialMinute = settings.workoutReminderMinute,
            onDismissRequest = { showTimePicker = false },
            onConfirm = { hour, minute ->
                onTimeChange(hour, minute)
                showTimePicker = false
            }
        )
    }

    // Formateo visual de la hora del recordatorio
    val displayTime =
        remember(settings.workoutReminderHour, settings.workoutReminderMinute, isSystem24Hour) {
            if (isSystem24Hour) {
                String.format(
                    "%02d:%02d",
                    settings.workoutReminderHour,
                    settings.workoutReminderMinute
                )
            } else {
                val hour =
                    if (settings.workoutReminderHour % 12 == 0) 12 else settings.workoutReminderHour % 12
                val amPm = if (settings.workoutReminderHour < 12) "AM" else "PM"
                String.format("%d:%02d %s", hour, settings.workoutReminderMinute, amPm)
            }
        }
    FitlogCard(
        modifier = Modifier.padding(vertical = PaddingDim.SMALL),
        color = Color(0xFF1E293B).copy(alpha = 0.6f), // Glassmorphism base
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)) // Borde sutil
    ) {
        SettingSectionTitle(
            title = stringResource(id = R.string.notifications).uppercase(),
            icon = FitlogIcons.Notifications
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showTimePicker = true
                }
                .padding(PaddingDim.MEDIUM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                FitlogText(
                    text = stringResource(id = R.string.workout_reminder),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                FitlogText(
                    text = displayTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF10B981) // Emerald Green para el valor activo
                )
            }

            Icon(
                imageVector = FitlogIcons.Clock,
                contentDescription = null,
                tint = Color(0xFF10B981)
            )
        }
    }

    // --- IDIOMA ---
    FitlogCard(
        modifier = Modifier.padding(vertical = PaddingDim.SMALL),
        color = Color(0xFF1E293B).copy(alpha = 0.6f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        SettingSectionTitle(
            title = stringResource(id = R.string.language).uppercase(),
            icon = FitlogIcons.Language
        )
        Column(modifier = Modifier.padding(bottom = PaddingDim.SMALL)) {
            SettingOption(
                label = stringResource(id = R.string.follow_system),
                selected = settings.languageConfig == LanguageConfig.FOLLOW_SYSTEM,
                onClick = { onLanguageChange(LanguageConfig.FOLLOW_SYSTEM) }
            )
            SettingOption(
                label = stringResource(id = R.string.english),
                selected = settings.languageConfig == LanguageConfig.ENGLISH,
                onClick = { onLanguageChange(LanguageConfig.ENGLISH) }
            )
            SettingOption(
                label = stringResource(id = R.string.spanish),
                selected = settings.languageConfig == LanguageConfig.SPANISH,
                onClick = { onLanguageChange(LanguageConfig.SPANISH) }
            )
        }
    }

    // --- UNIDADES ---
    FitlogCard(
        modifier = Modifier.padding(vertical = PaddingDim.SMALL),
        color = Color(0xFF1E293B).copy(alpha = 0.6f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        SettingSectionTitle(
            title = stringResource(id = R.string.measurement_units).uppercase(),
            icon = FitlogIcons.Straighten // Icono de regla/medida
        )
        Column(modifier = Modifier.padding(bottom = PaddingDim.SMALL)) {
            SettingOption(
                label = stringResource(id = R.string.metric_system),
                selected = settings.unitsConfig == UnitsConfig.METRIC,
                onClick = { onUnitsChange(UnitsConfig.METRIC) }
            )
            SettingOption(
                label = stringResource(id = R.string.imperial_system),
                selected = settings.unitsConfig == UnitsConfig.IMPERIAL,
                onClick = { onUnitsChange(UnitsConfig.IMPERIAL) }
            )

        }
    }

    // --- BOTÓN SOBRIO DE ABOUT ---
    Spacer(modifier = Modifier.height(PaddingDim.LARGE))
    TextButton(
        onClick = { navController.navigate(AboutRoute.AboutScreenRoute.route) },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF1E293B).copy(alpha = 0.6f))
    ) {
        FitlogText(
            text = stringResource(id = R.string.about_me),
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF1E293B).copy(alpha = 0.6f),
            fontWeight = FontWeight.Bold
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitlogTimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Int, Int) -> Unit,
    initialHour: Int,
    initialMinute: Int,
) {
    //  estados locales para los números
    var selectedHour by remember { mutableIntStateOf(initialHour) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)), // Casi negro
            border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FitlogText(
                    text = stringResource(id = R.string.workout_reminder),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Selector de Tiempo Estilo Minimalista
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Hora
                    TimeNumberColumn(
                        value = selectedHour,
                        range = 0..23,
                        onValueChange = { selectedHour = it }
                    )

                    FitlogText(
                        text = ":",
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    // Minutos
                    TimeNumberColumn(
                        value = selectedMinute,
                        range = 0..59,
                        onValueChange = { selectedMinute = it }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Botón de Confirmación Estilo "Pill"
                Button(
                    onClick = { onConfirm(selectedHour, selectedMinute) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text(stringResource(id = R.string.done), fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }
    }
}

@Composable
fun TimeNumberColumn(value: Int, range: IntRange, onValueChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = { if (value < range.last) onValueChange(value + 1) }) {
            Icon(Icons.Default.KeyboardArrowUp, null, tint = Color(0xFF10B981))
        }
        Text(
            text = String.format("%02d", value),
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Light, // El peso ligero da elegancia
            color = Color.White
        )
        IconButton(onClick = { if (value > range.first) onValueChange(value - 1) }) {
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
                modifier = Modifier.size(20.dp).padding(end = PaddingDim.SMALL)
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
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FitlogText(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) Color.White else Color.White.copy(alpha = 0.6f)
        )
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF10B981), // Emerald Green
                unselectedColor = Color.White.copy(alpha = 0.3f)
            )
        )
    }
}