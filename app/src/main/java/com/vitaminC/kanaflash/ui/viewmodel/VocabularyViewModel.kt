package com.vitaminC.kanaflash.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vitaminC.kanaflash.data.entity.Deck
import com.vitaminC.kanaflash.data.entity.VocabularyEntry
import com.vitaminC.kanaflash.data.repository.VocabularyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VocabularyViewModel(
    private val repository: VocabularyRepository,
    private val deckId: Long
) : ViewModel() {

    val vocabularyList: StateFlow<List<VocabularyEntry>> =
        repository.observeByDeck(deckId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val deck: StateFlow<Deck?> =
        repository.observeDeckById(deckId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val deckTitle: StateFlow<String> =
        repository.observeDeckById(deckId)
            .map { it?.title ?: "Deck" }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = "Deck"
            )

    fun addEntry(romaji: String, hiragana: String, meaning: String) {
        val trimmedRomaji = romaji.trim()
        val trimmedHiragana = hiragana.trim()
        val trimmedMeaning = meaning.trim()

        if (trimmedRomaji.isBlank() || trimmedHiragana.isBlank()) return

        viewModelScope.launch {
            repository.insert(
                VocabularyEntry(
                    romaji = trimmedRomaji,
                    hiragana = trimmedHiragana,
                    meaning = trimmedMeaning.ifBlank { null },
                    deckId = deckId
                )
            )
        }
    }

    fun updateEntry(id: Long, romaji: String, hiragana: String, meaning: String) {
        val trimmedRomaji = romaji.trim()
        val trimmedHiragana = hiragana.trim()
        val trimmedMeaning = meaning.trim()

        if (trimmedRomaji.isBlank() || trimmedHiragana.isBlank()) return

        viewModelScope.launch {
            repository.update(
                VocabularyEntry(
                    id = id,
                    romaji = trimmedRomaji,
                    hiragana = trimmedHiragana,
                    meaning = trimmedMeaning.ifBlank { null },
                    deckId = deckId
                )
            )
        }
    }

    fun deleteEntry(entry: VocabularyEntry) {
        viewModelScope.launch {
            repository.delete(entry)
        }
    }
}

class VocabularyViewModelFactory(
    private val repository: VocabularyRepository,
    private val deckId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VocabularyViewModel::class.java)) {
            return VocabularyViewModel(repository, deckId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
