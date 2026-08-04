package com.saico.feature.gymwork.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saico.feature.gymwork.state.GymWorkUiState

@Composable
fun PlanningView(
    uiState: GymWorkUiState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = uiState.routine?.title ?: "Rutina del Día",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        uiState.routine?.exercises?.let { exercises ->
            items(exercises) { exercise ->
                WgerExerciseCard(exercise = exercise)
            }
        }
        
        item {
            Spacer(Modifier.height(80.dp)) // Padding for Start Button
        }
    }
}
