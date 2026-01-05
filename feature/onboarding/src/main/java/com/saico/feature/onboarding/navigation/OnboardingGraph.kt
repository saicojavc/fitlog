package com.saico.feature.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.saico.core.ui.navigation.routes.dashboard.OnboardingRoute
import com.saico.feature.onboarding.OnboardingScreen

fun NavGraphBuilder.onboardingGraph(){
    navigation(
        startDestination = OnboardingRoute.OnboardingScreenRoute.route,
        route = OnboardingRoute.RootRoute.route
    ){
        composable(route = OnboardingRoute.OnboardingScreenRoute.route){
            OnboardingScreen()
        }
    }
}
