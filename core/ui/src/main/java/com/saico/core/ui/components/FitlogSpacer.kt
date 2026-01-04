package com.saico.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.saico.core.ui.theme.FitlogTheme

@Composable
fun SpacerHeight(
    height: Dp = 16.dp, // Replaced PaddingDim.LARGE
) {
    Spacer(modifier = Modifier.height(height))
}

@Composable
fun SpacerWidth(
    width: Dp = 16.dp, // Replaced PaddingDim.LARGE
) {
    Spacer(modifier = Modifier.width(width))
}

@Preview("Spacers")
@Composable
private fun FitlogSpacersPreview() {
    FitlogTheme {
        Column {
            Text(text = "Text 1")
            SpacerHeight(32.dp)
            Text(text = "Text 2 (32dp below)")
            Row {
                Text(text = "Row 1")
                SpacerWidth()
                Text(text = "Row 2 (16dp right)")
            }
        }
    }
}
