package com.saico.feature.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    uiState.userProfile?.let { userProfile ->
        Column {
            Text(text = "Age: ${userProfile.age}")
            Text(text = "Weight: ${userProfile.weightKg}")
            Text(text = "Height: ${userProfile.heightCm}")
            Text(text = "Gender: ${userProfile.gender}")
            Text(text = "Daily Steps Goal: ${userProfile.dailyStepsGoal}")
            Text(text = "Daily Calories Goal: ${userProfile.dailyCaloriesGoal}")
        }
    }
}
