package com.saico.feature.weighttracking.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.saico.core.ui.navigation.routes.weighttracking.WeightTrackingRoute
import com.saico.feature.weighttracking.WeightTrackingScreen

fun NavGraphBuilder.weighttrackingGraph(navController: NavHostController) {
    navigation(
        startDestination = WeightTrackingRoute.WeightTrackingScreenRoute.route,
        route = WeightTrackingRoute.RootRoute.route
    ){
        composable(route = WeightTrackingRoute.WeightTrackingScreenRoute.route){
            WeightTrackingScreen(navController = navController)

        }
    }
}