package com.saico.feature.onboarding.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.SpacerHeight
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.CornerDim
import com.saico.core.ui.theme.LightPrimary
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.onboarding.state.OnboardingUiState
import kotlinx.coroutines.launch

@Composable
fun OnboardingFinish(
    uiState: OnboardingUiState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.padding(
                top = PaddingDim.LARGE,
                start = PaddingDim.LARGE,
                end = PaddingDim.LARGE
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FitlogIcon(
                modifier = Modifier
                    .padding(horizontal = PaddingDim.MEDIUM)
                    .size(PaddingDim.EXTRA_HUGE)
                    .border(1.dp, Color.White, CircleShape),
                imageVector = FitlogIcons.Check,
                background = Color.Transparent,
                tint = Color.White,
            )

            SpacerHeight(PaddingDim.SMALL)

            FitlogText(
                text = stringResource(id = R.string.all_set),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
            )

            SpacerHeight(PaddingDim.SMALL)

            FitlogText(
                text = stringResource(id = R.string.review_your_data),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
            )
        }

        SpacerHeight(PaddingDim.LARGE)

        FitlogCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(
                topStart = CornerDim.HUGE,
                topEnd = CornerDim.HUGE,
                bottomStart = CornerDim.ZERO,
                bottomEnd = CornerDim.ZERO
            )
        ) {
            SpacerHeight(PaddingDim.MEDIUM)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(PaddingDim.MEDIUM)
            ) {
                FitlogText(
                    modifier = Modifier.padding( PaddingDim.MEDIUM),
                    text = stringResource(id = R.string.your_profile),
                    style = MaterialTheme.typography.headlineSmall
                )

                SpacerHeight(PaddingDim.MEDIUM)

                FitlogCard(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = Color.Transparent,
                    shape = RoundedCornerShape(CornerDim.MEDIUM),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PaddingDim.MEDIUM),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                FitlogIcon(
                                    imageVector = FitlogIcons.Cake,
                                    contentDescription = null,
                                    background = Color.Transparent
                                )
                                FitlogText(text = stringResource(id = R.string.age))
                            }
                            FitlogText(text = "${uiState.age} ${stringResource(id = R.string.years)}")
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val genderIcon = when (uiState.gender) {
                                    "Male" -> FitlogIcons.Male
                                    "Female" -> FitlogIcons.Female
                                    else -> FitlogIcons.Person
                                }
                                FitlogIcon(
                                    imageVector = genderIcon,
                                    contentDescription = null,
                                    background = Color.Transparent
                                )
                                FitlogText(text = stringResource(id = R.string.gender))
                            }
                            FitlogText(text = uiState.gender)
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PaddingDim.MEDIUM),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                FitlogIcon(
                                    imageVector = FitlogIcons.Weight,
                                    contentDescription = null,
                                    background = Color.Transparent
                                )
                                FitlogText(text = stringResource(id = R.string.weight))
                            }
                            FitlogText(text = "${uiState.weight} kg")
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                FitlogIcon(
                                    imageVector = FitlogIcons.Height,
                                    contentDescription = null,
                                    background = Color.Transparent
                                )
                                FitlogText(text = stringResource(id = R.string.height))
                            }
                            FitlogText(text = "${uiState.height} cm")
                        }
                    }
                }

                SpacerHeight(PaddingDim.MEDIUM)

                FitlogText(
                    modifier = Modifier.padding(PaddingDim.MEDIUM),
                    text = stringResource(id = R.string.your_main_goal),
                    style = MaterialTheme.typography.headlineSmall
                )

                FitlogCard(
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = LightPrimary,
                    shape = RoundedCornerShape(CornerDim.MEDIUM),
                ) {
                    Column(
                        modifier = Modifier.padding(PaddingDim.MEDIUM)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FitlogIcon(imageVector = FitlogIcons.Walk, contentDescription = null, background = Color.Transparent, tint = Color.White)
                            FitlogText(text = stringResource(id = R.string.lose_weight), color = Color.White)
                        }
                        SpacerHeight(PaddingDim.SMALL)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FitlogText(text = stringResource(id = R.string.running), color = Color.White)
                            FitlogText(text = stringResource(id = R.string.times_a_week), color = Color.White)
                        }
                        Divider(
                            modifier = Modifier.padding(vertical = PaddingDim.SMALL),
                            color = Color.White,
                            thickness = 1.dp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FitlogText(text = stringResource(id = R.string.level), color = Color.White)
                            FitlogText(text = stringResource(id = R.string.beginner), color = Color.White)
                        }
                    }
                }

                Button(
                    onClick = {

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PaddingDim.LARGE)
                ) {
                    Text(text = stringResource(id = R.string.save_and_continue))
                }

            }
        }
    }
}