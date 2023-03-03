package com.reedsloan.beekeepingapp.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.reedsloan.beekeepingapp.presentation.ui.custom_theme.customTheme
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun ScrollColumn(
    width: Dp,
    height: Dp,
    lazyListState: LazyListState,
    items: List<String>,
    itemCount: Int,
    selectedItem: String,
    textStyle: TextStyle = TextStyle.Default,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.SpaceEvenly,
) {
    LazyColumn(
        modifier = Modifier
            .width(width)
            .height(height),
        flingBehavior = rememberSnapperFlingBehavior(lazyListState = lazyListState),
        state = lazyListState,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        items(itemCount) {
            val item = items[it % items.size]
            val fontColor = when (item) {
                selectedItem -> customTheme.onBackgroundText
                else -> customTheme.onBackgroundText.copy(alpha = 0.5f)
            }
            Text(item, style = textStyle.copy(color = fontColor))
        }
    }
}