package com.saico.core.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.saico.core.ui.theme.PaddingDim
import com.saico.core.ui.theme.techBlue

@Composable
fun InfoDialog(@StringRes title: Int, @StringRes text: Int, onDismiss: () -> Unit) {
    FitlogAlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(title),
            )
        },
        text = { Text(text = stringResource(text)) },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(text = "Understood") }
        },
    )
}

@Composable
fun FitlogInfoDialog(
    isVisible: Boolean,
    @StringRes title: Int,
    text: String,
    onDismiss: () -> Unit,
) {
    if (isVisible) {
        FitlogAlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = stringResource(title),
                )
            },
            text = { Text(text = text) },
            confirmButton = {
                TextButton(onClick = onDismiss) { Text(text = "Understood") }
            },
        )
    }
}

@Composable
fun LoadingDialog(@StringRes textMsg: Int? = null) {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = AlertDialogDefaults.shape,
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .padding(all = PaddingDim.EXTRA_LARGE),
                verticalArrangement = Arrangement.spacedBy(PaddingDim.SMALL),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
                textMsg?.let {
                    Text(
                        text = stringResource(
                            id = it
                        ),
                        color = AlertDialogDefaults.textContentColor,
                    )
                }
            }
        }
    }

}

@Composable
fun FitlogDialog(
    onDismiss: () -> Unit,
    @StringRes title: Int,
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    dismissButton: @Composable (() -> Unit)? = null,
    confirmButton: @Composable () -> Unit,
) {

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .graphicsLayer {
                    shadowElevation = 20.dp.toPx()
                    shape = RoundedCornerShape(28.dp)
                    clip = true
                }
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(28.dp)
                ),
            color = Color(0xFF0D1424).copy(alpha = 0.98f), // Fondo casi opaco para legibilidad
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ICONO CON CÍRCULO DE FONDO (La "Chicha")
                icon?.let {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(techBlue.copy(alpha = 0.1f), CircleShape)
                            .border(1.dp, techBlue.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = it,
                            contentDescription = null,
                            tint = techBlue,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Text(
                    text = stringResource(id = title).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // BOTONES CON DISEÑO CUSTOM
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón Cancelar (Outline sutil)
                    Box(modifier = Modifier.weight(1f)) {
                        dismissButton?.invoke()
                    }

                    // Botón Confirmar (El que tiene el peso visual)
                    Box(modifier = Modifier.weight(1f)) {
                        confirmButton()
                    }
                }
            }
        }
    }
}
@Composable
fun FitlogAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    containerColor: Color = if (isSystemInDarkTheme()) Color.Black.copy(alpha = 0.5f)
    else MaterialTheme.colorScheme.surface,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        modifier = modifier,
        dismissButton = dismissButton,
        icon = icon,
        title = title,
        text = text,
        tonalElevation = 0.dp,
        containerColor = containerColor
    )
}
