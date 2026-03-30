package com.vitaminC.kanaflash.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vitaminC.kanaflash.data.entity.VocabularyEntry
import com.vitaminC.kanaflash.ui.viewmodel.VocabularyViewModel
import com.vitaminC.kanaflash.ui.viewmodel.VocabularyViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(
    factory: VocabularyViewModelFactory
) {
    val viewModel: VocabularyViewModel = viewModel(factory = factory)
    val vocabularyList by viewModel.vocabularyList.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<VocabularyEntry?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KanaFlash Vocabulary") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Text("Add")
            }
        }
    ) { innerPadding ->
        if (vocabularyList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No vocabulary added yet.",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Tap the Add button to create your first entry.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vocabularyList, key = { it.id }) { entry ->
                    VocabularyItemCard(
                        entry = entry,
                        onEditClick = { editingEntry = entry },
                        onDeleteClick = { viewModel.deleteEntry(entry) }
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
}

@Composable
private fun VocabularyItemCard(
    entry: VocabularyEntry,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Romaji: ${entry.romaji}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Hiragana: ${entry.hiragana}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Meaning: ${entry.meaning ?: "-"}",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEditClick) {
                    Text("Edit")
                }
                TextButton(onClick = onDeleteClick) {
                    Text("Delete")
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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = romaji,
                    onValueChange = { romaji = it },
                    label = { Text("Romaji") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = hiragana,
                    onValueChange = { hiragana = it },
                    label = { Text("Hiragana") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = meaning,
                    onValueChange = { meaning = it },
                    label = { Text("Meaning (Optional)") },
                    modifier = Modifier.fillMaxWidth()
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
