package com.saico.feature.gymwork.component

import android.text.format.DateUtils.formatElapsedTime
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.icon.FitlogIcons
import com.saico.feature.gymwork.state.GymWorkUiState

@Composable
fun TimeCard(uiState: GymWorkUiState, onToggleTimer: () -> Unit) {
    // Definimos los colores de la nueva identidad
    val techBlue = Color(0xFF3FB9F6)
    val stopRed = Color(0xFFFF4550) // Un rojo más vibrante/neon para "Parar"

    FitlogCard(
        modifier = Modifier.fillMaxWidth(),
        // Usamos la transparencia que ya definiste para que se vean las partículas detrás
        color = Color(0xFF1E293B).copy(alpha = 0.5f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(id = R.string.elapsed_time).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 2.sp
                )
                Text(
                    text = formatElapsedTime(uiState.elapsedTime),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.ExtraLight,
                        letterSpacing = (-2).sp // Aumentamos la compresión para look digital pro
                    ),
                    color = Color.White
                )
            }

            // Botón de Play/Stop con efecto Glow
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(
                        elevation = if (uiState.isTimerRunning) 15.dp else 0.dp,
                        shape = CircleShape,
                        spotColor = techBlue,
                        ambientColor = techBlue
                    )
                    .clip(CircleShape)
                    .background(
                        if (uiState.isTimerRunning) techBlue.copy(alpha = 0.1f)
                        else Color.White.copy(alpha = 0.05f)
                    )
                    .border(
                        width = 1.dp,
                        color = if (uiState.isTimerRunning) techBlue.copy(alpha = 0.5f)
                        else Color.White.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
                    .clickable { onToggleTimer() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (uiState.isTimerRunning) FitlogIcons.Stop else FitlogIcons.Play,
                    contentDescription = null,
                    tint = if (uiState.isTimerRunning) techBlue else Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}