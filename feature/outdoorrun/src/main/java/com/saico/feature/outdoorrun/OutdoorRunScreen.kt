package com.saico.feature.outdoorrun

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.saico.core.common.util.UnitsConverter
import com.saico.core.model.UnitsConfig
import com.saico.core.ui.MapStyle
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogDialog
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.fireOrange
import com.saico.core.ui.theme.techBlue
import com.saico.feature.outdoorrun.model.OutdoorUiState
import kotlinx.coroutines.launch
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OutdoorRunScreen(
    navController: NavHostController,
    activityType: String,
    viewModel: OutdoorRunViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showGpsDialog by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    fun isGpsEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    LaunchedEffect(Unit) {
        viewModel.setActivityType(activityType)
        if (!isGpsEnabled(context)) {
            showGpsDialog = true
        }
    }

    if (showGpsDialog) {
        FitlogDialog(
            onDismiss = { },
            title = R.string.gps_disabled_title,
            text = stringResource(id = R.string.gps_disabled_message),
            icon = Icons.Default.LocationOn,
            confirmButton = {
                Surface(
                    onClick = {
                        showGpsDialog = false
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        techBlue,
                                        Color(0xFF216EE0)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            stringResource(id = R.string.activate),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White.copy(
                            alpha = 0.6f
                        )
                    )
                ) {
                    Text(stringResource(id = R.string.close))
                }
            }
        )
    }

    Content(
        navController = navController,
        uiState = uiState,
        onStartClick = { viewModel.startTracking() },
        onStopClick = { viewModel.stopTracking() },
        onSaveClick = { viewModel.saveSession() },
        onRecentrar = { /* Implementado internamente en Content */ }
    )
}

@SuppressLint("MissingPermission")
@Composable
fun Content(
    navController: NavHostController,
    uiState: OutdoorUiState,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onSaveClick: () -> Unit,
    onRecentrar: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val activityTitle = when (uiState.activityType) {
        "cycling" -> stringResource(R.string.cycling)
        else -> stringResource(R.string.outdoor_run)
    }

    val mainIcon = when (uiState.activityType) {
        "cycling" -> FitlogIcons.DirectionsBike
        else -> FitlogIcons.DirectionsRun
    }

    val hasLocationPermission = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 16f)
    }

    val centerMapOnMyLocation = {
        if (hasLocationPermission) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    scope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    it.latitude,
                                    it.longitude
                                ), 17f
                            )
                        )
                    }
                }
            }
        }
    }

    // Centrar automáticamente cuando hay nuevos puntos y la sesión está activa
    LaunchedEffect(uiState.routePath.lastOrNull()) {
        uiState.routePath.lastOrNull()?.let { lastPoint ->
            val target = LatLng(lastPoint.latitude, lastPoint.longitude)

            // Si la cámara está en (0,0), centramos inmediatamente sin importar si corre o no (primer fix)
            if (cameraPositionState.position.target.latitude == 0.0) {
                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(target, 17f))
            }
            // Si está corriendo, seguimos la posición automáticamente
            else if (uiState.isRunning) {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLng(target),
                    durationMs = 1000
                )
            }
        }
    }

    // Inicial centrado si ya tenemos permisos
    LaunchedEffect(Unit) {
        centerMapOnMyLocation()
    }

    // Dibujar ruta en el mapa
    val polylinePoints = remember(uiState.routePath) {
        uiState.routePath.map { LatLng(it.latitude, it.longitude) }
    }

    val mapProperties = remember(hasLocationPermission) {
        MapProperties(
            mapStyleOptions = MapStyleOptions(MapStyle.JSON),
            isMyLocationEnabled = hasLocationPermission
        )
    }
    val uiSettings =
        remember { MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false) }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings
        ) {
            if (polylinePoints.isNotEmpty()) {
                Polyline(
                    points = polylinePoints,
                    color = techBlue,
                    width = 12f,
                    startCap = RoundCap(),
                    endCap = RoundCap()
                )
            }
        }

        // Header
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 30.dp, start = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                onClick = { navController.popBackStack() },
                color = Color(0xFF0D1424).copy(alpha = 0.75f),
                shape = CircleShape,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Surface(
                color = Color(0xFF0D1424).copy(alpha = 0.75f),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = mainIcon,
                        contentDescription = null,
                        tint = if (uiState.activityType == "cycling") Color(0xFFD4FF00) else fireOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = activityTitle.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Metrics
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 90.dp, start = 10.dp, end = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricBubble(
                    label = "Time",
                    value = formatMillis(uiState.timeMillis),
                    icon = FitlogIcons.History,
                    accentColor = Color.White,
                    modifier = Modifier.weight(1f)
                )
                MetricBubble(
                    label = "Distance",
                    value = if (uiState.unitsConfig == UnitsConfig.IMPERIAL) {
                        UnitsConverter.formatDistance(
                            uiState.distanceMeters / 1000.0,
                            UnitsConfig.IMPERIAL
                        )
                    } else {
                        String.format(
                            Locale.getDefault(),
                            "%.2f km",
                            uiState.distanceMeters / 1000f
                        )
                    },
                    icon = FitlogIcons.Height,
                    accentColor = techBlue,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricBubble(
                    label = "Avg Speed",
                    value = if (uiState.unitsConfig == UnitsConfig.IMPERIAL) {
                        String.format(
                            Locale.getDefault(),
                            "%.1f mph",
                            uiState.averageSpeed * 0.621371f
                        )
                    } else {
                        String.format(Locale.getDefault(), "%.1f km/h", uiState.averageSpeed)
                    }, icon = FitlogIcons.Speed,
                    accentColor = fireOrange,
                    modifier = Modifier.weight(1f)
                )
                MetricBubble(
                    label = "Elevation",
                    value = if (uiState.unitsConfig == UnitsConfig.IMPERIAL) {
                        String.format(
                            Locale.getDefault(),
                            "+%.0f ft",
                            uiState.elevationGain * 3.28084f
                        )
                    } else {
                        String.format(Locale.getDefault(), "+%.0fm", uiState.elevationGain)
                    }, icon = Icons.Default.KeyboardArrowUp,
                    accentColor = Color(0xFFA855F7),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Recentrar
        Surface(
            onClick = centerMapOnMyLocation,
            color = Color(0xFF0D1424).copy(alpha = 0.75f),
            shape = CircleShape,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 135.dp, end = 25.dp)
                .size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "My Location",
                    tint = techBlue,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Botón Iniciar / Guardar
        val startColor = Color(0xFF10B981)
        val stopColor = fireOrange

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp, start = 30.dp, end = 30.dp)
        ) {
            Button(
                onClick = { if (uiState.isRunning) onStopClick() else onStartClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadow(
                        20.dp,
                        CircleShape,
                        spotColor = if (uiState.isRunning) stopColor else startColor
                    ),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                if (uiState.isRunning) listOf(stopColor, Color(0xFFE11D48))
                                else listOf(startColor, Color(0xFF059669))
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (uiState.isRunning) FitlogIcons.Save else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = (if (uiState.isRunning) "GUARDAR SESIÓN" else "EMPEZAR").uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Botón Guardar definitivo si se paró la sesión
        if (!uiState.isRunning && uiState.timeMillis > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 125.dp)
            ) {
                TextButton(onClick = onSaveClick) {
                    Text("FINALIZAR Y GUARDAR", color = techBlue, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

fun formatMillis(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    val hours = (millis / (1000 * 60 * 60))
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
}

@Composable
fun MetricBubble(
    label: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF0D1424).copy(alpha = 0.75f),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(accentColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
