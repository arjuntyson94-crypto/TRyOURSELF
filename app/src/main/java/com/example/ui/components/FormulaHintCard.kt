package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.FormulaBracketGold
import com.example.ui.theme.FormulaCardBg
import com.example.ui.theme.FormulaCardBorder

@Composable
fun FormulaHintCard(
    formula: String,
    formulaName: String?,
    nextStepNumber: Int,
    modifier: Modifier = Modifier
) {
    var studentTrialNote by remember { mutableStateOf("") }
    var showScratchpad by remember { mutableStateOf(false) }

    // Gentle pulse animation for the lightbulb
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val bulbScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bulbScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("formula_hint_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = FormulaCardBg
        ),
        border = BorderStroke(
            2.dp,
            Brush.horizontalGradient(
                colors = listOf(FormulaCardBorder, Amber500, FormulaBracketGold)
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Lightbulb + Think message
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Think and Try Yourself",
                    tint = FormulaBracketGold,
                    modifier = Modifier
                        .size(28.dp)
                        .scale(bulbScale)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "THINK & TRyOURSELF FIRST!",
                    color = FormulaBracketGold,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Before revealing Step $nextStepNumber, can you solve it using this formula?",
                color = Color(0xFFE2E8F0),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium
            )

            if (!formulaName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF0C4A6E)
                ) {
                    Text(
                        text = "Formula: $formulaName",
                        color = Color(0xFFBAE6FD),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // THE SQUARE BRACKET FORMULA DISPLAY (Crucial requirement from prompt)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF071215))
                    .border(1.dp, Color(0xFFCA8A04), RoundedCornerShape(14.dp))
                    .padding(vertical = 14.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "[ $formula ]",
                    color = FormulaBracketGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.testTag("formula_in_square_brackets")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Encouragement & Scratchpad Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✏️ Try solving it on your notebook",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )

                TextButton(
                    onClick = { showScratchpad = !showScratchpad },
                    modifier = Modifier.testTag("toggle_scratchpad_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showScratchpad) "Hide Pad" else "My Answer",
                        color = Color(0xFF38BDF8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            AnimatedVisibility(
                visible = showScratchpad,
                enter = fadeIn() + slideInVertically()
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    OutlinedTextField(
                        value = studentTrialNote,
                        onValueChange = { studentTrialNote = it },
                        placeholder = {
                            Text(
                                "Type your calculated answer here to test yourself...",
                                color = Color(0xFF64748B),
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("scratchpad_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color(0xFFE2E8F0),
                            focusedContainerColor = Color(0xFF071215),
                            unfocusedContainerColor = Color(0xFF071215),
                            focusedBorderColor = FormulaBracketGold,
                            unfocusedBorderColor = Color(0xFF1C3A42)
                        ),
                        singleLine = false,
                        maxLines = 2
                    )
                    if (studentTrialNote.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Awesome effort! Now hit 'Next' below to check if your answer matches the step.",
                            color = Emerald500,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
