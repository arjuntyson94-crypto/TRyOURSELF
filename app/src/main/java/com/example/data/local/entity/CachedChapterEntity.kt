package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_chapters")
data class CachedChapterEntity(
    @PrimaryKey val id: String,
    val classLevel: String, // CLASS_11 or CLASS_12
    val subject: String,    // MATHS, PHYSICS, CHEMISTRY
    val chapterNumber: Int,
    val title: String,
    val ncertReference: String,
    val weightageMarks: String,
    val summary: String,
    val keyPointsJson: String,
    val formulasJson: String,
    val derivationsJson: String,
    val boardQuestionsJson: String,
    val pdfEstimatedPages: Int = 4,
    val cachedTimestamp: Long = System.currentTimeMillis(),
    val isBookmarked: Boolean = false
)
