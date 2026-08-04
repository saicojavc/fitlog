package com.saico.feature.gymwork.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.saico.feature.gymwork.state.GymWorkUiState

@Composable
fun LiveWorkoutView(
    uiState: GymWorkUiState,
    onSetToggled: (Int, Int, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentExercise = uiState.routine?.exercises?.getOrNull(uiState.activeExerciseIndex)
    val sets = uiState.completedSetsMap[uiState.activeExerciseIndex] ?: emptyList()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (currentExercise != null) {
            AsyncImage(
                model = currentExercise.imageUrl ?: "https://wger.de/static/images/icons/exercise.png",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.05f)),
                contentScale = ContentScale.Fit
            )
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                text = currentExercise.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            
            Text(
                text = "EJERCICIO ${uiState.activeExerciseIndex + 1} DE ${uiState.routine.exercises.size}",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF38BDF8)
            )
            
            Spacer(Modifier.height(24.dp))
            
            SetsInteractionTable(
                sets = sets,
                onSetToggled = { setIdx, weight, reps ->
                    onSetToggled(uiState.activeExerciseIndex, setIdx, weight, reps)
                }
            )
            
            Spacer(Modifier.height(100.dp)) // Space for bottom bar
        }
    }
}
