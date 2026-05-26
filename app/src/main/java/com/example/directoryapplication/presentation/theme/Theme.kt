package com.example.directoryapplication.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    secondary = BrandSecondary,
    background = BrandBackground,
    surface = BrandSurface,
    onPrimary = BrandSurface,
    onBackground = BrandDarkBlue,
    onSurface = BrandDarkBlue,
    error = ErrorRed,
    errorContainer = ErrorContainer,
    onErrorContainer = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkBackground,
    onBackground = DarkText,
    onSurface = DarkText,
    error = DarkErrorRed,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkText
)

@Composable
fun DirectoryApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}