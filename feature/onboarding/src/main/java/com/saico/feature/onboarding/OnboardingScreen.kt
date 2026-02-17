package com.saico.feature.onboarding

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.saico.core.model.UnitsConfig
import com.saico.core.ui.R
import com.saico.core.ui.theme.GradientColors
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.onboarding.components.OnboardingDailyGoal
import com.saico.feature.onboarding.components.OnboardingFinish
import com.saico.feature.onboarding.components.OnboardingProfileConfiguration
import com.saico.feature.onboarding.state.OnboardingUiState
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        state = uiState,
        onAgeChange = viewModel::onAgeChange,
        onWeightChange = viewModel::onWeightChange,
        onHeightChange = viewModel::onHeightChange,
        onGenderSelected = viewModel::onGenderSelected,
        onGenderMenuExpanded = viewModel::onGenderMenuExpanded,
        onDailyStepsChange = viewModel::onDailyStepsChange,
        onCaloriesToBurnChange = viewModel::onCaloriesToBurnChange,
        onSaveUserProfile = viewModel::saveUserProfile,
        onUnitsConfigSelected = viewModel::onUnitsConfigSelected,
        navController = navController
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Content(
    state: OnboardingUiState,
    onAgeChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onGenderSelected: (String) -> Unit,
    onGenderMenuExpanded: (Boolean) -> Unit,
    onDailyStepsChange: (Int) -> Unit,
    onCaloriesToBurnChange: (Int) -> Unit,
    onSaveUserProfile: () -> Unit,
    onUnitsConfigSelected: (UnitsConfig) -> Unit,
    navController: NavHostController,
) {
    val pagerState = rememberPagerState { 3 }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(GradientColors)), // Tu degradado azul-verde
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 1. INDICADORES DE PASO (Más finos y modernos)
        if (pagerState.currentPage < pagerState.pageCount) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val isActive = pagerState.currentPage == iteration
                    val color = if (isActive) Color(0xFF10B981) else Color.White.copy(alpha = 0.2f)
                    val width = if (isActive) 32.dp else 12.dp // El paso activo es más largo

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .width(width)
                            .height(4.dp)
                            .animateContentSize() // Animación suave al cambiar
                    )
                }
            }
        }

        // 2. PAGER (userScrollEnabled = false para bloquear el dedo)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = false, // BLOQUEO DE GESTO MANUAL
            beyondViewportPageCount = 1
        ) { page ->
            when (page) {
                0 -> OnboardingProfileConfiguration(
                    age = state.age,
                    onAgeChange = onAgeChange,
                    weight = state.weight,
                    onWeightChange = onWeightChange,
                    height = state.height,
                    onHeightChange = onHeightChange,
                    gender = state.gender,
                    onGenderSelected = onGenderSelected,
                    isGenderMenuExpanded = state.isGenderMenuExpanded,
                    onGenderMenuExpanded = onGenderMenuExpanded,
                    unitsConfig = state.unitsConfig,
                    onUnitsConfigSelected = onUnitsConfigSelected
                )
                1 -> OnboardingDailyGoal(
                    dailySteps = state.dailySteps,
                    onDailyStepsChange = onDailyStepsChange,
                    caloriesToBurn = state.caloriesToBurn,
                    onCaloriesToBurnChange = onCaloriesToBurnChange
                )
                2 -> OnboardingFinish(
                    uiState = state,
                    onSaveUserProfile = onSaveUserProfile,
                    navController = navController,
                    unitsConfig = state.unitsConfig,
                )
            }
        }

        // 3. BOTÓN INFERIOR (Estilo Pill Premium)
        if (pagerState.currentPage < pagerState.pageCount - 1) {
            Button(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                },
                enabled = if (pagerState.currentPage == 0) state.isProfileConfigurationValid else true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = PaddingDim.VERY_HUGE, start = PaddingDim.LARGE, end = PaddingDim.LARGE)
                    .height(56.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    disabledContainerColor = Color(0xFF1E293B)
                )
            ) {
                Text(
                    text = stringResource(id = R.string.next).uppercase(),
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
