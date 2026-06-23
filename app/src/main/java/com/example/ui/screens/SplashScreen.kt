package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

class BackgroundParticle(
    var x: Float,
    var y: Float,
    val size: Float,
    var alpha: Float,
    val speedX: Float,
    val speedY: Float,
    val isPulse: Boolean = false
)

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Start splash screen animation flow on launch
    LaunchedEffect(Unit) {
        startAnimation = true
        // Keep splash screen visible for 3.2 seconds
        delay(3200)
        onSplashFinished()
    }

    // Logo fade-in
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1200, delayMillis = 200, easing = EaseInOutCubic),
        label = "LogoAlpha"
    )

    // Gradual Africa reveal
    val africaReveal by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(2200, delayMillis = 400, easing = EaseOutQuart),
        label = "AfricaReveal"
    )

    // Logo scale up
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0.85f,
        animationSpec = tween(2500, delayMillis = 100, easing = EaseOutBack),
        label = "LogoScale"
    )

    // Continuous metallic shimmer position
    val shimmerTransition = rememberInfiniteTransition(label = "LogoShimmer")
    val shimmerTranslateX by shimmerTransition.animateFloat(
        initialValue = -500f,
        targetValue = 1500f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerTranslate"
    )

    // Light rays offset
    val radialTransition = rememberInfiniteTransition(label = "LightRays")
    val rayOffsetX by radialTransition.animateFloat(
        initialValue = -0.2f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "RayOffsetX"
    )
    val rayOffsetY by radialTransition.animateFloat(
        initialValue = -0.2f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "RayOffsetY"
    )

    // Wave phase animations for continuous fluid flowing motion
    val waveTransition = rememberInfiniteTransition(label = "BottomWaves")
    val wavePhase1 by waveTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * java.lang.Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase1"
    )
    val wavePhase2 by waveTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * java.lang.Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(6500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase2"
    )

    // 30 Particles background motion
    val particles = remember {
        mutableStateListOf<BackgroundParticle>().apply {
            repeat(28) {
                val rx = kotlin.random.Random.nextFloat()
                val ry = kotlin.random.Random.nextFloat()
                val rSize = kotlin.random.Random.nextFloat() * 4f + 3f
                val rAlpha = kotlin.random.Random.nextFloat() * 0.40f + 0.15f
                val rSpeedX = (kotlin.random.Random.nextFloat() * 0.03f - 0.015f) * 0.12f
                val rSpeedY = (kotlin.random.Random.nextFloat() * -0.03f - 0.01f) * 0.12f
                val rPulse = kotlin.random.Random.nextInt(11) > 6
                add(
                    BackgroundParticle(
                        x = rx,
                        y = ry,
                        size = rSize,
                        alpha = rAlpha,
                        speedX = rSpeedX,
                        speedY = rSpeedY,
                        isPulse = rPulse
                    )
                )
            }
        }
    }

    var frameCount by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            particles.forEach { p ->
                p.x = (p.x + p.speedX + 1f) % 1f
                p.y = (p.y + p.speedY + 1f) % 1f
            }
            frameCount++
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LuxuryDarkBg)
            .testTag("splash_screen_container")
    ) {
        // 1. Shifting Radial Light Rays background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        DasaGold.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(rayOffsetX * width, rayOffsetY * height),
                    radius = width * 0.85f
                ),
                size = size
            )
        }

        // 2. Blockchain network nodes & digital lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Connect close nodes with faint digital lines
            for (i in 0 until particles.size) {
                val p1 = particles[i]
                for (j in i + 1 until particles.size) {
                    val p2 = particles[j]
                    val dx = p1.x - p2.x
                    val dy = p1.y - p2.y
                    val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                    if (dist < 0.22f) {
                        val lineAlpha = (1f - (dist / 0.22f)) * 0.12f * p1.alpha
                        drawLine(
                            color = DasaGold.copy(alpha = lineAlpha),
                            start = Offset(p1.x * width, p1.y * height),
                            end = Offset(p2.x * width, p2.y * height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }
            }

            // Draw particles themselves
            particles.forEach { p ->
                val pulseAlpha = if (p.isPulse) {
                    p.alpha * (0.4f + 0.6f * sin(frameCount * 0.06f))
                } else {
                    p.alpha
                }
                drawCircle(
                    color = DasaGold.copy(alpha = pulseAlpha),
                    radius = p.size,
                    center = Offset(p.x * width, p.y * height)
                )
            }
        }

        // 3. Central Interactive Animated Logo & Africa Map
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-30).dp)
                .alpha(logoAlpha)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .graphicsLayer {
                        scaleX = logoScale
                        scaleY = logoScale
                    },
                contentAlignment = Alignment.Center
            ) {
                // Glow effect backplate
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                DasaGold.copy(alpha = 0.18f * logoAlpha),
                                Color.Transparent
                            )
                        ),
                        radius = size.width * 0.45f
                    )
                }

                // Custom Canvas Logo Builder (Africa shape + glowing vertical lines)
                Canvas(modifier = Modifier.size(200.dp)) {
                    val width = size.width
                    val height = size.height

                    // Generate a beautiful programmatic Africa vector path
                    val africaPath = Path().apply {
                        moveTo(0.35f * width, 0.20f * height)
                        // North coast
                        lineTo(0.52f * width, 0.18f * height)
                        lineTo(0.68f * width, 0.23f * height)
                        // Northeast / Red Sea bend
                        lineTo(0.72f * width, 0.28f * height)
                        lineTo(0.70f * width, 0.33f * height)
                        // Horn of Africa
                        lineTo(0.76f * width, 0.38f * height)
                        lineTo(0.83f * width, 0.44f * height)
                        lineTo(0.79f * width, 0.50f * height)
                        // East coast downwards
                        lineTo(0.71f * width, 0.64f * height)
                        lineTo(0.67f * width, 0.76f * height)
                        lineTo(0.58f * width, 0.88f * height) // Southernmost tip
                        // West coast upwards
                        lineTo(0.53f * width, 0.86f * height)
                        lineTo(0.48f * width, 0.73f * height)
                        lineTo(0.43f * width, 0.66f * height)
                        lineTo(0.33f * width, 0.59f * height) // Gulf of Guinea
                        lineTo(0.25f * width, 0.55f * height)
                        // West Africa bulge
                        lineTo(0.14f * width, 0.47f * height)
                        lineTo(0.12f * width, 0.38f * height)
                        lineTo(0.18f * width, 0.30f * height)
                        lineTo(0.26f * width, 0.24f * height)
                        close()
                    }

                    // 1. Reveal Africa gradually with a clip rectangular scanner
                    clipRect(bottom = height * africaReveal) {
                        // 2. Build itself using vertical golden lines
                        clipPath(africaPath) {
                            // Draw vertical wires spaced apart
                            val lineSpacing = 14f
                            var curX = 0f
                            while (curX < width) {
                                drawLine(
                                    color = DasaGold.copy(alpha = 0.28f),
                                    start = Offset(curX, 0f),
                                    end = Offset(curX, height),
                                    strokeWidth = 2.dp.toPx()
                                )
                                curX += lineSpacing
                            }

                            // Glowing scan highlights
                            var curScanX = 0f
                            while (curScanX < width) {
                                val distanceToShimmer = kotlin.math.abs(curScanX - shimmerTranslateX)
                                if (distanceToShimmer < 180f) {
                                    val highlightAlpha = (1f - (distanceToShimmer / 180f)) * 0.8f
                                    drawLine(
                                        color = Color(0xFFFFF7C2).copy(alpha = highlightAlpha),
                                        start = Offset(curScanX, 0f),
                                        end = Offset(curScanX, height),
                                        strokeWidth = 2.5.dp.toPx()
                                    )
                                }
                                curScanX += lineSpacing
                            }
                        }

                        // 3. Draw Africa outline with glowing metallic sweep
                        val shimmerBrush = Brush.linearGradient(
                            colors = listOf(
                                DasaGold,
                                Color(0xFFFFF6CA),
                                DasaGold,
                                Color(0xFF9F7E1B),
                                DasaGold
                            ),
                            start = Offset(shimmerTranslateX - 200f, 0f),
                            end = Offset(shimmerTranslateX, height)
                        )
                        drawPath(
                            path = africaPath,
                            brush = shimmerBrush,
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "DASA CONNECT",
                style = MaterialTheme.typography.headlineMedium,
                color = DasaGold,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "DIGITAL ASSETS SUMMIT AFRICA",
                style = MaterialTheme.typography.labelMedium,
                color = LuxuryTextMuted,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        // 4. Loading indicator with gold shimmer & Smooth progress
        val loadingProgress by animateFloatAsState(
            targetValue = if (startAnimation) 1.0f else 0f,
            animationSpec = tween(2800, delayMillis = 100, easing = EaseInOutSine),
            label = "LoadingProgress"
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-110).dp)
                .padding(horizontal = 48.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Preparing Your DASA Experience",
                color = LuxuryTextMuted,
                style = MaterialTheme.typography.bodyMedium,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Premium fintech progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(2.dp))
            ) {
                // Gold shimmer bar gradient
                val barShimmerBrush = Brush.linearGradient(
                    colors = listOf(
                        DasaGold,
                        Color(0xFFFFF2B0),
                        DasaGold
                    ),
                    start = Offset(shimmerTranslateX - 100f, 0f),
                    end = Offset(shimmerTranslateX, 0f)
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = loadingProgress)
                        .background(barShimmerBrush, RoundedCornerShape(2.dp))
                )
            }
        }

        // 5. Flowing Bottom Wave Animation
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .align(Alignment.BottomCenter)
        ) {
            val wWidth = size.width
            val wHeight = size.height

            // Wave 1: slow bottom deep fill
            val path1 = Path()
            path1.moveTo(0f, wHeight)
            for (x in 0..wWidth.toInt() step 6) {
                val y = wHeight - 35.dp.toPx() + sin(x * 0.005f + wavePhase1) * 16.dp.toPx()
                path1.lineTo(x.toFloat(), y)
            }
            path1.lineTo(wWidth, wHeight)
            path1.close()
            drawPath(path1, color = DasaGold.copy(alpha = 0.05f))

            // Wave 2: layered fluid middle fill
            val path2 = Path()
            path2.moveTo(0f, wHeight)
            for (x in 0..wWidth.toInt() step 6) {
                val y = wHeight - 25.dp.toPx() + cos(x * 0.008f - wavePhase2) * 12.dp.toPx()
                path2.lineTo(x.toFloat(), y)
            }
            path2.lineTo(wWidth, wHeight)
            path2.close()
            drawPath(path2, color = DasaGold.copy(alpha = 0.12f))

            // Wave 3: dashed high-tech digital curve
            val path3 = Path()
            var first = true
            for (x in 0..wWidth.toInt() step 4) {
                val y = wHeight - 30.dp.toPx() + sin(x * 0.01f + wavePhase1 + 1.2f) * 10.dp.toPx()
                if (first) {
                    path3.moveTo(x.toFloat(), y)
                    first = false
                } else {
                    path3.lineTo(x.toFloat(), y)
                }
            }
            drawPath(
                path = path3,
                color = DasaGold.copy(alpha = 0.8f),
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(12f, 12f),
                        phase = wavePhase2 * 12f
                    )
                )
            )
        }
    }
}
