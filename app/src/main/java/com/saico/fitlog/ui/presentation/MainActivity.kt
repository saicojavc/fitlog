package com.saico.fitlog.ui.presentation

import android.Manifest
import android.app.ForegroundServiceStartNotAllowedException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.saico.core.model.LanguageConfig
import com.saico.core.notification.NotificationHelper
import com.saico.core.notification.NotificationScheduler
import com.saico.core.ui.navigation.Navigator
import com.saico.core.ui.theme.FitlogTheme
import com.saico.feature.about.navigation.aboutGraph
import com.saico.feature.dashboard.navigation.dashboardGraph
import com.saico.feature.dashboard.service.StepCounterService
import com.saico.feature.gymwork.navigation.gymWorkGraph
import com.saico.feature.onboarding.navigation.onboardingGraph
import com.saico.feature.outdoorrun.navigation.outdoorRunGraph
import com.saico.feature.setting.navigation.settingGraph
import com.saico.feature.stepshistory.navigation.stepsHistoryGraph
import com.saico.feature.weighttracking.navigation.weighttrackingGraph
import com.saico.feature.workout.navigation.workoutGraph
import com.saico.fitlog.ui.presentation.splash.SplashScreen
import com.saico.lfeature.ogin.navigation.loginGraph
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var notificationScheduler: NotificationScheduler

    @Inject
    lateinit var notificationHelper: NotificationHelper

    private val viewModel: MainActivityViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val userData by viewModel.userData.collectAsState()
            
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val activityRecognized = permissions[Manifest.permission.ACTIVITY_RECOGNITION] ?: true
                val notificationsGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false
                } else true

                if (activityRecognized) {
                    startStepCounterService()
                }
                
                if (notificationsGranted) {
                    userData?.let { scheduleInitialNotifications(it.workoutReminderHour, it.workoutReminderMinute) }
                }
            }

            LaunchedEffect(Unit) {
                val permissionsToRequest = mutableListOf<String>()
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                        permissionsToRequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
                    } else {
                        startStepCounterService()
                    }
                } else {
                    startStepCounterService()
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                // Localización (Necesaria para OutdoorRun)
                if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
                }

                if (permissionsToRequest.isNotEmpty()) {
                    permissionLauncher.launch(permissionsToRequest.toTypedArray())
                } else {
                    userData?.let { scheduleInitialNotifications(it.workoutReminderHour, it.workoutReminderMinute) }
                }
            }

            val dynamicColor = userData?.useDynamicColor ?: false

            LaunchedEffect(userData?.languageConfig) {
                userData?.languageConfig?.let { config ->
                    val locale = when (config) {
                        LanguageConfig.ENGLISH -> Locale("en")
                        LanguageConfig.SPANISH -> Locale("es")
                        LanguageConfig.FOLLOW_SYSTEM -> Locale.getDefault()
                    }
                    updateLocale(this@MainActivity, locale)
                }
            }

            FitlogTheme(dynamicColor = dynamicColor) {
                val navController = rememberNavController()
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (viewModel.isLoading) {
                        SplashScreen()
                    } else {
                        MainContainer(
                            startDestination = viewModel.firstScreen,
                            navigator = navigator,
                            navController = navController
                        )
                    }
                }
            }
        }
    }

    private fun startStepCounterService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val intent = Intent(this, StepCounterService::class.java)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } catch (e: Exception) {
            // Manejo preventivo para Android 12+ (ForegroundServiceStartNotAllowedException)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && e is ForegroundServiceStartNotAllowedException) {
                Log.e("FitlogService", "No se permite iniciar el servicio de pasos desde background: ${e.message}")
            } else {
                Log.e("FitlogService", "Error al iniciar StepCounterService: ${e.message}")
            }
        }
    }

    private fun scheduleInitialNotifications(reminderHour: Int, reminderMinute: Int) {
        notificationScheduler.scheduleDailyMotivationalNotification()
        notificationScheduler.scheduleWorkoutReminder(hour = reminderHour, minute = reminderMinute)
        notificationScheduler.scheduleDailySummaryNotification(hour = 21, minute = 0)
    }

    private fun updateLocale(context: Context, locale: Locale) {
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}

@Composable
private fun MainContainer(
    startDestination: String,
    navigator: Navigator,
    navController: NavHostController
) {
    Column {
        NavHost(
            navController = navController,
            startDestination = startDestination,
        ) {
            loginGraph(navHostController = navController)
            onboardingGraph(navController = navController)
            dashboardGraph(navController = navController)
            workoutGraph(navController = navController)
            gymWorkGraph(navController = navController)
            settingGraph(navController = navController)
            stepsHistoryGraph(navController = navController)
            aboutGraph(navController = navController)
            weighttrackingGraph(navController = navController)
            outdoorRunGraph(navController = navController)
        }
    }
}
