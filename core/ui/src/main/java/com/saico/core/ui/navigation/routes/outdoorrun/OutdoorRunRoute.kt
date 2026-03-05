package com.saico.core.ui.navigation.routes.outdoorrun

import com.saico.core.ui.navigation.routes.Route

interface OutdoorRunRoute: Route{

    data object RootRoute : Route{
        override val analyticsTag: String = "outdoor-flow"
        override val route: String = "outdoor"
    }

    data object OutdoorRunScreenRoute : Route{
        override val analyticsTag: String = "outdoor-screen-flow"
        override val route: String = "outdoor/outdoor-screen/{activityType}"

        fun createRoute(activityType: String) = "outdoor/outdoor-screen/$activityType"
    }
}