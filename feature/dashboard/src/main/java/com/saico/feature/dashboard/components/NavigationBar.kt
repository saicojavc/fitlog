package com.saico.feature.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saico.core.ui.R
import com.saico.core.ui.icon.FitlogIcons
import com.saico.feature.dashboard.model.BottomAppBarItems

@Composable
fun NavigationBar(
    selectedBottomAppBarItem: BottomAppBarItems,
    onItemSelected: (BottomAppBarItems) -> Unit
) {
    // 1. Contenedor externo totalmente transparente
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .navigationBarsPadding() // Empuja el contenido hacia arriba de la barra de gestos
            .padding(horizontal = 16.dp, vertical = 8.dp) // Opcional: hace que la barra "flote"
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp) // Altura estándar de una Nav Bar
                .clip(RoundedCornerShape(32.dp)) // Redondeo total para estilo "píldora" o solo arriba
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.15f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(32.dp)
                ),
            color = Color(0xFF1E293B).copy(alpha = 0.8f), // Glassmorphism
            tonalElevation = 0.dp
        ) {
            // 2. Usamos una Row simple para los ítems en lugar del componente NavigationBar de Material
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ITEM HOME
                FitlogNavItem(
                    isSelected = selectedBottomAppBarItem == BottomAppBarItems.HOME,
                    onClick = { onItemSelected(BottomAppBarItems.HOME) },
                    icon = FitlogIcons.Home,
                    label = stringResource(id = R.string.home)
                )
                // ITEM HISTORY
                FitlogNavItem(
                    isSelected = selectedBottomAppBarItem == BottomAppBarItems.HISTORY,
                    onClick = { onItemSelected(BottomAppBarItems.HISTORY) },
                    icon = FitlogIcons.History,
                    label = stringResource(id = R.string.history)
                )
                // ITEM PROFILE
                FitlogNavItem(
                    isSelected = selectedBottomAppBarItem == BottomAppBarItems.PROFILE,
                    onClick = { onItemSelected(BottomAppBarItems.PROFILE) },
                    icon = FitlogIcons.Person,
                    label = stringResource(id = R.string.profile)
                )
            }
        }
    }
}

@Composable
fun RowScope.FitlogNavItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String
) {
    val color = if (isSelected) Color(0xFF3FB9F6) else Color.White.copy(alpha = 0.6f)

    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // Quita el efecto gris feo al hacer clic
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(26.dp)
        )
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}