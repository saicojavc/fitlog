package com.saico.feature.dashboard.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.isActive
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// --- MODELO DE DATOS DE PARTÍCULA ---
data class DataBit(
    val id: Int,
    var x: Float,
    var y: Float,
    var speed: Float,
    var angle: Double,
    var alpha: Float = 0f,
    var size: Float
)

// --- CONFIGURACIÓN ESTÉTICA ---
val TechBlue = Color(0xFF00E5FF)
val DarkBackground = Color(0xFF030303) // Aún más oscuro

@Composable
fun SyncLevelUpScreen(streakDays: Int, progress : Float) {

    // --- ESTADO DEL SISTEMA DE PARTÍCULAS ---
    val numParticles = 70 // Cantidad de "bits"
    val dataBits = remember { mutableStateListOf<DataBit>() }
    var center by remember { mutableStateOf(Offset.Zero) }
    // Orbe principal (radio + border) es 100.dp, necesitamos que mueran antes.
    val targetRadiusPx = 110.dp.value // Radio de colisión (píxeles conceptuales)

    // Inicializar partículas si no existen
    if (dataBits.isEmpty()) {
        repeat(numParticles) {
            dataBits.add(createRandomParticle(id = it))
        }
    }

    // --- GAME LOOP DE PARTÍCULAS (LaunchedEffect) ---
    LaunchedEffect(key1 = true) {
        val random = Random()
        while (isActive) {
            withFrameMillis { frameTime ->
                dataBits.forEachIndexed { index, bit ->
                    // Si la partícula no se ha movido (recién creada), darle posición inicial
                    if (bit.x == 0f && bit.y == 0f && center != Offset.Zero) {
                        respawnParticle(bit, center, random)
                    }

                    // Calcular vector hacia el centro
                    val dx = center.x - bit.x
                    val dy = center.y - bit.y
                    val dist = sqrt(dx * dx + dy * dy)

                    // Si está viva, moverla hacia el centro
                    if (dist > targetRadiusPx) {
                        val moveX = (dx / dist) * bit.speed
                        val moveY = (dy / dist) * bit.speed
                        bit.x += moveX
                        bit.y += moveY

                        // Aumentar alpha (fade in) a medida que se acerca
                        if (bit.alpha < 0.8f) bit.alpha += 0.02f
                    } else {
                        // Murió (llegó al orbe): Respawn en el borde
                        if (center != Offset.Zero) {
                            respawnParticle(bit, center, random)
                        }
                    }
                }
            }
        }
    }

    // --- ANIMACIÓN DE PULSACIÓN DEL ORBE (Visual) ---
    val infiniteTransition = rememberInfiniteTransition(label = "OrbePulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f, // Pulsación más sutil
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "Pulse"
    )

    // --- LAYOUT PRINCIPAL (Pantalla Completa) ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {

        // --- CAPA 1: CANVA DE PARTÍCULAS (Data Bits) ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (center == Offset.Zero) {
                center = this.size.center
            }

            dataBits.forEach { bit ->
                drawCircle(
                    color = TechBlue.copy(alpha = bit.alpha),
                    radius = bit.size,
                    center = Offset(bit.x, bit.y)
                )
            }
        }

        // --- CAPA 2: EL ORBE DE ENERGÍA ---
        Box(contentAlignment = Alignment.Center) {
            // Brillo exterior (Glow Radial)
            Box(
                modifier = Modifier
                    .size(260.dp * pulseScale)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(TechBlue.copy(alpha = 0.25f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )

            // Orbe Principal (Cuerpo sólido y borde)
            Surface(
                modifier = Modifier.size(200.dp),
                shape = CircleShape,
                color = Color.Black,
                border = BorderStroke(4.dp, TechBlue)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "PROTOCOLO",
                        style = TextStyle(color = TechBlue.copy(alpha = 0.6f), fontSize = 12.sp, letterSpacing = 3.sp, fontFamily = FontFamily.Monospace)
                    )
                    Text(
                        text = "DAY",
                        style = TextStyle(color = TechBlue.copy(alpha = 0.8f), fontSize = 16.sp, letterSpacing = 5.sp)
                    )
                    Text(
                        text = "$streakDays",
                        style = TextStyle(
                            color = TechBlue,
                            fontSize = 90.sp, // Más grande y dominante
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Text(
                        text = "SYNCED",
                        style = TextStyle(color = TechBlue.copy(alpha = 0.8f), fontSize = 16.sp, letterSpacing = 3.sp)
                    )
                }
            }
        }

        // --- CAPA 3: STATUS BAR (INFERIOR) ---
        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 50.dp).width(250.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("RECONSTRUCTING DATA...", style = TextStyle(color = TechBlue.copy(alpha = 0.5f), fontSize = 9.sp, fontFamily = FontFamily.Monospace))
                Text("${(progress * 100).toInt()}%", style = TextStyle(color = TechBlue, fontSize = 9.sp, fontFamily = FontFamily.Monospace))
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Barra de Carga Técnica
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color.White.copy(alpha = 0.1f))) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(TechBlue)
                        .border(1.dp, Color.White.copy(alpha = 0.3f))
                )
            }
        }
    }
}

// --- FUNCIONES AUXILIARES DE PARTÍCULAS ---

fun createRandomParticle(id: Int): DataBit {
    val random = Random()
    return DataBit(
        id = id,
        x = 0f, // Se inicializan en 0, respawnParticle les dará la posición real
        y = 0f,
        speed = 2f + random.nextFloat() * 4f, // Velocidad variada
        angle = 0.0,
        size = 2f + random.nextFloat() * 3f, // Tamaños variados (píxeles)
        alpha = 0f // Empiezan invisibles
    )
}

fun respawnParticle(bit: DataBit, center: Offset, random: Random) {
    // Spawn en un círculo grande alrededor del centro (fuera de la vista inicial o en los bordes)
    val spawnRadius = sqrt(center.x * center.x + center.y * center.y) * 0.9f
    val angle = random.nextDouble() * 2.0 * Math.PI

    bit.x = center.x + (spawnRadius * cos(angle)).toFloat()
    bit.y = center.y + (spawnRadius * sin(angle)).toFloat()
    bit.angle = angle
    bit.alpha = 0f // Reset alpha para fade in
}