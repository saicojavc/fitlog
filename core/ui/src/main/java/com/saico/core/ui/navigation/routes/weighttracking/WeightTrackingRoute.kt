package com.saico.core.ui.navigation.routes.weighttracking

import com.saico.core.ui.navigation.routes.Route

interface WeightTrackingRoute: Route {
    data object RootRoute : WeightTrackingRoute {
        override val analyticsTag: String = "weight-tracking-flow"
        override val route: String = "weight-tracking"

    }
    data object WeightTrackingScreenRoute : WeightTrackingRoute{
        override val analyticsTag: String = "weight-tracking-screen-flow"
        override val route: String = "weight-tracking/weight-tracking-screen"
    }
}