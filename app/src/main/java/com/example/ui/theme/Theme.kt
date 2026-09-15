package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ThemeGreen,
    onPrimary = Color(0xFF022C22),
    primaryContainer = ThemeGreenContainer,
    onPrimaryContainer = Color(0xFFD1FAE5),
    secondary = ThemeSkyBlue,
    onSecondary = Color(0xFF082F49),
    secondaryContainer = ThemeSkyBlueContainer,
    onSecondaryContainer = Color(0xFFE0F2FE),
    tertiary = ThemeMutedYellow,
    onTertiary = Color(0xFF422006),
    tertiaryContainer = ThemeMutedYellowContainer,
    onTertiaryContainer = Color(0xFFFEF9C3),
    background = ThemeBgDark,
    surface = ThemeSurfaceDark,
    surfaceVariant = ThemeSurfaceVariantDark,
    outline = ThemeBorderDark,
    outlineVariant = ThemeBorderLight,
    onSurface = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF94A3B8)
)

private val LightColorScheme = darkColorScheme(
    primary = ThemeGreen,
    onPrimary = Color(0xFF022C22),
    primaryContainer = ThemeGreenContainer,
    onPrimaryContainer = Color(0xFFD1FAE5),
    secondary = ThemeSkyBlue,
    onSecondary = Color(0xFF082F49),
    secondaryContainer = ThemeSkyBlueContainer,
    onSecondaryContainer = Color(0xFFE0F2FE),
    tertiary = ThemeMutedYellow,
    onTertiary = Color(0xFF422006),
    tertiaryContainer = ThemeMutedYellowContainer,
    onTertiaryContainer = Color(0xFFFEF9C3),
    background = ThemeBgDark,
    surface = ThemeSurfaceDark,
    surfaceVariant = ThemeSurfaceVariantDark,
    outline = ThemeBorderDark,
    outlineVariant = ThemeBorderLight,
    onSurface = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF94A3B8)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Futuristic cyber theme active by default
    dynamicColor: Boolean = false, // Preserve our handcrafted futuristic neon cyber theme
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
