package com.saico.feature.outdoorrun

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.fireOrange
import com.saico.core.ui.theme.techBlue
import com.saico.core.ui.R
import com.saico.feature.outdoorrun.style.MapStyle
import kotlin.collections.emptyList

// Asumiendo que estas son tus rutas y recursos locales
// import com.tupackage.ui.theme.PaddingDim
// import com.tupackage.ui.components.FitlogText
// import com.tupackage.ui.icons.FitlogIcons

@Composable
fun OutdoorRunScreen(navController: NavHostController) {



        Content(navController = navController)

}

@Composable
fun Content(navController: NavHostController) {

    // 1. Estado de la cámara (Centrado por defecto, luego se moverá con el GPS)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(4.6097, -74.0817), 16f)
    }

    // 2. Configuración Visual del Mapa
    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapStyleOptions = MapStyleOptions(MapStyle.JSON),
                isMyLocationEnabled = false // Lo activaremos cuando pidamos permisos
            )
        )
    }
    val uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false))
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // --- 1. MAPA (Simulación de estilo Dark/Cyber) ---
        // En una implementación real, aquí usarías GoogleMap() con MapStyleOptions
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings
        ) {
            // Aquí dibujaremos la línea azul neón cuando tengamos los puntos GPS
            Polyline(
                points = emptyList<LatLng>(),
                color = techBlue,
                width = 15f,
                startCap = RoundCap(), // <--- Cambiado de 'cap' a 'startCap'
                endCap = RoundCap()    // <--- Cambiado de 'cap' a 'endCap'
            )
        }

        // --- 2. CABECERA CON MÉTRICAS (Burbujas Flotantes) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 20.dp, end = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Burbuja de Pasos
                MetricBubble(
                    label = "Steps",
                    value = "8,432",
                    icon = FitlogIcons.Walk,
                    accentColor = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )
                // Burbuja de Distancia
                MetricBubble(
                    label = "Distance",
                    value = "5.2 km",
                    icon = FitlogIcons.Height,
                    accentColor = techBlue,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Burbuja de Velocidad
                MetricBubble(
                    label = "Avg Speed",
                    value = "12.4 KMH",
                    icon = FitlogIcons.Speed,
                    accentColor = fireOrange,
                    modifier = Modifier.weight(1f)
                )
                // Burbuja de Elevación
                MetricBubble(
                    label = "Elevation",
                    value = "+120m",
                    icon = Icons.Default.KeyboardArrowUp,
                    accentColor = Color(0xFFA855F7),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // --- 3. BOTÓN DE ACCIÓN INFERIOR ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp, start = 30.dp, end = 30.dp)
        ) {
            Button(
                onClick = { /* Acción futura de guardar */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadow(20.dp, CircleShape, spotColor = techBlue),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(listOf(techBlue, Color(0xFF216EE0))))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(FitlogIcons.Save, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = stringResource(id = R.string.save_session).uppercase(),
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
        color = Color(0xFF0D1424).copy(alpha = 0.75f), // Glassmorphism oscuro
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