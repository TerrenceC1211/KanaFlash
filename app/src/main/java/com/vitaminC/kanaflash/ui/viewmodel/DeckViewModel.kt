package com.vitaminC.kanaflash.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vitaminC.kanaflash.data.entity.Deck
import com.vitaminC.kanaflash.data.repository.VocabularyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DeckViewModel(
    private val repository: VocabularyRepository
) : ViewModel() {

    val deckList: StateFlow<List<Deck>> =
        repository.observeAllDecks().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addDeck(title: String) {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) return

        viewModelScope.launch {
            repository.insertDeck(Deck(title = trimmedTitle))
        }
    }

    fun renameDeck(deck: Deck, newTitle: String) {
        val trimmedTitle = newTitle.trim()
        if (trimmedTitle.isBlank()) return

        viewModelScope.launch {
            repository.updateDeck(deck.copy(title = trimmedTitle))
        }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch {
            repository.deleteDeck(deck)
        }
    }
}

class DeckViewModelFactory(
    private val repository: VocabularyRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeckViewModel::class.java)) {
            return DeckViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
