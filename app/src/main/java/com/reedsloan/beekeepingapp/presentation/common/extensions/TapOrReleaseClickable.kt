package com.reedsloan.beekeepingapp.presentation.common.extensions

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlinx.coroutines.launch

fun Modifier.tapOrReleaseClickable(
    interactionSource: MutableInteractionSource, onTap: () -> Unit, onLongPress: () -> Unit
): Modifier = composed {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    pointerInput(interactionSource) {
        val pressInteraction = PressInteraction.Press(Offset.Zero)
        val releaseInteraction = PressInteraction.Release(pressInteraction)
        detectTapGestures(onPress = {
            interactionSource.tryEmit(pressInteraction)
            // await the release
            tryAwaitRelease()
            interactionSource.tryEmit(releaseInteraction)
        }, onTap = {
            onTap()
        }, onLongPress = {
            interactionSource.tryEmit(pressInteraction)
            scope.launch {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            onLongPress()
        })
    }
}