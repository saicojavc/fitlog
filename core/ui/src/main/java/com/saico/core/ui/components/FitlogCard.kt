package com.saico.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.saico.core.ui.theme.FitlogTheme

@Composable
fun FitlogCard(
    modifier: Modifier = Modifier,
    shape: Shape = ShapeDefaults.Medium,
    elevation: Dp = 4.dp, // Replaced ElevationDim.MEDIUM
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        border = border,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        shadowElevation = elevation,
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                content = content
            )
        },
    )
}

@Composable
fun FitlogBoxCard(
    modifier: Modifier = Modifier,
    shape: Shape = ShapeDefaults.Medium,
    elevation: Dp = 4.dp, // Replaced ElevationDim.MEDIUM
    border: BorderStroke? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        border = border,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        shadowElevation = elevation,
        content = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                content = content
            )
        },
    )
}

@Preview("Cards")
@Composable
private fun FitlogCardsPreview() {
    FitlogTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                FitlogCard(elevation = 8.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("FitlogCard (Column)", style = MaterialTheme.typography.titleMedium)
                        Text("This card contains a Column.")
                    }
                }

                FitlogBoxCard(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = "FitlogBoxCard (Box)",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
