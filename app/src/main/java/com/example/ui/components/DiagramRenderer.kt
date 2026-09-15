package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Subject
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DiagramRenderer(
    subject: Subject,
    diagramType: String? = null,
    title: String = "Vector / Schematic Diagram",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFF2E335A), RoundedCornerShape(14.dp))
            .testTag("inline_diagram_renderer"),
        color = Color(0xFF101326)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = "Diagram",
                    tint = Color(subject.primaryColorHex),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    color = Color(0xFFE2E8F0),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF1E2442)
                ) {
                    Text(
                        text = when (subject) {
                            Subject.PHYSICS -> "Vector Mechanics"
                            Subject.CHEMISTRY -> "Molecular Bond"
                            Subject.MATHS -> "Coordinate Plot"
                            else -> "STEM Model"
                        },
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0A0C18))
                    .border(0.5.dp, Color(0xFF1B203B), RoundedCornerShape(10.dp))
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    val w = size.width
                    val h = size.height

                    when (subject) {
                        Subject.PHYSICS -> {
                            // Ground line
                            val groundY = h * 0.78f
                            drawLine(
                                color = Color(0xFF334155),
                                start = Offset(20f, groundY),
                                end = Offset(w - 20f, groundY),
                                strokeWidth = 2.dp.toPx()
                            )

                            // Initial vehicle/object point
                            val objX = w * 0.25f
                            val objY = groundY - 14.dp.toPx()
                            drawCircle(
                                color = Color(0xFF38BDF8),
                                radius = 9.dp.toPx(),
                                center = Offset(objX, objY)
                            )

                            // Motion path (dashed/dotted arc)
                            val destX = w * 0.75f
                            val destY = groundY - 14.dp.toPx()
                            val motionPath = Path().apply {
                                moveTo(objX, objY)
                                quadraticTo(w * 0.5f, groundY - 40.dp.toPx(), destX, destY)
                            }
                            drawPath(
                                path = motionPath,
                                color = Color(0xFF64748B),
                                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                            )

                            // Target object
                            drawCircle(
                                color = Color(0xFF10B981),
                                radius = 9.dp.toPx(),
                                center = Offset(destX, destY)
                            )

                            // Velocity Vector Arrow (v)
                            val arrowStartX = destX
                            val arrowStartY = destY
                            val arrowEndX = (destX + 45.dp.toPx()).coerceAtMost(w - 15f)
                            val arrowEndY = destY
                            drawLine(
                                color = Color(0xFFFBBF24),
                                start = Offset(arrowStartX, arrowStartY),
                                end = Offset(arrowEndX, arrowEndY),
                                strokeWidth = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            // Arrow head
                            drawLine(
                                color = Color(0xFFFBBF24),
                                start = Offset(arrowEndX - 8.dp.toPx(), arrowEndY - 6.dp.toPx()),
                                end = Offset(arrowEndX, arrowEndY),
                                strokeWidth = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = Color(0xFFFBBF24),
                                start = Offset(arrowEndX - 8.dp.toPx(), arrowEndY + 6.dp.toPx()),
                                end = Offset(arrowEndX, arrowEndY),
                                strokeWidth = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }

                        Subject.CHEMISTRY -> {
                            // Molecular Ring (Benzene or Water H-O-H model)
                            val centerX = w * 0.5f
                            val centerY = h * 0.5f
                            val ringRadius = 32.dp.toPx()

                            // Central node (Oxygen / Carbon)
                            drawCircle(
                                color = Color(0xFFEF4444), // Red for Oxygen
                                radius = 14.dp.toPx(),
                                center = Offset(centerX, centerY)
                            )

                            // Bond 1 to Hydrogen 1 (angle 210 deg)
                            val angle1 = 210.0 * PI / 180.0
                            val h1X = centerX + ringRadius * cos(angle1).toFloat()
                            val h1Y = centerY + ringRadius * sin(angle1).toFloat()

                            drawLine(
                                color = Color(0xFF94A3B8),
                                start = Offset(centerX, centerY),
                                end = Offset(h1X, h1Y),
                                strokeWidth = 3.dp.toPx()
                            )
                            drawCircle(
                                color = Color(0xFF38BDF8), // Blue for Hydrogen
                                radius = 9.dp.toPx(),
                                center = Offset(h1X, h1Y)
                            )

                            // Bond 2 to Hydrogen 2 (angle 330 deg)
                            val angle2 = 330.0 * PI / 180.0
                            val h2X = centerX + ringRadius * cos(angle2).toFloat()
                            val h2Y = centerY + ringRadius * sin(angle2).toFloat()

                            drawLine(
                                color = Color(0xFF94A3B8),
                                start = Offset(centerX, centerY),
                                end = Offset(h2X, h2Y),
                                strokeWidth = 3.dp.toPx()
                            )
                            drawCircle(
                                color = Color(0xFF38BDF8),
                                radius = 9.dp.toPx(),
                                center = Offset(h2X, h2Y)
                            )
                        }

                        Subject.MATHS -> {
                            // Coordinate axes
                            val originX = w * 0.5f
                            val originY = h * 0.55f

                            // X Axis
                            drawLine(
                                color = Color(0xFF334155),
                                start = Offset(20f, originY),
                                end = Offset(w - 20f, originY),
                                strokeWidth = 1.5.dp.toPx()
                            )
                            // Y Axis
                            drawLine(
                                color = Color(0xFF334155),
                                start = Offset(originX, 15f),
                                end = Offset(originX, h - 15f),
                                strokeWidth = 1.5.dp.toPx()
                            )

                            // Parabola curve y = ax² + bx + c
                            val parabolaPath = Path().apply {
                                val startX = originX - 60.dp.toPx()
                                val startY = originY - 35.dp.toPx()
                                moveTo(startX, startY)
                                quadraticTo(originX, originY + 25.dp.toPx(), originX + 60.dp.toPx(), startY)
                            }
                            drawPath(
                                path = parabolaPath,
                                color = Color(0xFFA855F7),
                                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                            )

                            // Vertex point
                            drawCircle(
                                color = Color(0xFFFBBF24),
                                radius = 4.dp.toPx(),
                                center = Offset(originX, originY + 25.dp.toPx())
                            )
                        }

                        else -> {
                            // Default STEM grid
                            drawLine(
                                color = Color(0xFF1E293B),
                                start = Offset(20f, h * 0.5f),
                                end = Offset(w - 20f, h * 0.5f),
                                strokeWidth = 2.dp.toPx()
                            )
                        }
                    }
                }
            }
        }
    }
}
