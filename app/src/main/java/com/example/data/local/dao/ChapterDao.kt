package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.CachedChapterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Query("SELECT * FROM cached_chapters ORDER BY chapterNumber ASC")
    fun getAllChaptersFlow(): Flow<List<CachedChapterEntity>>

    @Query("SELECT * FROM cached_chapters WHERE classLevel = :classLevel ORDER BY chapterNumber ASC")
    fun getChaptersByClassFlow(classLevel: String): Flow<List<CachedChapterEntity>>

    @Query("SELECT * FROM cached_chapters WHERE classLevel = :classLevel AND subject = :subject ORDER BY chapterNumber ASC")
    fun getChaptersByClassAndSubjectFlow(classLevel: String, subject: String): Flow<List<CachedChapterEntity>>

    @Query("SELECT * FROM cached_chapters WHERE id = :id LIMIT 1")
    suspend fun getChapterById(id: String): CachedChapterEntity?

    @Query("SELECT * FROM cached_chapters WHERE id = :id LIMIT 1")
    fun getChapterByIdFlow(id: String): Flow<CachedChapterEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<CachedChapterEntity>)

    @Query("UPDATE cached_chapters SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: String, isBookmarked: Boolean)

    @Query("SELECT COUNT(*) FROM cached_chapters")
    suspend fun getChapterCount(): Int

    @Query("SELECT MAX(cachedTimestamp) FROM cached_chapters")
    suspend fun getLastCachedTimestamp(): Long?
}
