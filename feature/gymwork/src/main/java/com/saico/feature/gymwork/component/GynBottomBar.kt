package com.saico.feature.gymwork.component

import android.text.format.DateUtils.formatElapsedTime
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saico.core.ui.R
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.gymwork.state.GymWorkUiState

@Composable
fun GymBottomBar(
    uiState: GymWorkUiState,
    onSaveSession: () -> Unit,
) {
    val blueGradient = Brush.horizontalGradient(listOf(Color(0xFF3FB9F6), Color(0xFF216EE0)))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            // 1. Aplicamos el redondeo superior
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.15f), Color.Transparent)
                ),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ),
        color = Color(0xFF0D1424).copy(alpha = 0.9f), // Glassmorphism
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // 2. SOLUCIÓN: El modificador correcto para el espacio inferior de Android
                .navigationBarsPadding()
                .padding(PaddingDim.MEDIUM)
        ) {
            // Stats Row (Tiempo y Calorías)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = PaddingDim.MEDIUM),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.total_time).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Text(
                        text = formatElapsedTime(uiState.elapsedTime),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(id = R.string.calories).uppercase(),
                        color = Color.White.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "${uiState.totalCalories} KCAL",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF3FB9F6)
                    )
                }
            }

            // Botón "Guardar Sesión" con el nuevo estilo
            Button(
                onClick = onSaveSession,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(12.dp, CircleShape, spotColor = Color(0xFF3FB9F6)),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(blueGradient)
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(FitlogIcons.Save, null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.save_session).uppercase(),
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}