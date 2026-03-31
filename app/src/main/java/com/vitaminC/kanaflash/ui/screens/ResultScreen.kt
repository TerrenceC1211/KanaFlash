package com.vitaminC.kanaflash.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vitaminC.kanaflash.ui.components.KanaFlashBottomBar
import com.vitaminC.kanaflash.ui.navigation.AppSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    score: Int,
    total: Int,
    onDeckClick: () -> Unit,
    onHomeClick: () -> Unit,
    onLearnClick: () -> Unit,
    onRetryQuiz: () -> Unit
) {
    val percentage = if (total > 0) (score * 100) / total else 0

    val performanceTitle = when {
        percentage >= 90 -> "Excellent work"
        percentage >= 70 -> "Strong progress"
        percentage >= 50 -> "Nice effort"
        else -> "Keep going"
    }

    val performanceMessage = when {
        percentage >= 90 -> "Your recall is looking very strong. Keep the streak going with another round."
        percentage >= 70 -> "You are building solid recognition. A few more reps will make it even more automatic."
        percentage >= 50 -> "You are getting there. A bit more repetition should help the tricky words stick."
        else -> "This is part of the process. Another short review session will help build familiarity."
    }

    val percentageLabel = when {
        percentage >= 90 -> "Mastery"
        percentage >= 70 -> "Solid"
        percentage >= 50 -> "Growing"
        else -> "Practice"
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Result Summary") },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(backgroundBrush)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Surface(
                    modifier = Modifier.padding(top = 8.dp),
                    shape = RoundedCornerShape(999.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                ) {
                    Text(
                        text = "Quiz Complete",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }

                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier.size(156.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                    ) {}

                    Surface(
                        modifier = Modifier.size(122.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$percentage%",
                                style = MaterialTheme.typography.headlineLarge,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = percentageLabel,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(34.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = performanceTitle,
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Text(
                            text = "Score: $score / $total",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = performanceMessage,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ) {
                    Text(
                        text = if (percentage >= 70) {
                            "Your deck review is paying off. Try another quiz or switch to flashcards for quick reinforcement."
                        } else {
                            "Short, repeated sessions work best. Review a few cards, then come back for another quiz attempt."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)
                    )
                }

                Button(
                    onClick = onRetryQuiz,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Retry Quiz")
                }

                OutlinedButton(
                    onClick = onHomeClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back to Home")
                }
            }
        }
    }
}
