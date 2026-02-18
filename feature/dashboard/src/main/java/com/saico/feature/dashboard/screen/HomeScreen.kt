package com.saico.feature.dashboard.screen

import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.saico.feature.dashboard.components.RegisterWork
import com.saico.feature.dashboard.components.StepsDailyCard
import com.saico.feature.dashboard.components.UpdateVersionDialog
import com.saico.feature.dashboard.components.WeeklyActivityCard
import com.saico.feature.dashboard.components.WeightTrackerCard
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
    val context = LocalContext.current
    var showUpdateDialog by remember { mutableStateOf(false) }

    // Obtener versión local dinámicamente y limpiar sufijos de debug
    val localVersion = remember {
        try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            // Limpia sufijos como -debug para comparar solo la versión base (ej: 1.0.1)
            (packageInfo.versionName ?: "").substringBefore("-")
        } catch (e: Exception) {
            ""
        }
    }

    // Comparar con la versión de Firebase
    LaunchedEffect(uiState.remoteVersion) {
        if (uiState.remoteVersion != null && uiState.remoteVersion != localVersion) {
            showUpdateDialog = true
        }
    }

    if (showUpdateDialog) {
        UpdateVersionDialog(
            remoteVersion = uiState.remoteVersion ?: "",
            onDismiss = { showUpdateDialog = false },
            context = context,
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            StepsDailyCard(
                uiState = uiState,
                navController = navController
            )
        }
        item {
            WeightTrackerCard(
                uiState = uiState,
                navController = navController
            )
        }
        item {
            WeeklyActivityCard(
                workouts = uiState.weeklyWorkouts,
                dailySteps = uiState.dailySteps,
                dailyStepsGoal = uiState.userProfile?.dailyStepsGoal ?: 1,
                navController = navController
            )
        }
        item {
            RegisterWork(navController = navController)
        }
    }
}
