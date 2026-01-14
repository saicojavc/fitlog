package com.saico.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.saico.core.ui.theme.FitlogTheme

@Composable
fun FitlogTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    errorMessage: String? = null,
    maxLines: Int = if (singleLine) 1 else 5,
    maxLength: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    shape: Shape = OutlinedTextFieldDefaults.shape,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    FitlogOutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = label,
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        errorMessage = errorMessage,
        maxLines = maxLines,
        maxLength = maxLength,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        colors = colors,
        shape = shape,
        textStyle = textStyle
    )
}

@Composable
fun FitlogOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    errorMessage: String? = null,
    maxLines: Int = if (singleLine) 1 else 5,
    maxLength: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    shape: Shape = OutlinedTextFieldDefaults.shape,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                if (maxLength != null) {
                    if (it.length <= maxLength) {
                        onValueChange(it)
                    }
                } else {
                    onValueChange(it)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = label) },
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            maxLines = maxLines,
            isError = errorMessage != null,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            colors = colors,
            shape = shape,
            textStyle = textStyle,
        )

        errorMessage?.let {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp),
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun FitlogTextFieldEmail(
    modifier: Modifier = Modifier,
    label: String = "Email",
    value: String,
    onValueChange: (String) -> Unit,
    maxLength: Int = 40,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    FitlogOutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        maxLength = maxLength,
        label = label,
        errorMessage = errorMessage,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email, imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
    )
}

@Composable
fun FitlogTextFieldPassword(
    modifier: Modifier = Modifier,
    label: String = "Password",
    value: String,
    onValueChange: (String) -> Unit,
    maxLength: Int = 40,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    var passwordHidden by remember { mutableStateOf(true) }

    FitlogOutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        maxLength = maxLength,
        label = label,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        keyboardActions = keyboardActions,
        errorMessage = errorMessage,
        visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
    )
}

@Preview("Text Fields")
@Composable
private fun FitlogTextFieldsPreview() {
    var text by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    FitlogTheme {
        Surface {
            Column(modifier = Modifier.padding(16.dp)) {
                FitlogTextField(value = text, onValueChange = { text = it }, label = "Generic Field")
                FitlogTextFieldEmail(value = email, onValueChange = { email = it })
                FitlogTextFieldPassword(value = password, onValueChange = { password = it })
            }
        }
    }
}
