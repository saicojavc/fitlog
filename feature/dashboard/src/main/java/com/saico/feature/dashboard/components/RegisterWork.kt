package com.saico.feature.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.navigation.routes.gymwork.GymWorkRoute
import com.saico.core.ui.navigation.routes.outdoorrun.OutdoorRunRoute
import com.saico.core.ui.navigation.routes.workout.WorkoutRoute
import com.saico.core.ui.theme.DarkPrimary
import com.saico.core.ui.theme.LightSuccess
import com.saico.core.ui.theme.PaddingDim

@Composable
fun RegisterWork(navController: NavHostController) {
    val techBlue = Color(0xFF3FB9F6)
    val fireOrange = Color(0xFFFF9F1C)
    val neonPurple = Color(0xFFA855F7)
    val cyclingLime = Color(0xFFD4FF00) // Color específico para Ciclismo

    // Lista de actividades extendida
    val activities = listOf(
        ActivityItem(
            name = stringResource(R.string.gym),
            icon = FitlogIcons.Weight,
            color = neonPurple,
            route = GymWorkRoute.GymWorkScreenRoute.route
        ),
        ActivityItem(
            name = stringResource(R.string.treadmill),
            icon = FitlogIcons.Treadmill,
            color = techBlue,
            route = WorkoutRoute.WorkoutScreenRoute.route
        ),
        ActivityItem(
            name = stringResource(R.string.outdoor_run),
            icon = FitlogIcons.DirectionsRun,
            color = fireOrange,
            route = OutdoorRunRoute.OutdoorRunScreenRoute.createRoute("outdoor_run")
        ),
        ActivityItem(
            name = stringResource(R.string.cycling), // "Bicicleta" o "Ciclismo"
            icon = FitlogIcons.DirectionsBike,
            color = cyclingLime,
            route = OutdoorRunRoute.OutdoorRunScreenRoute.createRoute("cycling")
        ),

        ActivityItem(
            name = stringResource(R.string.swimming),
            icon = FitlogIcons.Pool,
            color = techBlue,
            route = "swimming_route"
        )
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = PaddingDim.MEDIUM),
        contentPadding = PaddingValues(horizontal = PaddingDim.LARGE),
        horizontalArrangement = Arrangement.spacedBy(20.dp) // Un poco más de espacio para que respiren los halos
    ) {
        items(activities) { activity ->
            ActivityCircleCard(activity) {
                navController.navigate(activity.route)
            }
        }
    }
}

@Composable
fun ActivityCircleCard(
    activity: ActivityItem,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        // El "Botón de Acción" Circular Cyber
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(72.dp)
                // Efecto de iluminación neón en el suelo del círculo
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    spotColor = activity.color,
                    ambientColor = activity.color
                )
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E293B).copy(alpha = 0.8f),
                            Color(0xFF0F172A).copy(alpha = 0.9f)
                        )
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(activity.color.copy(alpha = 0.6f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
                .clickable(onClick = onClick)
        ) {
            Icon(
                imageVector = activity.icon,
                contentDescription = activity.name,
                tint = activity.color,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(Modifier.height(10.dp))

        // Etiqueta de la actividad
        Text(
            text = activity.name.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 9.sp,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

// Clase de datos para organizar las actividades
data class ActivityItem(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)
