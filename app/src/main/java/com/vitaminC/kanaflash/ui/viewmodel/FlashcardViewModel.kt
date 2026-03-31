package com.vitaminC.kanaflash.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vitaminC.kanaflash.data.entity.Deck
import com.vitaminC.kanaflash.data.entity.VocabularyEntry
import com.vitaminC.kanaflash.data.repository.VocabularyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FlashcardViewModel(
    private val repository: VocabularyRepository
) : ViewModel() {

    val deckList: StateFlow<List<Deck>> =
        repository.observeAllDecks().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    var selectedDeckId by mutableStateOf<Long?>(null)
        private set

    var vocabularyList by mutableStateOf<List<VocabularyEntry>>(emptyList())
        private set

    init {
        loadVocabulary()
    }

    fun setSelectedDeck(deckId: Long?) {
        selectedDeckId = deckId
        loadVocabulary()
    }

    private fun loadVocabulary() {
        viewModelScope.launch {
            vocabularyList = repository.getVocabularyForSelection(selectedDeckId)
        }
    }
}

class FlashcardViewModelFactory(
    private val repository: VocabularyRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlashcardViewModel::class.java)) {
            return FlashcardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
