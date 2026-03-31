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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Card
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
import com.vitaminC.kanaflash.ui.components.KanaFlashBottomBar
import com.vitaminC.kanaflash.ui.navigation.AppSection
import com.vitaminC.kanaflash.ui.viewmodel.QuizViewModel
import com.vitaminC.kanaflash.ui.viewmodel.QuizViewModelFactory
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

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

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background
        )
    )

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
        when {
            vocabularyList.size < 4 -> {
                QuizMessageState(
                    title = "Not enough vocabulary for quiz mode",
                    description = "Please add at least 4 saved words before starting a quiz session.",
                    buttonLabel = "Go to Deck",
                    onButtonClick = onDeckClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                )
            }

            questions.isEmpty() -> {
                QuizMessageState(
                    title = "Quiz could not be generated",
                    description = "Try adding more varied vocabulary entries, then start the quiz again.",
                    buttonLabel = "Go to Deck",
                    onButtonClick = onDeckClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                )
            }

            else -> {
                val currentQuestion = questions[currentQuestionIndex]
                val correctAnswer = currentQuestion.correctAnswer
                val isLastQuestion = currentQuestionIndex == questions.lastIndex

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

                    val scrollState = rememberScrollState()

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
                                    text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }


                        }

                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(30.dp),
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
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
        )

        Button(onClick = onButtonClick) {
            Text(buttonLabel)
        }
    }
}
