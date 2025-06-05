package com.aiswarya.wordconnections.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun WordConnectionsErrorMessage(
    message: String,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    showRetryButton: Boolean = true
) {
    var showError by remember { mutableStateOf(true) }
    val haptic = LocalHapticFeedback.current
    val isOfflineMessage = message.contains("Playing offline")

    LaunchedEffect(showError) {
        if (showError) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            delay(5000) // Auto-dismiss after 5 seconds
            showError = false
            onDismiss()
        }
    }

    if (showError) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        ) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .widthIn(max = 280.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isOfflineMessage) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.errorContainer,
                    contentColor = if (isOfflineMessage) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onErrorContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = if (isOfflineMessage) Icons.Default.Info else Icons.Default.Warning,
                        contentDescription = if (isOfflineMessage) "Info" else "Error",
                        tint = if (isOfflineMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(32.dp)
                    )

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                showError = false
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Dismiss")
                        }

                        if (showRetryButton) {
                            Button(
                                onClick = {
                                    showError = false
                                    onRetry()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                )
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Preview parameter provider for different error messages
class ErrorMessageProvider : PreviewParameterProvider<String> {
    override val values = sequenceOf(
        "Failed to load puzzle",
        "No internet connection. Please check your network.",
        "Playing offline with a cached puzzle.",
        "Playing offline with a default puzzle."
    )
}

@Preview(
    name = "Error Message - Light Theme",
    showBackground = true,
    backgroundColor = 0xFF6200EE
)
@Composable
private fun WordConnectionsErrorMessagePreview(
    @PreviewParameter(ErrorMessageProvider::class) message: String
) {
    MaterialTheme {
        WordConnectionsErrorMessage(
            message = message,
            onDismiss = { },
            onRetry = { },
            showRetryButton = !message.contains("Playing offline")
        )
    }
}

@Preview(
    name = "Error Message - Dark Theme",
    showBackground = true,
    backgroundColor = 0xFF121212,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun WordConnectionsErrorMessageDarkPreview() {
    MaterialTheme {
        WordConnectionsErrorMessage(
            message = "Playing offline with a default puzzle.",
            onDismiss = { },
            onRetry = { },
            showRetryButton = false
        )
    }
}

@Preview(
    name = "Error Message - Long Text",
    showBackground = true,
    backgroundColor = 0xFF6200EE
)
@Composable
private fun WordConnectionsErrorMessageLongTextPreview() {
    MaterialTheme {
        WordConnectionsErrorMessage(
            message = "No internet connection. Playing offline with a default puzzle to ensure you can continue enjoying the game.",
            onDismiss = { },
            onRetry = { },
            showRetryButton = false
        )
    }
}