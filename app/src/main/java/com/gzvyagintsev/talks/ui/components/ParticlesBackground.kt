package com.gzvyagintsev.talks.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Animated particle background with connection lines.
 *
 * Architecture: position updates happen in LaunchedEffect (side-effect zone),
 * Canvas only READS current positions — no mutation in draw lambda.
 *
 * Density-aware: particle sizes and connection distances use dp → px conversion.
 */
@Composable
fun ParticlesBackground(
    modifier: Modifier = Modifier,
    particleCount: Int = 55,
    dotColor: Color = Color(0xFF1A1A1A),
    accentDotColor: Color = Color(0xFFFFCE32),
    lineColor: Color = Color(0xFF1A1A1A)
) {
    // Density-aware sizing
    val density = LocalDensity.current
    val connectionDistPx = with(density) { 130.dp.toPx() }
    val speed = with(density) { 0.25.dp.toPx() }

    // Fade in
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200)
        visible = true
    }
    val fadeAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(1500, easing = LinearEasing),
        label = "fade"
    )

    // Particle data
    val particles = remember { Array(particleCount) { Dot.make(density) } }

    // Canvas dimensions — shared between LaunchedEffect and Canvas
    var canvasWidth by remember { mutableFloatStateOf(0f) }
    var canvasHeight by remember { mutableFloatStateOf(0f) }

    // Snapshot X/Y arrays — Canvas reads these (immutable from Canvas perspective)
    // LaunchedEffect writes to particles, then copies to snapshot arrays
    val xPositions = remember { FloatArray(particleCount) }
    val yPositions = remember { FloatArray(particleCount) }

    // Frame trigger — changes every frame, Canvas observes it
    var frameTick by remember { mutableLongStateOf(0L) }

    // Animation loop — runs in coroutine, updates positions (side-effect zone)
    LaunchedEffect(Unit) {
        var lastNanos = 0L
        while (true) {
            withFrameNanos { nanos ->
                val dt = if (lastNanos == 0L) 16_666_666L else (nanos - lastNanos)
                lastNanos = nanos
                val dtFactor = (dt / 16_666_666f).coerceIn(0.5f, 3f)

                val w = canvasWidth
                val h = canvasHeight
                if (w > 0f && h > 0f) {
                    particles.forEachIndexed { i, p ->
                        // Init position
                        if (!p.ready) {
                            p.x = p.fx * w
                            p.y = p.fy * h
                            p.ready = true
                        }

                        // Update position
                        p.x += p.vx * speed * dtFactor
                        p.y += p.vy * speed * dtFactor

                        // Wrap edges
                        if (p.x < -10f) p.x += w + 20f
                        if (p.x > w + 10f) p.x -= w + 20f
                        if (p.y < -10f) p.y += h + 20f
                        if (p.y > h + 10f) p.y -= h + 20f

                        // Copy to snapshot arrays
                        xPositions[i] = p.x
                        yPositions[i] = p.y
                    }
                }
                // Trigger Canvas redraw
                frameTick = nanos
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        canvasWidth = size.width
        canvasHeight = size.height
        if (size.width == 0f || size.height == 0f) return@Canvas

        // Read frameTick to observe changes (forces redraw each frame)
        @Suppress("UNUSED_VARIABLE")
        val tick = frameTick
        val a = fadeAlpha

        // Draw connection lines (read-only access to snapshot positions)
        for (i in particles.indices) {
            for (j in i + 1 until particles.size) {
                val dx = xPositions[i] - xPositions[j]
                val dy = yPositions[i] - yPositions[j]
                val d = sqrt(dx * dx + dy * dy)
                if (d < connectionDistPx) {
                    drawLine(
                        color = lineColor.copy(alpha = (1f - d / connectionDistPx) * 0.18f * a),
                        start = Offset(xPositions[i], yPositions[i]),
                        end = Offset(xPositions[j], yPositions[j]),
                        strokeWidth = 1.2f
                    )
                }
            }
        }

        // Draw dots (read-only)
        particles.forEachIndexed { i, p ->
            drawCircle(
                color = (if (p.isYellow) accentDotColor else dotColor).copy(alpha = p.opacity * a),
                radius = p.sizePx,
                center = Offset(xPositions[i], yPositions[i])
            )
        }
    }
}

private class Dot(
    var x: Float = 0f,
    var y: Float = 0f,
    val vx: Float,
    val vy: Float,
    val sizePx: Float,
    val opacity: Float,
    val isYellow: Boolean,
    val fx: Float,
    val fy: Float,
    var ready: Boolean = false
) {
    companion object {
        fun make(density: androidx.compose.ui.unit.Density): Dot {
            val angle = Random.nextFloat() * 6.2832f
            val spd = Random.nextFloat() * 0.6f + 0.3f
            val sizeDp = Random.nextFloat() * 1.5f + 1f  // 1-2.5 dp
            return Dot(
                vx = cos(angle) * spd,
                vy = sin(angle) * spd,
                sizePx = with(density) { sizeDp.dp.toPx() },
                opacity = Random.nextFloat() * 0.35f + 0.2f,
                isYellow = Random.nextFloat() < 0.35f,
                fx = Random.nextFloat(),
                fy = Random.nextFloat()
            )
        }
    }
}
