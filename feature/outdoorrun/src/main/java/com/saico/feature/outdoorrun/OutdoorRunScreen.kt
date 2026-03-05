package com.saico.feature.outdoorrun

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogDialog
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.fireOrange
import com.saico.core.ui.theme.techBlue
import com.saico.feature.outdoorrun.style.MapStyle
import kotlinx.coroutines.launch

@Composable
fun OutdoorRunScreen(navController: NavHostController) {
    val context = LocalContext.current
    var showGpsDialog by remember { mutableStateOf(false) }

    // Función para verificar si el GPS está activado
    fun isGpsEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // Verificar GPS al entrar
    LaunchedEffect(Unit) {
        if (!isGpsEnabled(context)) {
            showGpsDialog = true
        }
    }

    if (showGpsDialog) {
        FitlogDialog(
            onDismiss = { showGpsDialog = false },
            title = R.string.gps_disabled_title,
            text = stringResource(id = R.string.gps_disabled_message),
            icon = Icons.Default.LocationOn, // Icono de GPS
            confirmButton = {
                // BOTÓN CON GRADIENTE
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
                        Text("ACTIVAR", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showGpsDialog = false },
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
                    Text("CANCELAR")
                }
            }
        )
    }

    Content(navController = navController)
}

@SuppressLint("MissingPermission")
@Composable
fun Content(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Verificar permisos
    val hasLocationPermission = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Estado de la cámara
    val cameraPositionState = rememberCameraPositionState {
        // Bogotá por defecto hasta obtener ubicación
        position = CameraPosition.fromLatLngZoom(LatLng(4.6097, -74.0817), 16f)
    }

    // Función para centrar el mapa
    val centerMapOnMyLocation = {
        if (hasLocationPermission) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val userLatLng = LatLng(it.latitude, it.longitude)
                        scope.launch {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(userLatLng, 17f),
                                durationMs = 1000
                            )
                        }
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    // Centrar automáticamente al abrir si hay permisos
    LaunchedEffect(Unit) {
        centerMapOnMyLocation()
    }

    // Configuración Visual del Mapa
    val mapProperties = remember(hasLocationPermission) {
        MapProperties(
            mapStyleOptions = MapStyleOptions(MapStyle.JSON),
            isMyLocationEnabled = hasLocationPermission
        )
    }
    val uiSettings = remember {
        MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false)
    }
    var isRunning by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        // --- 1. MAPA ---
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings
        ) {
            Polyline(
                points = emptyList<LatLng>(),
                color = techBlue,
                width = 15f,
                startCap = RoundCap(),
                endCap = RoundCap()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 110.dp, start = 20.dp, end = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricBubble(
                    label = "Steps",
                    value = "0",
                    icon = FitlogIcons.Walk,
                    accentColor = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )
                MetricBubble(
                    label = "Distance",
                    value = "0.0 km",
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
                    value = "0.0 KMH",
                    icon = FitlogIcons.Speed,
                    accentColor = fireOrange,
                    modifier = Modifier.weight(1f)
                )
                MetricBubble(
                    label = "Time",
                    value = "00.00.00",
                    icon = FitlogIcons.Clock,
                    accentColor = Color(0xFFA855F7),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // --- 4. BOTÓN RECENTRAR (Abajo Derecha - Encima del botón guardar) ---
        Surface(
            onClick = { centerMapOnMyLocation() },
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

        // --- 5. BOTÓN DE ACCIÓN INFERIOR (Guardar) ---
        val startColor = Color(0xFF10B981) // Verde esmeralda para Iniciar
        val stopColor = techBlue // El azul que ya usas para Guardar

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp, start = 30.dp, end = 30.dp)
        ) {
            Button(
                onClick = {
                    if (!isRunning) {
                        isRunning = true
                        // Aquí podrías iniciar el cronómetro y el GPS
                    } else {
                        // Aquí abres el diálogo de guardar o finalizas
                        isRunning = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = CircleShape,
                        spotColor = if (isRunning) stopColor else startColor
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
                                if (isRunning) listOf(stopColor, Color(0xFF216EE0))
                                else listOf(startColor, Color(0xFF059669))
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Icono dinámico
                        Icon(
                            imageVector = if (isRunning) FitlogIcons.Save else FitlogIcons.Play,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(Modifier.width(12.dp))
                        // Texto dinámico
                        Text(
                            text = stringResource(
                                id = if (isRunning) R.string.save_session else R.string.get_started
                            ).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
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
