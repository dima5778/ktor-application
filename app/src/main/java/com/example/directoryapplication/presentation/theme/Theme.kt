package com.example.directoryapplication.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ModernColorScheme = lightColorScheme(
    primary = BrandPrimary,
    secondary = BrandSecondary,
    background = BrandBackground,
    surface = BrandSurface,
    onPrimary = BrandSurface,
    onBackground = BrandDarkBlue,
    onSurface = BrandDarkBlue,
    error = ErrorRed,
    errorContainer = ErrorContainer
)

@Composable
fun DirectoryApplicationTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ModernColorScheme,
        typography = Typography,
        content = content
    )
}