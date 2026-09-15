package com.example.data.model

enum class CBSEClass(val displayName: String, val roman: String, val subtitle: String) {
    CLASS_11("Class 11", "XI", "Higher Secondary • Foundation for JEE / NEET / Boards"),
    CLASS_12("Class 12", "XII", "Senior Secondary • CBSE Board Examination & Entrance")
}

data class CBSEFormula(
    val name: String,
    val formulaText: String, // e.g. "[ v = u + at ]"
    val description: String,
    val units: String? = null
)

data class CBSEBoardQuestion(
    val question: String,
    val yearRepeated: String, // e.g. "CBSE 2023, 2020, 2018 (3 Marks)"
    val answerSummary: String
)

data class CBSENoteChapter(
    val id: String,
    val classLevel: CBSEClass,
    val subject: Subject,
    val chapterNumber: Int,
    val title: String,
    val ncertReference: String,
    val weightageMarks: String,
    val summary: String,
    val keyPoints: List<String>,
    val formulas: List<CBSEFormula>,
    val derivationsOrReactions: List<String>,
    val boardQuestions: List<CBSEBoardQuestion>,
    val pdfEstimatedPages: Int = 4,
    val isBookmarked: Boolean = false,
    val cachedTimestamp: Long = System.currentTimeMillis()
)

data class CBSEFormulaHandbookItem(
    val id: String,
    val chapterId: String,
    val chapterTitle: String,
    val classLevel: CBSEClass,
    val subject: Subject,
    val formula: CBSEFormula,
    val isFavorite: Boolean = false
)

data class CacheStatus(
    val totalChapters: Int = 0,
    val totalFormulas: Int = 0,
    val lastCachedTimestamp: Long = System.currentTimeMillis(),
    val isOfflineReady: Boolean = true,
    val storageType: String = "Room SQLite Cache"
)

data class StudentNotebookEntry(
    val id: String,
    val classLevel: CBSEClass,
    val subject: Subject,
    val chapterTitle: String,
    val title: String,
    val content: String,
    val formulasJotted: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val isStarred: Boolean = false
)
