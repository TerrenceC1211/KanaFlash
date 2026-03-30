package com.vitaminC.kanaflash.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.IconButton

import com.vitaminC.kanaflash.data.entity.VocabularyEntry
import com.vitaminC.kanaflash.ui.viewmodel.VocabularyViewModel
import com.vitaminC.kanaflash.ui.viewmodel.VocabularyViewModelFactory

import com.vitaminC.kanaflash.ui.components.KanaFlashBottomBar
import com.vitaminC.kanaflash.ui.navigation.AppSection

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
                onClick = { showAddDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add vocabulary"
                )
            }
        }



    ) { innerPadding ->
        if (vocabularyList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Your deck is empty.",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Start by adding a Romaji and Hiragana pair to build your study list.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
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
            initialRomaji = "",
            initialHiragana = "",
            initialMeaning = "",
            onDismiss = { showAddDialog = false },
            onConfirm = { romaji, hiragana, meaning ->
                viewModel.addEntry(romaji, hiragana, meaning)
                showAddDialog = false
            }
        )
    }

    editingEntry?.let { entry ->
        VocabularyEntryDialog(
            title = "Edit Vocabulary",
            initialRomaji = entry.romaji,
            initialHiragana = entry.hiragana,
            initialMeaning = entry.meaning.orEmpty(),
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
            title = {
                Text("Delete Vocabulary")
            },
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
                TextButton(
                    onClick = { entryPendingDelete = null }
                ) {
                    Text("Cancel")
                }
            }
        )
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
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
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

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 12.dp)
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

@Composable
private fun VocabularyEntryDialog(
    title: String,
    initialRomaji: String,
    initialHiragana: String,
    initialMeaning: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var romaji by remember { mutableStateOf(initialRomaji) }
    var hiragana by remember { mutableStateOf(initialHiragana) }
    var meaning by remember { mutableStateOf(initialMeaning) }

    val isValid = romaji.trim().isNotBlank() && hiragana.trim().isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = hiragana,
                    onValueChange = { hiragana = it },
                    label = { Text("Hiragana") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = romaji,
                    onValueChange = { romaji = it },
                    label = { Text("Romaji") },
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
                onClick = { onConfirm(romaji, hiragana, meaning) },
                enabled = isValid
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
