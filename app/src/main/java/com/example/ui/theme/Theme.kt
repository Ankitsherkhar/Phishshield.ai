package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PhishShieldColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = CyberNavyDarker,
    primaryContainer = CyberNavySurfaceVariant,
    onPrimaryContainer = ElectricCyan,
    secondary = NeonSky,
    onSecondary = CyberNavyDarker,
    secondaryContainer = CyberNavySurface,
    onSecondaryContainer = NeonSky,
    tertiary = NeonPurple,
    onTertiary = Color.White,
    background = CyberNavyBg,
    onBackground = TextPrimary,
    surface = CyberNavySurface,
    onSurface = TextPrimary,
    surfaceVariant = CyberNavySurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CyberCardBorder,
    error = RiskCriticalRed,
    onError = Color.White
)

@Composable
fun PhishShieldTheme(
    content: @Composable () -> Unit
) {
    // Professional Cybersecurity app strictly uses dark navy theme with neon accents
    MaterialTheme(
        colorScheme = PhishShieldColorScheme,
        typography = Typography,
        content = content
    )
}
