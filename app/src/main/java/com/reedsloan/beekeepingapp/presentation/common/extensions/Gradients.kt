package com.reedsloan.beekeepingapp.presentation.ui.custom_theme

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Modifier.gradient(start: Color? = null, end: Color? = null): Modifier = composed {
    if(start == null || end == null) {
    // gradient from secondary color to onPrimaryColor (top to bottom)
    background(
        Brush.verticalGradient(
            colors = listOf(
                customTheme.secondaryColor, customTheme.onPrimaryColor
            )
        )
    )
    } else {
        background(
            Brush.verticalGradient(
                colors = listOf(
                    start, end
                )
            )
        )
    }
}
