package com.reedsloan.beekeepingapp.presentation.home_screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.presentation.common.NavigationBar

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    NavigationBar(navController = navController, homeViewModel)
}