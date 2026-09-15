package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ScratchStroke(
    val path: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

@Composable
fun ScratchpadCanvas(
    modifier: Modifier = Modifier,
    heightDp: Int = 180,
    onStrokesChanged: ((Int) -> Unit)? = null
) {
    val strokes = remember { mutableStateListOf<ScratchStroke>() }
    var currentStrokePoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var selectedColor by remember { mutableStateOf(Color(0xFF38BDF8)) } // Sky blue
    var strokeWidth by remember { mutableFloatStateOf(4.5f) }

    val palette = listOf(
        Color(0xFF38BDF8), // Electric Sky Blue
        Color(0xFF10B981), // Emerald Green
        Color(0xFFEAB308), // Muted Yellow
        Color(0xFF34D399), // Soft Mint Green
        Color(0xFFFFFFFF)  // Clean White
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF0284C7), RoundedCornerShape(16.dp))
            .testTag("scratchpad_canvas_card"),
        color = Color(0xFF0A3C5E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header bar with tools
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Scratchpad",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Digital Scratchpad",
                        color = Color(0xFFE2E8F0),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Palette colors
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    palette.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(if (selectedColor == color) 22.dp else 18.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (selectedColor == color) 2.dp else 0.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .pointerInput(color) {
                                    selectedColor = color
                                }
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Undo button
                    IconButton(
                        onClick = {
                            if (strokes.isNotEmpty()) {
                                strokes.removeAt(strokes.lastIndex)
                                onStrokesChanged?.invoke(strokes.size)
                            }
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("scratchpad_undo_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            tint = if (strokes.isNotEmpty()) Color(0xFF94A3B8) else Color(0xFF475569),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Clear button
                    IconButton(
                        onClick = {
                            strokes.clear()
                            currentStrokePoints = emptyList()
                            onStrokesChanged?.invoke(0)
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("scratchpad_clear_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear Canvas",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // The Interactive Drawing Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heightDp.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF062438))
                    .border(0.5.dp, Color(0xFF0284C7), RoundedCornerShape(12.dp))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentStrokePoints = listOf(offset)
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                currentStrokePoints = currentStrokePoints + change.position
                            },
                            onDragEnd = {
                                if (currentStrokePoints.isNotEmpty()) {
                                    strokes.add(
                                        ScratchStroke(
                                            path = currentStrokePoints,
                                            color = selectedColor,
                                            strokeWidth = strokeWidth
                                        )
                                    )
                                    currentStrokePoints = emptyList()
                                    onStrokesChanged?.invoke(strokes.size)
                                }
                            },
                            onDragCancel = {
                                currentStrokePoints = emptyList()
                            }
                        )
                    }
                    .testTag("scratchpad_interactive_canvas")
            ) {
                if (strokes.isEmpty() && currentStrokePoints.isEmpty()) {
                    Text(
                        text = "✏️ Work out this step here with your finger or stylus...",
                        color = Color(0xFF475569),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Draw completed strokes
                    strokes.forEach { stroke ->
                        if (stroke.path.size > 1) {
                            val path = Path().apply {
                                moveTo(stroke.path.first().x, stroke.path.first().y)
                                for (i in 1 until stroke.path.size) {
                                    lineTo(stroke.path[i].x, stroke.path[i].y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = stroke.color,
                                style = Stroke(
                                    width = stroke.strokeWidth.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        } else if (stroke.path.size == 1) {
                            drawCircle(
                                color = stroke.color,
                                radius = stroke.strokeWidth.dp.toPx() / 2,
                                center = stroke.path.first()
                            )
                        }
                    }

                    // Draw current in-progress stroke
                    if (currentStrokePoints.size > 1) {
                        val path = Path().apply {
                            moveTo(currentStrokePoints.first().x, currentStrokePoints.first().y)
                            for (i in 1 until currentStrokePoints.size) {
                                lineTo(currentStrokePoints[i].x, currentStrokePoints[i].y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = selectedColor,
                            style = Stroke(
                                width = strokeWidth.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    } else if (currentStrokePoints.size == 1) {
                        drawCircle(
                            color = selectedColor,
                            radius = strokeWidth.dp.toPx() / 2,
                            center = currentStrokePoints.first()
                        )
                    }
                }
            }
        }
    }
}
