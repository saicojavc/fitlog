package com.saico.core.ui.components

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun GravityParticlesBackground(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sensorManager =
        remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelerometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    var tiltX by remember { mutableStateOf(0f) }
    var tiltY by remember { mutableStateOf(0f) }

    val particleColors = listOf(
        Color(0xFF3FB9F6).copy(alpha = 0.4f),
        Color(0xFF945FFB).copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.2f)
    )

    val particles = remember {
        List(60) {
            ParticleState(
                pos = Offset(Random.nextFloat(), Random.nextFloat()),
                velocity = Offset(0f, 0f),
                size = Random.nextFloat() * 5f + 2f,
                color = particleColors.random()
            )
        }
    }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    // Reducción drástica del impulso (Aceleración)
                    tiltX = -it.values[0] * 0.00001f
                    tiltY = it.values[1] * 0.00001f
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        onDispose { sensorManager.unregisterListener(listener) }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val frame by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(16, easing = LinearEasing)),
        label = "frame"
    )

    val nightBlueGradient = remember {
        Brush.verticalGradient(
            colors = listOf(Color(0xFF020509), Color(0xFF0D1424), Color(0xFF16223B))
        )
    }

    Canvas(modifier = modifier
        .fillMaxSize()
        .background(nightBlueGradient)) {
        val f = frame // trigger recomposition

        particles.forEach { p ->
            // 1. APLICAR FRICCIÓN MÁS FUERTE (Damping)
            // Cambiamos de 0.95f a 0.90f para que se frenen antes
            val friction = 0.90f

            // 2. IMPULSO MUY DÉBIL
            // Mantenemos el tiltX/Y pero con un tope de velocidad (Terminal Velocity) más bajo
            // Bajamos de 0.0007f a 0.0004f
            val newVelX = (p.velocity.x * friction + tiltX).coerceIn(-0.0004f, 0.0004f)
            val newVelY = (p.velocity.y * friction + tiltY).coerceIn(-0.0004f, 0.0004f)

            p.velocity = Offset(newVelX, newVelY)

            // 3. ACTUALIZAR POSICIÓN
            p.pos = Offset(
                x = (p.pos.x + p.velocity.x + 1f) % 1f,
                y = (p.pos.y + p.velocity.y + 1f) % 1f
            )

            drawCircle(
                color = p.color,
                radius = p.size.dp.toPx(),
                center = Offset(p.pos.x * size.width, p.pos.y * size.height)
            )
        }
    }
}

// Clase de apoyo para el estado de cada punto
class ParticleState(
    var pos: Offset,
    var velocity: Offset,
    val size: Float,
    val color: Color
)