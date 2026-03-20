package com.vitaminC.kanaflash.data.repository

import com.vitaminC.kanaflash.data.dao.VocabularyDao
import com.vitaminC.kanaflash.data.entity.VocabularyEntry
import kotlinx.coroutines.flow.Flow

class VocabularyRepository(private val vocabularyDao: VocabularyDao) {
    fun observeAll(): Flow<List<VocabularyEntry>> {
        return vocabularyDao.observeAll()
    }

    suspend fun getAll(): List<VocabularyEntry> {
        return vocabularyDao.getAll()
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
}
