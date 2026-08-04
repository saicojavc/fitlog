package com.saico.feature.gymwork.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saico.core.model.ExerciseSetEntry
import com.saico.core.ui.icon.FitlogIcons

@Composable
fun SetsInteractionTable(
    sets: List<ExerciseSetEntry>,
    onSetToggled: (Int, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("SERIE", Modifier.width(50.dp), color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center)
            Text("PESO (LB)", Modifier.weight(1f), color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center)
            Text("REPS", Modifier.weight(1f), color = Color.Gray, fontSize = 12.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.width(50.dp))
        }

        sets.forEachIndexed { index, set ->
            SetRow(
                set = set,
                onToggled = { weight, reps -> onSetToggled(index, weight, reps) }
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetRow(
    set: ExerciseSetEntry,
    onToggled: (String, String) -> Unit
) {
    var weight by remember(set) { mutableStateOf(if(set.weightLb > 0) set.weightLb.toString() else "") }
    var reps by remember(set) { mutableStateOf(if(set.reps > 0) set.reps.toString() else "") }
    
    val rowColor = if (set.isCompleted) Color(0xFF38BDF8).copy(alpha = 0.1f) else Color.Transparent
    val borderColor = if (set.isCompleted) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.1f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(rowColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${set.setIndex + 1}",
            modifier = Modifier.width(34.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        TextField(
            value = weight,
            onValueChange = { weight = it },
            modifier = Modifier.weight(1f).height(48.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            placeholder = { Text("0", color = Color.Gray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
        )

        TextField(
            value = reps,
            onValueChange = { reps = it },
            modifier = Modifier.weight(1f).height(48.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            placeholder = { Text("0", color = Color.Gray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center)
        )

        IconButton(
            onClick = { onToggled(weight, reps) },
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (set.isCompleted) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector = if (set.isCompleted) FitlogIcons.Check else FitlogIcons.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
