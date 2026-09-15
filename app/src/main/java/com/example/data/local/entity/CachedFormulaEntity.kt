package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_formulas")
data class CachedFormulaEntity(
    @PrimaryKey val id: String,
    val chapterId: String,
    val chapterTitle: String,
    val classLevel: String, // CLASS_11 or CLASS_12
    val subject: String,    // MATHS, PHYSICS, CHEMISTRY
    val name: String,
    val formulaText: String,
    val description: String,
    val units: String? = null,
    val isFavorite: Boolean = false,
    val cachedTimestamp: Long = System.currentTimeMillis()
)
