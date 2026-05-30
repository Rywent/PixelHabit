package com.rywent.pixelhabit.presentation.components.panels

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

class Spark(
    var x: Float,
    var y: Float,
    var velocityX: Float,
    var velocityY: Float,
    var life: Float,
    val maxLife: Float,
    var size: Float,
    val color: Color
)

@Composable
fun StreakPanelTwo(
    streak: Int,
    isResetMode: Boolean = false
) {
    val ignitionPhase = remember { Animatable(0f) }
    val animatedStreak = remember { Animatable(0f) }
    val numberVisibility = remember { Animatable(0f) }
    val streakLabelVisibility = remember { Animatable(0f) }
    val warningVisibility = remember { Animatable(0f) }

    val fontSize = when {
        streak < 10 -> 72.sp
        streak < 100 -> 64.sp
        streak < 1000 -> 48.sp
        streak < 10000 -> 36.sp
        else -> 28.sp
    }

    LaunchedEffect(isResetMode) {
        if (isResetMode) {
            ignitionPhase.snapTo(1f)
            numberVisibility.snapTo(1f)
            animatedStreak.snapTo(streak.toFloat())
            streakLabelVisibility.snapTo(1f)

            kotlinx.coroutines.delay(300)

            numberVisibility.snapTo(0f)
            launch {
                numberVisibility.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                )
            }

            kotlinx.coroutines.delay(500)

            streakLabelVisibility.snapTo(0f)
            streakLabelVisibility.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
            )

            kotlinx.coroutines.delay(600)

            ignitionPhase.animateTo(
                targetValue = 0.15f,
                animationSpec = tween(
                    durationMillis = 2000,
                    easing = FastOutSlowInEasing
                )
            )

            launch {
                streakLabelVisibility.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                )
            }

            kotlinx.coroutines.delay(400)

            warningVisibility.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
            )

        } else {
            kotlinx.coroutines.delay(300)

            launch {
                ignitionPhase.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 1600,
                        easing = FastOutSlowInEasing
                    )
                )
            }

            kotlinx.coroutines.delay(800)

            launch {
                numberVisibility.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                )
            }

            launch {
                animatedStreak.animateTo(
                    targetValue = streak.toFloat(),
                    animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
                )
            }

            kotlinx.coroutines.delay(550)

            streakLabelVisibility.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
            )
        }
    }

    val sway1 by rememberInfiniteTransition(label = "sway1").animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val sway2 by rememberInfiniteTransition(label = "sway2").animateFloat(
        initialValue = 1f,
        targetValue = -1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val sway3 by rememberInfiniteTransition(label = "sway3").animateFloat(
        initialValue = -0.5f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val sway4 by rememberInfiniteTransition(label = "sway4").animateFloat(
        initialValue = 0.8f,
        targetValue = -0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val sparks = remember { mutableStateListOf<Spark>() }
    var frameCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(ignitionPhase.value) {
        if (ignitionPhase.value > 0.6f) {
            while (true) {
                frameCount++

                if (frameCount % Random.nextInt(10, 18) == 0) {
                    val sparkX = Random.nextFloat() * 30f - 15f
                    val sparkY = -Random.nextFloat() * 40f - 5f

                    val angle = Math.toRadians(Random.nextDouble(-60.0, 60.0))
                    val speed = Random.nextFloat() * 3f + 1f

                    sparks.add(
                        Spark(
                            x = sparkX,
                            y = sparkY,
                            velocityX = (sin(angle) * speed).toFloat(),
                            velocityY = (-cos(angle) * speed).toFloat(),
                            life = 0f,
                            maxLife = Random.nextFloat() * 2f + 0.8f,
                            size = Random.nextFloat() * 2f + 0.8f,
                            color = when (Random.nextInt(4)) {
                                0 -> Color(0xFFFFD700)
                                1 -> Color(0xFFFFA500)
                                2 -> Color(0xFFFF4500)
                                else -> Color(0xFFFFEB3B)
                            }
                        )
                    )
                }

                sparks.forEach { spark ->
                    spark.life += 0.016f
                    spark.x += spark.velocityX
                    spark.y += spark.velocityY
                    spark.velocityX *= 0.995f
                    spark.velocityY += 0.015f
                    spark.size *= 0.997f
                }

                sparks.removeAll {
                    it.life >= it.maxLife ||
                            it.y > 120f ||
                            it.y < -120f ||
                            abs(it.x) > 80f
                }

                kotlinx.coroutines.delay(16)
            }
        }

        if (isResetMode && ignitionPhase.value < 0.3f) {
            sparks.clear()
        }
    }

    val textPulse by rememberInfiniteTransition(label = "textPulse").animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-8).dp)
    ) {
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(180.dp)) {
                val centerX = size.width / 2
                val baseY = size.height * 0.84f

                val baseIntensity = ignitionPhase.value
                if (baseIntensity < 0.01f) return@Canvas

                val flameScale = baseIntensity.coerceAtLeast(0.1f)

                val mainFlameColor = if (isResetMode && baseIntensity < 0.3f) {
                    lerp(Color(0xFFFF5722), Color(0xFF616161), 1f - baseIntensity / 0.3f)
                } else {
                    lerp(Color.White, Color(0xFFFF5722), baseIntensity)
                }

                val innerFlameColor = if (isResetMode && baseIntensity < 0.3f) {
                    lerp(Color(0xFFFF9800), Color(0xFF9E9E9E), 1f - baseIntensity / 0.3f)
                } else {
                    lerp(Color(0xFFFFE0B2), Color(0xFFFF9800), baseIntensity)
                }

                val sways = listOf(sway1, sway2, sway3, sway4)

                for (layer in 3 downTo 0) {
                    val layerScale = flameScale * (1f - layer * 0.10f)
                    val alpha = (1f - layer * 0.18f) * flameScale.coerceIn(0.3f, 1f)

                    val height = size.height * 0.85f * layerScale
                    val width = size.width * 1f * layerScale

                    val swayAmount = sways[layer] * (6f + layer * 2f) * baseIntensity
                    val tipSway = sways[layer] * (4f + layer * 1.5f) * baseIntensity

                    val topY = baseY - height
                    val midY = baseY - height * 0.50f
                    val lowerY = baseY - height * 0.10f

                    val flamePath = Path().apply {
                        moveTo(
                            centerX + tipSway * 0.3f,
                            topY - swayAmount * 0.1f
                        )

                        cubicTo(
                            centerX - width * 0.18f - swayAmount * 0.5f,
                            topY + height * 0.10f,
                            centerX - width * 0.60f - swayAmount * 0.8f,
                            midY,
                            centerX - width * 0.28f - swayAmount * 0.3f,
                            lowerY
                        )

                        cubicTo(
                            centerX - width * 0.10f - swayAmount * 0.1f,
                            baseY - height * 0.02f,
                            centerX + width * 0.10f - swayAmount * 0.1f,
                            baseY - height * 0.02f,
                            centerX + width * 0.28f - swayAmount * 0.3f,
                            lowerY
                        )

                        cubicTo(
                            centerX + width * 0.60f - swayAmount * 0.8f,
                            midY,
                            centerX + width * 0.18f - swayAmount * 0.5f,
                            topY + height * 0.10f,
                            centerX + tipSway * 0.3f,
                            topY - swayAmount * 0.1f
                        )
                        close()
                    }

                    val gradientCenter = Offset(
                        centerX + swayAmount * 0.15f,
                        baseY - height * 0.42f
                    )

                    drawPath(
                        path = flamePath,
                        brush = Brush.radialGradient(
                            colors = listOf(
                                mainFlameColor.copy(alpha = alpha * 0.96f),
                                innerFlameColor.copy(alpha = alpha * 0.78f),
                                mainFlameColor.copy(alpha = alpha * 0.22f)
                            ),
                            center = gradientCenter,
                            radius = (size.width * 0.34f * flameScale).coerceAtLeast(1f)
                        )
                    )
                }

                val glowRadius = (size.width * 0.5f * flameScale).coerceAtLeast(1f)
                if (glowRadius > 0f) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                mainFlameColor.copy(alpha = 0.4f * flameScale),
                                mainFlameColor.copy(alpha = 0.12f * flameScale),
                                Color.Transparent
                            ),
                            center = Offset(centerX, baseY - size.height * 0.3f * flameScale),
                            radius = glowRadius
                        ),
                        radius = glowRadius,
                        center = Offset(centerX, baseY - size.height * 0.3f * flameScale)
                    )
                }

                if (flameScale > 0.4f) {
                    sparks.forEach { spark ->
                        val lifeProgress = spark.life / spark.maxLife
                        val sparkAlpha = when {
                            lifeProgress < 0.15f -> lifeProgress / 0.15f
                            lifeProgress > 0.6f -> 1f - (lifeProgress - 0.6f) / 0.4f
                            else -> 1f
                        }

                        val sparkX = centerX + spark.x
                        val sparkY = baseY + spark.y

                        drawCircle(
                            color = Color.White.copy(alpha = sparkAlpha * 0.8f),
                            radius = spark.size * 0.5f,
                            center = Offset(sparkX, sparkY)
                        )

                        drawCircle(
                            color = spark.color.copy(alpha = sparkAlpha * 0.7f),
                            radius = spark.size,
                            center = Offset(sparkX, sparkY)
                        )

                        drawCircle(
                            color = spark.color.copy(alpha = sparkAlpha * 0.15f),
                            radius = spark.size * 3f,
                            center = Offset(sparkX, sparkY)
                        )
                    }
                }
            }

            if (numberVisibility.value > 0f) {
                val appearScale = when {
                    numberVisibility.value < 1f -> 0.05f + numberVisibility.value * 0.95f
                    animatedStreak.value < streak -> 1f
                    else -> textPulse
                }

                Text(
                    text = "${animatedStreak.value.toInt()}",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = fontSize
                    ),
                    color = if (isResetMode && ignitionPhase.value < 0.3f) {
                        Color.White.copy(alpha = 0.5f)
                    } else {
                        Color.White
                    },
                    maxLines = 1,
                    modifier = Modifier
                        .scale(appearScale)
                        .alpha(numberVisibility.value.coerceIn(0f, 1f))
                )
            }
        }

        if (isResetMode) {
            if (warningVisibility.value > 0f) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .alpha(warningVisibility.value)
                        .scale(0.5f + warningVisibility.value * 0.5f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "STREAK LOST",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFF6B35),
                        letterSpacing = 4.sp
                    )
                    Text(
                        text = "Don't give up!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFFF8A65)
                    )
                }
            }
        } else {
            if (streakLabelVisibility.value > 0f) {
                Text(
                    text = "STREAK",
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 6.sp
                    ),
                    color = Color(0xFFFF6B35).copy(alpha = 0.7f),
                    modifier = Modifier
                        .scale(streakLabelVisibility.value)
                        .alpha(streakLabelVisibility.value)
                )
            }
        }
    }
}

private fun lerp(start: Color, end: Color, fraction: Float): Color {
    val clampedFraction = fraction.coerceIn(0f, 1f)
    return Color(
        red = start.red + (end.red - start.red) * clampedFraction,
        green = start.green + (end.green - start.green) * clampedFraction,
        blue = start.blue + (end.blue - start.blue) * clampedFraction,
        alpha = start.alpha + (end.alpha - start.alpha) * clampedFraction
    )
}