package com.saico.feature.dashboard.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false), // Para controlar el ancho
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xFF1E293B)) // Color(0xFF1E293B).copy(alpha = 0.9f)
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(32.dp)),
        content = {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- ICONO DE ACTUALIZACIÓN ---
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(EmeraldGreen.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = FitlogIcons.SystemUpdate,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- TÍTULO ---
                FitlogText(
                    text = stringResource(id = R.string.update_available_title).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // --- CUERPO ---
                FitlogText(
                    text = stringResource(id = R.string.update_available_message, remoteVersion),
                    style = MaterialTheme.typography.bodyMedium,
                    color = CoolGray, // Color(0xFF94A3B8)
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- BOTÓN CONFIRMAR (EMERALD) ---
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
                        .height(56.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    FitlogText(
                        text = stringResource(id = R.string.update_now).uppercase(),
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- BOTÓN CANCELAR ---
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FitlogText(
                        text = stringResource(id = R.string.later),
                        color = CoolGray,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    )
}