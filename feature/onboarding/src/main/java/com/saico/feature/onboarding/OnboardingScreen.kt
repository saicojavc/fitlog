package com.saico.feature.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.saico.core.ui.R
import com.saico.core.ui.theme.CornerDim
import com.saico.core.ui.theme.FitlogTheme
import com.saico.core.ui.theme.LightBackground
import com.saico.core.ui.theme.LightPrimary
import com.saico.core.ui.theme.LightSuccess
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.onboarding.components.OnboardingDailyGoal
import com.saico.feature.onboarding.components.OnboardingFinish
import com.saico.feature.onboarding.components.OnboardingProfileConfiguration
import com.saico.feature.onboarding.state.OnboardingUiState
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel()
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
        onCaloriesToBurnChange = viewModel::onCaloriesToBurnChange
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
    onCaloriesToBurnChange: (Int) -> Unit
) {
    val pagerState = rememberPagerState { 3 }
    val scope = rememberCoroutineScope()

    val gradientColors = if (isSystemInDarkTheme()) {
        listOf(LightPrimary, LightSuccess)
    } else {
        listOf(LightPrimary, LightSuccess, LightBackground)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Top Indicators
        if (pagerState.currentPage < pagerState.pageCount ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = PaddingDim.LARGE),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    Box(
                        modifier = Modifier
                            .padding(horizontal = PaddingDim.VERY_SMALL)
                            .clip(RoundedCornerShape(CornerDim.SMALL))
                            .background(color)
                            .width(PaddingDim.HUGE)
                            .height(PaddingDim.SMALL)
                    )
                }
            }
        }

        // Pager that hosts the different pages
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f), // Let the pager take up available space
            userScrollEnabled = pagerState.currentPage < pagerState.pageCount
        ) { page ->
            when (page) {
                0 -> {
                    Column(modifier = Modifier.padding(PaddingDim.LARGE)) {
                        OnboardingProfileConfiguration(
                            age = state.age,
                            onAgeChange = onAgeChange,
                            weight = state.weight,
                            onWeightChange = onWeightChange,
                            height = state.height,
                            onHeightChange = onHeightChange,
                            gender = state.gender,
                            onGenderSelected = onGenderSelected,
                            isGenderMenuExpanded = state.isGenderMenuExpanded,
                            onGenderMenuExpanded = onGenderMenuExpanded
                        )
                    }
                }
                1 -> {
                    Column(modifier = Modifier.padding(PaddingDim.LARGE)) {
                        OnboardingDailyGoal(
                            dailySteps = state.dailySteps,
                            onDailyStepsChange = onDailyStepsChange,
                            caloriesToBurn = state.caloriesToBurn,
                            onCaloriesToBurnChange = onCaloriesToBurnChange
                        )
                    }
                }
                2 -> {
                    OnboardingFinish(
                        uiState = state
                    )
                }
            }
        }

        // Bottom Button
        if (pagerState.currentPage < pagerState.pageCount - 1) {
            Button(
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage < pagerState.pageCount - 1) {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingDim.LARGE)
            ) {
                Text(text = stringResource(id = R.string.next))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    FitlogTheme {
        val uiState = OnboardingUiState(age = "25", weight = "70", height = "180")
        Content(
            state = uiState,
            onAgeChange = {},
            onWeightChange = {},
            onHeightChange = {},
            onGenderSelected = {},
            onGenderMenuExpanded = {},
            onDailyStepsChange = {},
            onCaloriesToBurnChange = {}
        )
    }
}
