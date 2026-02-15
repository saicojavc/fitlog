package com.saico.fitlog.ui.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.SpacerHeight
import com.saico.core.ui.theme.AppDim
import com.saico.core.ui.theme.FontSizes
import com.saico.core.ui.theme.GradientColors
import com.saico.core.ui.theme.PaddingDim

@Composable
fun SplashScreen(){


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(GradientColors))
            .padding(PaddingDim.LARGE),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        SpacerHeight(PaddingDim.EXTRA_HUGE)

        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Logo",
            modifier = Modifier
                .padding(PaddingDim.EXTRA_HUGE)
                .size(AppDim.LOGIN_HEADER_ICON_SIZE)
                .border(
                    width = 1.dp,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
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
            color = Color.White
        )

        SpacerHeight(PaddingDim.SMALL)

        FitlogText(
            text = stringResource(id = R.string.login_subtitle),
            fontSize = FontSizes.TITLE,
            textAlign = TextAlign.Center,
            color = Color.White
        )

        SpacerHeight(PaddingDim.EXTRA_HUGE)

    }
}