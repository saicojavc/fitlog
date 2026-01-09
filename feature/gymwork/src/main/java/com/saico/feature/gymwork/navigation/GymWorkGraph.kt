package com.saico.feature.gymwork.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.saico.core.ui.navigation.routes.gymwork.GymWorkRoute
import com.saico.feature.gymwork.GymWorkScreen

fun NavGraphBuilder.gymWorkGraph(navController: NavHostController) {
    navigation(
        startDestination = GymWorkRoute.GymWorkScreenRoute.route,
        route = GymWorkRoute.RootRoute.route
    ){
        composable(route = GymWorkRoute.GymWorkScreenRoute.route){
            GymWorkScreen(navController = navController)
        }
    }
}