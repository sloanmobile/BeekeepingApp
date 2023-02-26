package com.reedsloan.beekeepingapp.presentation.ui.custom_theme

/**
 * This class is used to store the colors for the custom theme.
 */
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val defaultTheme
    @Composable
    get() = ThemeColors(
        primaryColor = Color(0xFFEBA937),
        onBackgroundText = Color(0xFF000000),
        backgroundColor = Color(0xFFF1F1F1),
        onBackgroundColor = Color(0xFF000000),
        onPrimaryText = Color(0xFF1C1C1C),
        onPrimaryColor = Color(0xFFFFFFFF),
        surfaceColor = Color(0xFFFFFFFF),
        onSurfaceColor = Color(0xFF000000),
        onSurfaceText = Color(0xFF1C1C1C),
        cancelColor = Color(0xFFFF4747),
        onCancelColor = Color(0xFFFFFFFF),
        secondaryColor = Color(0xFFFFF9E9),
        onSecondaryColor = Color(0xFF000000),
        onSecondaryText = Color(0xFF000000),
        primaryColorLight = Color(0xFFFACC57),
        hintColor = Color(0xFF616161)
    )

val customTheme
    @Composable
    get() = defaultTheme