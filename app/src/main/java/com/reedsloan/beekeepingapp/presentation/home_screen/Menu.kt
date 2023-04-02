package com.reedsloan.beekeepingapp.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.R
import com.reedsloan.beekeepingapp.presentation.common.navigation_bar.NavbarEntry
import com.reedsloan.beekeepingapp.presentation.viewmodel.hives.HiveViewModel
import com.reedsloan.beekeepingapp.presentation.screens.Screen

@Composable
fun Menu(navController: NavController, hiveViewModel: HiveViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // home
        NavbarEntry(
            title = "Home",
            icon = Icons.Filled.Home,
            navController = navController,
            hiveViewModel = hiveViewModel,
            destination = Screen.HomeScreen,
            isSelected = navController.currentDestination?.route == Screen.HomeScreen.route
        )

        // hives
        NavbarEntry(
            title = "Hives",
            painterResource = painterResource(id = R.drawable.hive),
            navController = navController,
            hiveViewModel = hiveViewModel,
            destination = Screen.HiveScreen,
            isSelected = navController.currentDestination?.route == Screen.HiveScreen.route
        )

        // settings
        NavbarEntry(
            title = "Settings",
            icon = Icons.Filled.Settings,
            navController = navController,
            hiveViewModel = hiveViewModel,
            destination = Screen.SettingsScreen,
            isSelected = navController.currentDestination?.route == Screen.SettingsScreen.route
        )
    }
}