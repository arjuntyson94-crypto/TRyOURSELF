package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ==========================================
// Primary App Trio: Green, Sky Blue, Muted Yellow
// ==========================================
val ThemeGreen = Color(0xFF10B981) // Vibrant Emerald Green
val ThemeGreenLight = Color(0xFF34D399) // Soft Mint Green
val ThemeGreenDark = Color(0xFF059669) // Deep Forest Green
val ThemeGreenContainer = Color(0xFF064E3B)
val ThemeGreenSubtle = Color(0xFF022C22)

val ThemeSkyBlue = Color(0xFF38BDF8) // Electric Sky Blue
val ThemeSkyBluePrimary = Color(0xFF0EA5E9) // Sky 500
val ThemeSkyBlueDark = Color(0xFF0284C7) // Sky 600
val ThemeSkyBlueContainer = Color(0xFF0C4A6E)
val ThemeSkyBlueSubtle = Color(0xFF082F49)
val ThemeSkyBlueLight = Color(0xFFBAE6FD)

val ThemeMutedYellow = Color(0xFFEAB308) // Muted Warm Yellow
val ThemeMutedYellowLight = Color(0xFFFDE047) // Soft Butter Yellow
val ThemeMutedYellowGold = Color(0xFFFBBF24) // Muted Yellow Gold
val ThemeMutedYellowDark = Color(0xFFCA8A04) // Deep Amber Yellow
val ThemeMutedYellowContainer = Color(0xFF422006)
val ThemeMutedYellowSubtle = Color(0xFF2E1A04)

// Sky Blue Background & Surface System
val ThemeBgSkyBlue = Color(0xFF082F49) // Rich Sky Blue (Sky 950)
val ThemeBgSkyBlueTop = Color(0xFF0C4A6E) // Radiant Sky Blue (Sky 900)
val ThemeBgDark = Color(0xFF082F49) // Sky Blue Canvas
val ThemeSurfaceDark = Color(0xFF0A3C5E) // Sky Blue Card Surface
val ThemeSurfaceVariantDark = Color(0xFF062438) // Nested Sky Blue container
val ThemeBorderDark = Color(0xFF0284C7) // Sky Blue 600
val ThemeBorderLight = Color(0xFF38BDF8) // Sky Blue 400

val SkyBlueAppBackgroundBrush = Brush.verticalGradient(
    listOf(
        Color(0xFF0C4A6E), // Sky 900
        Color(0xFF082F49), // Sky 950
        Color(0xFF051C2C)  // Deep Sky Blue Base
    )
)

// Gradients combining Green, Sky Blue, and Muted Yellow
val AppTrioGradient = Brush.linearGradient(
    listOf(ThemeGreen, ThemeSkyBlue, ThemeMutedYellowGold)
)
val SkyGreenGradient = Brush.linearGradient(
    listOf(ThemeSkyBlue, ThemeGreen)
)
val YellowGreenGradient = Brush.linearGradient(
    listOf(ThemeMutedYellowGold, ThemeGreen)
)

// Legacy aliases mapped to the new Green, Sky Blue, Muted Yellow palette
val Emerald500 = ThemeGreen
val Emerald600 = ThemeGreenDark
val Emerald100 = Color(0xFFD1FAE5)

val Sky500 = ThemeSkyBluePrimary
val Sky600 = ThemeSkyBlueDark
val Sky100 = Color(0xFFE0F2FE)

val Amber500 = ThemeMutedYellow
val Amber600 = ThemeMutedYellowDark
val Amber100 = Color(0xFFFEF9C3)

val Indigo600 = ThemeSkyBluePrimary
val Indigo700 = ThemeSkyBlueDark
val Indigo900 = ThemeSkyBlueContainer
val Indigo50 = Color(0xFFE0F2FE)

val Violet500 = ThemeGreen
val Violet600 = ThemeGreenDark
val Violet100 = Color(0xFFD1FAE5)

val Rose500 = ThemeMutedYellowDark
val Rose100 = Color(0xFFFEF3C7)

val Slate950 = ThemeBgDark
val Slate900 = ThemeBgDark
val Slate800 = ThemeSurfaceDark
val Slate700 = ThemeSurfaceVariantDark
val Slate100 = Color(0xFFF1F5F9)
val Slate50 = Color(0xFFF8FAFC)

// Special pedagogical formula accent: Muted Yellow
val FormulaBracketGold = ThemeMutedYellowGold
val FormulaCardBg = Color(0xFF0A3C5E)
val FormulaCardBorder = ThemeMutedYellow

// Futuristic HUD Palette mapped to Green, Sky Blue, and Muted Yellow
val CyberVoid = ThemeBgDark
val CyberBg = ThemeBgDark
val CyberSurface = ThemeSurfaceDark
val CyberSurfaceVariant = ThemeSurfaceVariantDark
val CyberCardBorder = ThemeBorderDark

val CyberCyan = ThemeSkyBlue
val CyberCyanDim = ThemeSkyBlueDark
val CyberNeonPurple = ThemeGreen
val CyberElectricBlue = ThemeSkyBlue
val CyberMatrixGreen = ThemeGreen
val CyberLaserPink = ThemeMutedYellow
val CyberPlasmaGold = ThemeMutedYellowGold
val CyberHoloGradient = listOf(ThemeGreen, ThemeSkyBlue, ThemeMutedYellowGold)

