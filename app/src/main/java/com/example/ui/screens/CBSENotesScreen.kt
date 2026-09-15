package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.cbse.CBSENotesRepository
import com.example.data.model.CBSEClass
import com.example.data.model.CBSEFormulaHandbookItem
import com.example.data.model.CBSENoteChapter
import com.example.data.model.CacheStatus
import com.example.data.model.StudentNotebookEntry
import com.example.data.model.Subject
import com.example.ui.components.CBSENotesPdfDialog
import com.example.ui.theme.FormulaBracketGold
import com.example.ui.theme.Indigo600
import com.example.ui.util.CBSEPdfExporter
import kotlinx.coroutines.launch
import java.util.UUID

enum class CBSEStudySection(val title: String, val emoji: String) {
    REVISION_NOTES("Revision Notes", "📖"),
    FORMULA_HANDBOOK("Formula Handbook", "⚡")
}

@Composable
fun CBSENotesScreen(
    initialClass: CBSEClass = CBSEClass.CLASS_12,
    onSaveToNotebook: (StudentNotebookEntry) -> Unit,
    cacheStatus: CacheStatus = CacheStatus(),
    onRefreshCache: () -> Unit = {},
    onToggleChapterBookmark: (String, Boolean) -> Unit = { _, _ -> },
    onToggleFormulaFavorite: (String, Boolean) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedClass by remember { mutableStateOf(initialClass) }
    var selectedSubject by remember { mutableStateOf(Subject.ALL) }
    var selectedSection by remember { mutableStateOf(CBSEStudySection.REVISION_NOTES) }
    var searchQuery by remember { mutableStateOf("") }
    var showOnlyBookmarked by remember { mutableStateOf(false) }

    var selectedChapterForDetail by remember { mutableStateOf<CBSENoteChapter?>(null) }
    var chapterForPdfPreview by remember { mutableStateOf<CBSENoteChapter?>(null) }

    // Reactive Flow from Room SQLite Database
    val cachedChapters by CBSENotesRepository.getChaptersFlow(context, selectedClass, selectedSubject)
        .collectAsStateWithLifecycle(initialValue = CBSENotesRepository.getChapters(selectedClass, selectedSubject))

    val cachedFormulas by CBSENotesRepository.getFormulasFlow(context, selectedClass, selectedSubject)
        .collectAsStateWithLifecycle(initialValue = CBSENotesRepository.getAllCuratedFormulas(selectedClass, selectedSubject))

    val chapters = remember(cachedChapters, searchQuery, showOnlyBookmarked) {
        var list = cachedChapters
        if (showOnlyBookmarked) {
            list = list.filter { it.isBookmarked }
        }
        if (searchQuery.isNotBlank()) {
            list = list.filter { ch ->
                ch.title.contains(searchQuery, ignoreCase = true) ||
                        ch.summary.contains(searchQuery, ignoreCase = true) ||
                        ch.formulas.any { it.name.contains(searchQuery, ignoreCase = true) || it.formulaText.contains(searchQuery, ignoreCase = true) } ||
                        ch.keyPoints.any { it.contains(searchQuery, ignoreCase = true) }
            }
        }
        list
    }

    val formulas = remember(cachedFormulas, searchQuery, showOnlyBookmarked) {
        var list = cachedFormulas
        if (showOnlyBookmarked) {
            list = list.filter { it.isFavorite }
        }
        if (searchQuery.isNotBlank()) {
            list = list.filter { item ->
                item.formula.name.contains(searchQuery, ignoreCase = true) ||
                        item.formula.formulaText.contains(searchQuery, ignoreCase = true) ||
                        item.formula.description.contains(searchQuery, ignoreCase = true) ||
                        item.chapterTitle.contains(searchQuery, ignoreCase = true)
            }
        }
        list
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF082F49))
    ) {
        if (selectedChapterForDetail != null) {
            ChapterDetailView(
                chapter = selectedChapterForDetail!!,
                onBack = { selectedChapterForDetail = null },
                onViewPdf = { chapterForPdfPreview = selectedChapterForDetail },
                onSaveToNotebook = onSaveToNotebook
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))

                    // Title Header
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("cbse_notes_header_card"),
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFF064E3B)
                                    ) {
                                        Text(
                                            text = "📚 CBSE CURRICULUM",
                                            color = Color(0xFFD1FAE5),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF422006)
                                    ) {
                                        Text(
                                            text = "NCERT 2025-26",
                                            color = Color(0xFFFEF08A),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF991B1B)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PictureAsPdf,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("PDF Ready", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Class 11 & 12 CBSE Notes",
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Comprehensive chapter-wise NCERT revision notes, formula handbooks with [ formula ] brackets, derivations, and downloadable PDF sheets.",
                                color = Color(0xFFBAE6FD),
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // CLASS 11 vs CLASS 12 SWITCHER
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF071215), RoundedCornerShape(12.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                CBSEClass.entries.forEach { cls ->
                                    val isSelected = selectedClass == cls
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { selectedClass = cls }
                                            .testTag("class_tab_${cls.name}"),
                                        color = if (isSelected) Color(0xFF0EA5E9) else Color.Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 10.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = cls.displayName,
                                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = CircleShape,
                                                color = if (isSelected) Color(0xFF0C4A6E) else Color(0xFF14282D)
                                            ) {
                                                Text(
                                                    text = cls.roman,
                                                    color = if (isSelected) Color(0xFFFEF08A) else Color(0xFF64748B),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // LOCAL OFFLINE CACHE BANNER
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("offline_cache_status_banner"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1E22)),
                        border = BorderStroke(1.dp, Color(0xFF1C3A42))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF064E3B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = "Offline Ready",
                                        tint = Color(0xFF34D399),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "⚡ Local SQLite Offline Cache",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF065F46)
                                        ) {
                                            Text(
                                                text = "NO INTERNET NEEDED",
                                                color = Color(0xFFA7F3D0),
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${cacheStatus.totalChapters} Chapters • ${cacheStatus.totalFormulas} Formulas Stored in Room DB",
                                        color = Color(0xFF93C5FD),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = onRefreshCache,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFF3B82F6)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("verify_cache_btn")
                            ) {
                                Icon(Icons.Default.Cached, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sync", color = Color(0xFF93C5FD), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // STUDY SECTION SELECTOR: REVISION NOTES vs FORMULA HANDBOOK
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF072E47), RoundedCornerShape(14.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CBSEStudySection.entries.forEach { section ->
                            val isSelected = selectedSection == section
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedSection = section }
                                    .testTag("study_section_${section.name}"),
                                color = if (isSelected) Color(0xFF0EA5E9) else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = section.emoji,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = section.title,
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Subject Filters & Bookmarks Toggle
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(Subject.entries) { subj ->
                            val isSelected = selectedSubject == subj
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedSubject = subj },
                                label = {
                                    Text(
                                        text = "${subj.emoji} ${subj.displayName}",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(subj.primaryColorHex),
                                    selectedLabelColor = Color.White,
                                    containerColor = Color(0xFF14282D),
                                    labelColor = Color(0xFFCBD5E1)
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) Color(subj.primaryColorHex) else Color(0xFF1C3A42)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("filter_subject_${subj.name}")
                            )
                        }

                        item {
                            FilterChip(
                                selected = showOnlyBookmarked,
                                onClick = { showOnlyBookmarked = !showOnlyBookmarked },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (showOnlyBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = null,
                                        tint = if (showOnlyBookmarked) Color(0xFFFEF08A) else Color(0xFF94A3B8),
                                        modifier = Modifier.size(15.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = "Bookmarked",
                                        fontSize = 12.sp,
                                        fontWeight = if (showOnlyBookmarked) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF422006),
                                    selectedLabelColor = Color(0xFFFEF08A),
                                    containerColor = Color(0xFF14282D),
                                    labelColor = Color(0xFFCBD5E1)
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (showOnlyBookmarked) Color(0xFFEAB308) else Color(0xFF1C3A42)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("filter_bookmarked")
                            )
                        }
                    }
                }

                // Search Bar
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_notes_input"),
                        placeholder = {
                            Text(
                                text = if (selectedSection == CBSEStudySection.REVISION_NOTES)
                                    "Search offline chapters, key points, or topics..."
                                else
                                    "Search formulas in offline SQLite handbook...",
                                color = Color(0xFF64748B),
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF38BDF8))
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF38BDF8))
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF062438),
                            unfocusedContainerColor = Color(0xFF072E47),
                            focusedBorderColor = Color(0xFF0EA5E9),
                            unfocusedBorderColor = Color(0xFF0284C7),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                }

                // Section Content Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedSection == CBSEStudySection.REVISION_NOTES)
                                "${selectedClass.displayName} Chapters (${chapters.size})"
                            else
                                "Offline Formulas (${formulas.size})",
                            color = Color(0xFFE2E8F0),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Stored locally for offline access",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    }
                }

                // List Items: Either Chapters or Formulas
                if (selectedSection == CBSEStudySection.REVISION_NOTES) {
                    items(chapters) { chapter ->
                        ChapterListItemCard(
                            chapter = chapter,
                            onOpen = { selectedChapterForDetail = chapter },
                            onPdfClick = { chapterForPdfPreview = chapter },
                            onToggleBookmark = {
                                onToggleChapterBookmark(chapter.id, !chapter.isBookmarked)
                            }
                        )
                    }
                } else {
                    items(formulas) { formulaItem ->
                        FormulaHandbookCard(
                            item = formulaItem,
                            onToggleFavorite = {
                                onToggleFormulaFavorite(formulaItem.id, !formulaItem.isFavorite)
                            },
                            onSaveToNotebook = onSaveToNotebook
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }

        // PDF Dialog Preview
        if (chapterForPdfPreview != null) {
            CBSENotesPdfDialog(
                chapter = chapterForPdfPreview!!,
                onDismiss = { chapterForPdfPreview = null }
            )
        }
    }
}

