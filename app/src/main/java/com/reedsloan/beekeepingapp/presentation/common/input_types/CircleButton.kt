package com.reedsloan.beekeepingapp.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.reedsloan.beekeepingapp.presentation.common.extensions.tapOrReleaseClickable
import com.reedsloan.beekeepingapp.presentation.ui.custom_theme.customTheme

@Composable
fun CircleButton(
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {},
    onLongPress: () -> Unit = {},
    icon: ImageVector? = null,
    iconColor: Color = customTheme.onPrimaryColor,
    backgroundColor: Color = customTheme.primaryColor,
    padding: Dp = 0.dp,
    size: Dp = 64.dp,
    content: @Composable () -> Unit? = {},
) {
    // replace with a column instead of a button
    val interactionSource = remember { MutableInteractionSource() }
    val indication = rememberRipple(bounded = false, radius = size / 2)

    Column(modifier = Modifier.padding(padding)) {
        Column(
            modifier = modifier
                .size(size)
                .background(backgroundColor, CircleShape)
                .tapOrReleaseClickable(
                    interactionSource = interactionSource, onTap = onTap, onLongPress = onLongPress
                )
                .indication(interactionSource, indication),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            content()
            // icon if provided
            if (icon != null) {
                Icon(
                    imageVector = icon, contentDescription = null, tint = iconColor
                )
            }
        }
    }
}