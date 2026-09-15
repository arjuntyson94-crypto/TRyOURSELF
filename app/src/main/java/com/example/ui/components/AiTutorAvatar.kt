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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

enum class TutorMood {
    RESTING,
    THINKING,
    CELEBRATING,
    HINTING
}

@Composable
fun AiTutorAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    mood: TutorMood = TutorMood.RESTING,
    tagline: String? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "TutorAnim")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = if (mood == TutorMood.THINKING) 1.12f else 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (mood == TutorMood.THINKING) 800 else 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val orbitalAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (mood == TutorMood.THINKING) 2000 else 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )

    val primaryColor = when (mood) {
        TutorMood.RESTING -> Color(0xFF38BDF8) // Sky Blue
        TutorMood.THINKING -> Color(0xFF0EA5E9) // Electric Sky Blue
        TutorMood.CELEBRATING -> Color(0xFF10B981) // Emerald Green
        TutorMood.HINTING -> Color(0xFFEAB308) // Muted Yellow
    }

    val secondaryColor = when (mood) {
        TutorMood.RESTING -> Color(0xFF10B981) // Green
        TutorMood.THINKING -> Color(0xFFEAB308) // Muted Yellow
        TutorMood.CELEBRATING -> Color(0xFF34D399) // Mint Green
        TutorMood.HINTING -> Color(0xFFFBBF24) // Gold Yellow
    }

    Row(
        modifier = modifier.testTag("ai_tutor_avatar_widget"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            // Ambient orbital particle Canvas
            Canvas(
                modifier = Modifier
                    .size(size)
                    .scale(pulseScale)
            ) {
                val radius = this.size.minDimension / 2f
                val center = Offset(radius, radius)

                // Soft background radial aura
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(primaryColor.copy(alpha = 0.35f), Color.Transparent),
                        center = center,
                        radius = radius
                    )
                )

                // Outer quantum particle ring
                val orbitRadius = radius * 0.88f
                drawCircle(
                    color = primaryColor.copy(alpha = 0.25f),
                    radius = orbitRadius,
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // Orbiting satellite energy particles
                val count = if (mood == TutorMood.THINKING) 4 else 2
                for (i in 0 until count) {
                    val angleRad = Math.toRadians((orbitalAngle + (i * 360f / count)).toDouble())
                    val particleX = center.x + orbitRadius * cos(angleRad).toFloat()
                    val particleY = center.y + orbitRadius * sin(angleRad).toFloat()
                    drawCircle(
                        color = if (i % 2 == 0) primaryColor else secondaryColor,
                        radius = if (mood == TutorMood.THINKING) 3.5.dp.toPx() else 2.5.dp.toPx(),
                        center = Offset(particleX, particleY)
                    )
                }
            }

            // Core sphere with futuristic gradient
            Box(
                modifier = Modifier
                    .size(size * 0.68f)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(primaryColor, secondaryColor)
                        )
                    )
                    .border(1.5.dp, Color.White.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (mood) {
                        TutorMood.RESTING -> "✨"
                        TutorMood.THINKING -> "💭"
                        TutorMood.CELEBRATING -> "🌟"
                        TutorMood.HINTING -> "💡"
                    },
                    fontSize = (size.value * 0.34f).sp
                )
            }
        }

        if (tagline != null) {
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Nova",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = primaryColor.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, primaryColor)
                    ) {
                        Text(
                            text = if (mood == TutorMood.THINKING) "Analyzing..." else "AI Tutor",
                            color = primaryColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = tagline,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
        }
    }
}
