package com.vitaminC.kanaflash.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vitaminC.kanaflash.data.entity.VocabularyEntry
import com.vitaminC.kanaflash.ui.components.DeckSelectionMenu
import com.vitaminC.kanaflash.ui.components.KanaFlashBottomBar
import com.vitaminC.kanaflash.ui.navigation.AppSection
import com.vitaminC.kanaflash.ui.viewmodel.WritePracticeViewModel
import com.vitaminC.kanaflash.ui.viewmodel.WritePracticeViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritePracticeScreen(
    factory: WritePracticeViewModelFactory,
    onDeckClick: () -> Unit,
    onHomeClick: () -> Unit,
    onLearnClick: () -> Unit
) {
    val viewModel: WritePracticeViewModel = viewModel(factory = factory)
    val vocabularyList by viewModel.vocabularyList.collectAsStateWithLifecycle()
    val deckList by viewModel.deckList.collectAsStateWithLifecycle()

    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    var isAnswerVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(vocabularyList) {
        currentIndex = 0
        isAnswerVisible = false
    }

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Write Mode") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
            ) {
                Text(
                    text = if (vocabularyList.isEmpty()) {
                        "No words available"
                    } else {
                        "Word ${currentIndex + 1} of ${vocabularyList.size}"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }

            DeckSelectionMenu(
                deckList = deckList,
                selectedDeckId = viewModel.selectedDeckId,
                onDeckSelected = { deckId ->
                    viewModel.setSelectedDeck(deckId)
                },
                label = "Write Deck"
            )

            if (vocabularyList.isEmpty()) {
                EmptyWriteState(
                    onDeckClick = onDeckClick
                )
            } else {
                val currentWord = vocabularyList[currentIndex]

                WritePromptCard(currentWord = currentWord)

                WritingPadPlaceholder(
                    currentWord = currentWord,
                    isAnswerVisible = isAnswerVisible
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            isAnswerVisible = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear")
                    }

                    Button(
                        onClick = {
                            isAnswerVisible = !isAnswerVisible
                        },
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Text(if (isAnswerVisible) "Hide" else "Reveal")
                    }

                    Button(
                        onClick = {
                            if (currentIndex < vocabularyList.lastIndex) {
                                currentIndex += 1
                            } else {
                                currentIndex = 0
                            }
                            isAnswerVisible = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (currentIndex < vocabularyList.lastIndex) "Next" else "Restart")
                    }
                }
            }
        }
    }
}

@Composable
private fun WritePromptCard(
    currentWord: VocabularyEntry
) {
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
                text = "Write the Japanese text for:",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = currentWord.romaji,
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}

@Composable
private fun WritingPadPlaceholder(
    currentWord: VocabularyEntry,
    isAnswerVisible: Boolean
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isAnswerVisible) 240.dp else 180.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Writing Pad",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "The drawing canvas will be added next. For now, try recalling or writing the answer before revealing it.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isAnswerVisible) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Correct Answer",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = currentWord.hiragana,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = currentWord.meaning ?: "No meaning added",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyWriteState(
    onDeckClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "No words available for writing practice",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Add vocabulary to this deck, or switch to another deck or All Decks.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Button(onClick = onDeckClick) {
            Text("Go to Deck")
        }
    }
}
