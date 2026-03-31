package com.vitaminC.kanaflash.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vitaminC.kanaflash.data.entity.VocabularyEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {
    @Query("SELECT * FROM vocabulary_entries ORDER BY id ASC")
    fun observeAll(): Flow<List<VocabularyEntry>>

    @Query("SELECT * FROM vocabulary_entries ORDER BY id ASC")
    suspend fun getAll(): List<VocabularyEntry>

    @Query("SELECT * FROM vocabulary_entries WHERE deckId = :deckId ORDER BY id ASC")
    fun observeByDeck(deckId: Long): Flow<List<VocabularyEntry>>

    @Query("SELECT * FROM vocabulary_entries WHERE deckId = :deckId ORDER BY id ASC")
    suspend fun getByDeck(deckId: Long): List<VocabularyEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: VocabularyEntry)

    @Update
    suspend fun update(entry: VocabularyEntry)

    @Delete
    suspend fun delete(entry: VocabularyEntry)

    @Query("SELECT * FROM vocabulary_entries WHERE id = :id")
    suspend fun getById(id: Long): VocabularyEntry?
}
