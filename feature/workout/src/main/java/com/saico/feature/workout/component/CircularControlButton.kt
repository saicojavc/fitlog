package com.saico.feature.workout.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.saico.core.ui.components.FitlogIcon

@Composable
fun CircularControlButton(
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
    size: Dp = 64.dp
) {
    val backgroundColor = if (enabled) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f)

    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
    ) {
        FitlogIcon(
            imageVector = icon,
            contentDescription = null, // La función del botón es visual
            tint = Color.White,
            modifier = Modifier.size(size),
            background = Color.Transparent
        )
    }
}