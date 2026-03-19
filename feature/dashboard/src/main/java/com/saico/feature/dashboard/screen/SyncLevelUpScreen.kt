package com.saico.feature.dashboard.screen

import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saico.core.ui.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// --- 1. MODELO DE DATOS Y NIVELES ---

data class DataBit(
    val id: Int,
    var x: Float,
    var y: Float,
    var speed: Float,
    var alpha: Float = 0f,
    var size: Float
)

enum class NodeLevel(val color: Color, val speedMult: Float) {
    FRAGMENTED(Color(0xFF00E5FF), 1.0f), // Nivel 0-10 (Tu azul actual)
    SYNCED(Color(0xFF76FF03), 1.6f),         // Nivel 11-30 (Verde Neón)
    MAINFRAME(Color(0xFFFF1744), 2.5f)  // Nivel 31+ (Rojo Crítico)
}

fun getCurrentNodeLevel(streak: Int): NodeLevel {
    return when {
        streak <= 10 -> NodeLevel.FRAGMENTED
        streak <= 30 -> NodeLevel.SYNCED
        else -> NodeLevel.MAINFRAME
    }
}

// --- 2. COMPONENTE PRINCIPAL ---

@Composable
fun SyncLevelUpScreen(streakDays: Int, progress: Float) {
    // Determinar el nivel actual basado en la racha
    val nodeLevel = remember(streakDays) { getCurrentNodeLevel(streakDays) }
    val accentColor = nodeLevel.color

    var currentDisplayStreak by remember { mutableIntStateOf(streakDays) }
    var isLevelingUp by remember { mutableStateOf(false) }

    // Estados de partículas
    val dataBits = remember { mutableStateListOf<DataBit>() }
    var center by remember { mutableStateOf(Offset.Zero) }
    val numParticles = 85
    val targetRadiusPx = 110.dp.value

    if (dataBits.isEmpty()) {
        repeat(numParticles) { dataBits.add(createRandomParticle(it)) }
    }

    // Lógica de impacto al subir de nivel
    LaunchedEffect(streakDays) {
        if (streakDays > currentDisplayStreak) {
            isLevelingUp = true
            delay(100)
            currentDisplayStreak = streakDays
            delay(1000)
            isLevelingUp = false
        }
    }

    // Game Loop de Partículas (Velocidad afectada por el nivel)
    LaunchedEffect(center, isLevelingUp, nodeLevel) {
        val random = Random()
        while (isActive) {
            withFrameMillis {
                dataBits.forEach { bit ->
                    if (bit.x == 0f && center != Offset.Zero) respawnParticle(bit, center, random)

                    val dx = center.x - bit.x
                    val dy = center.y - bit.y
                    val dist = sqrt(dx * dx + dy * dy)

                    // La velocidad base aumenta con el nivel de nodo
                    val currentSpeed = bit.speed * nodeLevel.speedMult
                    val direction = if (isLevelingUp) -7f else 1.2f

                    if (dist > targetRadiusPx || isLevelingUp) {
                        bit.x += (dx / dist) * currentSpeed * direction
                        bit.y += (dy / dist) * currentSpeed * direction
                        if (bit.alpha < 0.8f) bit.alpha += 0.02f
                    } else {
                        if (center != Offset.Zero) respawnParticle(bit, center, random)
                    }
                }
            }
        }
    }

    // --- ANIMACIONES DE SISTEMA ---
    val infiniteTransition = rememberInfiniteTransition(label = "PulseSystem")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isLevelingUp) 1.2f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isLevelingUp) 60 else 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "Pulse"
    )

    val shakeOffset by animateFloatAsState(
        targetValue = if (isLevelingUp) 8f else 0f,
        animationSpec = repeatable(10, tween(50), RepeatMode.Reverse), label = "Shake"
    )

    val waveRadius by animateFloatAsState(
        targetValue = if (isLevelingUp) 2000f else 0f,
        animationSpec = if (isLevelingUp) tween(800, easing = EaseOutExpo) else snap(),
        label = "Wave"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF020509), Color(0xFF020202))))
            .offset(x = shakeOffset.dp, y = (shakeOffset / 2).dp),
        contentAlignment = Alignment.Center
    ) {
        // --- CAPA 1: CANVAS DE PARTÍCULAS ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (center == Offset.Zero) center = size.center
            dataBits.forEach { bit ->
                drawCircle(
                    color = accentColor.copy(alpha = bit.alpha),
                    radius = bit.size,
                    center = Offset(bit.x, bit.y)
                )
            }

            if (isLevelingUp) {
                drawCircle(
                    color = accentColor.copy(alpha = 1f - (waveRadius / 2000f)),
                    radius = waveRadius,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        // --- CAPA 2: EL ORBE ---
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.graphicsLayer(scaleX = pulse, scaleY = pulse)
        ) {
            // Brillo Exterior
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .background(
                        Brush.radialGradient(
                            if (isLevelingUp) listOf(Color.White, accentColor, Color.Transparent)
                            else listOf(accentColor.copy(alpha = 0.15f), Color.Transparent)
                        ), shape = CircleShape
                    )
            )

            Surface(
                modifier = Modifier.size(200.dp),
                shape = CircleShape,
                color = Color.Black,
                border = BorderStroke(
                    if (isLevelingUp) 6.dp else 3.dp,
                    if (isLevelingUp) Color.White else accentColor
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.day).uppercase(), // Nombre del Nivel dinámico
                        style = TextStyle(
                            color = accentColor.copy(0.6f),
                            fontSize = 9.sp,
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    )

                    Box(contentAlignment = Alignment.Center) {
                        if (isLevelingUp) {
                            Text(
                                "$currentDisplayStreak",
                                style = TextStyle(
                                    color = Color.Red.copy(0.4f),
                                    fontSize = 112.sp,
                                    fontWeight = FontWeight.Black
                                ),
                                modifier = Modifier.offset(x = 4.dp)
                            )
                        }
                        Text(
                            text = "$currentDisplayStreak",
                            style = TextStyle(
                                color = if (isLevelingUp) Color.White else accentColor,
                                fontSize = 110.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }

                    Text(
                        text = if (isLevelingUp) stringResource(id = R.string.overdrive) else stringResource(
                            id = R.string.synced
                        ),
                        style = TextStyle(
                            color = accentColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    )
                }
            }
        }

        // --- CAPA 3: STATUS BAR ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            StatusProgressBar(progress, accentColor, isLevelingUp)
        }
    }
}

