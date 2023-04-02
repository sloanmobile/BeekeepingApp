package com.reedsloan.beekeepingapp.presentation.common.navigation_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.presentation.viewmodel.hives.HiveViewModel
import com.reedsloan.beekeepingapp.presentation.home_screen.MenuState
import com.reedsloan.beekeepingapp.presentation.screens.Screen

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
                modifier = Modifier
                    .padding(8.dp)
                    .size(32.dp)
            )
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(8.dp)
                    .size(32.dp)
            )
        }
        Text(
            text = title,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}