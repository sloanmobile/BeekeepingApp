package com.reedsloan.beekeepingapp.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.presentation.home_screen.MenuState
import com.reedsloan.beekeepingapp.presentation.screens.Screen
import com.reedsloan.beekeepingapp.presentation.ui.custom_theme.customTheme
import com.reedsloan.beekeepingapp.presentation.ui.theme.Typography

@Composable
fun NavbarEntry(
    title: String,
    icon: ImageVector? = null,
    painterResource: Painter? = null,
    navController: NavController,
    hiveViewModel: HiveViewModel,
    destination: Screen,
    isSelected: Boolean,
) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(
                if (isSelected) customTheme.primaryColor
                else customTheme.primaryColorLight, RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                if (hiveViewModel.state.navigationBarMenuState == MenuState.CLOSED) {
                    hiveViewModel.onTapNavigationExpandButton()
                    return@clickable
                }
                hiveViewModel.navigate(navController, destination)
            }) {
        if (painterResource != null) {
            Icon(
                painter = painterResource,
                contentDescription = null,
                tint = customTheme.onPrimaryColor,
                modifier = Modifier
                    .padding(8.dp)
                    .size(32.dp)
            )
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = customTheme.onPrimaryColor,
                modifier = Modifier
                    .padding(8.dp)
                    .size(32.dp)
            )
        }
        Text(
            text = title,
            style = Typography.h3,
            color = customTheme.onPrimaryColor,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}