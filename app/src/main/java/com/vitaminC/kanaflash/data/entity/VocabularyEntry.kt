package com.vitaminC.kanaflash.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
/**
 * Room entity that stores vocabulary items shown in flashcards.
 */
@Entity(tableName = "vocabulary_entries")
data class VocabularyEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val romaji: String,
    val hiragana: String,
    val meaning: String?
)