@Composable
fun StatusProgressBar(progress: Float, accentColor: Color, isLevelingUp: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp)
            .padding(bottom = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isLevelingUp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(id = R.string.reconstructing_data),
                    style = TextStyle(
                        color = accentColor.copy(0.5f),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
                Text(
                    "${(progress * 100).toInt()}%",
                    style = TextStyle(
                        color = accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .border(
                        1.dp,
                        accentColor.copy(0.3f),
                        CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp)
                    )
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(accentColor)
                )
            }
        } else {
            Text(
                text = stringResource(id = R.string.expantion_progress),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    }
}

// --- FUNCIONES AUXILIARES ---

fun createRandomParticle(id: Int): DataBit {
    val random = Random() // Ahora sí crea la instancia correctamente
    return DataBit(
        id = id,
        x = 0f,
        y = 0f,
        speed = 2f + random.nextFloat() * 4f,
        size = 2f + random.nextFloat() * 3f,
        alpha = 0f
    )
}

fun respawnParticle(bit: DataBit, center: Offset, random: Random) {
    // Cálculo del radio de spawn (fuera de la pantalla)
    val spawnRadius = sqrt(center.x * center.x + center.y * center.y) * 0.9f
    val angle = random.nextDouble() * 2.0 * Math.PI

    // Posicionamiento en el borde del círculo
    bit.x = center.x + (spawnRadius * cos(angle)).toFloat()
    bit.y = center.y + (spawnRadius * sin(angle)).toFloat()

    // Quitamos 'bit.angle = angle' porque ya no existe en la data class
    bit.alpha = 0f
}

