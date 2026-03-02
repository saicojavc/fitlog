package com.saico.fitlog.ui.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.GravityParticlesBackground
import com.saico.core.ui.components.SpacerHeight
import com.saico.core.ui.theme.AppDim
import com.saico.core.ui.theme.FontSizes
import com.saico.core.ui.theme.PaddingDim

@Composable
fun SplashScreen() {
    // 1. Usamos un Box para apilar el fondo y el contenido
    Box(modifier = Modifier.fillMaxSize()) {

        // 2. Capa de fondo: Partículas con gravedad
        GravityParticlesBackground()

        // 3. Capa de contenido: Tu columna original
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingDim.LARGE),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SpacerHeight(PaddingDim.EXTRA_HUGE)

            // Logo con el nuevo borde azul tecnológico
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Logo",
                modifier = Modifier
                    .padding(PaddingDim.EXTRA_HUGE)
                    .size(AppDim.LOGIN_HEADER_ICON_SIZE)
                    .shadow(
                        elevation = 20.dp,
                        shape = CircleShape,
                        spotColor = Color(0xFF3FB9F6),
                        ambientColor = Color(0xFF3FB9F6)
                    )
                    .border(
                        width = 2.dp, // Un poco más grueso para que resalte
                        shape = CircleShape,
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF3FB9F6), Color(0xFF216EE0))
                        )
                    )
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            SpacerHeight(PaddingDim.LARGE)

            FitlogText(
                text = stringResource(id = R.string.welcome_to_fitlog),
                fontSize = FontSizes.REALLY_BIG,
                lineHeight = FontSizes.EXTRA_BIG,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight.Black // Más peso para impacto visual
            )

            SpacerHeight(PaddingDim.SMALL)

            FitlogText(
                text = stringResource(id = R.string.login_subtitle),
                fontSize = FontSizes.TITLE,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.8f) // Un poco de transparencia para elegancia
            )

            SpacerHeight(PaddingDim.EXTRA_HUGE)
        }
    }
}