package com.saico.feature.dashboard

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.saico.core.ui.theme.LightBackground
import com.saico.core.ui.theme.LightPrimary
import com.saico.core.ui.theme.LightSuccess
import com.saico.feature.dashboard.components.HistoryScreen
import com.saico.feature.dashboard.components.HomeScreen
import com.saico.feature.dashboard.components.NavigationBar
import com.saico.feature.dashboard.components.ProfileScreen
import com.saico.feature.dashboard.components.TopAppBar
import com.saico.feature.dashboard.model.BottomAppBarItems
import com.saico.feature.dashboard.state.DashboardUiState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 1. Prepara el estado del permiso
    val permissionState = rememberPermissionState(
        permission = Manifest.permission.ACTIVITY_RECOGNITION
    )

    // 2. Prepara el lanzador para la solicitud de permiso
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // El permiso fue otorgado, la lógica del ViewModel ya se está ejecutando
            } else {
                // El usuario denegó el permiso. Puedes mostrar un mensaje si lo deseas.
            }
        }
    )

    // 3. Lanza la solicitud de permiso si es necesario
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!permissionState.status.isGranted) {
                permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
    }

    Content(uiState)
}

@Composable
fun Content(uiState: DashboardUiState) {

    var selectedBottomAppBarItem by remember { mutableStateOf(BottomAppBarItems.HOME) }
    val gradientColors = if (isSystemInDarkTheme()) {
        listOf(LightPrimary, LightSuccess)
    } else {
        listOf(LightPrimary, LightSuccess, LightBackground)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar()
        },
        bottomBar = {
            NavigationBar(
                selectedBottomAppBarItem = selectedBottomAppBarItem,
                onItemSelected = { selectedBottomAppBarItem = it }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors))
                .padding(paddingValues)

        ) {
            when (selectedBottomAppBarItem) {
                BottomAppBarItems.HOME -> {
                    HomeScreen(uiState = uiState)
                }

                BottomAppBarItems.HISTORY -> {
                    HistoryScreen()
                }

                BottomAppBarItems.PROFILE -> {
                    ProfileScreen()
                }
            }
        }
    }

}