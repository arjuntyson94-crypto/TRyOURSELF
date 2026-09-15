package com.example.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Subject
import com.example.data.model.TopicMastery
import com.example.data.model.UserProfile
import com.example.ui.theme.Emerald500
import com.example.ui.theme.FormulaBracketGold

@Composable
fun ProgressInsightsScreen(
    userProfile: UserProfile,
    onPracticeTopic: ((String, Subject) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val sampleMasteries = listOf(
        TopicMastery(Subject.PHYSICS, "Kinematics & Equations of Motion", 85, isWeakArea = false, doubtsCount = 4),
        TopicMastery(Subject.MATHS, "Quadratic Equations & Discriminant", 90, isWeakArea = false, doubtsCount = 5),
        TopicMastery(Subject.CHEMISTRY, "Stoichiometry & Molar Mass", 70, isWeakArea = false, doubtsCount = 2),
        TopicMastery(Subject.PHYSICS, "Force & Friction Vector Diagrams", 45, isWeakArea = true, doubtsCount = 3),
        TopicMastery(Subject.MATHS, "Trigonometric Identites", 50, isWeakArea = true, doubtsCount = 2),
        TopicMastery(Subject.CHEMISTRY, "pH & Ionic Equilibrium", 75, isWeakArea = false, doubtsCount = 3)
    )

    val weakAreas = sampleMasteries.filter { it.isWeakArea }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF071215))
            .padding(horizontal = 16.dp)
            .testTag("progress_insights_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))

            // Gamification & Streak Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("insights_streak_banner"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1E22)),
                border = BorderStroke(1.dp, Color(0xFF0284C7))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "LEARNING MOMENTUM",
                                color = Color(0xFFBAE6FD),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${userProfile.streakDays} Day Streak 🔥",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Brush.radialGradient(listOf(Color(0xFFEF4444), Color(0xFFB91C1C)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Fire Streak",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatItem(label = "Total XP", value = "${userProfile.xp} XP", color = FormulaBracketGold)
                        StatItem(label = "Curriculum", value = userProfile.curriculumBoard, color = Color(0xFF38BDF8))
                        StatItem(label = "Grade Level", value = userProfile.grade, color = Color(0xFFFEF08A))
                    }
                }
            }
        }

        // Badges Showcase
        item {
            Text(
                text = "ACHIEVEMENT BADGES",
                color = Color(0xFFBAE6FD),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val badges = listOf(
                    Triple("First Spark", "⚡", "Solved first doubt independently"),
                    Triple("Formula Alchemist", "🔑", "Applied formulas without hints"),
                    Triple("Persistent Thinker", "🧠", "Attempted 3 steps without giving up"),
                    Triple("Streak Titan", "🔥", "Maintained 5-day streak")
                )
                items(badges) { (name, emoji, desc) ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF0E1E22),
                        border = BorderStroke(1.dp, Color(0xFF1C3A42)),
                        modifier = Modifier.width(150.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = emoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = name,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = desc,
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                lineHeight = 13.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        // Weak-Area Flags Section
        if (weakAreas.isNotEmpty()) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Flag",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ATTENTION REQUIRED (WEAK AREAS)",
                        color = Color(0xFFF87171),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    weakAreas.forEach { area ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF221115),
                            border = BorderStroke(1.dp, Color(0xFF7F1D1D)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${area.subject.emoji} ${area.topicName}",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Current mastery: ${area.scorePercentage}% · Multiple worked reveals used",
                                        color = Color(0xFFFCA5A5),
                                        fontSize = 11.sp
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF991B1B),
                                    modifier = Modifier.clickable {
                                        onPracticeTopic?.invoke(area.topicName, area.subject)
                                    }
                                ) {
                                    Text(
                                        text = "Practice",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Topic Mastery Heatmap
        item {
            Text(
                text = "TOPIC MASTERY HEATMAP",
                color = Color(0xFFBAE6FD),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                sampleMasteries.forEach { mastery ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0E1E22),
                        border = BorderStroke(1.dp, Color(0xFF1C3A42)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${mastery.subject.emoji} ${mastery.topicName}",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${mastery.scorePercentage}%",
                                    color = if (mastery.scorePercentage >= 80) Emerald500 else if (mastery.scorePercentage >= 60) FormulaBracketGold else Color(0xFFEF4444),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { mastery.scorePercentage / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = if (mastery.scorePercentage >= 80) Emerald500 else if (mastery.scorePercentage >= 60) FormulaBracketGold else Color(0xFFEF4444),
                                trackColor = Color(0xFF1E2442)
                            )
                        }
                    }
                }
            }
        }

        // Parent / Teacher Report Export Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("parent_report_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E28)),
                border = BorderStroke(1.dp, Color(0xFF0284C7))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Report",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Parent & Teacher Progress Summary",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Export an objective, privacy-safe analytics digest (hints used, attempts made, concepts practiced) without exposing raw questions or student conversation.",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            exportParentReport(context, userProfile, sampleMasteries)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("export_report_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Export",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export Progress Digest", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color(0xFF94A3B8),
            fontSize = 11.sp
        )
    }
}

private fun exportParentReport(context: Context, profile: UserProfile, masteries: List<TopicMastery>) {
    val report = buildString {
        appendLine("TRyOURSELF — Student Learning Progress Digest")
        appendLine("Student Grade: ${profile.grade} (${profile.curriculumBoard})")
        appendLine("Active Daily Streak: ${profile.streakDays} Days")
        appendLine("Total Earned Experience: ${profile.xp} XP")
        appendLine("--------------------------------------")
        appendLine("TOPIC MASTERY HIGHLIGHTS:")
        masteries.forEach { m ->
            val flag = if (m.isWeakArea) " [Needs Revision]" else " [Strong]"
            appendLine("• ${m.topicName} (${m.subject.displayName}): ${m.scorePercentage}%$flag")
        }
        appendLine("--------------------------------------")
        appendLine("Privacy Notice: DPDP compliant. No personal chat records or unmoderated logs are shared.")
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "TRyOURSELF Student Progress Digest")
        putExtra(Intent.EXTRA_TEXT, report)
    }
    context.startActivity(Intent.createChooser(intent, "Share Parent Report"))
}
