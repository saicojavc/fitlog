package com.saico.feature.gymwork.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogTextButtonBorder
import com.saico.core.ui.components.TextButtonStyle
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.gymwork.StatItem
import com.saico.feature.gymwork.state.GymExerciseItem

@Composable
fun ExerciseCard(
    exercise: GymExerciseItem,
    onToggleExpansion: () -> Unit,
    onRemove: () -> Unit,
    onEdit: () -> Unit
) {
    FitlogCard(
        modifier = Modifier.fillMaxWidth().clickable{ onToggleExpansion() }
    ) {
        Column(modifier = Modifier.padding(PaddingDim.MEDIUM)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                FitlogIcon(
                    imageVector = FitlogIcons.Weight,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    background = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.width(PaddingDim.MEDIUM))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${exercise.sets} series",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                IconButton(onClick = onToggleExpansion) {
                    Icon(
                        imageVector = if (exercise.isExpanded) FitlogIcons.ArrowUp else FitlogIcons.ArrowDown,
                        contentDescription = if (exercise.isExpanded) "Contraer" else "Expandir"
                    )
                }
            }

            AnimatedVisibility(visible = exercise.isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(PaddingDim.MEDIUM))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(PaddingDim.SMALL),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(label = "Peso", value = "${exercise.weightLb} Lb")
                        StatItem(label = "Reps", value = "${exercise.reps}")
                        StatItem(label = "Series", value = "${exercise.sets}")
                    }
                    Spacer(modifier = Modifier.height(PaddingDim.MEDIUM))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FitlogTextButtonBorder(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            textButtonStyle = TextButtonStyle.DISMISS,
                            label = "Eliminar",
                            onClick = onRemove,
                            content = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = FitlogIcons.Delete, contentDescription = null)
                                    Spacer(modifier = Modifier.width(PaddingDim.VERY_SMALL))
                                    Text(text = "Eliminar")
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(PaddingDim.SMALL))
                        FitlogTextButtonBorder(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            label = "Editar",
                            onClick = onEdit,
                            content = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = FitlogIcons.Edit, contentDescription = null)
                                    Spacer(modifier = Modifier.width(PaddingDim.VERY_SMALL))
                                    Text(text = "Editar")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
