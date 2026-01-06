package com.saico.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.FitlogTheme

@Composable
fun FitlogIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    background: Color,
    shape: Shape = RoundedCornerShape(8.dp),
    contentDescription: String? = null,
    tint: Color = LocalContentColor.current
) {
    Box(
        modifier = modifier
            .size(40.dp) // Replaced AppDim.LIST_ICONS_SIZE
            .clip(shape) 
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.padding(8.dp), // Replaced PaddingDim.VERY_SMALL
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}

@Preview("Fitlog Icon Square")
@Composable
private fun FitlogIconPreview() {
    FitlogTheme {
        FitlogIcon(
            imageVector = FitlogIcons.UserProfile,
            background = MaterialTheme.colorScheme.primaryContainer,
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Preview("Fitlog Icon Circle")
@Composable
private fun FitlogIconCirclePreview() {
    FitlogTheme {
        FitlogIcon(
            imageVector = FitlogIcons.UserProfile,
            background = MaterialTheme.colorScheme.primaryContainer,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = CircleShape
        )
    }
}
