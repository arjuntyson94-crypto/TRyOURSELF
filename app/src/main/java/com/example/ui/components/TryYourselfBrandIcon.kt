package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Signature Brand Gradient: Vibrant Green -> Sky Blue -> Muted Yellow
val TryYourselfBrandGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF10B981), // Vibrant emerald green
        Color(0xFF0EA5E9), // Electric sky blue
        Color(0xFFEAB308)  // Warm muted yellow
    ),
    start = Offset.Zero,
    end = Offset.Infinite
)

/**
 * Renders the TRyOURSELF brand icon matching the official app launcher icon:
 * Green - Sky Blue - Muted Yellow gradient squircle with the white upward chevron and vertical pill.
 */
@Composable
fun TryYourselfBrandIcon(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    shapeCornerRadius: Dp = (size * 0.26f)
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(shapeCornerRadius), spotColor = Color(0xFF10B981))
            .clip(RoundedCornerShape(shapeCornerRadius))
            .background(TryYourselfBrandGradient),
        contentAlignment = Alignment.Center
    ) {
        val chevronPath = remember { Path() }

        Canvas(modifier = Modifier.size(size)) {
            val scale = this.size.minDimension / 108f

            // 1. Upward Chevron Roof
            chevronPath.reset()
            chevronPath.moveTo(54f * scale, 26.8f * scale)
            chevronPath.lineTo(81.5f * scale, 43.1f * scale)
            chevronPath.lineTo(76.9f * scale, 50.9f * scale)
            chevronPath.lineTo(54f * scale, 37.2f * scale)
            chevronPath.lineTo(31.1f * scale, 50.9f * scale)
            chevronPath.lineTo(26.5f * scale, 43.1f * scale)
            chevronPath.close()

            drawPath(
                path = chevronPath,
                color = Color.White
            )

            // 2. Vertical Rounded Bar (Pill)
            val pillWidth = 9f * scale
            val pillHeight = 28f * scale
            val pillLeft = 49.5f * scale
            val pillTop = 52f * scale
            val pillRadius = 4.5f * scale

            drawRoundRect(
                color = Color.White,
                topLeft = Offset(pillLeft, pillTop),
                size = Size(pillWidth, pillHeight),
                cornerRadius = CornerRadius(pillRadius, pillRadius)
            )
        }
    }
}
