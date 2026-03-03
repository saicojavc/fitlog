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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.sp
import com.saico.core.model.UnitsConfig
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogDropdown
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.SpacerHeight
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.PaddingDim
import com.saico.core.ui.theme.techBlue

@OptIn(ExperimentalMaterial3Api::class)
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
        // Títulos con más peso visual
        FitlogText(
            text = stringResource(id = R.string.personalize_your_experience).uppercase(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 1.sp
        )

        SpacerHeight(PaddingDim.SMALL)

        FitlogText(
            text = stringResource(id = R.string.profile_metrics_description),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        SpacerHeight(PaddingDim.EXTRA_LARGE)

        // Selector de Unidades (Estilo Cyber-Switch)
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingDim.SMALL)
        ) {
            UnitsConfig.entries.forEachIndexed { index, config ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = UnitsConfig.entries.size),
                    onClick = { onUnitsConfigSelected(config) },
                    selected = config == unitsConfig,
                    label = {
                        Text(
                            text = when (config) {
                                UnitsConfig.METRIC -> stringResource(R.string.metric_system)
                                UnitsConfig.IMPERIAL -> stringResource(R.string.imperial_system)
                            }.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = techBlue.copy(alpha = 0.2f),
                        inactiveContainerColor = Color.White.copy(alpha = 0.05f),
                        activeContentColor = techBlue,
                        inactiveContentColor = Color.White.copy(alpha = 0.4f),
                        activeBorderColor = techBlue.copy(alpha = 0.5f),
                        inactiveBorderColor = Color.White.copy(alpha = 0.1f)
                    )
                )
            }
        }

        SpacerHeight(PaddingDim.LARGE)

        // Tarjeta Principal (Glassmorphism Oscuro)
        FitlogCard(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF0D1424).copy(alpha = 0.7f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(PaddingDim.LARGE),
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
                    modifier = Modifier.padding(vertical = PaddingDim.LARGE),
                    color = Color.White.copy(alpha = 0.08f)
                )

                // Sección PESO Y ALTURA
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(PaddingDim.LARGE)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        ProfileInputItem(
                            label = if (unitsConfig == UnitsConfig.METRIC)
                                stringResource(id = R.string.weight_kg) else stringResource(id = R.string.weight_lb),
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
                            // Altura Imperial (FT / IN) con estilo Tech
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = FitlogIcons.Height,
                                        contentDescription = null,
                                        tint = techBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    FitlogText(
                                        text = stringResource(id = R.string.height).uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.5f),
                                        letterSpacing = 1.sp
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.Bottom,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    BasicTextField(
                                        value = heightFt,
                                        onValueChange = onHeightFtChange,
                                        textStyle = MaterialTheme.typography.headlineSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Black
                                        ),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        cursorBrush = SolidColor(techBlue),
                                        modifier = Modifier.width(35.dp),
                                        decorationBox = { innerTextField ->
                                            if (heightFt.isEmpty()) Text("0", color = Color.White.copy(0.1f), style = MaterialTheme.typography.headlineSmall)
                                            innerTextField()
                                        }
                                    )
                                    FitlogText(text = "FT", color = techBlue, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                    Spacer(Modifier.width(12.dp))
                                    BasicTextField(
                                        value = heightIn,
                                        onValueChange = onHeightInChange,
                                        textStyle = MaterialTheme.typography.headlineSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Black
                                        ),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        cursorBrush = SolidColor(techBlue),
                                        modifier = Modifier.width(35.dp),
                                        decorationBox = { innerTextField ->
                                            if (heightIn.isEmpty()) Text("0", color = Color.White.copy(0.1f), style = MaterialTheme.typography.headlineSmall)
                                            innerTextField()
                                        }
                                    )
                                    FitlogText(text = "IN", color = techBlue, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = PaddingDim.LARGE),
                    color = Color.White.copy(alpha = 0.08f)
                )

                // Sección GÉNERO
                FitlogText(
                    text = stringResource(id = R.string.gender).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f),
                    letterSpacing = 1.sp
                )

                FitlogDropdown(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
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

        // Banner Informativo (Look de Notificación de Sistema)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = techBlue.copy(alpha = 0.05f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, techBlue.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(PaddingDim.MEDIUM),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = techBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = stringResource(id = R.string.info_calorie_calculation),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    lineHeight = 16.sp
                )
            }
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
    val techBlue = Color(0xFF3FB9F6)
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = techBlue, // Cambio a Azul
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            FitlogText(
                text = label.uppercase(), // Consistencia visual
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Black
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            cursorBrush = SolidColor(techBlue), // Cursor Azul
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text("0", style = MaterialTheme.typography.headlineSmall, color = Color.White.copy(0.1f))
                }
                innerTextField()
            },
            modifier = Modifier.padding(top = 4.dp).fillMaxWidth()
        )
    }
}
