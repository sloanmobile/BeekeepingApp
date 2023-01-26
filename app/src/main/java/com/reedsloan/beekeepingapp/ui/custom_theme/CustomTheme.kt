package com.reedsloan.beekeepingapp.ui.custom_theme

/**
 * This class is used to store the colors for the custom theme.
 */
import androidx.compose.ui.graphics.Color

val defaultTheme = ThemeColors(
    primaryColor = Color(0xFFEBA937),
    onBackgroundText = Color(0xFF000000),
    backgroundColor = Color(0xFFFEFFEA),
    onBackgroundColor = Color(0xFF000000),
    onPrimaryText = Color(0xFF1C1C1C),
    onPrimaryColor = Color(0xFFFFFFFF),
    surfaceColor = Color(0xFFFFFFFF),
    onSurfaceColor = Color(0xFF000000),
    onSurfaceText = Color(0xFF1C1C1C),
    cancelColor = Color(0xFFFF4747),
    onCancelColor = Color(0xFFFFFFFF)
)

var customTheme = defaultTheme