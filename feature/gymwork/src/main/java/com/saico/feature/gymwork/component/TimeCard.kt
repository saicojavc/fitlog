package com.saico.feature.gymwork.component

import android.text.format.DateUtils.formatElapsedTime
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogCard
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.gymwork.state.GymWorkUiState

@Composable
fun TimeCard(uiState: GymWorkUiState, onToggleTimer: () -> Unit) {
    FitlogCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingDim.MEDIUM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.padding(PaddingDim.MEDIUM),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.elapsed_time),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = formatElapsedTime(uiState.elapsedTime),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )

            }
            IconButton(
                onClick = onToggleTimer,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (uiState.isTimerRunning) Color.Red else MaterialTheme.colorScheme.primary)
            ) {
                FitlogIcon(
                    imageVector = if (uiState.isTimerRunning) FitlogIcons.Stop else FitlogIcons.Play,
                    contentDescription = null,
                    tint = Color.White,
                    background = Color.Transparent
                )
            }
        }

    }
}
