package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ProblemDoubt
import com.example.data.model.SolutionStep
import com.example.data.model.StepAttempt
import com.example.data.model.Subject
import com.example.ui.components.AiTutorAvatar
import com.example.ui.components.DiagramRenderer
import com.example.ui.components.FormulaHintCard
import com.example.ui.components.ScratchpadCanvas
import com.example.ui.components.StepCard
import com.example.ui.components.TutorMood
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.FormulaBracketGold
import com.example.ui.theme.Indigo600
import com.example.ui.util.HapticHelper
import com.example.ui.util.TtsHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SolverScreen(
    doubt: ProblemDoubt,
    visibleStepCount: Int = 1,
    activeStepIndex: Int = 0,
    isCompleted: Boolean = false,
    onNextStep: () -> Unit = {},
    onRestart: () -> Unit = {},
    onBack: () -> Unit = {},
    onStepMastered: ((Int, Int) -> Unit)? = null, // (stepNumber, xpGained)
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Internal state for the 3-tier Attempt -> Hint -> Reveal flow per active step
    var currentStepIndex by remember(doubt.id) { mutableIntStateOf(activeStepIndex.coerceAtMost(doubt.steps.lastIndex)) }
    var stepAttempts by remember(doubt.id) { mutableStateOf(mapOf<Int, StepAttempt>()) }
    var isThinking by remember { mutableStateOf(false) }
    var tutorMood by remember { mutableStateOf(TutorMood.RESTING) }
    var isScratchpadOpen by remember { mutableStateOf(false) }
    var isSimplerOpen by remember { mutableStateOf(false) }

    // Text to Speech
    val ttsHelper = remember { TtsHelper(context) }

    val totalSteps = doubt.steps.size
    val currentStep = doubt.steps.getOrNull(currentStepIndex) ?: doubt.steps.first()
    val activeAttempt = stepAttempts[currentStepIndex] ?: StepAttempt(stepNumber = currentStep.stepNumber)
    val isAllSolved = currentStepIndex >= totalSteps - 1 && (activeAttempt.isCompleted || isCompleted)

    val progress = if (isAllSolved) 1f else ((currentStepIndex.toFloat()) / totalSteps.toFloat()).coerceIn(0f, 1f)

    // Auto-scroll when stepping forward
    LaunchedEffect(currentStepIndex) {
        listState.animateScrollToItem(currentStepIndex + 1)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF082F49))
    ) {
        // TOP NAVIGATION BAR
        Surface(
            color = Color(0xFF0A3858),
            border = BorderStroke(1.dp, Color(0xFF0284C7))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            ttsHelper.stop()
                            onBack()
                        },
                        modifier = Modifier.testTag("solver_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column {
                        Text(
                            text = doubt.subject.displayName,
                            color = Color(doubt.subject.primaryColorHex),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isAllSolved) "Problem Solved! 🎉" else "Step ${currentStepIndex + 1} of $totalSteps",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // AI Tutor Avatar with dynamic mood state
                AiTutorAvatar(
                    size = 40.dp,
                    mood = tutorMood,
                    tagline = if (isThinking) "Evaluating..." else "Guiding you"
                )
            }
        }

        // PROGRESS BAR
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp),
            color = if (isAllSolved) Color(0xFF10B981) else Color(0xFF0EA5E9),
            trackColor = Color(0xFF072E47)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                // Problem Statement Header Card
                ProblemStatementCard(doubt = doubt)
            }

            // Inline Diagram if problem relates to Physics, Chemistry, or Math
            if (doubt.steps.any { it.diagramType != null } || doubt.subject != Subject.ALL) {
                item {
                    DiagramRenderer(
                        subject = doubt.subject,
                        title = when (doubt.subject) {
                            Subject.PHYSICS -> "Physical Model & Vector Breakdown"
                            Subject.CHEMISTRY -> "Reaction & Molecular Configuration"
                            Subject.MATHS -> "Geometric & Coordinate Graph"
                            else -> "STEM Visualization"
                        }
                    )
                }
            }

            // PAST COMPLETED STEPS (Locked & Validated with Green Badges)
            for (i in 0 until currentStepIndex) {
                val step = doubt.steps[i]
                item {
                    StepCard(
                        step = step,
                        isLatestUnlocked = false
                    )
                }
            }

            // CURRENT ACTIVE STEP: 3-TIER ATTEMPT -> HINT -> REVEAL PEDAGOGICAL LOOP
            if (!isAllSolved) {
                item {
                    CurrentStepAttemptCard(
                        step = currentStep,
                        stepIndex = currentStepIndex,
                        attempt = activeAttempt,
                        isThinking = isThinking,
                        isScratchpadOpen = isScratchpadOpen,
                        isSimplerOpen = isSimplerOpen,
                        onToggleScratchpad = { isScratchpadOpen = !isScratchpadOpen },
                        onToggleSimpler = { isSimplerOpen = !isSimplerOpen },
                        onNarrate = {
                            ttsHelper.speak("Step ${currentStep.stepNumber}: ${currentStep.title}. ${currentStep.explanation}")
                        },
                        onInputChange = { input ->
                            stepAttempts = stepAttempts + (currentStepIndex to activeAttempt.copy(studentInput = input))
                        },
                        onSubmitAttempt = {
                            scope.launch {
                                isThinking = true
                                tutorMood = TutorMood.THINKING
                                delay(900) // Pedagogical evaluation feel
                                isThinking = false

                                val input = activeAttempt.studentInput.trim()
                                val isAttemptCorrect = evaluateAttempt(input, currentStep)

                                if (isAttemptCorrect) {
                                    HapticHelper.playSuccess(context)
                                    tutorMood = TutorMood.CELEBRATING
                                    stepAttempts = stepAttempts + (currentStepIndex to activeAttempt.copy(
                                        isEvaluated = true,
                                        isCorrect = true,
                                        isCompleted = true,
                                        feedback = "🎯 Brilliant! Your reasoning is completely spot-on! Step unlocked."
                                    ))
                                    onStepMastered?.invoke(currentStep.stepNumber, 25)
                                } else {
                                    tutorMood = TutorMood.HINTING
                                    stepAttempts = stepAttempts + (currentStepIndex to activeAttempt.copy(
                                        isEvaluated = true,
                                        isCorrect = false,
                                        feedback = "🤔 Close, but check your calculation or variables again! Request a Level 1 hint below to get on track."
                                    ))
                                }
                            }
                        },
                        onRequestHint1 = {
                            tutorMood = TutorMood.HINTING
                            stepAttempts = stepAttempts + (currentStepIndex to activeAttempt.copy(
                                hintLevel = 1.coerceAtLeast(activeAttempt.hintLevel)
                            ))
                        },
                        onRevealFormulaChip = {
                            stepAttempts = stepAttempts + (currentStepIndex to activeAttempt.copy(
                                isFormulaRevealed = true,
                                hintLevel = 2.coerceAtLeast(activeAttempt.hintLevel)
                            ))
                        },
                        onRevealFullStep = {
                            stepAttempts = stepAttempts + (currentStepIndex to activeAttempt.copy(
                                isCompleted = true,
                                hintLevel = 3,
                                isFormulaRevealed = true
                            ))
                            tutorMood = TutorMood.RESTING
                        },
                        onProceedNextStep = {
                            onNextStep()
                            if (currentStepIndex + 1 < totalSteps) {
                                currentStepIndex += 1
                                tutorMood = TutorMood.RESTING
                                isScratchpadOpen = false
                                isSimplerOpen = false
                            } else {
                                tutorMood = TutorMood.CELEBRATING
                            }
                        }
                    )
                }
            }

            // WHEN ALL STEPS ARE SOLVED: CELEBRATION & FINAL ANSWER CARD
            if (isAllSolved) {
                item {
                    FinalAnswerCelebrationCard(
                        finalAnswer = doubt.finalAnswer,
                        onRestart = {
                            currentStepIndex = 0
                            stepAttempts = emptyMap()
                            tutorMood = TutorMood.RESTING
                            onRestart()
                        },
                        onBack = {
                            ttsHelper.stop()
                            onBack()
                        },
                        onShare = {
                            shareDoubtSummary(context, doubt)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

// Check whether student attempt contains reasonable answer or keywords
private fun evaluateAttempt(studentInput: String, step: SolutionStep): Boolean {
    if (studentInput.length < 2) return false
    val lower = studentInput.lowercase()

    // If calculation has numbers, check if numbers match
    val numbersInCalc = Regex("[0-9.]+").findAll(step.calculation ?: "").map { it.value }.toSet()
    val numbersInInput = Regex("[0-9.]+").findAll(studentInput).map { it.value }.toSet()

    if (numbersInCalc.isNotEmpty() && numbersInCalc.intersect(numbersInInput).isNotEmpty()) {
        return true
    }

    // Check against expected keywords or formula symbols
    val keywords = listOfNotNull(
        step.expectedKeyword?.lowercase(),
        step.formulaName?.lowercase(),
        step.title.lowercase().split(" ").firstOrNull { it.length > 4 }
    )

    if (keywords.any { lower.contains(it) }) {
        return true
    }

    // Any substantive attempt of 5+ words gets positive affirmation
    return studentInput.split(" ").filter { it.isNotBlank() }.size >= 3
}

@Composable
private fun CurrentStepAttemptCard(
    step: SolutionStep,
    stepIndex: Int,
    attempt: StepAttempt,
    isThinking: Boolean,
    isScratchpadOpen: Boolean,
    isSimplerOpen: Boolean,
    onToggleScratchpad: () -> Unit,
    onToggleSimpler: () -> Unit,
    onNarrate: () -> Unit,
    onInputChange: (String) -> Unit,
    onSubmitAttempt: () -> Unit,
    onRequestHint1: () -> Unit,
    onRevealFormulaChip: () -> Unit,
    onRevealFullStep: () -> Unit,
    onProceedNextStep: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_step_card_${step.stepNumber}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A3C5E)),
        border = BorderStroke(1.5.dp, Color(0xFF0EA5E9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row: Step Pill + Title + Audio Narrate
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF0EA5E9)))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${step.stepNumber}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = step.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onNarrate,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("narrate_step_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Read aloud",
                        tint = Color(0xFFA5B4FC),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Step prompt / task description
            Text(
                text = step.explanation,
                color = Color(0xFFCBD5E1),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            // "Explain it simpler" toggle
            Row(
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .clickable { onToggleSimpler() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSimplerOpen) "▼ Hide simpler intuition" else "💡 Explain it simpler (Everyday Analogy)",
                    color = Color(0xFF38BDF8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            AnimatedVisibility(
                visible = isSimplerOpen,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0F2438),
                    border = BorderStroke(0.5.dp, Color(0xFF0284C7)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = step.simplerExplanation
                            ?: "Imagine you are filling up a measuring cup step by step. Here, we isolate the unknown piece by gathering everything we already know on the other side of the balance scale!",
                        color = Color(0xFFBAE6FD),
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // If Step is NOT yet completed: Show 1. Attempt section
            if (!attempt.isCompleted) {
                // Section 1: ATTEMPT INPUT
                Text(
                    text = "YOUR ATTEMPT",
                    color = Color(0xFF38BDF8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = attempt.studentInput,
                    onValueChange = onInputChange,
                    placeholder = {
                        Text("Type your calculation or formula reasoning...", color = Color(0xFF64748B), fontSize = 13.sp)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("step_attempt_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF062438),
                        unfocusedContainerColor = Color(0xFF062438),
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF0284C7),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Scratchpad toggle button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isScratchpadOpen) Color(0xFF0C4A6E) else Color(0xFF14282D),
                        border = BorderStroke(1.dp, if (isScratchpadOpen) Color(0xFF0284C7) else Color(0xFF1C3A42)),
                        modifier = Modifier
                            .clickable { onToggleScratchpad() }
                            .testTag("toggle_scratchpad_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Scratchpad",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isScratchpadOpen) "Close Scratchpad" else "✍️ Open Scratchpad Canvas",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Submit Attempt Button
                    Button(
                        onClick = onSubmitAttempt,
                        enabled = !isThinking && attempt.studentInput.isNotBlank(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9)),
                        modifier = Modifier.testTag("submit_attempt_button")
                    ) {
                        if (isThinking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Checking...", fontSize = 12.sp)
                        } else {
                            Text("Check Attempt", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Digital Scratchpad Canvas
                AnimatedVisibility(
                    visible = isScratchpadOpen,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        ScratchpadCanvas(heightDp = 160)
                    }
                }

                // AI Feedback on Attempt
                if (attempt.feedback != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (attempt.isCorrect == true) Color(0xFF064E3B) else Color(0xFF422006),
                        border = BorderStroke(1.dp, if (attempt.isCorrect == true) Color(0xFF10B981) else Color(0xFFEAB308)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Text(
                                text = attempt.feedback,
                                color = Color.White,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // 2. GRADUATED HINTS SECTION
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "GRADUATED HINTS",
                    color = Color(0xFFEAB308),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // LEVEL 1 HINT: Conceptual Nudge
                if (attempt.hintLevel >= 1) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0C4A6E),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Hint 1",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Level 1 Nudge: ${step.conceptualNudge}",
                                color = Color(0xFFE0F2FE),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = onRequestHint1,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("request_hint_1_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Nudge",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("💡 Need a Conceptual Nudge? (Level 1 Hint)", color = Color(0xFF38BDF8), fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // LEVEL 2 HINT: Collapsed Formula Chip (Tappable chip, student chooses to reveal)
                if (step.formula != null) {
                    if (attempt.isFormulaRevealed) {
                        FormulaHintCard(
                            formula = step.formula,
                            formulaName = step.formulaName,
                            nextStepNumber = step.stepNumber
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    } else {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF282110),
                            border = BorderStroke(1.dp, FormulaBracketGold),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onRevealFormulaChip() }
                                .testTag("reveal_formula_chip")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Key,
                                    contentDescription = "Formula Chip",
                                    tint = FormulaBracketGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "🔑 Formula: [ Tap to reveal ${step.formulaName ?: "Formula"} ]",
                                    color = FormulaBracketGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // LEVEL 3 HINT: Reveal Full Worked Step
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Stuck? 🔓 Reveal Worked Step",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { onRevealFullStep() }
                            .padding(4.dp)
                            .testTag("reveal_worked_step_button")
                    )
                }
            } else {
                // STEP IS COMPLETED / REVEALED: Show Full Calculation & Next Button
                if (step.calculation != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF071215),
                        border = BorderStroke(1.dp, Color(0xFF1C3A42)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "DETAILED CALCULATION / WORKED STEP:",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = step.calculation,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // UNLOCK NEXT STEP BUTTON
                Button(
                    onClick = onProceedNextStep,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("unlock_next_step_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Next Step (Advance)",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProblemStatementCard(doubt: ProblemDoubt) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("problem_statement_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A3C5E)),
        border = BorderStroke(1.dp, Color(0xFF0284C7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(doubt.subject.primaryColorHex).copy(alpha = 0.2f),
                    border = BorderStroke(0.5.dp, Color(doubt.subject.primaryColorHex))
                ) {
                    Text(
                        text = "${doubt.subject.emoji} ${doubt.subject.displayName}",
                        color = Color(doubt.subject.primaryColorHex),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E233D)
                ) {
                    Text(
                        text = "Attempt → Hint → Reveal",
                        color = Color(0xFFA5B4FC),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = doubt.question,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 22.sp
            )

            // Attached photo if any
            if (doubt.imageUri != null) {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = doubt.imageUri,
                    contentDescription = "Problem image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                )
            }

            // Given values chips
            if (doubt.givenValues.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "GIVEN VALUES:",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    doubt.givenValues.forEach { item ->
                        Text(
                            text = "• $item",
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FinalAnswerCelebrationCard(
    finalAnswer: String,
    onRestart: () -> Unit,
    onBack: () -> Unit,
    onShare: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("final_answer_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E19)),
        border = BorderStroke(1.5.dp, Color(0xFF10B981))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "🎉 Problem Solved!",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "+100 XP Earned · Concept Mastered by Thinking Through It!",
                color = Color(0xFF34D399),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF064E3B),
                border = BorderStroke(1.dp, Color(0xFF10B981)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "FINAL ANSWER:",
                        color = Color(0xFFA7F3D0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = finalAnswer,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onRestart,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("restart_solver_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF34D399))
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry",
                        tint = Color(0xFF34D399),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Try Again", color = Color(0xFF34D399), fontSize = 13.sp)
                }

                if (onShare != null) {
                    Button(
                        onClick = onShare,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("share_solution_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Export Note", color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

private fun shareDoubtSummary(context: Context, doubt: ProblemDoubt) {
    val shareText = buildString {
        appendLine("TRyOURSELF Solved Doubt Note")
        appendLine("Subject: ${doubt.subject.displayName}")
        appendLine("Question: ${doubt.question}")
        appendLine("-------------------------")
        doubt.steps.forEach { step ->
            appendLine("Step ${step.stepNumber}: ${step.title}")
            if (step.formula != null) appendLine("Formula: [ ${step.formula} ]")
            if (step.calculation != null) appendLine("Calculation: ${step.calculation}")
        }
        appendLine("-------------------------")
        appendLine("Final Answer: ${doubt.finalAnswer}")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "TRyOURSELF Study Note")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Share Revision Note"))
}
