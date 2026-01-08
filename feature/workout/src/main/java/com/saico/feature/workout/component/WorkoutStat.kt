package com.saico.feature.workout.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.SpacerHeight
import com.saico.core.ui.theme.AppDim
import com.saico.core.ui.theme.PaddingDim

@Composable
fun WorkoutStat(icon: ImageVector, value: String, unit: String, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FitlogIcon(
            imageVector = icon,
            contentDescription = unit,
            background = Color.Transparent,
            modifier = Modifier.size(
                AppDim.IMAGES_SIZE
            ),
            tint = tint
        )
        SpacerHeight(PaddingDim.SMALL)
        FitlogText(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        FitlogText(
            text = unit,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}