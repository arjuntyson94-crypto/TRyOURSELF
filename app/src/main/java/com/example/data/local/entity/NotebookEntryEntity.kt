package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notebook_entries")
data class NotebookEntryEntity(
    @PrimaryKey val id: String,
    val classLevel: String,
    val subject: String,
    val chapterTitle: String,
    val title: String,
    val content: String,
    val formulasJottedJson: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isStarred: Boolean = false
)
