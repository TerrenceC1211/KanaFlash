package com.vitaminC.kanaflash.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vitaminC.kanaflash.data.entity.VocabularyEntry
import com.vitaminC.kanaflash.ui.viewmodel.FlashcardViewModel
import com.vitaminC.kanaflash.ui.viewmodel.FlashcardViewModelFactory
import com.vitaminC.kanaflash.ui.components.KanaFlashBottomBar
import com.vitaminC.kanaflash.ui.navigation.AppSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    factory: FlashcardViewModelFactory,
    onDeckClick: () -> Unit,
    onHomeClick: () -> Unit,
    onLearnClick: () -> Unit
)
 {
    val viewModel: FlashcardViewModel = viewModel(factory = factory)
    val vocabularyList by viewModel.vocabularyList.collectAsStateWithLifecycle()

    var isShuffled by rememberSaveable { mutableStateOf(false) }
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    var isAnswerVisible by rememberSaveable { mutableStateOf(false) }
    var deck by remember { mutableStateOf<List<VocabularyEntry>>(emptyList()) }

    LaunchedEffect(vocabularyList, isShuffled) {
        deck = if (isShuffled) vocabularyList.shuffled() else vocabularyList

        currentIndex = if (deck.isEmpty()) {
            0
        } else {
            currentIndex.coerceIn(0, deck.lastIndex)
        }

        isAnswerVisible = false
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Flashcard Study") },

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
        if (deck.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No flashcards available yet.",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Add vocabulary in your deck first, then return here to study.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
                )
                Button(onClick = onDeckClick) {
                    Text("Go to Deck")
                }

            }
        } else {
            val currentCard = deck[currentIndex]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "Card ${currentIndex + 1} of ${deck.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            isShuffled = !isShuffled
                            currentIndex = 0
                        }
                    ) {
                        Text(if (isShuffled) "Shuffle On" else "Shuffle Off")
                    }
                }

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable {
                            isAnswerVisible = !isAnswerVisible
                        },
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


                        Text(
                            text = "Hiragana",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = currentCard.hiragana,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )

                        if (isAnswerVisible) {
                            Text(
                                text = "Romaji",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 16.dp)
                            )

                            Text(
                                text = currentCard.romaji,
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = currentCard.meaning ?: "No meaning added",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 12.dp),
                                textAlign = TextAlign.Center
                            )
                        } else {
                            Text(
                                text = "Tap to reveal",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 16.dp)
                            )

                            Text(
                                text = "Tap anywhere on the card to reveal the Romaji answer.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 6.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (currentIndex > 0) {
                                currentIndex -= 1
                                isAnswerVisible = false
                            }
                        },
                        enabled = currentIndex > 0,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Previous")
                    }

                    Button(
                        onClick = {
                            isAnswerVisible = !isAnswerVisible
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isAnswerVisible) "Hide" else "Reveal")
                    }

                    OutlinedButton(
                        onClick = {
                            if (currentIndex < deck.lastIndex) {
                                currentIndex += 1
                                isAnswerVisible = false
                            }
                        },
                        enabled = currentIndex < deck.lastIndex,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }
}
