package com.saico.core.ui.navigation.routes.gymwork

import com.saico.core.ui.navigation.routes.Route

interface GymWorkRoute : Route{

    data object RootRoute : GymWorkRoute{
        override val analyticsTag: String = "gym-work-flow"
        override val route: String = "gym-work"
    }
    data object GymWorkScreenRoute : GymWorkRoute{
        override val analyticsTag: String = "gym-work-screen-flow"
        override val route: String = "gym-work/gym-work-screen"

    }
}