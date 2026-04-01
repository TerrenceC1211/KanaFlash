package com.vitaminC.kanaflash.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vitaminC.kanaflash.data.entity.VocabularyEntry
import com.vitaminC.kanaflash.ui.components.DeckSelectionMenu
import com.vitaminC.kanaflash.ui.components.KanaFlashBottomBar
import com.vitaminC.kanaflash.ui.navigation.AppSection
import com.vitaminC.kanaflash.ui.viewmodel.WritePracticeViewModel
import com.vitaminC.kanaflash.ui.viewmodel.WritePracticeViewModelFactory

private data class DrawStroke(
    val path: Path
)

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

    val strokes = remember { mutableStateListOf<DrawStroke>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }

    fun clearCanvas() {
        strokes.clear()
        currentPath = null
    }

    fun undoLastStroke() {
        if (strokes.isNotEmpty()) {
            strokes.removeAt(strokes.lastIndex)
        }
        currentPath = null
    }

    fun moveToPreviousWord() {
        if (currentIndex > 0) {
            currentIndex -= 1
            isAnswerVisible = false
            clearCanvas()
        }
    }

    fun moveToNextWord() {
        if (currentIndex < vocabularyList.lastIndex) {
            currentIndex += 1
        } else {
            currentIndex = 0
        }
        isAnswerVisible = false
        clearCanvas()
    }

    LaunchedEffect(vocabularyList) {
        currentIndex = 0
        isAnswerVisible = false
        clearCanvas()
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
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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

                WritingPad(
                    currentWord = currentWord,
                    isAnswerVisible = isAnswerVisible,
                    strokes = strokes,
                    currentPath = currentPath,
                    onToggleAnswer = {
                        isAnswerVisible = !isAnswerVisible
                    },
                    onCurrentPathChange = { updatedPath ->
                        currentPath = updatedPath
                    },
                    onStrokeFinished = { finishedPath ->
                        strokes.add(DrawStroke(finishedPath))
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { moveToPreviousWord() },
                        enabled = currentIndex > 0,
                        modifier = Modifier.weight(1.1f)
                    ) {
                        Text("Back")
                    }

                    ActionIconButton(
                        onClick = { undoLastStroke() },
                        enabled = strokes.isNotEmpty(),
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Undo,
                                contentDescription = "Undo stroke"
                            )
                        },
                        modifier = Modifier.weight(0.9f)
                    )

                    ActionIconButton(
                        onClick = {
                            isAnswerVisible = false
                            clearCanvas()
                        },
                        enabled = strokes.isNotEmpty() || currentPath != null || isAnswerVisible,
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.CleaningServices,
                                contentDescription = "Clear writing"
                            )
                        },
                        modifier = Modifier.weight(0.9f)
                    )

                    Button(
                        onClick = { moveToNextWord() },
                        modifier = Modifier.weight(1.1f)
                    ) {
                        Text(if (currentIndex < vocabularyList.lastIndex) "Next" else "Restart")
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionIconButton(
    onClick: () -> Unit,
    enabled: Boolean,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        icon()
    }
}

@Composable
private fun WritePromptCard(
    currentWord: VocabularyEntry
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Write the Japanese text for:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = currentWord.romaji,
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WritingPad(
    currentWord: VocabularyEntry,
    isAnswerVisible: Boolean,
    strokes: List<DrawStroke>,
    currentPath: Path?,
    onToggleAnswer: () -> Unit,
    onCurrentPathChange: (Path?) -> Unit,
    onStrokeFinished: (Path) -> Unit
) {
    val strokeColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.background.copy(alpha = 0.96f)
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val guideLineColor = onSurfaceVariantColor.copy(alpha = 0.12f)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Writing Pad",
                style = MaterialTheme.typography.titleMedium,
                color = strokeColor
            )

            Text(
                text = "Write by hand, then tap the answer panel to reveal or hide it.",
                style = MaterialTheme.typography.bodySmall,
                color = onSurfaceVariantColor
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp),
                shape = RoundedCornerShape(20.dp),
                color = backgroundColor
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            var activePath: Path? = null

                            detectDragGestures(
                                onDragStart = { offset: Offset ->
                                    activePath = Path().apply {
                                        moveTo(offset.x, offset.y)
                                    }
                                    onCurrentPathChange(
                                        Path().apply {
                                            activePath?.let { addPath(it) }
                                        }
                                    )
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    activePath?.lineTo(change.position.x, change.position.y)
                                    onCurrentPathChange(
                                        Path().apply {
                                            activePath?.let { addPath(it) }
                                        }
                                    )
                                },
                                onDragEnd = {
                                    activePath?.let { finishedPath ->
                                        onStrokeFinished(
                                            Path().apply { addPath(finishedPath) }
                                        )
                                    }
                                    activePath = null
                                    onCurrentPathChange(null)
                                },
                                onDragCancel = {
                                    activePath = null
                                    onCurrentPathChange(null)
                                }
                            )
                        }
                ) {
                    val sectionHeight = size.height / 4f

                    for (index in 1..3) {
                        val y = sectionHeight * index
                        drawLine(
                            color = guideLineColor,
                            start = Offset(x = 0f, y = y),
                            end = Offset(x = size.width, y = y),
                            strokeWidth = 2f
                        )
                    }

                    strokes.forEach { stroke ->
                        drawPath(
                            path = stroke.path,
                            color = strokeColor,
                            style = Stroke(
                                width = 10f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }

                    currentPath?.let { activePath ->
                        drawPath(
                            path = activePath,
                            color = strokeColor,
                            style = Stroke(
                                width = 10f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }

            Surface(
                onClick = onToggleAnswer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp),
                shape = RoundedCornerShape(20.dp),
                color = strokeColor.copy(alpha = 0.10f)
            ) {
                if (isAnswerVisible) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Correct Answer",
                                style = MaterialTheme.typography.labelLarge,
                                color = strokeColor
                            )

                            Icon(
                                imageVector = Icons.Filled.VisibilityOff,
                                contentDescription = null,
                                tint = strokeColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = currentWord.hiragana,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = currentWord.meaning ?: "No meaning added",
                            style = MaterialTheme.typography.bodyMedium,
                            color = onSurfaceVariantColor
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Visibility,
                                contentDescription = null,
                                tint = strokeColor.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )

                            Text(
                                text = "Tap to reveal the correct answer",
                                style = MaterialTheme.typography.bodyMedium,
                                color = onSurfaceVariantColor
                            )
                        }
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
