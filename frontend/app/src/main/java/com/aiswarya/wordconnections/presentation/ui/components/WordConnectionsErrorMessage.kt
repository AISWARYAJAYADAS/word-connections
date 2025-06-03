package com.aiswarya.wordconnections.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    modifier: Modifier = Modifier
) {
    var showError by remember { mutableStateOf(true) }
    val haptic = LocalHapticFeedback.current

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
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error,
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

// Preview parameter provider for different error messages
class ErrorMessageProvider : PreviewParameterProvider<String> {
    override val values = sequenceOf(
        "Failed to load puzzle",
        "Network connection error. Please check your internet connection and try again.",
        "Server is temporarily unavailable",
        "An unexpected error occurred"
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
            onRetry = { }
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
            message = "Failed to load puzzle. Please check your connection.",
            onDismiss = { },
            onRetry = { }
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
            message = "A very long error message that should wrap properly within the card container and demonstrate how the layout handles extended text content gracefully.",
            onDismiss = { },
            onRetry = { }
        )
    }
}