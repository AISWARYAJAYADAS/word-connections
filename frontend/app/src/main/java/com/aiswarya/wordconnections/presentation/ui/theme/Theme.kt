package com.aiswarya.wordconnections.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aiswarya.wordconnections.R

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1E88E5), // Vibrant Blue
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3F2FD), // Light Blue
    onPrimaryContainer = Color(0xFF0D47A1),
    secondary = Color(0xFF43A047), // Green
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC8E6C9), // Light Green
    onSecondaryContainer = Color(0xFF1B5E20),
    tertiary = Color(0xFF7B1FA2), // Purple
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE1BEE7), // Light Purple
    onTertiaryContainer = Color(0xFF4A0072),
    background = Color(0xFFF8FAFC), // Soft White
    onBackground = Color(0xFF1A1C1E),
    surface = Color.White,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFECEFF1),
    onSurfaceVariant = Color(0xFF424242),
    error = Color(0xFFD32F2F),
    onError = Color.White,
    errorContainer = Color(0xFFFFCDD2),
    onErrorContainer = Color(0xFFB71C1C),
    outline = Color(0xFFB0BEC5)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6), // Light Blue
    onPrimary = Color(0xFF0D47A1),
    primaryContainer = Color(0xFF1976D2),
    onPrimaryContainer = Color(0xFFE3F2FD),
    secondary = Color(0xFF81C784), // Light Green
    onSecondary = Color(0xFF1B5E20),
    secondaryContainer = Color(0xFF388E3C),
    onSecondaryContainer = Color(0xFFC8E6C9),
    tertiary = Color(0xFFCE93D8), // Light Purple
    onTertiary = Color(0xFF4A0072),
    tertiaryContainer = Color(0xFF7B1FA2),
    onTertiaryContainer = Color(0xFFE1BEE7),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFB0BEC5),
    error = Color(0xFFEF5350),
    onError = Color(0xFF212121),
    errorContainer = Color(0xFFB71C1C),
    onErrorContainer = Color(0xFFFFCDD2),
    outline = Color(0xFF78909C)
)

private val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

private val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)

@Composable
fun WordConnectionsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = AppTypography,
        shapes = Shapes(
            extraSmall = RoundedCornerShape(12.dp),
            small = RoundedCornerShape(16.dp),
            medium = RoundedCornerShape(20.dp),
            large = RoundedCornerShape(28.dp),
            extraLarge = RoundedCornerShape(36.dp)
        ),
        content = content
    )
}