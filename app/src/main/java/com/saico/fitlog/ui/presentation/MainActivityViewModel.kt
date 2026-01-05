package com.saico.fitlog.ui.presentation

import androidx.lifecycle.ViewModel
import com.saico.core.ui.navigation.routes.login.LoginRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {
    val firstScreen = LoginRoute.RootRoute.route
}
