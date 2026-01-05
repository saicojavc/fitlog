package com.saico.feature.onboarding.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.saico.core.ui.theme.PaddingDim

@Composable
fun OnboardingFinish(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingDim.LARGE),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Alcanza tus metas", style = MaterialTheme.typography.headlineMedium)
    }
}