@Composable
private fun FormulaHandbookCard(
    item: CBSEFormulaHandbookItem,
    onToggleFavorite: () -> Unit,
    onSaveToNotebook: (StudentNotebookEntry) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("formula_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A3C5E)),
        border = BorderStroke(1.dp, if (item.isFavorite) Color(0xFFEAB308) else Color(0xFF0284C7))
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
                        color = Color(item.subject.primaryColorHex).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(item.subject.primaryColorHex))
                    ) {
                        Text(
                            text = "${item.subject.emoji} ${item.subject.displayName}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.chapterTitle,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF065F46)
                    ) {
                        Text(
                            text = "OFFLINE READY",
                            color = Color(0xFFA7F3D0),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("fav_btn_${item.id}")
                    ) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (item.isFavorite) Color(0xFFFBBF24) else Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.formula.name,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Bracketed Formula Box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF071215),
                border = BorderStroke(1.dp, Color(0xFFCA8A04)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = item.formula.formulaText,
                        color = Color(0xFFFEF08A),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    if (item.formula.units != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "SI Units / Standard: ${item.formula.units}",
                            color = Color(0xFFBAE6FD),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.formula.description,
                color = Color(0xFFCBD5E1),
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = {
                        onSaveToNotebook(
                            StudentNotebookEntry(
                                id = UUID.randomUUID().toString(),
                                classLevel = item.classLevel,
                                subject = item.subject,
                                chapterTitle = item.chapterTitle,
                                title = item.formula.name,
                                content = "Formula: ${item.formula.formulaText}\n\nDescription: ${item.formula.description}${item.formula.units?.let { "\nUnits: $it" } ?: ""}",
                                formulasJotted = listOf(item.formula.formulaText),
                                timestamp = System.currentTimeMillis(),
                                isStarred = true
                            )
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC7D2FE)),
                    border = BorderStroke(1.dp, Color(0xFF4338CA)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Jot to Notebook", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun ChapterListItemCard(
    chapter: CBSENoteChapter,
    onOpen: () -> Unit,
    onPdfClick: () -> Unit,
    onToggleBookmark: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("chapter_card_${chapter.id}"),
        shape = RoundedCornerShape(16.dp),
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
                        shape = RoundedCornerShape(8.dp),
                        color = Color(chapter.subject.primaryColorHex).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(chapter.subject.primaryColorHex))
                    ) {
                        Text(
                            text = "${chapter.subject.emoji} ${chapter.subject.displayName}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Chapter ${chapter.chapterNumber}",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF065F46)
                    ) {
                        Text(
                            text = "Offline Cached ✓",
                            color = Color(0xFFA7F3D0),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF78350F).copy(alpha = 0.3f),
                        border = BorderStroke(1.dp, Color(0xFFF59E0B))
                    ) {
                        Text(
                            text = chapter.weightageMarks,
                            color = Color(0xFFFDE68A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("chapter_bookmark_btn_${chapter.id}")
                    ) {
                        Icon(
                            imageVector = if (chapter.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark Chapter",
                            tint = if (chapter.isBookmarked) Color(0xFFFDE047) else Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = chapter.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = chapter.summary,
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                lineHeight = 17.sp,
                maxLines = 2
            )

            // Formulas snippet preview
            if (chapter.formulas.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    border = BorderStroke(1.dp, Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡ Key Formula: ",
                            color = Color(0xFFFBBF24),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = chapter.formulas.first().formulaText,
                            color = Color(0xFFFDE68A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onOpen,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0EA5E9))
                ) {
                    Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Read Notes", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onPdfClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171)),
                    border = BorderStroke(1.dp, Color(0xFFEF4444))
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFFF87171))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Notes PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ChapterDetailView(
    chapter: CBSENoteChapter,
    onBack: () -> Unit,
    onViewPdf: () -> Unit,
    onSaveToNotebook: (StudentNotebookEntry) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("📖 Key Notes", "⚡ Formulas", "🎯 Derivations", "📝 Board Q&A")
    var copiedToNotebookMessage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF082F49))
    ) {
        // Top app bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0A3858))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("chapter_detail_back")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column {
                    Text(
                        text = "${chapter.classLevel.displayName} • Ch ${chapter.chapterNumber}",
                        color = Color(0xFFA5B4FC),
                        fontSize = 11.sp
                    )
                    Text(
                        text = chapter.title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onViewPdf,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("detail_view_pdf_btn")
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = {
                        val entry = StudentNotebookEntry(
                            id = UUID.randomUUID().toString(),
                            classLevel = chapter.classLevel,
                            subject = chapter.subject,
                            chapterTitle = chapter.title,
                            title = "Revision: ${chapter.title}",
                            content = "CBSE Class Notes for ${chapter.title}:\n\n${chapter.summary}\n\nKey Concepts:\n" +
                                    chapter.keyPoints.joinToString("\n• ", prefix = "• "),
                            formulasJotted = chapter.formulas.map { it.formulaText },
                            isStarred = true
                        )
                        onSaveToNotebook(entry)
                        copiedToNotebookMessage = true
                    }
                ) {
                    Icon(
                        imageVector = if (copiedToNotebookMessage) Icons.Default.Check else Icons.Default.BookmarkAdd,
                        contentDescription = "Save to Notebook",
                        tint = if (copiedToNotebookMessage) Color(0xFF10B981) else Color(0xFFFBBF24)
                    )
                }
            }
        }

        // Sub Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF072E47),
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFF38BDF8)
                )
            },
            edgePadding = 12.dp
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) Color(0xFFC7D2FE) else Color(0xFF94A3B8)
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // Key Notes & Synopsis
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A3C5E)),
                            border = BorderStroke(1.dp, Color(0xFF0284C7))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Syllabus & Weightage", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF818CF8))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("CBSE Board Marks: ${chapter.weightageMarks}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFDE68A))
                                Text("NCERT Reference: ${chapter.ncertReference}", fontSize = 12.sp, color = Color(0xFF94A3B8))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Chapter Synopsis:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(chapter.summary, fontSize = 13.sp, lineHeight = 19.sp, color = Color(0xFFCBD5E1))
                            }
                        }
                    }

                    item {
                        Text("Core NCERT Concepts:", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    items(chapter.keyPoints) { point ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF171A38),
                            border = BorderStroke(1.dp, Color(0xFF262C52)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp)) {
                                Text("• ", color = Color(0xFF6366F1), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(point, color = Color(0xFFE2E8F0), fontSize = 13.sp, lineHeight = 18.sp)
                            }
                        }
                    }
                }

                1 -> {
                    // Formulas in TRyOURSELF Bracket Format
                    item {
                        Text(
                            text = "CBSE Governing Formulas in [ formula ] Format",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(chapter.formulas) { formula ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF171A38)),
                            border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = formula.name,
                                    color = Color(0xFFFBBF24),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF0F172A),
                                    border = BorderStroke(1.5.dp, Color(0xFFD97706)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = formula.formulaText,
                                        color = Color(0xFFFDE68A),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = formula.description,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )

                                if (formula.units != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "SI Unit: ${formula.units}",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Derivations
                    item {
                        Text(
                            text = "Essential CBSE Derivations & Mechanisms",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(chapter.derivationsOrReactions) { der ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF151833)),
                            border = BorderStroke(1.dp, Color(0xFF3B82F6))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = der,
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                3 -> {
                    // Board Questions
                    item {
                        Text(
                            text = "Repeated CBSE Board Exam Questions",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(chapter.boardQuestions) { q ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF171A38)),
                            border = BorderStroke(1.dp, Color(0xFF2563EB))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("CBSE Board Exam", color = Color(0xFF93C5FD), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(q.yearRepeated, color = Color(0xFFFBBF24), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(q.question, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)

                                Spacer(modifier = Modifier.height(10.dp))

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Model Marking Solution:", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(q.answerSummary, color = Color(0xFFCBD5E1), fontSize = 12.sp, lineHeight = 17.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
