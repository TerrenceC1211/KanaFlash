package com.vitaminC.kanaflash.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vitaminC.kanaflash.data.repository.VocabularyRepository
import com.vitaminC.kanaflash.ui.screens.DecksScreen
import com.vitaminC.kanaflash.ui.screens.FlashcardScreen
import com.vitaminC.kanaflash.ui.screens.HomeScreen
import com.vitaminC.kanaflash.ui.screens.QuizScreen
import com.vitaminC.kanaflash.ui.screens.ResultScreen
import com.vitaminC.kanaflash.ui.screens.VocabularyScreen
import com.vitaminC.kanaflash.ui.screens.WritePracticeScreen
import com.vitaminC.kanaflash.ui.viewmodel.DeckViewModelFactory
import com.vitaminC.kanaflash.ui.viewmodel.FlashcardViewModelFactory
import com.vitaminC.kanaflash.ui.viewmodel.HomeViewModelFactory
import com.vitaminC.kanaflash.ui.viewmodel.QuizViewModelFactory
import com.vitaminC.kanaflash.ui.viewmodel.VocabularyViewModelFactory
import com.vitaminC.kanaflash.ui.viewmodel.WritePracticeViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanaFlashNavGraph(
    repository: VocabularyRepository,
    homeFactory: HomeViewModelFactory,
    deckFactory: DeckViewModelFactory,
    flashcardFactory: FlashcardViewModelFactory,
    quizFactory: QuizViewModelFactory,
    writeFactory: WritePracticeViewModelFactory
) {
    val navController = rememberNavController()
    var showLearnSheet by rememberSaveable { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = KanaFlashRoutes.HOME
    ) {
        composable(KanaFlashRoutes.HOME) {
            HomeScreen(
                factory = homeFactory,
                onVocabularyClick = {
                    navController.navigate(KanaFlashRoutes.DECKS) {
                        launchSingleTop = true
                    }
                },
                onLearnClick = {
                    showLearnSheet = true
                }
            )
        }

        composable(KanaFlashRoutes.DECKS) {
            DecksScreen(
                factory = deckFactory,
                onHomeClick = {
                    navController.navigate(KanaFlashRoutes.HOME) {
                        popUpTo(KanaFlashRoutes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onLearnClick = {
                    showLearnSheet = true
                },
                onDeckClick = { deckId ->
                    navController.navigate(KanaFlashRoutes.deckDetailRoute(deckId))
                }
            )
        }

        composable(
            route = KanaFlashRoutes.DECK_DETAIL,
            arguments = listOf(
                navArgument("deckId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getLong("deckId") ?: 1L
            val vocabularyFactory = VocabularyViewModelFactory(repository, deckId)

            VocabularyScreen(
                factory = vocabularyFactory,
                onBackToDecks = {
                    navController.popBackStack()
                },
                onHomeClick = {
                    navController.navigate(KanaFlashRoutes.HOME) {
                        popUpTo(KanaFlashRoutes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onLearnClick = {
                    showLearnSheet = true
                }
            )
        }

        composable(KanaFlashRoutes.FLASHCARDS) {
            FlashcardScreen(
                factory = flashcardFactory,
                onDeckClick = {
                    navController.navigate(KanaFlashRoutes.DECKS) {
                        launchSingleTop = true
                    }
                },
                onHomeClick = {
                    navController.navigate(KanaFlashRoutes.HOME) {
                        popUpTo(KanaFlashRoutes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onLearnClick = {
                    showLearnSheet = true
                }
            )
        }

        composable(KanaFlashRoutes.QUIZ) {
            QuizScreen(
                factory = quizFactory,
                onDeckClick = {
                    navController.navigate(KanaFlashRoutes.DECKS) {
                        launchSingleTop = true
                    }
                },
                onHomeClick = {
                    navController.navigate(KanaFlashRoutes.HOME) {
                        popUpTo(KanaFlashRoutes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onLearnClick = {
                    showLearnSheet = true
                },
                onQuizFinished = { score, total ->
                    navController.navigate(KanaFlashRoutes.resultRoute(score, total)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(KanaFlashRoutes.WRITE) {
            WritePracticeScreen(
                factory = writeFactory,
                onDeckClick = {
                    navController.navigate(KanaFlashRoutes.DECKS) {
                        launchSingleTop = true
                    }
                },
                onHomeClick = {
                    navController.navigate(KanaFlashRoutes.HOME) {
                        popUpTo(KanaFlashRoutes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onLearnClick = {
                    showLearnSheet = true
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
                onDeckClick = {
                    navController.navigate(KanaFlashRoutes.DECKS) {
                        launchSingleTop = true
                    }
                },
                onHomeClick = {
                    navController.navigate(KanaFlashRoutes.HOME) {
                        popUpTo(KanaFlashRoutes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onLearnClick = {
                    showLearnSheet = true
                },
                onRetryQuiz = {
                    navController.popBackStack()
                }
            )
        }
    }

    if (showLearnSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { showLearnSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Learn",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Choose a study mode for the vocabulary currently saved in your deck.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = {
                        showLearnSheet = false
                        navController.navigate(KanaFlashRoutes.FLASHCARDS) {
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Flashcards")
                }

                TextButton(
                    onClick = {
                        showLearnSheet = false
                        navController.navigate(KanaFlashRoutes.QUIZ) {
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Quiz")
                }

                TextButton(
                    onClick = {
                        showLearnSheet = false
                        navController.navigate(KanaFlashRoutes.WRITE) {
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Write")
                }
            }
        }
    }
}
