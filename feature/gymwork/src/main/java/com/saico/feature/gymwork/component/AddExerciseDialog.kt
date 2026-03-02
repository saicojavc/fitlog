package com.saico.feature.gymwork.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
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

    val techBlue = Color(0xFF3FB9F6)

    Dialog(onDismissRequest = onDismiss) {
        // Usamos FitlogCard para mantener el Glassmorphism y las partículas de fondo visibles
        FitlogCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(32.dp),
            color = Color(0xFF0D1424).copy(alpha = 0.95f), // Fondo oscuro profundo
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Título de Sección
                Text(
                    text = if (initialExercise == null)
                        stringResource(R.string.new_exercise).uppercase()
                    else stringResource(R.string.edit_exercise).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )

                // Input de Nombre con estilo Minimalista
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.03f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicTextField(
                        value = name,
                        onValueChange = { name = it },
                        cursorBrush = SolidColor(techBlue),
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            if (name.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.exercise_name),
                                    color = Color.White.copy(alpha = 0.2f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                // Fila de inputs numéricos con los nuevos colores
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompactInput(value = sets, label = "SETS", onValueChange = { sets = it })
                    // Divisores verticales sutiles
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(Color.White.copy(alpha = 0.1f))
                    )
                    CompactInput(value = reps, label = "REPS", onValueChange = { reps = it })
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(Color.White.copy(alpha = 0.1f))
                    )
                    CompactInput(value = weight, label = "LB", onValueChange = { weight = it })
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón de Confirmación con degradado BottomColor
                Button(
                    onClick = { onConfirm(name, sets, reps, weight) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            if (name.isNotBlank()) 12.dp else 0.dp,
                            CircleShape,
                            spotColor = techBlue
                        ),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    enabled = name.isNotBlank(),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = if (name.isNotBlank())
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF3FB9F6),
                                            Color(0xFF216EE0)
                                        )
                                    )
                                else Brush.linearGradient(
                                    listOf(
                                        Color.Gray.copy(0.2f),
                                        Color.Gray.copy(0.2f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.confirm).uppercase(),
                            fontWeight = FontWeight.Black,
                            color = if (name.isNotBlank()) Color.White else Color.White.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompactInput(value: String, label: String, onValueChange: (String) -> Unit) {
    val techBlue = Color(0xFF3FB9F6)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = techBlue.copy(alpha = 0.8f),
            fontWeight = FontWeight.Bold
        )
        BasicTextField(
            value = value,
            onValueChange = { if (it.length <= 3) onValueChange(it) }, // Limitar longitud
            cursorBrush = SolidColor(techBlue),
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily.Monospace // Look digital
            ),
            modifier = Modifier
                .width(65.dp)
                .padding(top = 4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}