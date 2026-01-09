package com.saico.feature.gymwork

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogTopAppBar
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.LightBackground
import com.saico.core.ui.theme.LightPrimary
import com.saico.core.ui.theme.LightSuccess
import com.saico.core.ui.theme.PaddingDim

@Composable
fun GymWorkScreen( navController: NavHostController,){

    Content(navController = navController)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content(
    navController: NavHostController,
){

    val gradientColors = if (isSystemInDarkTheme()) {
        listOf(LightPrimary, LightSuccess)
    } else {
        listOf(LightPrimary, LightSuccess, LightBackground)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            FitlogTopAppBar(
                title = stringResource(id = R.string.register_session),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isSystemInDarkTheme()) Color.Black.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    FitlogIcon(
                        modifier = Modifier.clickable{
                            navController.popBackStack()
                        },
                        imageVector = FitlogIcons.ArrowBack,
                        background = Color.Transparent,
                        contentDescription = null
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors))
                .padding(paddingValues)
                .padding(PaddingDim.MEDIUM),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }
    }
}