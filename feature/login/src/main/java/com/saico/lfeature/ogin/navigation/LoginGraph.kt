package com.saico.lfeature.ogin.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.saico.core.ui.navigation.routes.login.LoginRoute
import com.saico.lfeature.ogin.LoginScreen

fun NavGraphBuilder.loginGraph(navHostController: NavHostController){
    navigation(
        startDestination = LoginRoute.LoginScreenRoute.route,
        route = LoginRoute.RootRoute.route
    ){
        composable(route = LoginRoute.LoginScreenRoute.route){
            LoginScreen(navController = navHostController)
        }

    }
}