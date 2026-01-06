package com.saico.feature.dashboard.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogNavigationBar
import com.saico.core.ui.components.FitlogNavigationBarItem
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.icon.FitlogIcons
import com.saico.feature.dashboard.model.BottomAppBarItems

@Composable
fun NavigationBar(
    selectedBottomAppBarItem: BottomAppBarItems,
    onItemSelected: (BottomAppBarItems) -> Unit
) {
    FitlogNavigationBar(
        modifier = Modifier
            .fillMaxWidth(),
        containerColor = if (isSystemInDarkTheme()) Color.Black.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
        contentColor = if (isSystemInDarkTheme()) Color.Black.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
        content = {
            FitlogNavigationBarItem(
                isSelected = selectedBottomAppBarItem == BottomAppBarItems.HOME,
                onClick = { onItemSelected(BottomAppBarItems.HOME) },
                icon = FitlogIcons.Home,
                label = {
                    FitlogText(
                        text = stringResource(id = R.string.home)
                    )
                },
            )
            FitlogNavigationBarItem(
                isSelected = selectedBottomAppBarItem == BottomAppBarItems.HISTORY,
                onClick = { onItemSelected(BottomAppBarItems.HISTORY) },
                icon = FitlogIcons.History,
                label = {
                    FitlogText(
                        text = stringResource(id = R.string.history)
                    )
                },
            )
            FitlogNavigationBarItem(
                isSelected = selectedBottomAppBarItem == BottomAppBarItems.PROFILE,
                onClick = { onItemSelected(BottomAppBarItems.PROFILE) },
                icon = FitlogIcons.Person,
                label = {
                    FitlogText(
                        text = stringResource(id = R.string.profile)
                    )
                },
            )
        }
    )
}
