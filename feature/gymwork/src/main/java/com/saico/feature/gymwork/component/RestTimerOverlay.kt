package com.saico.feature.gymwork.component

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saico.core.ui.components.FitlogText

@Composable
fun RestTimerOverlay(
    remainingSeconds: Int,
    onSkip: () -> Unit
) {
    AnimatedVisibility(
        visible = remainingSeconds > 0,
        enter = fadeIn() + slideInVertically { it },
        exit = fadeOut() + slideOutVertically { it }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF1E293B))
                    .padding(40.dp)
            ) {
                FitlogText(
                    text = "TIEMPO DE DESCANSO",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF38BDF8)
                )
                
                Spacer(Modifier.height(16.dp))
                
                Text(
                    text = String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                
                Spacer(Modifier.height(32.dp))
                
                Button(
                    onClick = onSkip,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                    shape = CircleShape
                ) {
                    Text("SALTAR DESCANSO", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
