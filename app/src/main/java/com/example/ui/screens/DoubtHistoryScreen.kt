package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DoubtStatus
import com.example.data.model.ProblemDoubt
import com.example.data.model.Subject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DoubtHistoryScreen(
    doubts: List<ProblemDoubt>,
    onSelectDoubt: (ProblemDoubt) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSubjectFilter by remember { mutableStateOf(Subject.ALL) }
    var selectedStatusFilter by remember { mutableStateOf<DoubtStatus?>(null) }

    val filteredDoubts = doubts.filter { doubt ->
        val matchesSearch = searchQuery.isBlank() ||
                doubt.question.contains(searchQuery, ignoreCase = true) ||
                doubt.topic.contains(searchQuery, ignoreCase = true) ||
                doubt.finalAnswer.contains(searchQuery, ignoreCase = true)
        val matchesSubject = selectedSubjectFilter == Subject.ALL || doubt.subject == selectedSubjectFilter
        val matchesStatus = selectedStatusFilter == null || doubt.status == selectedStatusFilter
        matchesSearch && matchesSubject && matchesStatus
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF071215))
            .padding(horizontal = 16.dp)
            .testTag("doubt_history_screen")
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Title Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "History",
                tint = Color(0xFF0EA5E9),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Doubt History Table",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${filteredDoubts.size} total questions recorded",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search doubts, topics, or formulas...", color = Color(0xFF64748B), fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF0EA5E9)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("history_search_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF0E1E22),
                unfocusedContainerColor = Color(0xFF0E1E22),
                focusedBorderColor = Color(0xFF0EA5E9),
                unfocusedBorderColor = Color(0xFF1C3A42),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Subject Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(Subject.entries) { subject ->
                val isSelected = selectedSubjectFilter == subject
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) Color(subject.primaryColorHex) else Color(0xFF14282D),
                    border = BorderStroke(1.dp, if (isSelected) Color.White.copy(alpha = 0.5f) else Color(0xFF1C3A42)),
                    modifier = Modifier
                        .clickable { selectedSubjectFilter = subject }
                        .testTag("filter_subject_${subject.name}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = subject.emoji, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = subject.displayName,
                            color = if (isSelected) Color.White else Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Status Filter Chips (All / Solved / In Progress)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val isAll = selectedStatusFilter == null
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isAll) Color(0xFF0C4A6E) else Color(0xFF14282D),
                border = BorderStroke(1.dp, if (isAll) Color(0xFF0EA5E9) else Color(0xFF1C3A42)),
                modifier = Modifier.clickable { selectedStatusFilter = null }
            ) {
                Text(
                    text = "All Status",
                    color = if (isAll) Color.White else Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            DoubtStatus.entries.forEach { status ->
                val isSelected = selectedStatusFilter == status
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) Color(status.colorHex).copy(alpha = 0.3f) else Color(0xFF14282D),
                    border = BorderStroke(1.dp, if (isSelected) Color(status.colorHex) else Color(0xFF1C3A42)),
                    modifier = Modifier.clickable { selectedStatusFilter = status }
                ) {
                    Text(
                        text = status.displayName,
                        color = if (isSelected) Color(status.colorHex) else Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Doubt List Table
        if (filteredDoubts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "No doubts found matching criteria.",
                    color = Color(0xFF64748B),
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredDoubts) { doubt ->
                    DoubtRowCard(
                        doubt = doubt,
                        onClick = { onSelectDoubt(doubt) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun DoubtRowCard(
    doubt: ProblemDoubt,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }
    val formattedDate = remember(doubt.timestamp) { dateFormat.format(Date(doubt.timestamp)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("doubt_row_${doubt.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1E22)),
        border = BorderStroke(1.dp, Color(0xFF1C3A42))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(doubt.subject.primaryColorHex).copy(alpha = 0.2f),
                        border = BorderStroke(0.5.dp, Color(doubt.subject.primaryColorHex))
                    ) {
                        Text(
                            text = "${doubt.subject.emoji} ${doubt.subject.displayName}",
                            color = Color(doubt.subject.primaryColorHex),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = doubt.topic,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(doubt.status.colorHex).copy(alpha = 0.15f),
                    border = BorderStroke(0.5.dp, Color(doubt.status.colorHex))
                ) {
                    Text(
                        text = doubt.status.displayName,
                        color = Color(doubt.status.colorHex),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = doubt.question,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${doubt.steps.size} guided steps · $formattedDate",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Resume",
                        color = Color(0xFF0EA5E9),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Resume",
                        tint = Color(0xFF0EA5E9),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
