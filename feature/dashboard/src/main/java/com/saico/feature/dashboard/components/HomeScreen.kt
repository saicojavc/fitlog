package com.saico.feature.dashboard.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.saico.feature.dashboard.state.DashboardUiState

@Composable
fun HomeScreen(uiState: DashboardUiState) {
    ContentHomeScreen(uiState = uiState)
}

@Composable
fun ContentHomeScreen(uiState: DashboardUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            StepsDailyCard(uiState = uiState)
        }
        item {
            WeeklyActivityCard(workouts = uiState.weeklyWorkouts)
        }
    }
}
