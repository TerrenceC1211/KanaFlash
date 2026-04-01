package com.vitaminC.kanaflash.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val StudyGreen = Color(0xFF5F7F5F)
val StudyCream = Color(0xFFFFFBF5)
val StudySoftSurface = Color(0xFFD7DECF)

@Composable
fun StudyPrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = StudyGreen,
            contentColor = StudyCream,
            disabledContainerColor = StudyGreen.copy(alpha = 0.35f),
            disabledContentColor = StudyCream.copy(alpha = 0.75f)
        ),
        content = content
    )
}

@Composable
fun StudyOutlineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
        border = BorderStroke(1.dp, StudyGreen.copy(alpha = if (enabled) 1f else 0.35f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = StudyGreen,
            disabledContentColor = StudyGreen.copy(alpha = 0.35f),
            containerColor = MaterialTheme.colorScheme.surface
        ),
        content = content
    )
}

@Composable
fun StudyIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    FilledIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = StudySoftSurface,
            contentColor = StudyGreen,
            disabledContainerColor = StudySoftSurface.copy(alpha = 0.55f),
            disabledContentColor = StudyGreen.copy(alpha = 0.35f)
        ),
        content = content
    )
}
