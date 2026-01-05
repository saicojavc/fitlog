package com.saico.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saico.core.ui.theme.FitlogTheme

@Composable
fun FitlogButton(
    modifier: Modifier = Modifier,
    label: String = "",
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.textShape,
    content: @Composable RowScope.() -> Unit = {  },
    onClick: () -> Unit,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        content = content,
        shape = shape,
        colors = colors
    )
}

@Composable
fun FitlogTextButton(
    modifier: Modifier = Modifier,
    textButtonStyle: TextButtonStyle = TextButtonStyle.DEFAULT,
    label: String = "",
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit = { Text(text = label) },
    onClick: () -> Unit,
) {
    val textColor = when (textButtonStyle) {
        TextButtonStyle.DEFAULT -> MaterialTheme.colorScheme.primary
        TextButtonStyle.DISMISS -> MaterialTheme.colorScheme.error
    }

    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = textColor
        ),
        content = content
    )
}

@Composable
fun FitlogTextButtonBorder(
    modifier: Modifier = Modifier,
    textButtonStyle: TextButtonStyle = TextButtonStyle.DEFAULT,
    label: String = "",
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit = { Text(text = label) },
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    onClick: () -> Unit,
    border: BorderStroke = BorderStroke(
        1.dp, // PaddingDim.SUPER_SMALL
        MaterialTheme.colorScheme.outline
    )
) {
    val textColor = when (textButtonStyle) {
        TextButtonStyle.DEFAULT -> MaterialTheme.colorScheme.primary
        TextButtonStyle.DISMISS -> MaterialTheme.colorScheme.error
    }

    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        border = border,
        colors = ButtonDefaults.textButtonColors(
            contentColor = textColor
        ),
        contentPadding = contentPadding,
        content = content
    )
}

@Preview("Buttons")
@Composable
private fun FitlogButtonsPreview() {
    FitlogTheme {
        Surface {
            Column {
                FitlogButton(label = "FitlogButton (Enabled)", onClick = {})
                Spacer(modifier = Modifier.height(8.dp))
                FitlogButton(label = "FitlogButton (Disabled)", enabled = false, onClick = {})
                Spacer(modifier = Modifier.height(16.dp))
                FitlogTextButton(label = "TextButton (Default)", onClick = {})
                Spacer(modifier = Modifier.height(8.dp))
                FitlogTextButton(
                    label = "TextButton (Dismiss)",
                    textButtonStyle = TextButtonStyle.DISMISS,
                    onClick = {}
                )
                Spacer(modifier = Modifier.height(16.dp))
                FitlogTextButtonBorder(label = "TextButtonBorder (Default)", onClick = {})
                Spacer(modifier = Modifier.height(8.dp))
                FitlogTextButtonBorder(
                    label = "TextButtonBorder (Dismiss)",
                    textButtonStyle = TextButtonStyle.DISMISS,
                    onClick = {}
                )
            }
        }
    }
}
