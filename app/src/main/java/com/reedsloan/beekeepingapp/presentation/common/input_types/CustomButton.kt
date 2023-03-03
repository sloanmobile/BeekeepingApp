package com.reedsloan.beekeepingapp.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.reedsloan.beekeepingapp.presentation.ui.custom_theme.customTheme

@Composable
fun CustomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String? = null,
    textColor: Color = customTheme.onPrimaryColor,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(
        backgroundColor = customTheme.primaryColor, contentColor = customTheme.onPrimaryColor
    ),
    buttonBorder: BorderStroke? = null,
    elevation: ButtonElevation? = null,
    content: @Composable () -> Unit? = {},
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = buttonColors,
        enabled = enabled,
        border = buttonBorder,
        elevation = elevation
    ) {
        content()
        // text if provided
        text?.let {
            Text(
                text = it,
                color = textColor,
            )
        }
    }
}