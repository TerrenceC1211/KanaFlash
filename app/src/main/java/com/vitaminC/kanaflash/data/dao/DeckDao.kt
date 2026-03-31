package com.vitaminC.kanaflash.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vitaminC.kanaflash.data.entity.Deck
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM decks ORDER BY id ASC")
    fun observeAll(): Flow<List<Deck>>

    @Query("SELECT * FROM decks ORDER BY id ASC")
    suspend fun getAll(): List<Deck>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deck: Deck): Long

    @Update
    suspend fun update(deck: Deck)

    @Delete
    suspend fun delete(deck: Deck)

    @Query("SELECT * FROM decks WHERE id = :id")
    suspend fun getById(id: Long): Deck?

    @Query("SELECT * FROM decks WHERE id = :id")
    fun observeById(id: Long): Flow<Deck?>
}
