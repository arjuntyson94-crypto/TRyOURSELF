package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.CachedFormulaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FormulaDao {
    @Query("SELECT * FROM cached_formulas ORDER BY classLevel DESC, subject ASC, name ASC")
    fun getAllFormulasFlow(): Flow<List<CachedFormulaEntity>>

    @Query("SELECT * FROM cached_formulas WHERE classLevel = :classLevel ORDER BY subject ASC, name ASC")
    fun getFormulasByClassFlow(classLevel: String): Flow<List<CachedFormulaEntity>>

    @Query("SELECT * FROM cached_formulas WHERE classLevel = :classLevel AND subject = :subject ORDER BY name ASC")
    fun getFormulasByClassAndSubjectFlow(classLevel: String, subject: String): Flow<List<CachedFormulaEntity>>

    @Query("SELECT * FROM cached_formulas WHERE name LIKE '%' || :query || '%' OR formulaText LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR chapterTitle LIKE '%' || :query || '%'")
    fun searchFormulas(query: String): Flow<List<CachedFormulaEntity>>

    @Query("SELECT * FROM cached_formulas WHERE isFavorite = 1 ORDER BY classLevel DESC, subject ASC, name ASC")
    fun getFavoriteFormulasFlow(): Flow<List<CachedFormulaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFormulas(formulas: List<CachedFormulaEntity>)

    @Query("UPDATE cached_formulas SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM cached_formulas")
    suspend fun getFormulaCount(): Int
}
