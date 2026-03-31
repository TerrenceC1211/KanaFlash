package com.vitaminC.kanaflash.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.vitaminC.kanaflash.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vitaminC.kanaflash.data.entity.VocabularyEntry
import com.vitaminC.kanaflash.ui.components.KanaFlashBottomBar
import com.vitaminC.kanaflash.ui.navigation.AppSection
import com.vitaminC.kanaflash.ui.viewmodel.HomeViewModel
import com.vitaminC.kanaflash.ui.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.delay




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    factory: HomeViewModelFactory,
    onVocabularyClick: () -> Unit,
    onFlashcardsClick: () -> Unit,
    onQuizClick: () -> Unit
) {
    val viewModel: HomeViewModel = viewModel(factory = factory)
    val vocabularyList by viewModel.vocabularyList.collectAsStateWithLifecycle()

    var showLearnSheet by rememberSaveable { mutableStateOf(false) }
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }

    val previewDeck = remember(vocabularyList) {
        if (vocabularyList.isEmpty()) emptyList() else vocabularyList.shuffled()
    }

    LaunchedEffect(previewDeck) {
        currentIndex = 0
        if (previewDeck.size > 1) {
            while (true) {
                delay(2500)
                currentIndex = (currentIndex + 1) % previewDeck.size
            }
        }
    }

    val currentCard = if (previewDeck.isNotEmpty()) previewDeck[currentIndex] else null

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            KanaFlashBottomBar(
                activeSection = AppSection.HOME,
                onDeckClick = onVocabularyClick,
                onHomeClick = { },
                onLearnClick = { showLearnSheet = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "KanaFlash logo",
                modifier = Modifier
                    .padding(top = 20.dp)
                    .size(width = 190.dp, height = 140.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "Learn kana through quick review and active recall.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(0.82f)
            )






            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 36.dp, bottom = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                if (currentCard == null) {
                    EmptyPreviewCard()
                } else {
                    FlashPreviewDeck(card = currentCard)
                }
            }
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
                        onFlashcardsClick()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Flashcards")
                }

                TextButton(
                    onClick = {
                        showLearnSheet = false
                        onQuizClick()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Quiz")
                }
            }
        }
    }
}

@Composable
private fun FlashPreviewDeck(
    card: VocabularyEntry
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth(0.72f)
                .offset(x = (-105).dp, y = 8.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.36f)
            ),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)
        ) {
            Box(modifier = Modifier.padding(vertical = 60.dp))
        }

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth(0.72f)
                .offset(x = 105.dp, y = 8.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.36f)
            ),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)
        ) {
            Box(modifier = Modifier.padding(vertical = 60.dp))
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(0.84f),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(30.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 34.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = card.hiragana,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = card.romaji,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = card.meaning ?: "No meaning added",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun EmptyPreviewCard() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(0.84f),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(30.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "No vocabulary yet",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Add words in your deck to show a rotating preview here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
