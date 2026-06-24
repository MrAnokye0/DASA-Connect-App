package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun AfricaNetworkGlobe(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "GlobeAnimation")
    
    // Slow elegant drift phase
    val driftPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * java.lang.Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DriftPhase"
    )

    // Moving packet progress
    val packetProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PacketProgress"
    )

    // Glowing heartbeat scale
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    // Coordinates approximating Africa
    val relativeNodes = remember {
        listOf(
            // North outline
            Offset(0.35f, 0.22f), Offset(0.45f, 0.20f), Offset(0.55f, 0.18f), Offset(0.65f, 0.21f), Offset(0.72f, 0.26f),
            // East coast
            Offset(0.74f, 0.32f), Offset(0.77f, 0.40f), Offset(0.83f, 0.45f), Offset(0.79f, 0.50f),
            Offset(0.74f, 0.58f), Offset(0.69f, 0.67f), Offset(0.65f, 0.76f), Offset(0.60f, 0.84f), Offset(0.55f, 0.90f),
            // Southern tip
            Offset(0.51f, 0.89f),
            // West coast
            Offset(0.48f, 0.78f), Offset(0.44f, 0.70f), Offset(0.36f, 0.63f), Offset(0.28f, 0.58f),
            // West Africa bulge
            Offset(0.18f, 0.52f), Offset(0.13f, 0.44f), Offset(0.17f, 0.34f), Offset(0.25f, 0.26f), Offset(0.32f, 0.23f),
            // Core central nodes
            Offset(0.32f, 0.37f), Offset(0.42f, 0.32f), Offset(0.52f, 0.28f), Offset(0.62f, 0.30f), Offset(0.68f, 0.36f),
            Offset(0.40f, 0.47f), Offset(0.50f, 0.44f), Offset(0.60f, 0.42f), Offset(0.66f, 0.48f),
            Offset(0.46f, 0.57f), Offset(0.56f, 0.54f), Offset(0.62f, 0.60f), Offset(0.58f, 0.70f),
            Offset(0.52f, 0.68f), Offset(0.53f, 0.80f)
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("africa_network_globe_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LuxuryCardBg),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    BorderColor,
                    DasaGold.copy(alpha = 0.25f),
                    BorderColor
                )
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "INTELLIGENT FINANCIAL NETWORK",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        ),
                        color = DasaGold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Real-time Interconnected Africa Nodes",
                        style = MaterialTheme.typography.bodySmall,
                        color = LuxuryTextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .background(DasaEmerald.copy(alpha = 0.12f), RoundedCornerShape(100.dp))
                        .border(1.dp, DasaEmerald, RoundedCornerShape(100.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(DasaEmerald, RoundedCornerShape(100.dp))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE ECOSYSTEM",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = DasaEmerald
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // The main high-fidelity visualization
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(DasaDeepNavy.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    val width = size.width
                    val height = size.height

                    // Dynamic calculated nodes with orbit drift
                    val dynamicNodes = relativeNodes.mapIndexed { idx, offset ->
                        val orbitRadius = 6f
                        val speedFactor = if (idx % 2 == 0) 1.2f else 0.8f
                        val driftX = sin(driftPhase * speedFactor + idx) * orbitRadius
                        val driftY = cos(driftPhase * speedFactor + idx) * orbitRadius
                        Offset(
                            offset.x * width + driftX,
                            offset.y * height + driftY
                        )
                    }

                    // 1. Draw glowing blockchain paths first
                    for (i in dynamicNodes.indices) {
                        val p1 = dynamicNodes[i]
                        for (j in i + 1 until dynamicNodes.size) {
                            val p2 = dynamicNodes[j]
                            val dx = p1.x - p2.x
                            val dy = p1.y - p2.y
                            val dist = sqrt(dx * dx + dy * dy)
                            val maxConnectDist = width * 0.18f

                            if (dist < maxConnectDist) {
                                val proximityRatio = 1f - (dist / maxConnectDist)
                                val baseAlpha = proximityRatio * 0.18f
                                
                                // Draw double-layered lines for digital asset pipelines
                                // Glowing background blur line
                                drawLine(
                                    color = DasaGold.copy(alpha = baseAlpha * 0.4f),
                                    start = p1,
                                    end = p2,
                                    strokeWidth = 4.dp.toPx()
                                )
                                // Solid vector connection line
                                drawLine(
                                    color = DasaGold.copy(alpha = baseAlpha),
                                    start = p1,
                                    end = p2,
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                        }
                    }

                    // 2. Draw flowing digital asset packet pulses along selected paths
                    // We generate deterministic packet pairs
                    for (k in 0 until dynamicNodes.size step 3) {
                        val startNodeIdx = k
                        val endNodeIdx = (k + 7) % dynamicNodes.size
                        
                        val pStart = dynamicNodes[startNodeIdx]
                        val pEnd = dynamicNodes[endNodeIdx]
                        
                        val dx = pStart.x - pEnd.x
                        val dy = pStart.y - pEnd.y
                        val dist = sqrt(dx * dx + dy * dy)
                        if (dist < width * 0.4f) {
                            // Calculate current moving position along the route
                            val currentX = pStart.x + (pEnd.x - pStart.x) * packetProgress
                            val currentY = pStart.y + (pEnd.y - pStart.y) * packetProgress
                            
                            // Pulse glowing golden core
                            drawCircle(
                                color = Color(0xFFFFF6C2),
                                radius = 3.dp.toPx(),
                                center = Offset(currentX, currentY)
                            )
                            // Outer soft gold aura
                            drawCircle(
                                color = DasaGold.copy(alpha = 0.4f),
                                radius = 6.dp.toPx() * pulseScale,
                                center = Offset(currentX, currentY)
                            )
                        }
                    }

                    // 3. Draw premium nodes (ports of Africa)
                    dynamicNodes.forEachIndexed { idx, offset ->
                        val isHub = idx % 5 == 0
                        val nodeRadius = if (isHub) 4.5.dp.toPx() else 2.5.dp.toPx()
                        
                        if (isHub) {
                            // Radial glow behind major fintech hubs (e.g., Lagos, Nairobi, Cape Town)
                            drawCircle(
                                color = DasaGold.copy(alpha = 0.15f * pulseScale),
                                radius = nodeRadius * 3f,
                                center = offset
                            )
                            drawCircle(
                                color = DasaGold,
                                radius = nodeRadius,
                                center = offset
                            )
                            drawCircle(
                                color = Color.White,
                                radius = nodeRadius * 0.4f,
                                center = offset
                            )
                        } else {
                            // Regular asset nodes
                            drawCircle(
                                color = DasaGold.copy(alpha = 0.75f),
                                radius = nodeRadius,
                                center = offset
                            )
                        }
                    }
                }
            }
        }
    }
}
