package com.reedsloan.beekeepingapp.presentation.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.reedsloan.beekeepingapp.presentation.ui.custom_theme.customTheme

@Composable
fun LoadingIndicator(isLoading: Boolean) {
    val animatedAlpha = remember { Animatable(0F) }

    LaunchedEffect(key1 = isLoading) {
        animatedAlpha.animateTo(
            targetValue = if (isLoading) 1f else 0f, animationSpec = tween(
                durationMillis = 150, delayMillis = 150, easing = FastOutLinearInEasing
            )
        )
    }

    Column(
        Modifier
            .padding(8.dp)
            .size(32.dp)
            .alpha(animatedAlpha.value)
    ) {
        CircularProgressIndicator(
            color = customTheme.onPrimaryColor, strokeWidth = 4.dp
        )
    }
}