package com.reedsloan.beekeepingapp.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.presentation.home_screen.HomeViewModel
import com.reedsloan.beekeepingapp.presentation.home_screen.MenuState
import com.reedsloan.beekeepingapp.presentation.screens.Screen
import com.reedsloan.beekeepingapp.ui.custom_theme.customTheme


@Composable
fun NavigationBar(navController: NavController, homeViewModel: HomeViewModel) {
    val state = homeViewModel.state
    val scope = rememberCoroutineScope()
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(customTheme.primaryColor)
                .height(64.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // hamburger menu
            Icon(
                imageVector = when (state.menuState) {
                    MenuState.Closed -> Icons.Default.Menu
                    MenuState.Open -> Icons.Default.ExpandLess
                },
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        homeViewModel.toggleMenu()
                    }
                    .background(customTheme.onPrimaryColor, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                tint = customTheme.onPrimaryText
            )
            Text(
                text = "Beekeeping App",
                fontWeight = Bold,
                color = customTheme.onPrimaryColor,
                fontSize = 24.sp,
            )
            Spacer(modifier = Modifier.width(48.dp))
        }
        if (state.menuState == MenuState.Open) {
            Menu(navController, homeViewModel)
        }
    }
}

@Composable
fun Menu(navController: NavController, homeViewModel: HomeViewModel) {
    val state = homeViewModel.state
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(customTheme.primaryColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // home screen
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, customTheme.onPrimaryColor, RoundedCornerShape(8.dp))
                .height(48.dp)
                .padding(horizontal = 16.dp)
                .clickable {
                    homeViewModel.toggleMenu()
                    navController.navigate(Screen.HomeScreen.route)
                }
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                tint = customTheme.onPrimaryColor
            )
            Text(
                text = "Home",
                color = customTheme.onPrimaryColor,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        // add screen
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, customTheme.onPrimaryColor, RoundedCornerShape(8.dp))
                .height(48.dp)
                .padding(horizontal = 16.dp)
                .clickable {
                    homeViewModel.toggleMenu()
                    navController.navigate(Screen.AddScreen.route)
                }
        ) {
            Icon(
                imageVector = Icons.Default.Hive,
                contentDescription = null,
                tint = customTheme.onPrimaryColor
            )
            Text(
                text = "Test Screen",
                color = customTheme.onPrimaryColor,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}