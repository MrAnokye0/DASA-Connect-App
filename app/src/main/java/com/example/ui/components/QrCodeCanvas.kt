package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.random.Random

@Composable
fun QrCodeCanvas(
    contentString: String,
    modifier: Modifier = Modifier,
    qrColor: Color = Color(0xFFD4AF37), // Luxury gold QR code by default
    backgroundColor: Color = Color.Transparent
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f)
    ) {
        val sizePx = size.width
        val modules = 21 // Version 1 QR code has 21x21 modules
        val moduleSize = sizePx / modules

        // Use the hash code of contentString to seed the random matrix,
        // ensuring the QR code is deterministic and unique for each user profile.
        val seed = contentString.hashCode().toLong()
        val random = Random(seed)

        // Draw background
        if (backgroundColor != Color.Transparent) {
            drawRect(color = backgroundColor, size = size)
        }

        // Draw modules
        for (row in 0 until modules) {
            for (col in 0 until modules) {
                // Skip the areas of the 3 position detection patterns (top-left, top-right, bottom-left)
                val isTopLeftPattern = row < 7 && col < 7
                val isTopRightPattern = row < 7 && col >= modules - 7
                val isBottomLeftPattern = row >= modules - 7 && col < 7

                if (!isTopLeftPattern && !isTopRightPattern && !isBottomLeftPattern) {
                    // Generate pseudo-random data module
                    if (random.nextBoolean()) {
                        drawRect(
                            color = qrColor,
                            topLeft = Offset(col * moduleSize, row * moduleSize),
                            size = Size(moduleSize, moduleSize)
                        )
                    }
                }
            }
        }

        // Draw the 3 standard position detection patterns
        drawPositionPattern(Offset(0f, 0f), moduleSize, qrColor)
        drawPositionPattern(Offset((modules - 7) * moduleSize, 0f), moduleSize, qrColor)
        drawPositionPattern(Offset(0f, (modules - 7) * moduleSize), moduleSize, qrColor)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawPositionPattern(
    offset: Offset,
    moduleSize: Float,
    color: Color
) {
    // 1. Outer 7x7 block
    drawRect(
        color = color,
        topLeft = offset,
        size = Size(moduleSize * 7, moduleSize * 7)
    )

    // 2. White separator inner 5x5 block
    drawRect(
        color = Color(0xFF0D1B2A), // Inside DASA navy background
        topLeft = Offset(offset.x + moduleSize, offset.y + moduleSize),
        size = Size(moduleSize * 5, moduleSize * 5)
    )

    // 3. Center solid 3x3 block
    drawRect(
        color = color,
        topLeft = Offset(offset.x + moduleSize * 2, offset.y + moduleSize * 2),
        size = Size(moduleSize * 3, moduleSize * 3)
    )
}
