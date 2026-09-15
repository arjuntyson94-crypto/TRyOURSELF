package com.example.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ProblemDoubt
import com.example.data.model.Subject
import com.example.ui.components.AttachmentOptionsSheet
import com.example.ui.components.HighlightedPlusButton
import com.example.ui.components.TryYourselfBrandIcon
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.FormulaBracketGold
import com.example.ui.theme.Indigo600
import com.example.ui.theme.Sky600
import com.example.ui.theme.Violet600
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.HomeUiState
import java.io.File
import java.io.FileOutputStream

@Composable
fun HomeScreen(
    state: HomeUiState,
    onQueryChange: (String) -> Unit,
    onSubjectSelect: (Subject) -> Unit,
    onFilterSubject: (Subject) -> Unit,
    onOpenAttachmentSheet: () -> Unit,
    onCloseAttachmentSheet: () -> Unit,
    onImageAttached: (Uri) -> Unit,
    onFileAttached: (Uri, String?) -> Unit,
    onRemoveAttachment: () -> Unit,
    onSubmit: () -> Unit,
    onSelectDoubt: (ProblemDoubt) -> Unit,
    onNavigateToTab: ((AppTab) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Photo picker (Gallery)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onImageAttached(uri)
        }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            try {
                val tempFile = File(context.cacheDir, "camera_capture_${System.currentTimeMillis()}.jpg")
                FileOutputStream(tempFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                onImageAttached(Uri.fromFile(tempFile))
            } catch (e: Exception) {
                // handle error safely
            }
        }
    }

    // Generic file picker
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment ?: "document.pdf"
            onFileAttached(uri, fileName)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF082F49))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))

            // TOP HERO: Friendly greeting with "what's my task today "
            HeroTaskGreetingCard(
                onQuickPrompt = { prompt, subj ->
                    onSubjectSelect(subj)
                    onQueryChange(prompt)
                }
            )
        }

        if (onNavigateToTab != null) {
            item {
                CbseNotesQuickAccessBanner(
                    onSelectClass11 = { onNavigateToTab(AppTab.CLASS_11) },
                    onSelectClass12 = { onNavigateToTab(AppTab.CLASS_12) },
                    onSelectNotebook = { onNavigateToTab(AppTab.NOTEBOOK) }
                )
            }
        }

        item {
            // QUERY TABLE SECTION: Where students ask their doubts with highlighted + symbol
            QueryTableInputCard(
                queryText = state.queryText,
                onQueryChange = onQueryChange,
                selectedSubject = state.selectedSubject,
                onSubjectSelect = onSubjectSelect,
                attachedImageUri = state.attachedImageUri,
                attachedFileName = state.attachedFileName,
                onHighlightPlusClick = onOpenAttachmentSheet,
                onRemoveAttachment = onRemoveAttachment,
                onSubmit = onSubmit
            )
        }

        item {
            // QUERY TABLE DIRECTORY / RECENT DOUBTS
            DoubtsTableSection(
                doubts = state.doubtsTable,
                filterSubject = state.filterSubject,
                onFilterSubject = onFilterSubject,
                onSelectDoubt = onSelectDoubt
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (state.isAttachmentSheetOpen) {
        AttachmentOptionsSheet(
            onDismiss = onCloseAttachmentSheet,
            onTakePhoto = { cameraLauncher.launch() },
            onPickImage = { photoPickerLauncher.launch("image/*") },
            onPickFile = { filePickerLauncher.launch("*/*") }
        )
    }
}

@Composable
private fun HeroTaskGreetingCard(
    onQuickPrompt: (String, Subject) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_greeting_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0A3C5E)
        ),
        border = BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(
                listOf(Color(0xFF10B981), Color(0xFF0EA5E9), Color(0xFFEAB308))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF064E3B),
                    border = BorderStroke(1.dp, Color(0xFF10B981))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TRyOURSELF • STEM Homework Companion",
                            color = Color(0xFFD1FAE5),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = Color(0xFF422006),
                    border = BorderStroke(1.dp, Color(0xFFCA8A04))
                ) {
                    Text(
                        text = "🚀 Kids & Teens",
                        color = Color(0xFFFDE047),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // MANDATORY GREETING MESSAGE: "what's my task today " with brand icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "what's my task today ",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        modifier = Modifier.testTag("greeting_task_message")
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "CBSE Class 11 & 12 • Smart STEM Guidance",
                        color = Color(0xFF38BDF8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                TryYourselfBrandIcon(
                    size = 52.dp,
                    modifier = Modifier.testTag("hero_brand_icon")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Got a tough problem in Maths, Physics, or Chemistry? Ask below, think through each step, and try the formulas yourself!",
                color = Color(0xFFBAE6FD),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quick topic chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                QuickTopicChip("📐 Maths Roots", Color(0xFFEAB308)) {
                    onQuickPrompt("Find roots of 2x² - 4x - 6 = 0 using quadratic formula", Subject.MATHS)
                }
                QuickTopicChip("⚡ Physics Velocity", Color(0xFF0EA5E9)) {
                    onQuickPrompt("Find final velocity: car accelerates from rest at 2 m/s² for 5 seconds", Subject.PHYSICS)
                }
                QuickTopicChip("🧪 Gas Laws", Color(0xFF10B981)) {
                    onQuickPrompt("Boyle's law: Gas at 1.5 atm, 4L compressed to 2L. Find pressure.", Subject.CHEMISTRY)
                }
            }
        }
    }
}

