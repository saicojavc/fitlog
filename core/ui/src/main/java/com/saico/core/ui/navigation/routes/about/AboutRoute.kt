package com.saico.core.ui.navigation.routes.about

import com.saico.core.ui.navigation.routes.Route

interface AboutRoute: Route {
    data object RootRoute : AboutRoute {
        override val analyticsTag: String = "about-flow"
        override val route: String = "about"
    }
    data object AboutScreenRoute : AboutRoute{
        override val analyticsTag: String = "about-screen-flow"
        override val route: String = "about/about-screen"
    }
}