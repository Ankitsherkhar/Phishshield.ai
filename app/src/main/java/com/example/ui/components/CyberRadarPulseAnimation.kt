package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberNavyDarker
import com.example.ui.theme.CyberNavySurface
import com.example.ui.theme.CyberNavySurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonSky
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.sin

/**
 * A Lottie-inspired circular radar sweep and concentric pulse animation in Compose
 * to visualize the AI deep-analysis state in real-time.
 */
@Composable
fun CyberRadarPulseAnimation(
    phaseText: String,
    modifier: Modifier = Modifier,
    animationSize: Dp = 180.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweepAnimation")

    // Radar 360-degree sweep angle
    val radarSweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarSweep"
    )

    // Counter-rotating outer reticle angle
    val counterAngle by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CounterRotation"
    )

    // Concentric expanding pulse wave 1
    val pulseWave1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseWave1"
    )

    // Concentric expanding pulse wave 2 (phase offset)
    val pulseWave2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, delayMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseWave2"
    )

    // Breathing core scale
    val coreScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CoreBreathing"
    )

    // Cursor blink for terminal telemetry text
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 450, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CursorBlink"
    )

    CyberCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ai_radar_pulse_container"),
        borderColor = ElectricCyan.copy(alpha = 0.7f),
        backgroundColor = CyberNavySurfaceVariant.copy(alpha = 0.85f),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Badge
            Box(
                modifier = Modifier
                    .background(ElectricCyan.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .border(1.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(ElectricCyan, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AI NEURAL ENGINE INFERENCE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Central Lottie-inspired Circular Radar & Pulse Canvas
            Box(
                modifier = Modifier.size(animationSize),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(animationSize)) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val maxRadius = (size.minDimension / 2f) - 6.dp.toPx()
                    val coreRadius = maxRadius * 0.35f

                    // 1. Concentric Pulse Wave 1
                    val wave1Radius = coreRadius + (maxRadius - coreRadius) * pulseWave1
                    val wave1Alpha = (1f - pulseWave1).coerceIn(0f, 1f) * 0.8f
                    drawCircle(
                        color = ElectricCyan.copy(alpha = wave1Alpha),
                        radius = wave1Radius,
                        center = center,
                        style = Stroke(width = 2.dp.toPx() * (1f - pulseWave1 + 0.3f))
                    )

                    // 2. Concentric Pulse Wave 2
                    val wave2Radius = coreRadius + (maxRadius - coreRadius) * pulseWave2
                    val wave2Alpha = (1f - pulseWave2).coerceIn(0f, 1f) * 0.65f
                    drawCircle(
                        color = NeonPurple.copy(alpha = wave2Alpha),
                        radius = wave2Radius,
                        center = center,
                        style = Stroke(width = 2.dp.toPx() * (1f - pulseWave2 + 0.3f))
                    )

                    // 3. Static Guide Rings
                    drawCircle(
                        color = CyberCardBorder.copy(alpha = 0.45f),
                        radius = maxRadius,
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )
                    drawCircle(
                        color = CyberCardBorder.copy(alpha = 0.3f),
                        radius = maxRadius * 0.68f,
                        center = center,
                        style = Stroke(width = 0.8.dp.toPx())
                    )

                    // 4. Counter-rotating Reticle Segment Arcs
                    rotate(degrees = counterAngle, pivot = center) {
                        for (i in 0 until 4) {
                            val startAngle = i * 90f + 15f
                            drawArc(
                                color = NeonSky.copy(alpha = 0.5f),
                                startAngle = startAngle,
                                sweepAngle = 45f,
                                useCenter = false,
                                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
                                topLeft = Offset(center.x - maxRadius, center.y - maxRadius),
                                size = androidx.compose.ui.geometry.Size(maxRadius * 2, maxRadius * 2)
                            )
                        }
                    }

                    // 5. Radar Sweep Cone (Linear/Sweep Gradient)
                    rotate(degrees = radarSweepAngle, pivot = center) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    ElectricCyan.copy(alpha = 0.05f),
                                    ElectricCyan.copy(alpha = 0.22f),
                                    ElectricCyan.copy(alpha = 0.55f)
                                ),
                                center = center
                            ),
                            startAngle = 0f,
                            sweepAngle = 75f,
                            useCenter = true,
                            topLeft = Offset(center.x - maxRadius, center.y - maxRadius),
                            size = androidx.compose.ui.geometry.Size(maxRadius * 2, maxRadius * 2)
                        )

                        // Leading radar sweep edge line
                        val sweepRad = Math.toRadians(75.0)
                        val endX = center.x + (maxRadius * cos(sweepRad)).toFloat()
                        val endY = center.y + (maxRadius * sin(sweepRad)).toFloat()
                        drawLine(
                            brush = Brush.linearGradient(
                                listOf(ElectricCyan.copy(alpha = 0.2f), ElectricCyan)
                            ),
                            start = center,
                            end = Offset(endX, endY),
                            strokeWidth = 2.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // 6. Orbiting Threat Detection Nodes (Particles)
                    val orbitAngle1 = Math.toRadians((radarSweepAngle * 1.5f).toDouble())
                    val p1X = center.x + (maxRadius * 0.75f * cos(orbitAngle1)).toFloat()
                    val p1Y = center.y + (maxRadius * 0.75f * sin(orbitAngle1)).toFloat()
                    drawCircle(color = ElectricCyan, radius = 4.dp.toPx(), center = Offset(p1X, p1Y))
                    drawCircle(color = ElectricCyan.copy(alpha = 0.3f), radius = 8.dp.toPx(), center = Offset(p1X, p1Y))

                    val orbitAngle2 = Math.toRadians((counterAngle * 1.2f + 120f).toDouble())
                    val p2X = center.x + (maxRadius * 0.55f * cos(orbitAngle2)).toFloat()
                    val p2Y = center.y + (maxRadius * 0.55f * sin(orbitAngle2)).toFloat()
                    drawCircle(color = NeonPurple, radius = 3.5.dp.toPx(), center = Offset(p2X, p2Y))
                    drawCircle(color = NeonPurple.copy(alpha = 0.3f), radius = 7.dp.toPx(), center = Offset(p2X, p2Y))

                    // 7. Core Pulsing Hologram Base
                    val scaledCoreRadius = coreRadius * coreScale
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ElectricCyan.copy(alpha = 0.35f),
                                Color(0xFF0F1E40)
                            ),
                            center = center,
                            radius = scaledCoreRadius
                        ),
                        radius = scaledCoreRadius,
                        center = center
                    )
                    drawCircle(
                        color = ElectricCyan.copy(alpha = 0.8f),
                        radius = scaledCoreRadius,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                // Core Floating Icon (Shield & Brain)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(CyberNavyDarker.copy(alpha = 0.85f), CircleShape)
                        .border(1.5.dp, ElectricCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "AI Scanner Active",
                        tint = ElectricCyan,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // High-Tech Telemetry Phase Text
            Text(
                text = "Multimodal Heuristic Analysis",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Dynamic Terminal Phase with Blinking Cursor
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "> $phaseText",
                    fontSize = 12.sp,
                    color = ElectricCyan,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold
                )
                if (cursorAlpha > 0.5f) {
                    Text(
                        text = "█",
                        fontSize = 12.sp,
                        color = ElectricCyan,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Indeterminate Cyber Gradient Bar
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = ElectricCyan,
                trackColor = CyberNavyDarker
            )
        }
    }
}
