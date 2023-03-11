package com.reedsloan.beekeepingapp.presentation.common.extensions

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.reedsloan.beekeepingapp.presentation.ui.custom_theme.gradient

fun Modifier.surfaceStyle(): Modifier = composed {
    clip(RoundedCornerShape(16.dp)).gradient()
}