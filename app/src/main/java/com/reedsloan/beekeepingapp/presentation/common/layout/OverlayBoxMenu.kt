package com.reedsloan.beekeepingapp.presentation.common.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.reedsloan.beekeepingapp.presentation.ui.custom_theme.customTheme

/**
 * A customizable menu that overlays the entire parent [Box] with the [content] in the center.
 *
 * Must be a direct child element of a [Box].
 */
@Composable
fun OverlayBoxMenu(
    onTapOutside: () -> Unit = {},
    onTapInside: () -> Unit = {},
    height: Dp = 350.dp,
    width: Dp = 300.dp,
    backgroundColor: Color = customTheme.surfaceColor,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier
        .alpha(0.5F)
        .fillMaxSize()
        .background(Color.Black)
        .zIndex(1F)
        .pointerInput(Unit) {
            detectTapGestures(
                // Consume the tap event so that the screen doesn't get tapped.
                onTap = {
                    onTapOutside()
                })
        })
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .alpha(1F)
            .background(backgroundColor)
            .width(width)
            .height(height)
            .zIndex(2F)
            .pointerInput(Unit) {
                detectTapGestures(
                    // Consume the tap event so that the screen doesn't get tapped.
                    onTap = {
                        onTapInside()
                    })
            }) {
        content()
    }
}