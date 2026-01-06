
package com.saico.feature.dashboard.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.saico.core.ui.navigation.routes.dashboard.DashboardRoute
import com.saico.feature.dashboard.DashboardScreen


fun NavGraphBuilder.dashboardGraph(navController: NavHostController) {
   navigation(
       startDestination = DashboardRoute.DashboardScreenRoute.route,
       route = DashboardRoute.RootRoute.route
   ){
       composable(route = DashboardRoute.DashboardScreenRoute.route){
           DashboardScreen(navController = navController)
       }
   }
}
