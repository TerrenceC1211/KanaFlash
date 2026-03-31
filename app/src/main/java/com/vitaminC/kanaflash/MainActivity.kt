package com.vitaminC.kanaflash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vitaminC.kanaflash.data.db.AppDatabaseProvider
import com.vitaminC.kanaflash.data.repository.VocabularyRepository
import com.vitaminC.kanaflash.ui.navigation.KanaFlashNavGraph
import com.vitaminC.kanaflash.ui.theme.KanaFlashTheme
import com.vitaminC.kanaflash.ui.viewmodel.DeckViewModelFactory
import com.vitaminC.kanaflash.ui.viewmodel.FlashcardViewModelFactory
import com.vitaminC.kanaflash.ui.viewmodel.HomeViewModelFactory
import com.vitaminC.kanaflash.ui.viewmodel.QuizViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabaseProvider.getDatabase(this)
        val repository = VocabularyRepository(
            database.vocabularyDao(),
            database.deckDao()
        )

        val homeFactory = HomeViewModelFactory(repository)
        val deckFactory = DeckViewModelFactory(repository)
        val flashcardFactory = FlashcardViewModelFactory(repository)
        val quizFactory = QuizViewModelFactory(repository)

        setContent {
            KanaFlashTheme {
                KanaFlashNavGraph(
                    repository = repository,
                    homeFactory = homeFactory,
                    deckFactory = deckFactory,
                    flashcardFactory = flashcardFactory,
                    quizFactory = quizFactory
                )
            }
        }
    }
}
