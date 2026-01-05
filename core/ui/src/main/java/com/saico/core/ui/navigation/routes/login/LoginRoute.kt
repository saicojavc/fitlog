package com.saico.core.ui.navigation.routes.login

import com.saico.core.ui.navigation.routes.Route

interface LoginRoute : Route {

    data object RootRoute : LoginRoute {
        override val analyticsTag: String = "loginR-flow"
        override val route: String = "login"
    }

    data object LoginScreenRoute : LoginRoute{
        override val analyticsTag: String = "loginS-screen-flow"
        override val route: String = "login/login-screen"
    }
}