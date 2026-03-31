package com.vitaminC.kanaflash.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vitaminC.kanaflash.data.dao.DeckDao
import com.vitaminC.kanaflash.data.dao.VocabularyDao
import com.vitaminC.kanaflash.data.entity.Deck
import com.vitaminC.kanaflash.data.entity.VocabularyEntry

@Database(
    entities = [Deck::class, VocabularyEntry::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun vocabularyDao(): VocabularyDao
}
