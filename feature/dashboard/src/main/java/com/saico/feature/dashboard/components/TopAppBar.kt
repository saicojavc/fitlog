package com.saico.feature.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.saico.core.ui.R
import com.saico.core.ui.components.FitlogIcon
import com.saico.core.ui.components.FitlogText
import com.saico.core.ui.components.FitlogTopAppBar
import com.saico.core.ui.icon.FitlogIcons
import com.saico.core.ui.navigation.routes.setting.SettingRoute
import com.saico.core.ui.theme.PaddingDim
import com.saico.feature.dashboard.screen.NodeLevel
import com.saico.feature.dashboard.screen.getCurrentNodeLevel
import com.saico.feature.dashboard.state.DashboardUiState
import kotlinx.coroutines.isActive
import java.util.Random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    navController: NavHostController,
    uiState: DashboardUiState
) {
    val streakDays = uiState.userProfile?.currentStreak ?: 0
    val nodeLevel = remember(streakDays) { getCurrentNodeLevel(streakDays) }
    val accentColor = nodeLevel.color

    FitlogTopAppBar(
        title = "",
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black.copy(alpha = 0.3f)
        ),
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // MINI ORBE DE RACHA
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(start = PaddingDim.MEDIUM)
                ) {
                    // 1. LAS PARTÍCULAS (Se dibujan al fondo)
                    MicroParticleOrbe(nodeLevel = nodeLevel)

                    // 2. GLOW EXTERIOR (Ahora usa accentColor)
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        accentColor.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                    )

                    // 3. EL ORBE (Cuerpo central)
                    Surface(
                        modifier = Modifier.size(34.dp),
                        shape = CircleShape,
                        color = Color.Black,
                        border = BorderStroke(1.5.dp, accentColor) // Borde dinámico
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            FitlogText(
                                text = streakDays.toString(),
                                style = TextStyle(
                                    color = accentColor, // Texto dinámico
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                            FitlogText(
                                text = "DAY",
                                style = TextStyle(
                                    color = accentColor.copy(alpha = 0.6f),
                                    fontSize = 6.sp,
                                    letterSpacing = 1.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    }
                }

                FitlogText(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )

                FitlogIcon(
                    modifier = Modifier
                        .padding(PaddingDim.MEDIUM)
                        .clickable {
                            navController.navigate(SettingRoute.RootRoute.route)
                        },
                    imageVector = FitlogIcons.Settings,
                    background = Color.Unspecified,
                )
            }
        },
    )
}

@Composable
fun MicroParticleOrbe(nodeLevel: NodeLevel) {
    val accentColor = nodeLevel.color
    val particles = remember { mutableStateListOf<MicroBit>() }
    var centerX by remember { mutableFloatStateOf(0f) }
    var centerY by remember { mutableFloatStateOf(0f) }

    // Este estado es el único propósito de forzar recomposición cada frame
    var tick by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        val random = Random()
        val spawnRadius = 150f

        while (isActive) {
            withFrameNanos { frameTime ->
                if (centerX == 0f) return@withFrameNanos

                if (particles.size < 30) {
                    val angle = random.nextDouble() * 2.0 * PI
                    particles.add(
                        MicroBit(
                            x = centerX + (cos(angle) * spawnRadius).toFloat(),
                            y = centerY + (sin(angle) * spawnRadius).toFloat(),
                            alpha = 0f,
                            speed = 0.7f + random.nextFloat() * 1.2f
                        )
                    )
                }

                val iterator = particles.listIterator()
                while (iterator.hasNext()) {
                    val bit = iterator.next()
                    val dx = centerX - bit.x
                    val dy = centerY - bit.y
                    val dist = sqrt(dx * dx + dy * dy)

                    if (dist > 8f) {
                        val currentSpeed = bit.speed * nodeLevel.speedMult
                        bit.x += (dx / dist) * currentSpeed
                        bit.y += (dy / dist) * currentSpeed
                        bit.alpha = (bit.alpha + 0.05f).coerceAtMost(0.7f)
                    } else {
                        val angle = random.nextDouble() * 2.0 * PI
                        bit.x = centerX + (cos(angle) * spawnRadius).toFloat()
                        bit.y = centerY + (sin(angle) * spawnRadius).toFloat()
                        bit.alpha = 0f
                        bit.speed = 0.7f + random.nextFloat() * 1.2f
                    }
                }

                tick = frameTime // Fuerza recomposición del Canvas cada frame
            }
        }
    }

    Canvas(
        modifier = Modifier
            .size(36.dp)
            .onGloballyPositioned {
                centerX = it.size.width / 2f
                centerY = it.size.height / 2f
            }
    ) {
        val a = tick // Lee tick para que el Canvas se suscriba al estado

        particles.forEach { bit ->
            drawCircle(
                color = accentColor.copy(alpha = bit.alpha),
                radius = 1.3.dp.toPx(),
                center = Offset(bit.x, bit.y)
            )

            val dx = centerX - bit.x
            val dy = centerY - bit.y
            val dist = sqrt(dx * dx + dy * dy)
            if (dist > 10f) {
                drawLine(
                    color = accentColor.copy(alpha = bit.alpha * 0.3f),
                    start = Offset(bit.x, bit.y),
                    end = Offset(bit.x - (dx / dist) * 10f, bit.y - (dy / dist) * 10f),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
    }
}

// Clase necesaria para las partículas del TopAppBar
data class MicroBit(
    var x: Float,
    var y: Float,
    var alpha: Float,
    var speed: Float
)