package com.saico.core.ui.navigation.routes.workout

import com.saico.core.ui.navigation.routes.Route

interface WorkoutRoute: Route {

    data object RootRoute : WorkoutRoute {
        override val analyticsTag: String = "workout-flow"
        override val route: String = "workout"
    }
    data object WorkoutScreenRoute : WorkoutRoute{
        override val analyticsTag: String = "workout-screen-flow"
        override val route: String = "workout/workout-screen"
    }
}