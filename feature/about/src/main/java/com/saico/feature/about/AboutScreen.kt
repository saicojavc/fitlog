package com.saico.feature.about

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.saico.core.ui.components.FitlogText

@Composable
fun AboutScreen(navController: NavHostController) {
    FitlogText(text = "About")
}