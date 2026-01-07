package com.saico.feature.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saico.feature.dashboard.state.DashboardUiState

@Composable
fun HomeScreen(uiState: DashboardUiState){

    DebugInfo(uiState)

}

@Composable
fun DebugInfo(uiState: DashboardUiState){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Pasos de hoy",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = uiState.dailySteps.toString(),
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "--- DEBUG INFO ---")
        Text(text = "Total Pasos Sensor (desde reinicio): ${uiState.totalSteps}")
        Text(text = "Offset guardado para hoy: ${uiState.stepOffset}")
        Text(text = "Cálculo: ${uiState.totalSteps} - ${uiState.stepOffset} = ${uiState.dailySteps}")
    }
}