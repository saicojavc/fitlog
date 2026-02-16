package com.saico.feature.gymwork.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.saico.core.ui.R
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.gymwork.state.GymExerciseItem

@Composable
fun AddExerciseDialog(
    initialExercise: GymExerciseItem? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(initialExercise?.name ?: "") }
    var sets by remember { mutableStateOf(initialExercise?.sets?.toString() ?: "") }
    var reps by remember { mutableStateOf(initialExercise?.reps?.toString() ?: "") }
    var weight by remember { mutableStateOf(initialExercise?.weightLb?.toString() ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            RoundedCornerShape(32.dp),
            CardDefaults.cardColors(containerColor = Color(0xFF111827)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (initialExercise == null) "NUEVO EJERCICIO" else "EDITAR EJERCICIO",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 2.sp
                )

                // Input de Nombre Simple
                BasicTextField(
                    value = name,
                    onValueChange = { name = it },
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    decorationBox = { innerTextField ->
                        if (name.isEmpty()) Text("Nombre del ejercicio", color = Color.Gray, textAlign = TextAlign.Center)
                        innerTextField()
                    }
                )

                Divider(color = Color.White.copy(alpha = 0.1f))

                // Fila de inputs numéricos más limpios
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    CompactInput(value = sets, label = "SETS", onValueChange = { sets = it })
                    CompactInput(value = reps, label = "REPS", onValueChange = { reps = it })
                    CompactInput(value = weight, label = "LB", onValueChange = { weight = it })
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onConfirm(name, sets, reps, weight) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    enabled = name.isNotBlank()
                ) {
                    Text("CONFIRMAR", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun CompactInput(value: String, label: String, onValueChange: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF10B981))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Light, textAlign = TextAlign.Center),
            modifier = Modifier.width(60.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}