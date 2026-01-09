package com.saico.feature.gymwork.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
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
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = PaddingDim.SMALL
        ) {
            Column(
                modifier = Modifier
                    .padding(PaddingDim.EXTRA_LARGE)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(PaddingDim.SMALL)
            ) {
                Text(
                    text = if (initialExercise == null) "Agregar Ejercicio" else "Editar Ejercicio",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(PaddingDim.SMALL))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del ejercicio") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(PaddingDim.SMALL)
                ) {
                    OutlinedTextField(
                        value = sets,
                        onValueChange = { if (it.all { char -> char.isDigit() }) sets = it },
                        label = { Text("Series") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = reps,
                        onValueChange = { if (it.all { char -> char.isDigit() }) reps = it },
                        label = { Text("Reps") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Peso (Lb)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(PaddingDim.MEDIUM))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    TextButton(
                        onClick = { 
                            if (name.isNotBlank()) {
                                onConfirm(name, sets, reps, weight)
                            }
                        },
                        enabled = name.isNotBlank() && sets.isNotBlank() && reps.isNotBlank()
                    ) {
                        Text(if (initialExercise == null) "Agregar" else "Actualizar")
                    }
                }
            }
        }
    }
}
