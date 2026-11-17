package com.example.swapcampus_isaaclee.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD92B2B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),
    secondary = Color(0xFF006B5E),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD1F2EB),
    onSecondaryContainer = Color(0xFF00201B),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    surfaceContainerLowest = Color(0xFFF6F7F9),
    surfaceContainerLow = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFF0F2F5),
    surfaceContainerHigh = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1C1E),
    onSurfaceVariant = Color(0xFF5A6068),
    outlineVariant = Color(0xFFE0E2E7)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB4AB),
    onPrimary = Color(0xFF690005),
    primaryContainer = Color(0xFF93000A),
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = Color(0xFF7AD6C5),
    onSecondary = Color(0xFF003730),
    secondaryContainer = Color(0xFF005046),
    onSecondaryContainer = Color(0xFF96F3E1),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    surfaceContainerLowest = Color(0xFF111315),
    surfaceContainerLow = Color(0xFF191C1E),
    surfaceContainer = Color(0xFF1E2124),
    surfaceContainerHigh = Color(0xFF282B2F),
    onSurface = Color(0xFFE2E2E6),
    onSurfaceVariant = Color(0xFFC4C6D0),
    outlineVariant = Color(0xFF44474E)
)

@Composable
fun SwapCampusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

