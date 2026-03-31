package com.vitaminC.kanaflash.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity that stores vocabulary items shown in flashcards.
 */
@Entity(
    tableName = "vocabulary_entries",
    foreignKeys = [
        ForeignKey(
            entity = Deck::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["deckId"])]
)
data class VocabularyEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val romaji: String,
    val hiragana: String,
    val meaning: String?,
    val deckId: Long = 1
)
