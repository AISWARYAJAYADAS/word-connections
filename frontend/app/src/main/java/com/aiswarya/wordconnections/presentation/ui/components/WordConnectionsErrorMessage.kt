package com.aiswarya.wordconnections.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun WordConnectionsErrorMessage(
    message: String,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showError by remember { mutableStateOf(true) }

    LaunchedEffect(showError) {
        if (showError) {
            delay(5000) // Auto-dismiss after 5 seconds
            showError = false
            onDismiss()
        }
    }

    if (showError) {
        Snackbar(
            modifier = modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .wrapContentHeight(),
            action = {
                TextButton(onClick = {
                    showError = false
                    onRetry()
                }) {
                    Text(
                        text = "Retry",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissAction = {
                TextButton(onClick = {
                    showError = false
                    onDismiss()
                }) {
                    Text(
                        text = "Dismiss",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            actionContentColor = MaterialTheme.colorScheme.primary,
            dismissActionContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}