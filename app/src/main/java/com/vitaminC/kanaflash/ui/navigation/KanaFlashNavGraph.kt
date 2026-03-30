package com.vitaminC.kanaflash.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vitaminC.kanaflash.ui.screens.FlashcardScreen
import com.vitaminC.kanaflash.ui.screens.HomeScreen
import com.vitaminC.kanaflash.ui.screens.QuizScreen
import com.vitaminC.kanaflash.ui.screens.ResultScreen
import com.vitaminC.kanaflash.ui.screens.VocabularyScreen
import com.vitaminC.kanaflash.ui.viewmodel.FlashcardViewModelFactory
import com.vitaminC.kanaflash.ui.viewmodel.QuizViewModelFactory
import com.vitaminC.kanaflash.ui.viewmodel.VocabularyViewModelFactory

@Composable
fun KanaFlashNavGraph(
    vocabularyFactory: VocabularyViewModelFactory,
    flashcardFactory: FlashcardViewModelFactory,
    quizFactory: QuizViewModelFactory
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
                factory = quizFactory,
                onBackToMenu = {
                    navController.popBackStack()
                },
                onQuizFinished = { score, total ->
                    navController.navigate(KanaFlashRoutes.resultRoute(score, total))
                }
            )
        }

        composable(
            route = KanaFlashRoutes.RESULT_WITH_ARGS,
            arguments = listOf(
                navArgument("score") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val total = backStackEntry.arguments?.getInt("total") ?: 0

            ResultScreen(
                score = score,
                total = total,
                onBackToMenu = {
                    navController.navigate(KanaFlashRoutes.HOME) {
                        popUpTo(KanaFlashRoutes.HOME) {
                            inclusive = true
                        }
                    }
                },
                onRetryQuiz = {
                    navController.popBackStack()
                }
            )
        }
    }
}
