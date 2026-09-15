package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CBSEClass
import com.example.data.model.StudentNotebookEntry
import com.example.data.model.Subject
import com.example.ui.util.CBSEPdfExporter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun CBSENotebookScreen(
    notes: List<StudentNotebookEntry>,
    onSaveNote: (StudentNotebookEntry) -> Unit,
    onDeleteNote: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var filterClass by remember { mutableStateOf<CBSEClass?>(null) }
    var filterSubject by remember { mutableStateOf(Subject.ALL) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<StudentNotebookEntry?>(null) }

    val filteredNotes = remember(notes, filterClass, filterSubject) {
        notes.filter { note ->
            (filterClass == null || note.classLevel == filterClass) &&
                    (filterSubject == Subject.ALL || note.subject == filterSubject)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF082F49))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                // Header Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cbse_notebook_header"),
                    shape = RoundedCornerShape(20.dp),
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
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF064E3B)
                            ) {
                                Text(
                                    text = "📝 CBSE NOTEBOOK",
                                    color = Color(0xFFD1FAE5),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = "${notes.size} Saved Notes",
                                color = Color(0xFFFEF08A),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "My CBSE Revision Notebook",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Your personal notes, formula cheat sheets, and board exam tips for Class 11 and Class 12. Export any note as a PDF with one tap.",
                            color = Color(0xFFBAE6FD),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF064E3B).copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, Color(0xFF059669))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⚡ Stored in Local Room SQLite Database",
                                    color = Color(0xFFA7F3D0),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• Available 100% Offline",
                                    color = Color(0xFF6EE7B7),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Filter by Class
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (filterClass == null) Color(0xFF0EA5E9) else Color(0xFF14282D),
                                modifier = Modifier
                                    .clickable { filterClass = null }
                                    .testTag("filter_class_all")
                            ) {
                                Text(
                                    text = "All Classes",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }

                            CBSEClass.entries.forEach { cls ->
                                val isSel = filterClass == cls
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSel) Color(0xFF0EA5E9) else Color(0xFF072E47),
                                    modifier = Modifier
                                        .clickable { filterClass = cls }
                                        .testTag("filter_class_${cls.name}")
                                ) {
                                    Text(
                                        text = cls.displayName,
                                        color = if (isSel) Color.White else Color(0xFFCBD5E1),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Subject Filter row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(Subject.entries) { subj ->
                        val isSel = filterSubject == subj
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) Color(subj.primaryColorHex) else Color(0xFF072E47),
                            border = BorderStroke(1.dp, if (isSel) Color(subj.primaryColorHex) else Color(0xFF0284C7)),
                            modifier = Modifier.clickable { filterSubject = subj }
                        ) {
                            Text(
                                text = "${subj.emoji} ${subj.displayName}",
                                color = if (isSel) Color.White else Color(0xFFCBD5E1),
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            if (filteredNotes.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A3C5E))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📓", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No notes saved for this filter yet",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap '+ New Note' to create your first CBSE revision sheet or bookmark formulas from the Notes tab!",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(filteredNotes, key = { it.id }) { note ->
                    StudentNoteCard(
                        note = note,
                        onEdit = {
                            editingNote = note
                            showAddNoteDialog = true
                        },
                        onDelete = { onDeleteNote(note.id) },
                        onToggleStar = {
                            onSaveNote(note.copy(isStarred = !note.isStarred))
                        },
                        onExportPdf = {
                            val text = CBSEPdfExporter.generateNotebookPdfText(note)
                            CBSEPdfExporter.shareOrDownloadPdf(
                                context = context,
                                fileName = "CBSE_Notebook_${note.classLevel.name}_${note.title.take(20)}",
                                documentText = text
                            )
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // FAB to add new note
        FloatingActionButton(
            onClick = {
                editingNote = null
                showAddNoteDialog = true
            },
            containerColor = Color(0xFF10B981),
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_notebook_note_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Note")
                Spacer(modifier = Modifier.width(6.dp))
                Text("New Note", fontWeight = FontWeight.Bold)
            }
        }

        // Add / Edit Note Dialog
        if (showAddNoteDialog) {
            AddOrEditNoteDialog(
                initialNote = editingNote,
                onDismiss = {
                    showAddNoteDialog = false
                    editingNote = null
                },
                onSave = { saved ->
                    onSaveNote(saved)
                    showAddNoteDialog = false
                    editingNote = null
                }
            )
        }
    }
}

@Composable
private fun StudentNoteCard(
    note: StudentNotebookEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleStar: () -> Unit,
    onExportPdf: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("student_note_card_${note.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A3C5E)),
        border = BorderStroke(1.dp, if (note.isStarred) Color(0xFFEAB308) else Color(0xFF0284C7))
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
                        color = Color(note.subject.primaryColorHex).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(note.subject.primaryColorHex))
                    ) {
                        Text(
                            text = "${note.classLevel.displayName} • ${note.subject.displayName}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = note.chapterTitle,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = onToggleStar,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (note.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Star",
                        tint = if (note.isStarred) Color(0xFFFBBF24) else Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = note.content,
                color = Color(0xFFCBD5E1),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            if (note.formulasJotted.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF062438), RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Jotted Formulas:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFEF08A))
                    note.formulasJotted.forEach { f ->
                        Text(
                            text = f,
                            color = Color(0xFFFEF08A),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(note.timestamp)),
                    color = Color(0xFF64748B),
                    fontSize = 11.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(onClick = onExportPdf, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF", tint = Color(0xFFF87171))
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF38BDF8))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                    }
                }
            }
        }
    }
}