@Composable
private fun QuickTopicChip(label: String, color: Color, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun QueryTableInputCard(
    queryText: String,
    onQueryChange: (String) -> Unit,
    selectedSubject: Subject,
    onSubjectSelect: (Subject) -> Unit,
    attachedImageUri: Uri?,
    attachedFileName: String?,
    onHighlightPlusClick: () -> Unit,
    onRemoveAttachment: () -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("query_table_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A3C5E)),
        border = BorderStroke(1.dp, Color(0xFF0284C7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Query Table • Ask Your Doubt",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                // Highlighted + button directly in the section header as requested
                HighlightedPlusButton(
                    onClick = onHighlightPlusClick,
                    showLabel = true
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subject selector
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(Subject.MATHS, Subject.PHYSICS, Subject.CHEMISTRY).forEach { subject ->
                    val isSelected = selectedSubject == subject
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(subject.primaryColorHex) else Color(0xFF072E47),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) Color.White.copy(alpha = 0.5f) else Color(0xFF0284C7)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSubjectSelect(subject) }
                            .testTag("subject_chip_${subject.name.lowercase()}")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = subject.emoji, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = subject.displayName,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Query Text Field
            OutlinedTextField(
                value = queryText,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = "Ask your doubt (e.g., 'Find velocity if acceleration is 3 m/s² for 4s' or snap a photo with +)",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("query_input_field"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color(0xFFF1F5F9),
                    focusedContainerColor = Color(0xFF062438),
                    unfocusedContainerColor = Color(0xFF072E47),
                    focusedBorderColor = Color(0xFF38BDF8),
                    unfocusedBorderColor = Color(0xFF0284C7)
                ),
                minLines = 3,
                maxLines = 6
            )

            // Attachment preview (if student snapped photo or uploaded file)
            AnimatedVisibility(visible = attachedImageUri != null || attachedFileName != null) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0C4A6E),
                        border = BorderStroke(1.dp, Color(0xFF0284C7)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (attachedImageUri != null) {
                                AsyncImage(
                                    model = attachedImageUri,
                                    contentDescription = "Attached Doubt Photo",
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF064E3B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = null,
                                        tint = Color(0xFF34D399)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = attachedFileName ?: "Attached Homework Photo",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Ready to analyze with question",
                                    color = Emerald500,
                                    fontSize = 11.sp
                                )
                            }

                            IconButton(
                                onClick = onRemoveAttachment,
                                modifier = Modifier.testTag("remove_attachment_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove attachment",
                                    tint = Color(0xFFF87171)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Row: Highlighted + shortcut and Primary "Solve Step-by-Step"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Circular highlighted + button
                HighlightedPlusButton(
                    onClick = onHighlightPlusClick,
                    showLabel = false
                )

                Button(
                    onClick = onSubmit,
                    enabled = queryText.isNotBlank() || attachedImageUri != null || attachedFileName != null,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("submit_query_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF14282D),
                        disabledContentColor = Color(0xFF64748B)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TRyOURSELF Step-by-Step",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DoubtsTableSection(
    doubts: List<ProblemDoubt>,
    filterSubject: Subject,
    onFilterSubject: (Subject) -> Unit,
    onSelectDoubt: (ProblemDoubt) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Query Table • Doubt Archive",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Review problems and practice step-by-step solutions",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter chips: All, Maths, Physics, Chemistry
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(Subject.entries) { subject ->
                val isSelected = filterSubject == subject
                FilterChip(
                    selected = isSelected,
                    onClick = { onFilterSubject(subject) },
                    label = {
                        Text(
                            text = "${subject.emoji} ${subject.displayName}",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(subject.primaryColorHex),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF072E47),
                        labelColor = Color(0xFFCBD5E1)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) Color.White.copy(alpha = 0.4f) else Color(0xFF0284C7)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val filteredList = if (filterSubject == Subject.ALL) {
            doubts
        } else {
            doubts.filter { it.subject == filterSubject }
        }

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0A3C5E))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No doubts recorded for this subject yet. Ask your question above!",
                    color = Color(0xFF64748B),
                    fontSize = 13.sp
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                filteredList.forEach { doubt ->
                    DoubtTableRowCard(doubt = doubt, onClick = { onSelectDoubt(doubt) })
                }
            }
        }
    }
}

@Composable
private fun DoubtTableRowCard(
    doubt: ProblemDoubt,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("doubt_item_${doubt.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A3C5E)),
        border = BorderStroke(1.dp, Color(0xFF0284C7))
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
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(doubt.subject.primaryColorHex).copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, Color(doubt.subject.primaryColorHex).copy(alpha = 0.6f))
                ) {
                    Text(
                        text = "${doubt.subject.emoji} ${doubt.subject.displayName}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0C4A6E)
                    ) {
                        Text(
                            text = "${doubt.steps.size} Steps",
                            color = Color(0xFFBAE6FD),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // Count formulas in this problem
                    val formulaCount = doubt.steps.count { !it.formula.isNullOrBlank() }
                    if (formulaCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF422006)
                        ) {
                            Text(
                                text = "[$formulaCount Formulas]",
                                color = FormulaBracketGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = doubt.question,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Tap to solve step-by-step ➔",
                    color = Color(0xFF38BDF8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                if (doubt.isSample) {
                    Text(
                        text = "⭐ Practice Problem",
                        color = Color(0xFFEAB308),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun CbseNotesQuickAccessBanner(
    onSelectClass11: () -> Unit,
    onSelectClass12: () -> Unit,
    onSelectNotebook: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("cbse_quick_access_card"),
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF064E3B)
                    ) {
                        Text(
                            text = "CBSE NCERT",
                            color = Color(0xFFD1FAE5),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Class 11 & 12 Notebook & Notes PDF",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Official CBSE syllabus notes with formulas in [ formula ] format, derivations, and exportable PDFs.",
                color = Color(0xFFBAE6FD),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF0EA5E9),
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onSelectClass11)
                        .testTag("quick_access_class_11")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 9.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📚 Class 11", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF10B981),
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onSelectClass12)
                        .testTag("quick_access_class_12")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 9.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📖 Class 12", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFCA8A04),
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onSelectNotebook)
                        .testTag("quick_access_notebook")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 9.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📝 Notebook", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

