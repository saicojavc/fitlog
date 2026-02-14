package com.saico.feature.about.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.saico.core.ui.navigation.routes.about.AboutRoute
import com.saico.feature.about.AboutScreen

fun NavGraphBuilder.aboutGraph(navController: NavHostController) {
    navigation(
        startDestination = AboutRoute.AboutScreenRoute.route,
        route = AboutRoute.RootRoute.route
    ){
        composable(route = AboutRoute.AboutScreenRoute.route){
            AboutScreen(navController = navController)
        }

    }
}