//// --- MODELO DE DATOS ---
//data class DataBit(
//    val id: Int,
//    var x: Float,
//    var y: Float,
//    var speed: Float,
//    var alpha: Float = 0f,
//    var size: Float
//)
//
//// --- CONFIGURACIÓN ESTÉTICA ---
//val TechBlue = Color(0xFF00E5FF)
//val DarkBackground = Color(0xFF020202)
//val NightBlueGradient = Brush.verticalGradient(
//    colors = listOf(Color(0xFF020509), Color(0xFF0D1424), Color(0xFF020202))
//)
//
//@Composable
//fun SyncLevelUpScreen(streakDays: Int, progress: Float) {
//    // Estados de racha y control de impacto
//    var currentDisplayStreak by remember { mutableIntStateOf(streakDays) }
//    var isLevelingUp by remember { mutableStateOf(false) }
//
//    // Estados de partículas
//    val dataBits = remember { mutableStateListOf<DataBit>() }
//    var center by remember { mutableStateOf(Offset.Zero) }
//    val numParticles = 80
//    val targetRadiusPx = 110.dp.value
//
//    // 1. Inicializar partículas
//    if (dataBits.isEmpty()) {
//        repeat(numParticles) { dataBits.add(createRandomParticle(it)) }
//    }
//
//    // 2. Lógica de subida de nivel (Impacto visual)
//    LaunchedEffect(streakDays) {
//        if (streakDays > currentDisplayStreak) {
//            isLevelingUp = true
//            // Pequeño delay para que el usuario vea la barra al 100% antes del impacto
//            delay(100)
//            currentDisplayStreak = streakDays
//            delay(1000) // Duración total del efecto "Overdrive"
//            isLevelingUp = false
//        }
//    }
//
//    // 3. Game Loop: Animación de Partículas
//    LaunchedEffect(center, isLevelingUp) {
//        val random = Random(System.currentTimeMillis())
//        while (isActive) {
//            withFrameMillis {
//                dataBits.forEach { bit ->
//                    if (bit.x == 0f && center != Offset.Zero) respawnParticle(bit, center, random)
//
//                    val dx = center.x - bit.x
//                    val dy = center.y - bit.y
//                    val dist = sqrt(dx * dx + dy * dy)
//
//                    // En Level Up las partículas salen disparadas hacia afuera
//                    val direction = if (isLevelingUp) -6f else 1.2f
//
//                    if (dist > targetRadiusPx || isLevelingUp) {
//                        bit.x += (dx / dist) * bit.speed * direction
//                        bit.y += (dy / dist) * bit.speed * direction
//                        if (bit.alpha < 0.8f) bit.alpha += 0.02f
//                    } else {
//                        if (center != Offset.Zero) respawnParticle(bit, center, random)
//                    }
//                }
//            }
//        }
//    }
//
//    // --- ANIMACIONES DE SISTEMA ---
//    val infiniteTransition = rememberInfiniteTransition(label = "PulseSystem")
//
//    // Vibración sutil vs Vibración crítica
//    val pulse by infiniteTransition.animateFloat(
//        initialValue = 1f,
//        targetValue = if (isLevelingUp) 1.15f else 1.05f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(if (isLevelingUp) 60 else 2000, easing = LinearEasing),
//            repeatMode = RepeatMode.Reverse
//        ), label = "Pulse"
//    )
//
//    // Sacudida de pantalla (Shake)
//    val shakeOffset by animateFloatAsState(
//        targetValue = if (isLevelingUp) 8f else 0f,
//        animationSpec = repeatable(10, tween(50), RepeatMode.Reverse), label = "Shake"
//    )
//
//    // Escala de impacto (Onda expansiva del orbe)
//    val impactScale by animateFloatAsState(
//        targetValue = if (isLevelingUp) 1.3f else 1f,
//        animationSpec = if (isLevelingUp) spring(dampingRatio = Spring.DampingRatioMediumBouncy) else tween(
//            500
//        ),
//        label = "Impact"
//    )
//
//    // Onda expansiva visual
//    val waveRadius by animateFloatAsState(
//        targetValue = if (isLevelingUp) 2000f else 0f,
//        animationSpec = if (isLevelingUp) tween(800, easing = EaseOutExpo) else snap(),
//        label = "Wave"
//    )
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(NightBlueGradient)
//            .offset(x = shakeOffset.dp, y = (shakeOffset / 2).dp),
//        contentAlignment = Alignment.Center
//    ) {
//        // --- CAPA 1: PARTICULAS ---
//        Canvas(modifier = Modifier.fillMaxSize()) {
//            if (center == Offset.Zero) center = size.center
//            dataBits.forEach { bit ->
//                drawCircle(
//                    color = TechBlue.copy(alpha = bit.alpha),
//                    radius = bit.size,
//                    center = Offset(bit.x, bit.y)
//                )
//            }
//
//            // Dibuja la onda expansiva de choque
//            if (isLevelingUp) {
//                drawCircle(
//                    color = TechBlue.copy(alpha = 1f - (waveRadius / 2000f)),
//                    radius = waveRadius,
//                    style = Stroke(width = 2.dp.toPx())
//                )
//            }
//        }
//
//        // --- CAPA 2: EL ORBE ---
//        Box(
//            contentAlignment = Alignment.Center,
//            modifier = Modifier.graphicsLayer(
//                scaleX = impactScale * pulse,
//                scaleY = impactScale * pulse
//            )
//        ) {
//            // Glow concéntrico
//            Box(
//                modifier = Modifier
//                    .size(260.dp)
//                    .background(
//                        Brush.radialGradient(
//                            if (isLevelingUp) listOf(Color.White, TechBlue, Color.Transparent)
//                            else listOf(TechBlue.copy(alpha = 0.2f), Color.Transparent)
//                        ), shape = CircleShape
//                    )
//            )
//
//            Surface(
//                modifier = Modifier.size(200.dp),
//                shape = CircleShape,
//                color = Color.Black,
//                border = BorderStroke(
//                    if (isLevelingUp) 6.dp else 3.dp,
//                    if (isLevelingUp) Color.White else TechBlue
//                )
//            ) {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.Center
//                ) {
//                    Text(
//                        text = if (isLevelingUp) stringResource(id = R.string.day) else stringResource(id = R.string.day),
//                        style = TextStyle(
//                            color = TechBlue.copy(0.7f),
//                            fontSize = 10.sp,
//                            letterSpacing = 3.sp,
//                            fontFamily = FontFamily.Monospace
//                        )
//                    )
//
//                    // Texto principal con sombra de aberración en Level Up
//                    Box(contentAlignment = Alignment.Center) {
//                        if (isLevelingUp) {
//                            Text(
//                                "$currentDisplayStreak",
//                                style = TextStyle(
//                                    color = Color.Red.copy(0.4f),
//                                    fontSize = 112.sp,
//                                    fontWeight = FontWeight.Black
//                                ),
//                                modifier = Modifier.offset(x = 4.dp)
//                            )
//                        }
//                        Text(
//                            text = "$currentDisplayStreak",
//                            style = TextStyle(
//                                color = if (isLevelingUp) Color.White else TechBlue,
//                                fontSize = 110.sp,
//                                fontWeight = FontWeight.Black,
//                                fontFamily = FontFamily.Monospace
//                            )
//                        )
//                    }
//
//                    Text(
//                        text = if (isLevelingUp) stringResource(id = R.string.overdrive)else stringResource(id = R.string.synced),
//                        style = TextStyle(
//                            color = TechBlue,
//                            fontSize = 14.sp,
//                            fontWeight = FontWeight.Bold,
//                            letterSpacing = 2.sp
//                        )
//                    )
//                }
//            }
//        }
//
//        // --- CAPA 3: STATUS BAR (ABAJO) ---
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .align(Alignment.BottomCenter),
//            contentAlignment = Alignment.Center
//        ) {
//            if (!isLevelingUp) {
//                StatusProgressBar(progress)
//            } else {
//                Text(
//                    text = stringResource(id = R.string.expantion_progress),
//                    style = TextStyle(
//                        color = Color.White,
//                        fontSize = 11.sp,
//                        fontFamily = FontFamily.Monospace,
//                        fontWeight = FontWeight.Bold
//                    ),
//                    modifier = Modifier.padding(bottom = 90.dp)
//                )
//            }
//        }
//    }
//}
//
//
//
//// --- FUNCIONES AUXILIARES DE PARTÍCULAS ---
//
//fun createRandomParticle(id: Int): DataBit {
//    val random = Random() // Ahora sí crea la instancia correctamente
//    return DataBit(
//        id = id,
//        x = 0f,
//        y = 0f,
//        speed = 2f + random.nextFloat() * 4f,
//        size = 2f + random.nextFloat() * 3f,
//        alpha = 0f
//    )
//}
//
//fun respawnParticle(bit: DataBit, center: Offset, random: Random) {
//    // Cálculo del radio de spawn (fuera de la pantalla)
//    val spawnRadius = sqrt(center.x * center.x + center.y * center.y) * 0.9f
//    val angle = random.nextDouble() * 2.0 * Math.PI
//
//    // Posicionamiento en el borde del círculo
//    bit.x = center.x + (spawnRadius * cos(angle)).toFloat()
//    bit.y = center.y + (spawnRadius * sin(angle)).toFloat()
//
//    // Quitamos 'bit.angle = angle' porque ya no existe en la data class
//    bit.alpha = 0f
//}
//
//@Composable
//fun StatusProgressBar(progress: Float) {
//    val techBlue = Color(0xFF00E5FF)
//    val techCyan = Color(0xFF00B8D4)
//
//    // Animación sutil para el brillo de la barra
//    val infiniteTransition = rememberInfiniteTransition(label = "BarGlow")
//    val alphaAnim by infiniteTransition.animateFloat(
//        initialValue = 0.7f,
//        targetValue = 1f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(1000, easing = LinearEasing),
//            repeatMode = RepeatMode.Reverse
//        ), label = "Glow"
//    )
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 40.dp)
//            .padding(bottom = 50.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // --- TEXTOS SUPERIORES ---
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = stringResource(id = R.string.reconstructing_data),
//                style = TextStyle(
//                    color = techBlue.copy(alpha = 0.6f),
//                    fontSize = 10.sp,
//                    fontFamily = FontFamily.Monospace,
//                    letterSpacing = 1.sp
//                )
//            )
//            Text(
//                text = "${(progress * 100).toInt()}%",
//                style = TextStyle(
//                    color = techBlue,
//                    fontSize = 12.sp,
//                    fontWeight = FontWeight.Bold,
//                    fontFamily = FontFamily.Monospace
//                )
//            )
//        }
//
//        Spacer(modifier = Modifier.height(10.dp))
//
//        // --- CONTENEDOR DE LA BARRA ---
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(12.dp)
//                .border(
//                    1.dp,
//                    techBlue.copy(alpha = 0.3f),
//                    CutCornerShape(topStart = 4.dp, bottomEnd = 4.dp)
//                )
//                .padding(2.dp) // Espacio interno para que la carga no toque el borde
//        ) {
//            // Fondo de la barra (Riel)
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.White.copy(alpha = 0.05f))
//            )
//
//            // Progreso Activo
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth(progress) // El ancho depende del progreso (0.0 a 1.0)
//                    .fillMaxHeight()
//                    .background(
//                        brush = Brush.horizontalGradient(
//                            colors = listOf(techCyan, techBlue)
//                        )
//                    )
//                    .graphicsLayer(alpha = alphaAnim) // Aplicamos el parpadeo sutil
//            )
//
//            // Efecto de "Scanline" (Líneas de barrido sobre la carga)
//            Canvas(modifier = Modifier.fillMaxSize()) {
//                val step = 4.dp.toPx()
//                for (x in 0..size.width.toInt() step step.toInt()) {
//                    drawLine(
//                        color = Color.Black.copy(alpha = 0.2f),
//                        start = Offset(x.toFloat(), 0f),
//                        end = Offset(x.toFloat(), size.height),
//                        strokeWidth = 1.dp.toPx()
//                    )
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(6.dp))
//
//        // --- DECORACIÓN TÉCNICA INFERIOR ---
//        Text(
//            text = stringResource(id = R.string.stability_levels),
//            style = TextStyle(
//                color = techBlue.copy(alpha = 0.3f),
//                fontSize = 8.sp,
//                fontFamily = FontFamily.Monospace
//            ),
//            modifier = Modifier.align(Alignment.Start)
//        )
//    }
//}