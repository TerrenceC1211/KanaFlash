package com.vitaminC.kanaflash.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vitaminC.kanaflash.ui.screens.FlashcardScreen
import com.vitaminC.kanaflash.ui.screens.HomeScreen
import com.vitaminC.kanaflash.ui.screens.QuizScreen
import com.vitaminC.kanaflash.ui.screens.ResultScreen
import com.vitaminC.kanaflash.ui.screens.VocabularyScreen
import com.vitaminC.kanaflash.ui.viewmodel.FlashcardViewModelFactory
import com.vitaminC.kanaflash.ui.viewmodel.VocabularyViewModelFactory

@Composable
fun KanaFlashNavGraph(
    vocabularyFactory: VocabularyViewModelFactory,
    flashcardFactory: FlashcardViewModelFactory
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = KanaFlashRoutes.HOME
    ) {
        composable(KanaFlashRoutes.HOME) {
            HomeScreen(
                onVocabularyClick = {
                    navController.navigate(KanaFlashRoutes.VOCABULARY)
                },
                onFlashcardsClick = {
                    navController.navigate(KanaFlashRoutes.FLASHCARDS)
                },
                onQuizClick = {
                    navController.navigate(KanaFlashRoutes.QUIZ)
                }
            )
        }

        composable(KanaFlashRoutes.VOCABULARY) {
            VocabularyScreen(
                factory = vocabularyFactory,
                onBackToMenu = {
                    navController.popBackStack()
                }
            )
        }

        composable(KanaFlashRoutes.FLASHCARDS) {
            FlashcardScreen(
                factory = flashcardFactory,
                onBackToMenu = {
                    navController.popBackStack()
                }
            )
        }

        composable(KanaFlashRoutes.QUIZ) {
            QuizScreen(
                onBackToMenu = {
                    navController.popBackStack()
                },
                onOpenResult = {
                    navController.navigate(KanaFlashRoutes.RESULT)
                }
            )
        }

        composable(KanaFlashRoutes.RESULT) {
            ResultScreen(
                onBackToMenu = {
                    navController.navigate(KanaFlashRoutes.HOME) {
                        popUpTo(KanaFlashRoutes.HOME) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
