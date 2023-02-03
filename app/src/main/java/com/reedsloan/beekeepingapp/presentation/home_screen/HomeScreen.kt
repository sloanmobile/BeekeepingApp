package com.reedsloan.beekeepingapp.presentation.home_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIconDefaults.Text
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.presentation.common.CustomButton
import com.reedsloan.beekeepingapp.presentation.common.HiveViewModel
import com.reedsloan.beekeepingapp.presentation.common.NavigationBar

@Composable
fun HomeScreen(
    navController: NavController,
    hiveViewModel: HiveViewModel
) {
    Column(modifier = Modifier.testTag("HomeScreen")) {
        NavigationBar(navController = navController, hiveViewModel)
        Text("This is the home screen")
        // button to toggle isLoading
        CustomButton(onClick = {
            hiveViewModel.state = hiveViewModel.state.copy(
                isLoading = !hiveViewModel.state.isLoading
            )
        }) {
            Text("Toggle isLoading")
        }
    }
}