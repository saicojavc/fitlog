package com.saico.feature.gymwork.component

import android.text.format.DateUtils.formatElapsedTime
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
    FitlogCard(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1E293B).copy(alpha = 0.5f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(4f),
            ) {
                Text(
                    text = stringResource(id = R.string.elapsed_time),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8)
                )
                Text(
                    text = formatElapsedTime(uiState.elapsedTime),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.ExtraLight, // Aquí está la elegancia
                        letterSpacing = (-1).sp
                    ),
                    color = Color.White
                )
            }

            // Botón de Play/Stop circular y minimalista
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .weight(1f)
                    .clip(CircleShape)
                    .background(
                        if (uiState.isTimerRunning) Color(0xFFEF4444).copy(alpha = 0.2f) else Color(
                            0xFF10B981
                        ).copy(alpha = 0.2f)
                    )
                    .clickable { onToggleTimer() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (uiState.isTimerRunning) FitlogIcons.Stop else FitlogIcons.Play,
                    contentDescription = null,
                    tint = if (uiState.isTimerRunning) Color(0xFFEF4444) else Color(0xFF10B981)
                )
            }
        }
    }
}