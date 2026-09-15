package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.NotebookEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotebookDao {
    @Query("SELECT * FROM notebook_entries ORDER BY timestamp DESC")
    fun getAllNotesFlow(): Flow<List<NotebookEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NotebookEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NotebookEntryEntity>)

    @Query("DELETE FROM notebook_entries WHERE id = :id")
    suspend fun deleteNoteById(id: String)

    @Query("UPDATE notebook_entries SET isStarred = :isStarred WHERE id = :id")
    suspend fun updateStarred(id: String, isStarred: Boolean)

    @Query("SELECT COUNT(*) FROM notebook_entries")
    suspend fun getNotesCount(): Int
}
