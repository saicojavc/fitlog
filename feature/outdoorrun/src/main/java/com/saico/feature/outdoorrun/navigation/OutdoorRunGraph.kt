package com.saico.feature.outdoorrun.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.saico.core.ui.navigation.routes.outdoorrun.OutdoorRunRoute
import com.saico.feature.outdoorrun.OutdoorRunScreen

fun NavGraphBuilder.outdoorRunGraph(navController: NavHostController) {

    navigation(
        startDestination = OutdoorRunRoute.OutdoorRunScreenRoute.route,
        route = OutdoorRunRoute.RootRoute.route
    ){
        composable(
            route = OutdoorRunRoute.OutdoorRunScreenRoute.route,
            arguments = listOf(
                navArgument("activityType") { type = NavType.StringType }
            )
        ){ backStackEntry ->
            val activityType = backStackEntry.arguments?.getString("activityType") ?: "outdoor_run"
            OutdoorRunScreen(navController = navController, activityType = activityType)
        }
    }
}