package com.saico.feature.setting.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.saico.core.ui.navigation.routes.setting.SettingRoute
import com.saico.feature.setting.SettingScreen

fun NavGraphBuilder.settingGraph(navController: NavHostController) {
    navigation(
        startDestination = SettingRoute.SettingScreenRoute.route,
        route = SettingRoute.RootRoute.route
    ){
        composable(route = SettingRoute.SettingScreenRoute.route){
            SettingScreen(navController = navController)
        }

    }
}