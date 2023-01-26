package com.reedsloan.beekeepingapp.presentation.home_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.pointer.PointerIconDefaults.Text
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.presentation.common.HiveViewModel
import com.reedsloan.beekeepingapp.presentation.common.NavigationBar

@Composable
fun HomeScreen(
    navController: NavController,
    hiveViewModel: HiveViewModel
) {
    Column() {
        NavigationBar(navController = navController, hiveViewModel)
        Text("This is the home screen")
    }
}