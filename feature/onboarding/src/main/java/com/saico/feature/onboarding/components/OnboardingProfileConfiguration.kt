package com.saico.feature.onboarding.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogDropdown
import com.saico.core.ui.components.FitlogOutlinedTextField
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.theme.PaddingDim

@Composable
fun OnboardingProfileConfiguration(
    age: String,
    onAgeChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit,
    height: String,
    onHeightChange: (String) -> Unit,
    gender: String,
    onGenderSelected: (String) -> Unit,
    isGenderMenuExpanded: Boolean,
    onGenderMenuExpanded: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.setup_your_profile),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(PaddingDim.SMALL))

        Text(
            text = stringResource(id = R.string.personalize_your_experience),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(PaddingDim.MEDIUM))

        FitlogCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingDim.SMALL),
        ) {
            Column(
                modifier = Modifier.padding(PaddingDim.SMALL),
                horizontalAlignment = Alignment.Start
            ) {
                FitlogOutlinedTextField(
                    modifier = Modifier.padding(PaddingDim.SMALL).fillMaxWidth(),
                    value = age,
                    onValueChange = onAgeChange,
                    label = stringResource(id = R.string.age),
                    shape = MaterialTheme.shapes.small,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(PaddingDim.MEDIUM))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FitlogOutlinedTextField(
                        modifier = Modifier.padding(PaddingDim.SMALL).weight(1f),
                        value = weight,
                        onValueChange = onWeightChange,
                        label = stringResource(id = R.string.weight_kg),
                        shape = MaterialTheme.shapes.small,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    FitlogOutlinedTextField(
                        modifier = Modifier.padding(PaddingDim.SMALL).weight(1f),
                        value = height,
                        onValueChange = onHeightChange,
                        label = stringResource(id = R.string.height_cm),
                        shape = MaterialTheme.shapes.small,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(PaddingDim.MEDIUM))

                FitlogText(
                    modifier = Modifier.padding(PaddingDim.SMALL),
                    text = stringResource(id = R.string.gender)
                )
                FitlogDropdown(
                    modifier = Modifier.fillMaxWidth().padding(PaddingDim.SMALL),
                    expanded = isGenderMenuExpanded,
                    onExpandedChange = onGenderMenuExpanded,
                    placeholder = stringResource(id = R.string.select_option),
                    options = listOf(
                        stringResource(id = R.string.male),
                        stringResource(id = R.string.female),
                        stringResource(id = R.string.not_specified)
                    ),
                    selectedOption = gender,
                    onOptionSelected = onGenderSelected
                )
            }
        }

        Spacer(modifier = Modifier.height(PaddingDim.MEDIUM))

        // Informational Card
        FitlogCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingDim.SMALL),
            color = MaterialTheme.colorScheme.tertiaryContainer
        ) {
            Row(
                modifier = Modifier.padding(PaddingDim.LARGE),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(PaddingDim.MEDIUM)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null, // Decorative icon
                    modifier = Modifier.size(PaddingDim.LARGE)
                )
                Text(
                    text = stringResource(id = R.string.info_calorie_calculation),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
