package com.saico.feature.stepshistory.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.saico.core.ui.navigation.routes.stepshistory.StepsHistoryRoute
import com.saico.feature.stepshistory.StepsHistoryScreen

fun NavGraphBuilder.stepsHistoryGraph(navController: NavHostController) {

    navigation(
        startDestination = StepsHistoryRoute.StepsHistoryScreenRoute.route,
        route = StepsHistoryRoute.RootRoute.route
    ){
        composable(route = StepsHistoryRoute.StepsHistoryScreenRoute.route){
            StepsHistoryScreen(navController = navController)
        }

    }

}