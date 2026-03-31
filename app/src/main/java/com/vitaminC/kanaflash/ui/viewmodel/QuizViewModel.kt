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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class QuizViewModel(
    private val repository: VocabularyRepository
) : ViewModel() {

    val deckList: StateFlow<List<Deck>> =
        repository.observeAllDecks().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val selectedDeckIdFlow = MutableStateFlow<Long?>(null)

    var selectedDeckId by mutableStateOf<Long?>(null)
        private set

    val vocabularyList: StateFlow<List<VocabularyEntry>> =
        selectedDeckIdFlow
            .flatMapLatest { deckId ->
                repository.observeVocabularyForSelection(deckId)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun setSelectedDeck(deckId: Long?) {
        selectedDeckId = deckId
        selectedDeckIdFlow.value = deckId
    }
}

class QuizViewModelFactory(
    private val repository: VocabularyRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            return QuizViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
