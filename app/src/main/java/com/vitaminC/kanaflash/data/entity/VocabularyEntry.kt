package com.vitaminC.kanaflash.data.entity

/**
 * Represents a vocabulary item that can be shown in flashcards.
 */
data class VocabularyEntry(
    val id: Long,
    val romaji: String,
    val hiragana: String,
    val meaning: String?
)