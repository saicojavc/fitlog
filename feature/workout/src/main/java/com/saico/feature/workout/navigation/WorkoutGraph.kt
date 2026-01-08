package com.saico.feature.workout.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.saico.core.ui.navigation.routes.workout.WorkoutRoute
import com.saico.feature.workout.WorkoutScreen

fun NavGraphBuilder.workoutGraph(navController: NavHostController) {
    navigation(
        startDestination = WorkoutRoute.WorkoutScreenRoute.route,
        route = WorkoutRoute.RootRoute.route
    ){
        composable(route = WorkoutRoute.WorkoutScreenRoute.route){
            WorkoutScreen(navController = navController)
        }

    }
}