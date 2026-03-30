package com.vitaminC.kanaflash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.vitaminC.kanaflash.data.db.AppDatabaseProvider
import com.vitaminC.kanaflash.data.repository.VocabularyRepository
import com.vitaminC.kanaflash.ui.navigation.KanaFlashNavGraph
import com.vitaminC.kanaflash.ui.theme.KanaFlashTheme
import com.vitaminC.kanaflash.ui.viewmodel.FlashcardViewModelFactory
import com.vitaminC.kanaflash.ui.viewmodel.QuizViewModelFactory
import com.vitaminC.kanaflash.ui.viewmodel.VocabularyViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabaseProvider.getDatabase(this)
        val repository = VocabularyRepository(database.vocabularyDao())

        val vocabularyFactory = VocabularyViewModelFactory(repository)
        val flashcardFactory = FlashcardViewModelFactory(repository)
        val quizFactory = QuizViewModelFactory(repository)

        setContent {
            KanaFlashTheme {
                KanaFlashNavGraph(
                    vocabularyFactory = vocabularyFactory,
                    flashcardFactory = flashcardFactory,
                    quizFactory = quizFactory
                )
            }
        }
    }
}
