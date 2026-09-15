package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ChapterDao
import com.example.data.local.dao.FormulaDao
import com.example.data.local.dao.NotebookDao
import com.example.data.local.entity.CachedChapterEntity
import com.example.data.local.entity.CachedFormulaEntity
import com.example.data.local.entity.NotebookEntryEntity

@Database(
    entities = [
        CachedChapterEntity::class,
        CachedFormulaEntity::class,
        NotebookEntryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chapterDao(): ChapterDao
    abstract fun formulaDao(): FormulaDao
    abstract fun notebookDao(): NotebookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cbse_offline_study_cache.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
