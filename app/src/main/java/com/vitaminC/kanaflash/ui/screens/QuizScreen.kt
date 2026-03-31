package com.vitaminC.kanaflash.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vitaminC.kanaflash.data.entity.VocabularyEntry
import com.vitaminC.kanaflash.ui.viewmodel.QuizViewModel
import com.vitaminC.kanaflash.ui.viewmodel.QuizViewModelFactory
import com.vitaminC.kanaflash.ui.components.KanaFlashBottomBar
import com.vitaminC.kanaflash.ui.navigation.AppSection

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
                    .distinct()
                    .shuffled()
                    .take(3)

                if (wrongAnswers.size == 3) {
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
        },

        ) { innerPadding ->
        when {
            vocabularyList.size < 4 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Not enough vocabulary for quiz mode.",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Please add at least 4 vocabulary entries before starting the quiz.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
                    )
                    Button(onClick = onDeckClick) {
                        Text("Go to Deck")
                    }

                }
            }

            questions.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Quiz could not be generated.",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Try adding more varied vocabulary entries and try again.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
                    )
                    Button(onClick = onDeckClick) {
                        Text("Go to Deck")
                    }

                }
            }

            else -> {
                val currentQuestion = questions[currentQuestionIndex]
                val correctAnswer = currentQuestion.correctAnswer

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text(
                        text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Score: $score",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Select the correct Japanese text for:",
                                style = MaterialTheme.typography.labelLarge,
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
                                isCorrect -> MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
                                isSelected -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.20f)
                                else -> MaterialTheme.colorScheme.surface
                            }

                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.elevatedCardColors(
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
                                    modifier = Modifier.padding(20.dp)
                                )
                            }
                        }
                    }

                    if (selectedAnswer != null) {
                        Text(
                            text = if (selectedAnswer == correctAnswer) {
                                "Correct!"
                            } else {
                                "Incorrect. The correct answer is $correctAnswer."
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = if (selectedAnswer == correctAnswer) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.tertiary
                            }
                        )

                        val isLastQuestion = currentQuestionIndex == questions.lastIndex

                        if (isLastQuestion) {
                            Button(
                                onClick = { onQuizFinished(score, questions.size) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Finish Quiz")
                            }
                        } else {
                            OutlinedButton(
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
