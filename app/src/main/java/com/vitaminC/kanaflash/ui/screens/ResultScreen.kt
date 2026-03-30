package com.vitaminC.kanaflash.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    score: Int,
    total: Int,
    onBackToMenu: () -> Unit,
    onRetryQuiz: () -> Unit
) {
    val percentage = if (total > 0) (score * 100) / total else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Result Summary") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Quiz Complete",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Score: $score / $total",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Accuracy: $percentage%",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = onRetryQuiz,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Retry Quiz")
            }

            OutlinedButton(
                onClick = onBackToMenu,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Menu")
            }
        }
    }
}
