package com.vitaminC.kanaflash.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vitaminC.kanaflash.data.entity.VocabularyEntry
import com.vitaminC.kanaflash.ui.components.KanaFlashBottomBar
import com.vitaminC.kanaflash.ui.navigation.AppSection
import com.vitaminC.kanaflash.ui.viewmodel.VocabularyViewModel
import com.vitaminC.kanaflash.ui.viewmodel.VocabularyViewModelFactory

private val JapaneseTextRegex = Regex("^[ぁ-ゖァ-ヶ一-龯々ー\\s]+$")
private val RomajiRegex = Regex("^[A-Za-z]+(?:[ '-][A-Za-z]+)*$")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(
    factory: VocabularyViewModelFactory,
    onHomeClick: () -> Unit,
    onLearnClick: () -> Unit
) {
    val viewModel: VocabularyViewModel = viewModel(factory = factory)
    val vocabularyList by viewModel.vocabularyList.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<VocabularyEntry?>(null) }
    var entryPendingDelete by remember { mutableStateOf<VocabularyEntry?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Vocabulary Deck") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            KanaFlashBottomBar(
                activeSection = AppSection.DECK,
                onDeckClick = { },
                onHomeClick = onHomeClick,
                onLearnClick = onLearnClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add vocabulary"
                )
            }
        }
    ) { innerPadding ->
        if (vocabularyList.isEmpty()) {
            EmptyVocabularyState(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                onAddWordClick = { showAddDialog = true }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 12.dp,
                    bottom = 108.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    ) {
                        Text(
                            text = "${vocabularyList.size} saved word" + if (vocabularyList.size == 1) "" else "s",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }

                items(vocabularyList, key = { it.id }) { entry ->
                    VocabularyItemCard(
                        entry = entry,
                        onEditClick = { editingEntry = entry },
                        onDeleteClick = { entryPendingDelete = entry }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        VocabularyEntryDialog(
            title = "Add Vocabulary",
            confirmLabel = "Save & Add Next",
            initialRomaji = "",
            initialHiragana = "",
            initialMeaning = "",
            keepOpenAfterConfirm = true,
            onDismiss = { showAddDialog = false },
            onConfirm = { romaji, hiragana, meaning ->
                viewModel.addEntry(romaji, hiragana, meaning)
            }
        )
    }

    editingEntry?.let { entry ->
        VocabularyEntryDialog(
            title = "Edit Vocabulary",
            confirmLabel = "Update",
            initialRomaji = entry.romaji,
            initialHiragana = entry.hiragana,
            initialMeaning = entry.meaning.orEmpty(),
            keepOpenAfterConfirm = false,
            onDismiss = { editingEntry = null },
            onConfirm = { romaji, hiragana, meaning ->
                viewModel.updateEntry(entry.id, romaji, hiragana, meaning)
                editingEntry = null
            }
        )
    }

    entryPendingDelete?.let { entry ->
        AlertDialog(
            onDismissRequest = { entryPendingDelete = null },
            title = { Text("Delete Vocabulary") },
            text = {
                Text("Are you sure you want to delete \"${entry.hiragana} (${entry.romaji})\"?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteEntry(entry)
                        entryPendingDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { entryPendingDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun EmptyVocabularyState(
    modifier: Modifier = Modifier,
    onAddWordClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Start building your deck",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Add Japanese text, Romaji, and optional meaning so your saved words can appear across study mode, quiz mode, and home preview.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
        )

        FloatingActionButton(onClick = onAddWordClick) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add first vocabulary"
            )
        }
    }
}

@Composable
private fun VocabularyItemCard(
    entry: VocabularyEntry,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = entry.hiragana,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = entry.romaji,
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = entry.meaning ?: "No meaning added",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                modifier = Modifier.padding(start = 12.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp)
                ) {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit vocabulary",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete vocabulary",
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VocabularyEntryDialog(
    title: String,
    confirmLabel: String,
    initialRomaji: String,
    initialHiragana: String,
    initialMeaning: String,
    keepOpenAfterConfirm: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var romaji by remember(title, initialRomaji) { mutableStateOf(initialRomaji) }
    var hiragana by remember(title, initialHiragana) { mutableStateOf(initialHiragana) }
    var meaning by remember(title, initialMeaning) { mutableStateOf(initialMeaning) }

    val trimmedRomaji = romaji.trim()
    val trimmedHiragana = hiragana.trim()
    val trimmedMeaning = meaning.trim()

    val isHiraganaFilled = trimmedHiragana.isNotBlank()
    val isRomajiFilled = trimmedRomaji.isNotBlank()

    val hiraganaError = when {
        !isHiraganaFilled -> "Japanese text is required."
        !JapaneseTextRegex.matches(trimmedHiragana) -> "Use Japanese characters only."
        else -> null
    }


    val romajiError = when {
        !isRomajiFilled -> "Romaji is required."
        !RomajiRegex.matches(trimmedRomaji) -> "Use English letters only. Spaces, apostrophes, and hyphens are allowed."
        else -> null
    }

    val isValid = hiraganaError == null && romajiError == null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(title)
                Text(
                    text = if (keepOpenAfterConfirm) {
                        "Save adds the current word and clears the form so you can enter the next one."
                    } else {
                        "Update the saved word details below."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = hiragana,
                    onValueChange = { hiragana = it },
                    label = { Text("Japanese") },
                    supportingText = {
                        Text(hiraganaError ?: "Example: こんにちは / コンニチハ / 今日")
                    },
                    isError = hiraganaError != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )


                OutlinedTextField(
                    value = romaji,
                    onValueChange = { romaji = it },
                    label = { Text("Romaji") },
                    supportingText = {
                        Text(romajiError ?: "Example: konnichiwa")
                    },
                    isError = romajiError != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = meaning,
                    onValueChange = { meaning = it },
                    label = { Text("Meaning (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (!isValid) return@TextButton

                    onConfirm(trimmedRomaji, trimmedHiragana, trimmedMeaning)

                    if (keepOpenAfterConfirm) {
                        romaji = ""
                        hiragana = ""
                        meaning = ""
                    }
                },
                enabled = isValid
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (keepOpenAfterConfirm) "Done" else "Cancel")
            }
        }
    )
}
