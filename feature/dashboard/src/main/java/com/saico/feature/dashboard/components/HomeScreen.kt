package com.saico.feature.dashboard.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.saico.feature.dashboard.state.DashboardUiState

@Composable
fun HomeScreen(uiState: DashboardUiState, navController: NavHostController) {

    ContentHomeScreen(
        uiState = uiState,
        navController = navController
        )
}

@Composable
fun ContentHomeScreen(
    uiState: DashboardUiState,
    navController: NavHostController,
    ) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            StepsDailyCard(uiState = uiState)
        }
        item {
            // 4. Pasamos los pasos del día actual al gráfico semanal
            WeeklyActivityCard(
                workouts = uiState.weeklyWorkouts,
                dailySteps = uiState.dailySteps,
                dailyStepsGoal = uiState.userProfile?.dailyStepsGoal ?: 1
            )
        }
        item {
            RegisterWork(navController = navController)
        }

    }
}