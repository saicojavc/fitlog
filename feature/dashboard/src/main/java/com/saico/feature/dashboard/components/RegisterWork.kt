package com.saico.feature.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.LightPrimaryVariant
import com.saico.core.ui.theme.PaddingDim
import com.saico.core.ui.theme.LightSuccess
import com.saico.core.ui.R
import com.saico.core.ui.navigation.routes.gymwork.GymWorkRoute
import com.saico.core.ui.navigation.routes.workout.WorkoutRoute
import com.saico.core.ui.theme.DarkPrimary
import com.saico.core.ui.theme.LightBackground

@Composable
fun RegisterWork(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingDim.SMALL),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FitlogCard(
            modifier = Modifier
                .weight(1f)
                .padding(PaddingDim.SMALL)
                .clickable{
                    navController.navigate(WorkoutRoute.WorkoutScreenRoute.route)
                },
            color = Color(0xFF1E293B),
            elevation = 1.dp,
            border = BorderStroke(1.dp, LightBackground.copy(alpha = 0.7f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingDim.MEDIUM),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                FitlogIcon(
                    imageVector = FitlogIcons.Walk,
                    background = Color.Unspecified,
                    tint = LightSuccess
                )
                FitlogText(
                    text = stringResource(id = R.string.register_workout)
                )
            }
        }

        FitlogCard(
            modifier = Modifier
                .weight(1f)
                .padding(PaddingDim.SMALL)
                .clickable{
                    navController.navigate(GymWorkRoute.GymWorkScreenRoute.route)
                },
            color =  Color(0xFF1E293B),
            elevation = 1.dp,
            border = BorderStroke(1.dp, LightBackground.copy(alpha = 0.7f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingDim.MEDIUM),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                FitlogIcon(
                    imageVector = FitlogIcons.Weight,
                    background = Color.Unspecified,
                    tint = DarkPrimary
                )
                FitlogText(
                    text = stringResource(id = R.string.register_gym)
                )
            }
        }
    }
}
