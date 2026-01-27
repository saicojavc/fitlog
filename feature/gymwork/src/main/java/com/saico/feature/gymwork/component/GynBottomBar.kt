package com.saico.feature.gymwork.component

import android.text.format.DateUtils.formatElapsedTime
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogButton
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.LightSuccess
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.gymwork.state.GymWorkUiState

@Composable
fun GymBottomBar(
    uiState: GymWorkUiState,
    onSaveSession: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingDim.MEDIUM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = stringResource(id = R.string.total_time), style = MaterialTheme.typography.labelSmall)
                Text(
                    text = formatElapsedTime(uiState.elapsedTime),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = PaddingDim.MEDIUM)
                ) {
                    Text(text = stringResource(id = R.string.calories), style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = "${uiState.totalCalories} kcal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            FitlogButton(
                modifier = Modifier
                    .weight(1f)
                    .padding(PaddingDim.MEDIUM),
                colors = ButtonColors(
                    LightSuccess,
                    Color.White,
                    Color.Transparent,
                    Color.Transparent
                ),
                onClick = onSaveSession,
                content = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = FitlogIcons.Save, contentDescription = null)
                        Text(text = stringResource(id = R.string.save_session))
                    }

                },

            )
        }

    }

}
