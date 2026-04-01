package com.vitaminC.kanaflash.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vitaminC.kanaflash.data.entity.VocabularyEntry
import com.vitaminC.kanaflash.ui.components.DeckSelectionMenu
import com.vitaminC.kanaflash.ui.components.KanaFlashBottomBar
import com.vitaminC.kanaflash.ui.navigation.AppSection
import com.vitaminC.kanaflash.ui.viewmodel.QuizViewModel
import com.vitaminC.kanaflash.ui.viewmodel.QuizViewModelFactory
import com.vitaminC.kanaflash.ui.components.StudyOutlineButton
import com.vitaminC.kanaflash.ui.components.StudyPrimaryButton

private data class QuizQuestion(
    val prompt: VocabularyEntry,
    val options: List<String>,
    val correctAnswer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    factory: QuizViewModelFactory,
    onDeckClick: () -> Unit,
    onHomeClick: () -> Unit,
    onLearnClick: () -> Unit,
    onQuizFinished: (Int, Int) -> Unit
) {
    val viewModel: QuizViewModel = viewModel(factory = factory)
    val vocabularyList by viewModel.vocabularyList.collectAsStateWithLifecycle()
    val deckList by viewModel.deckList.collectAsStateWithLifecycle()

    var currentQuestionIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedAnswer by rememberSaveable { mutableStateOf<String?>(null) }
    var score by rememberSaveable { mutableIntStateOf(0) }
    val questions = remember { mutableStateListOf<QuizQuestion>() }

    LaunchedEffect(vocabularyList) {
        questions.clear()

        if (vocabularyList.size >= 4) {
            val shuffledVocabulary = vocabularyList.shuffled()

            shuffledVocabulary.forEach { correctEntry ->
                val wrongAnswers = vocabularyList
                    .filter { it.id != correctEntry.id }
                    .map { it.hiragana }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .shuffled()
                    .take(3)

                if (wrongAnswers.size == 3 && correctEntry.hiragana.isNotBlank()) {
                    val options = (wrongAnswers + correctEntry.hiragana).shuffled()
                    questions.add(
                        QuizQuestion(
                            prompt = correctEntry,
                            options = options,
                            correctAnswer = correctEntry.hiragana
                        )
                    )
                }
            }
        }

        currentQuestionIndex = 0
        selectedAnswer = null
        score = 0
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background
        )
    )

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Quiz Mode") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            KanaFlashBottomBar(
                activeSection = AppSection.LEARN,
                onDeckClick = onDeckClick,
                onHomeClick = onHomeClick,
                onLearnClick = onLearnClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(backgroundBrush)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                    ) {
                        Text(
                            text = if (questions.isNotEmpty()) {
                                "Question ${currentQuestionIndex + 1} of ${questions.size}"
                            } else {
                                "Quiz unavailable"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }

                DeckSelectionMenu(
                    deckList = deckList,
                    selectedDeckId = viewModel.selectedDeckId,
                    onDeckSelected = { deckId ->
                        viewModel.setSelectedDeck(deckId)
                    },
                    label = "Quiz Deck"
                )

                when {
                    vocabularyList.size < 4 -> {
                        QuizMessageState(
                            title = "Not enough vocabulary for this deck",
                            description = "Please add at least 4 saved words, or switch to another deck or All Decks.",
                            buttonLabel = "Go to Deck",
                            onButtonClick = onDeckClick
                        )
                    }

                    questions.isEmpty() -> {
                        QuizMessageState(
                            title = "Quiz could not be generated",
                            description = "Try another deck or add more varied vocabulary entries first.",
                            buttonLabel = "Go to Deck",
                            onButtonClick = onDeckClick
                        )
                    }

                    else -> {
                        val currentQuestion = questions[currentQuestionIndex]
                        val correctAnswer = currentQuestion.correctAnswer
                        val isLastQuestion = currentQuestionIndex == questions.lastIndex

                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(34.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                ) {
                                    Text(
                                        text = "Prompt",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }

                                Text(
                                    text = "Select the correct Japanese text for:",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = currentQuestion.prompt.romaji,
                                    style = MaterialTheme.typography.headlineLarge
                                )
                            }
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            currentQuestion.options.forEach { option ->
                                val isSelected = selectedAnswer == option
                                val isCorrect = option == correctAnswer

                                val containerColor = when {
                                    selectedAnswer == null -> MaterialTheme.colorScheme.surface
                                    isCorrect -> MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                                    isSelected -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f)
                                    else -> MaterialTheme.colorScheme.surface
                                }

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(22.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = containerColor
                                    ),
                                    onClick = {
                                        if (selectedAnswer == null) {
                                            selectedAnswer = option
                                            if (option == correctAnswer) {
                                                score += 1
                                            }
                                        }
                                    }
                                ) {
                                    Text(
                                        text = option,
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp)
                                    )
                                }
                            }
                        }

                        if (selectedAnswer != null) {
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = if (selectedAnswer == correctAnswer) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                                } else {
                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.10f)
                                }
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 18.dp, vertical = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = if (selectedAnswer == correctAnswer) {
                                            "Correct"
                                        } else {
                                            "Not quite"
                                        },
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (selectedAnswer == correctAnswer) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.tertiary
                                        }
                                    )

                                    Text(
                                        text = if (selectedAnswer == correctAnswer) {
                                            "Nice recall. Move on when you're ready."
                                        } else {
                                            "The correct answer is $correctAnswer."
                                        },
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        if (selectedAnswer != null) {
                            if (isLastQuestion) {
                                StudyPrimaryButton(
                                    onClick = { onQuizFinished(score, questions.size) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Finish Quiz")
                                }
                            } else {
                                StudyOutlineButton(
                                    onClick = {
                                        currentQuestionIndex += 1
                                        selectedAnswer = null
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Next Question")
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
private fun QuizMessageState(
    title: String,
    description: String,
    buttonLabel: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        StudyPrimaryButton(onClick = onButtonClick) {
            Text(buttonLabel)
        }

    }
}
