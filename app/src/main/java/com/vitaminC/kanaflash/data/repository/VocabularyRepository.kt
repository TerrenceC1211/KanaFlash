package com.vitaminC.kanaflash.data.repository

import com.vitaminC.kanaflash.data.dao.DeckDao
import com.vitaminC.kanaflash.data.dao.VocabularyDao
import com.vitaminC.kanaflash.data.entity.Deck
import com.vitaminC.kanaflash.data.entity.VocabularyEntry
import kotlinx.coroutines.flow.Flow

class VocabularyRepository(
    private val vocabularyDao: VocabularyDao,
    private val deckDao: DeckDao
) {
    fun observeAll(): Flow<List<VocabularyEntry>> {
        return vocabularyDao.observeAll()
    }

    suspend fun getAll(): List<VocabularyEntry> {
        return vocabularyDao.getAll()
    }

    fun observeByDeck(deckId: Long): Flow<List<VocabularyEntry>> {
        return vocabularyDao.observeByDeck(deckId)
    }

    suspend fun getByDeck(deckId: Long): List<VocabularyEntry> {
        return vocabularyDao.getByDeck(deckId)
    }

    suspend fun getById(id: Long): VocabularyEntry? {
        return vocabularyDao.getById(id)
    }

    suspend fun insert(entry: VocabularyEntry) {
        vocabularyDao.insert(entry)
    }

    suspend fun update(entry: VocabularyEntry) {
        vocabularyDao.update(entry)
    }

    suspend fun delete(entry: VocabularyEntry) {
        vocabularyDao.delete(entry)
    }

    fun observeAllDecks(): Flow<List<Deck>> {
        return deckDao.observeAll()
    }

    fun observeDeckById(id: Long): Flow<Deck?> {
        return deckDao.observeById(id)
    }

    suspend fun getAllDecks(): List<Deck> {
        return deckDao.getAll()
    }

    suspend fun getDeckById(id: Long): Deck? {
        return deckDao.getById(id)
    }

    suspend fun insertDeck(deck: Deck): Long {
        return deckDao.insert(deck)
    }
}
