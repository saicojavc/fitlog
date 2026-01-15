package com.saico.core.ui.navigation.routes.setting

import com.saico.core.ui.navigation.routes.Route

interface SettingRoute : Route{

    data object RootRoute : SettingRoute {
        override val analyticsTag: String = "setting-flow"
        override val route: String = "setting"
    }

    data object SettingScreenRoute : SettingRoute{
        override val analyticsTag: String = "setting-screen-flow"
        override val route: String = "setting/setting-screen"
    }
}