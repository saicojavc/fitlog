package com.saico.feature.onboarding.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.saico.core.model.UnitsConfig
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogDropdown
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.SpacerHeight
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.PaddingDim

@Composable
fun OnboardingProfileConfiguration(
    age: String,
    onAgeChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit,
    height: String,
    onHeightChange: (String) -> Unit,
    heightFt: String,
    onHeightFtChange: (String) -> Unit,
    heightIn: String,
    onHeightInChange: (String) -> Unit,
    gender: String,
    onGenderSelected: (String) -> Unit,
    isGenderMenuExpanded: Boolean,
    onGenderMenuExpanded: (Boolean) -> Unit,
    unitsConfig: UnitsConfig,
    onUnitsConfigSelected: (UnitsConfig) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = PaddingDim.MEDIUM),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        FitlogText(
            text = stringResource(id = R.string.personalize_your_experience),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )

        SpacerHeight(PaddingDim.SMALL)

        FitlogText(
            text = stringResource(id = R.string.profile_metrics_description),
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF94A3B8),
        )

        SpacerHeight(PaddingDim.LARGE)

        // Selector de Unidades
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            UnitsConfig.values().forEachIndexed { index, config ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = UnitsConfig.values().size),
                    onClick = { onUnitsConfigSelected(config) },
                    selected = config == unitsConfig,
                    label = {
                        Text(
                            text = when (config) {
                                UnitsConfig.METRIC -> stringResource(R.string.metric_system)
                                UnitsConfig.IMPERIAL -> stringResource(R.string.imperial_system)
                            },
                            color = Color.White
                        )
                    },
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = Color(0xFF10B981),
                        inactiveContainerColor = Color(0xFF1E293B).copy(alpha = 0.6f),
                        activeContentColor = Color.White,
                        inactiveContentColor = Color.White.copy(alpha = 0.6f)
                    )
                )
            }
        }

        SpacerHeight(PaddingDim.MEDIUM)

        // Tarjeta Principal Estilo Glassmorphism
        FitlogCard(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF1E293B).copy(alpha = 0.6f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(PaddingDim.MEDIUM),
                horizontalAlignment = Alignment.Start
            ) {
                // Sección EDAD
                ProfileInputItem(
                    label = stringResource(id = R.string.age),
                    value = age,
                    icon = FitlogIcons.CalendarToday,
                    onValueChange = onAgeChange
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = PaddingDim.MEDIUM),
                    color = Color.White.copy(alpha = 0.05f)
                )

                // Sección PESO Y ALTURA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(PaddingDim.MEDIUM)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        ProfileInputItem(
                            label = if (unitsConfig == UnitsConfig.METRIC) stringResource(id = R.string.weight_kg) else stringResource(id = R.string.weight_lb),
                            value = weight,
                            icon = FitlogIcons.FitnessCenter,
                            onValueChange = onWeightChange
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        if (unitsConfig == UnitsConfig.METRIC) {
                            ProfileInputItem(
                                label = stringResource(id = R.string.height_cm),
                                value = height,
                                icon = FitlogIcons.Height,
                                onValueChange = onHeightChange
                            )
                        } else {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = FitlogIcons.Height,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    FitlogText(
                                        text = stringResource(id = R.string.height),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    BasicTextField(
                                        value = heightFt,
                                        onValueChange = onHeightFtChange,
                                        textStyle = MaterialTheme.typography.titleLarge.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Light,
                                            textAlign = TextAlign.End
                                        ),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        cursorBrush = SolidColor(Color(0xFF10B981)),
                                        modifier = Modifier.width(30.dp),
                                        decorationBox = { innerTextField ->
                                            if (heightFt.isEmpty()) Text("0", color = Color.White.copy(0.2f), style = MaterialTheme.typography.titleLarge)
                                            innerTextField()
                                        }
                                    )
                                    FitlogText(text = " ft ", color = Color.White.copy(0.4f), style = MaterialTheme.typography.labelSmall)
                                    BasicTextField(
                                        value = heightIn,
                                        onValueChange = onHeightInChange,
                                        textStyle = MaterialTheme.typography.titleLarge.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Light,
                                            textAlign = TextAlign.End
                                        ),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        cursorBrush = SolidColor(Color(0xFF10B981)),
                                        modifier = Modifier.width(30.dp),
                                        decorationBox = { innerTextField ->
                                            if (heightIn.isEmpty()) Text("0", color = Color.White.copy(0.2f), style = MaterialTheme.typography.titleLarge)
                                            innerTextField()
                                        }
                                    )
                                    FitlogText(text = " in", color = Color.White.copy(0.4f), style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = PaddingDim.MEDIUM),
                    color = Color.White.copy(alpha = 0.05f)
                )

                // Sección GÉNERO
                FitlogText(
                    text = stringResource(id = R.string.gender),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF94A3B8)
                )

                FitlogDropdown(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    expanded = isGenderMenuExpanded,
                    onExpandedChange = onGenderMenuExpanded,
                    placeholder = stringResource(id = R.string.select_option),
                    options = listOf(
                        stringResource(id = R.string.male),
                        stringResource(id = R.string.female)
                    ),
                    selectedOption = gender,
                    onOptionSelected = onGenderSelected,
                )
            }
        }

        SpacerHeight(PaddingDim.LARGE)


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF10B981).copy(alpha = 0.1f), CircleShape)
                .padding(PaddingDim.MEDIUM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(id = R.string.info_calorie_calculation),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun ProfileInputItem(
    label: String,
    value: String,
    icon: ImageVector,
    onValueChange: (String) -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            FitlogText(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF94A3B8)
            )
        }


        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Light
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            cursorBrush = SolidColor(Color(0xFF10B981)),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text("0", style = MaterialTheme.typography.titleLarge, color = Color.White.copy(0.2f))
                }
                innerTextField()
            },
            modifier = Modifier.padding(top = 4.dp).fillMaxWidth()
        )
    }
}
