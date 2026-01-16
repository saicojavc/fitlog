package com.saico.core.ui.navigation.routes.stepshistory

import com.saico.core.ui.navigation.routes.Route

interface StepsHistoryRoute : Route {

    data object RootRoute : StepsHistoryRoute {
        override val analyticsTag: String = "steps-history-flow"
        override val route: String = "steps-history"
    }

    data object StepsHistoryScreenRoute : StepsHistoryRoute{
        override val analyticsTag: String = "steps-history-screen-flow"
        override val route: String = "steps-history/steps-history-screen"

    }
}