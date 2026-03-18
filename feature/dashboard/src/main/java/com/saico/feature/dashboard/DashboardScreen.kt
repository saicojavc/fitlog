package com.saico.feature.dashboard

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.saico.core.model.UserProfile
import com.saico.core.ui.components.GravityParticlesBackground
import com.saico.feature.dashboard.components.NavigationBar
import com.saico.feature.dashboard.components.TopAppBar
import com.saico.feature.dashboard.model.BottomAppBarItems
import com.saico.feature.dashboard.screen.HistoryWorkScreen
import com.saico.feature.dashboard.screen.HomeScreen
import com.saico.feature.dashboard.screen.ProfileScreen
import com.saico.feature.dashboard.screen.SyncLevelUpScreen
import com.saico.feature.dashboard.state.DashboardUiState
import com.saico.feature.dashboard.state.HistoryFilter
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val permissionState = rememberPermissionState(
        permission = Manifest.permission.ACTIVITY_RECOGNITION
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!permissionState.status.isGranted) {
                permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().zIndex(0f)) {
            Content(
                uiState = uiState,
                navController = navController,
                onFilterSelected = viewModel::onFilterSelected,
                onExportPdf = { viewModel.exportHistoryToPdf(context) },
                onLoginWithGoogle = viewModel::loginWithGoogle,
                onLogout = viewModel::logout,
                onUpdateProfile = viewModel::updateUserProfile
            )
        }

        AnimatedVisibility(
            visible = uiState.showLevelUp,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize().zIndex(1f)
        ) {
            var progress by remember { mutableFloatStateOf(0f) }
            // Congelamos el valor objetivo al inicio de la animación
            val targetStreak = uiState.streakLevel
            var displayStreak by remember { mutableIntStateOf(if (targetStreak > 0) targetStreak - 1 else 0) }
            
            LaunchedEffect(uiState.showLevelUp) {
                if (uiState.showLevelUp) {
                    progress = 0f
                    // Reiniciamos al valor anterior al empezar
                    displayStreak = if (targetStreak > 0) targetStreak - 1 else 0
                    
                    // 1. Animación de carga (aprox 3 segundos)
                    while (progress < 1f) {
                        progress += 0.01f
                        delay(30)
                    }
                    
                    // 2. Breve pausa dramática
                    delay(300)
                    
                    // 3. ¡LEVEL UP! Mostramos el nuevo valor
                    displayStreak = targetStreak
                    
                    // 4. Tiempo para disfrutar el logro
                    delay(4500) 

                    viewModel.dismissLevelUp()
                }
            }

            SyncLevelUpScreen(
                streakDays = displayStreak,
                progress = progress
            )
        }
    }
}

@Composable
fun Content(
    uiState: DashboardUiState,
    navController: NavHostController,
    onFilterSelected: (HistoryFilter) -> Unit,
    onExportPdf: () -> Unit,
    onLoginWithGoogle: (String) -> Unit,
    onLogout: () -> Unit,
    onUpdateProfile: (UserProfile) -> Unit
) {
    var selectedBottomAppBarItem by remember { mutableStateOf(BottomAppBarItems.HOME) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(navController = navController)
        },
        bottomBar = {
            NavigationBar(
                selectedBottomAppBarItem = selectedBottomAppBarItem,
                onItemSelected = { selectedBottomAppBarItem = it }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            GravityParticlesBackground(modifier = Modifier.fillMaxSize())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                when (selectedBottomAppBarItem) {
                    BottomAppBarItems.HOME -> {
                        HomeScreen(
                            uiState = uiState,
                            navController = navController
                        )
                    }

                    BottomAppBarItems.HISTORY -> {
                        HistoryWorkScreen(
                            uiState = uiState,
                            onFilterSelected = onFilterSelected,
                            onExportPdf = onExportPdf
                        )
                    }

                    BottomAppBarItems.PROFILE -> {
                        ProfileScreen(
                            uiState = uiState,
                            onLoginWithGoogle = onLoginWithGoogle,
                            onLogout = onLogout,
                            onUpdateProfile = onUpdateProfile
                        )
                    }
                }
            }
        }

    }
}
