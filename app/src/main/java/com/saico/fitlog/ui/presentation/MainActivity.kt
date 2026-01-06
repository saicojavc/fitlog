package com.saico.fitlog.ui.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.saico.core.ui.navigation.Navigator
import com.saico.core.ui.navigation.routes.login.LoginRoute
import com.saico.core.ui.theme.FitlogTheme
import com.saico.feature.dashboard.navigation.dashboardGraph
import com.saico.feature.onboarding.navigation.onboardingGraph
import com.saico.lfeature.ogin.navigation.loginGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator


    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemIsDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemIsDark) }

            FitlogTheme(darkTheme = isDarkTheme) {

                val navController = rememberNavController()

                Surface(modifier = Modifier.fillMaxSize()) {
                    MainContainer(
                        startDestination = viewModel.firstScreen, // Set a default start destination
                        navigator = navigator,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
private fun MainContainer(
    startDestination: String,
    navigator: Navigator,
    navController: NavHostController
){
    Column{
        NavHost(
            navController = navController,
            startDestination = startDestination,
        ){
            loginGraph(navHostController = navController)

            onboardingGraph(navController = navController)

            dashboardGraph(navController = navController)
        }
    }
}

