package com.reedsloan.beekeepingapp.presentation.home_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@Composable
fun WorkInProgressOverlayText() {
    // the text should overlay the screen irregardless of where it is called
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1000f)
    ) {

        Text(
            text = "Work in progress.",
            color = Color.Black,
            fontSize = 36.sp,
            modifier = Modifier
                .alpha(0.05F)
                .align(Alignment.Center),
            maxLines = 1
        )
    }
}