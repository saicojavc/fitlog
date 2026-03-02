package com.saico.feature.dashboard.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.saico.core.ui.R
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.CoolGray
import com.saico.core.ui.theme.EmeraldGreen
import androidx.core.net.toUri
import com.saico.core.ui.components.FitlogText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateVersionDialog(
    remoteVersion: String,
    onDismiss: () -> Unit,
    context: Context,
) {
    val techBlue = Color(0xFF3FB9F6)
    val blueGradient = Brush.horizontalGradient(listOf(techBlue, Color(0xFF216EE0)))

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xFF0D1424).copy(alpha = 0.95f)) // Fondo profundo
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(32.dp)),
        content = {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- ICONO DE ACTUALIZACIÓN CON GLOW ---
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(15.dp, CircleShape, spotColor = techBlue)
                        .background(techBlue.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Círculo interno animado visualmente
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .border(2.dp, techBlue.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = FitlogIcons.SystemUpdate,
                            contentDescription = null,
                            tint = techBlue,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- TÍTULO ---
                FitlogText(
                    text = stringResource(id = R.string.update_available_title).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // --- VERSIÓN TAG (Estilo Badge) ---
                Surface(
                    color = techBlue.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, techBlue.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "v$remoteVersion",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = techBlue,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- MENSAJE ---
                FitlogText(
                    text = stringResource(id = R.string.update_available_message, remoteVersion),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF94A3B8), // CoolGray
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- BOTÓN ACTUALIZAR (GLOW + GRADIENT) ---
                Button(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://play.google.com/store/apps/details?id=com.saico.fitlog".toUri()
                        )
                        context.startActivity(intent)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(12.dp, CircleShape, spotColor = techBlue),
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
                        FitlogText(
                            text = stringResource(id = R.string.update_now).uppercase(),
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // --- BOTÓN CANCELAR ---
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FitlogText(
                        text = stringResource(id = R.string.later).uppercase(),
                        color = Color.White.copy(alpha = 0.4f),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    )
}