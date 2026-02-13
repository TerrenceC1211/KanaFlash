package com.vitaminC.kanaflash.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vitaminC.kanaflash.data.dao.VocabularyDao
import com.vitaminC.kanaflash.data.entity.VocabularyEntry

@Database(entities = [VocabularyEntry::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vocabularyDao(): VocabularyDao
}