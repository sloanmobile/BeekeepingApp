package com.reedsloan.beekeepingapp.presentation.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.reedsloan.beekeepingapp.presentation.ui.custom_theme.customTheme

@Composable
fun CustomAnimatedCheckbox(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    val animatedColor = animateColorAsState(
        targetValue = if (checked) customTheme.primaryColor else Color.LightGray,
        animationSpec = tween(
            durationMillis = 200, easing = LinearOutSlowInEasing
        )
    ).value

    val animatedStrokeWidth = animateDpAsState(
        targetValue = if (checked) 0.dp else 2.dp, animationSpec = tween(
            durationMillis = 200, easing = LinearOutSlowInEasing
        )
    ).value

    val animatedIconSize = animateDpAsState(
        targetValue = if (checked) 20.dp else 0.dp, animationSpec = tween(
            durationMillis = 200, easing = LinearOutSlowInEasing
        )
    ).value

    val animatedIconColor = animateColorAsState(
        targetValue = if (checked) customTheme.onPrimaryColor else Color.LightGray,
        animationSpec = tween(
            durationMillis = 200, easing = LinearOutSlowInEasing
        )
    ).value

    val animatedIcon = animateDpAsState(
        targetValue = if (checked) 0.dp else 4.dp, animationSpec = tween(
            durationMillis = 200, easing = LinearOutSlowInEasing
        )
    ).value

    val animatedIconPadding = animateDpAsState(
        targetValue = if (checked) 0.dp else 4.dp, animationSpec = tween(
            durationMillis = 200, easing = LinearOutSlowInEasing
        )
    ).value

    val animatedIconAlpha = animateFloatAsState(
        targetValue = if (checked) 1f else 0f, animationSpec = tween(
            durationMillis = 200, easing = LinearOutSlowInEasing
        )
    ).value
    Box(modifier) {
        // checkbox
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(animatedColor, CircleShape)
                .border(
                    width = animatedStrokeWidth, color = Color.LightGray, shape = CircleShape
                )
                .padding(animatedIconPadding)
                .clip(CircleShape)
                .clickable {
                    onCheckedChange(!checked)
                },
            contentAlignment = Alignment.Center,
        ) {
            // icon
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = animatedIconColor,
                modifier = Modifier
                    .size(animatedIconSize)
                    .alpha(animatedIconAlpha)
            )
        }
    }
}