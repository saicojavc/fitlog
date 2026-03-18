package com.saico.feature.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.FitlogTopAppBar
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.navigation.routes.setting.SettingRoute
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.dashboard.state.DashboardUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    navController: NavHostController,
    uiState: DashboardUiState
) {
    val techBlue = Color(0xFF00E5FF)
    val streakDays = uiState.userProfile?.currentStreak ?: 0

    FitlogTopAppBar(
        title = "",
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        ),
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // MINI ORBE DE RACHA (Sustituye al logo)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(start = PaddingDim.MEDIUM)
                ) {
                    // Glow exterior
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(techBlue.copy(alpha = 0.2f), Color.Transparent)
                                ),
                                shape = CircleShape
                            )
                    )
                    // Orbe pequeño
                    Surface(
                        modifier = Modifier.size(34.dp),
                        shape = CircleShape,
                        color = Color.Black,
                        border = BorderStroke(1.5.dp, techBlue)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            FitlogText(
                                text = streakDays.toString(),
                                style = TextStyle(
                                    color = techBlue,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                            FitlogText(
                                text = "DAY",
                                style = TextStyle(
                                    color = techBlue.copy(alpha = 0.6f),
                                    fontSize = 6.sp,
                                    letterSpacing = 1.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    }
                }

                FitlogText(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )

                FitlogIcon(
                    modifier = Modifier
                        .padding(PaddingDim.MEDIUM)
                        .clickable {
                            navController.navigate(SettingRoute.RootRoute.route)
                        },
                    imageVector = FitlogIcons.Settings,
                    background = Color.Unspecified,
                )
            }
        },
    )
}
