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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vitaminC.kanaflash.data.entity.Deck
import com.vitaminC.kanaflash.ui.components.KanaFlashBottomBar
import com.vitaminC.kanaflash.ui.navigation.AppSection
import com.vitaminC.kanaflash.ui.viewmodel.DeckViewModel
import com.vitaminC.kanaflash.ui.viewmodel.DeckViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecksScreen(
    factory: DeckViewModelFactory,
    onHomeClick: () -> Unit,
    onLearnClick: () -> Unit,
    onDeckClick: (Long) -> Unit
) {
    val viewModel: DeckViewModel = viewModel(factory = factory)
    val deckList by viewModel.deckList.collectAsStateWithLifecycle()
    val deckWordCounts by viewModel.deckWordCounts.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var deckPendingRename by remember { mutableStateOf<Deck?>(null) }
    var deckPendingDelete by remember { mutableStateOf<Deck?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Your Decks") },
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
                modifier = Modifier.padding(bottom = 8.dp),
                containerColor = Color(0xFF5F7F5F).copy(alpha = 0.9f),
                contentColor = Color(0xFFFFFBF5)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add deck"
                )
            }
        }
    ) { innerPadding ->
        if (deckList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No decks yet",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Create a deck to organize vocabulary into separate study sets.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
                )

                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Color(0xFF5F7F5F).copy(alpha = 0.9f),
                    contentColor = Color(0xFFFFFBF5)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create first deck"
                    )
                }
            }
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
                items(deckList, key = { it.id }) { deck ->
                    DeckItemCard(
                        deck = deck,
                        wordCount = deckWordCounts[deck.id] ?: 0,
                        canDelete = deckList.size > 1,
                        onClick = { onDeckClick(deck.id) },
                        onEditClick = { deckPendingRename = deck },
                        onDeleteClick = { deckPendingDelete = deck }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        DeckTitleDialog(
            title = "Create Deck",
            confirmLabel = "Create",
            initialTitle = "",
            supportingText = "Give this deck a short title, like Greetings, School, or Travel.",
            onDismiss = { showAddDialog = false },
            onConfirm = { title ->
                viewModel.addDeck(title)
                showAddDialog = false
            }
        )
    }

    deckPendingRename?.let { deck ->
        DeckTitleDialog(
            title = "Rename Deck",
            confirmLabel = "Save",
            initialTitle = deck.title,
            supportingText = "Update the deck title below.",
            onDismiss = { deckPendingRename = null },
            onConfirm = { newTitle ->
                viewModel.renameDeck(deck, newTitle)
                deckPendingRename = null
            }
        )
    }

    deckPendingDelete?.let { deck ->
        val wordCount = deckWordCounts[deck.id] ?: 0

        AlertDialog(
            onDismissRequest = { deckPendingDelete = null },
            title = { Text("Delete Deck") },
            text = {
                Text(
                    "Deleting \"${deck.title}\" will also delete $wordCount word" +
                            if (wordCount == 1) "" else "s" +
                                    ". This cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteDeck(deck)
                        deckPendingDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deckPendingDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (deckList.size == 1 && deckPendingDelete == null) {
        // no-op, just keeps the last-deck rule explicit in the screen logic
    }
}

@Composable
private fun DeckItemCard(
    deck: Deck,
    wordCount: Int,
    canDelete: Boolean,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = deck.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "$wordCount word" + if (wordCount == 1) "" else "s",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Tap to manage words",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Rename deck",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    enabled = canDelete
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = if (canDelete) "Delete deck" else "Cannot delete last deck",
                        tint = if (canDelete) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DeckTitleDialog(
    title: String,
    confirmLabel: String,
    initialTitle: String,
    supportingText: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var deckTitle by remember(initialTitle, title) { mutableStateOf(initialTitle) }
    val trimmedTitle = deckTitle.trim()
    val isValid = trimmedTitle.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = deckTitle,
                    onValueChange = { deckTitle = it },
                    label = { Text("Deck Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(trimmedTitle) },
                enabled = isValid
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