@Composable
private fun AddOrEditNoteDialog(
    initialNote: StudentNotebookEntry?,
    onDismiss: () -> Unit,
    onSave: (StudentNotebookEntry) -> Unit
) {
    var title by remember { mutableStateOf(initialNote?.title ?: "") }
    var chapterTitle by remember { mutableStateOf(initialNote?.chapterTitle ?: "General Revision") }
    var content by remember { mutableStateOf(initialNote?.content ?: "") }
    var formulasInput by remember { mutableStateOf(initialNote?.formulasJotted?.joinToString("\n") ?: "") }
    var selectedClass by remember { mutableStateOf(initialNote?.classLevel ?: CBSEClass.CLASS_12) }
    var selectedSubject by remember { mutableStateOf(initialNote?.subject ?: Subject.MATHS) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialNote == null) "New CBSE Notebook Entry" else "Edit Note",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Class selector
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CBSEClass.entries.forEach { cls ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedClass == cls) Color(0xFF0EA5E9) else Color(0xFF14282D),
                            modifier = Modifier.clickable { selectedClass = cls }
                        ) {
                            Text(
                                text = cls.displayName,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Subject selector
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(Subject.entries.filter { it != Subject.ALL }) { subj ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedSubject == subj) Color(subj.primaryColorHex) else Color(0xFF1E293B),
                            modifier = Modifier.clickable { selectedSubject = subj }
                        ) {
                            Text(
                                text = "${subj.emoji} ${subj.displayName}",
                                color = Color.White,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = chapterTitle,
                    onValueChange = { chapterTitle = it },
                    label = { Text("Chapter Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Revision Notes / Key Concepts") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = formulasInput,
                    onValueChange = { formulasInput = it },
                    label = { Text("Formulas (one per line, e.g. [ v = u + at ])") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val parsedFormulas = formulasInput.lines()
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                            .map { if (it.startsWith("[") && it.endsWith("]")) it else "[ $it ]" }

                        val note = StudentNotebookEntry(
                            id = initialNote?.id ?: UUID.randomUUID().toString(),
                            classLevel = selectedClass,
                            subject = selectedSubject,
                            chapterTitle = chapterTitle.ifBlank { "General Chapter" },
                            title = title,
                            content = content,
                            formulasJotted = parsedFormulas,
                            timestamp = System.currentTimeMillis(),
                            isStarred = initialNote?.isStarred ?: false
                        )
                        onSave(note)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Text("Save Note")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF94A3B8))
            }
        },
        containerColor = Color(0xFF0A3C5E)
    )
}
