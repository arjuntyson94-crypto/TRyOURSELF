package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Subject
import com.example.data.model.UserProfile
import com.example.ui.theme.FormulaBracketGold
import com.example.ui.theme.Indigo600

@Composable
fun OnboardingDialog(
    initialProfile: UserProfile,
    onSaveProfile: (UserProfile) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedGrade by remember { mutableStateOf(initialProfile.grade) }
    var selectedBoard by remember { mutableStateOf(initialProfile.curriculumBoard) }
    var isParentLinked by remember { mutableStateOf(initialProfile.parentLinked) }
    var childPrivacyActive by remember { mutableStateOf(initialProfile.isChildPrivacyActive) }

    val gradeOptions = listOf("Class 8", "Class 9", "Class 10", "Class 11", "Class 12")
    val boardOptions = listOf("CBSE", "ICSE", "State Board", "IB / Cambridge", "JEE / NEET")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, Color(0xFF0EA5E9), RoundedCornerShape(24.dp))
                .testTag("onboarding_dialog"),
            color = Color(0xFF0A3C5E)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                TryYourselfBrandIcon(size = 48.dp)

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Welcome to TRyOURSELF",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Set up your student profile for personalized hints and step-gated problem solving.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Section 1: Grade Level
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "YOUR GRADE / CLASS",
                        color = Color(0xFFBAE6FD),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(gradeOptions) { grade ->
                            val isSelected = selectedGrade == grade
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFF0EA5E9) else Color(0xFF14282D),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) Color(0xFF38BDF8) else Color(0xFF1C3A42)
                                ),
                                modifier = Modifier
                                    .clickable { selectedGrade = grade }
                                    .testTag("onboarding_grade_$grade")
                            ) {
                                Text(
                                    text = grade,
                                    color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Curriculum / Board
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "CURRICULUM / BOARD",
                        color = Color(0xFFBAE6FD),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        boardOptions.forEach { board ->
                            val isSelected = selectedBoard == board
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFF0C4A6E) else Color(0xFF14282D),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) Color(0xFF0EA5E9) else Color(0xFF1C3A42)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedBoard = board }
                                    .testTag("onboarding_board_$board")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = board,
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = FormulaBracketGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Child Safety & Privacy (DPDP Act Compliance)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF071215),
                    border = BorderStroke(1.dp, Color(0xFF059669)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Child Privacy",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Child Safety & DPDP Privacy Protection",
                                color = Color(0xFFE2E8F0),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "Zero behavioral advertising, under-13 compliant, and all solution data stored privately on device.",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            lineHeight = 14.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Optional Parent-Linked Reports",
                                color = Color(0xFFCBD5E1),
                                fontSize = 12.sp
                            )
                            Switch(
                                checked = isParentLinked,
                                onCheckedChange = { isParentLinked = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF10B981)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Save & Continue Button
                Button(
                    onClick = {
                        onSaveProfile(
                            initialProfile.copy(
                                grade = selectedGrade,
                                curriculumBoard = selectedBoard,
                                parentLinked = isParentLinked,
                                isChildPrivacyActive = childPrivacyActive
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("onboarding_save_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text(
                        text = "Save & Start Learning",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
