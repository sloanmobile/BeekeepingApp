package com.reedsloan.beekeepingapp.presentation.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Hive
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(val route: String, val displayText: String, val icon: ImageVector, val isBottomNav: Boolean = true) {
    HomeScreen("home_screen", "Home", Icons.Rounded.Home),
    ApiariesScreen("apiaries_screen", "Apiaries", Icons.Rounded.Hive, false),
    HivesScreen("hives_screen", "Hives", Icons.Rounded.Hive, false),
    SettingsScreen("settings_screen", "Settings", Icons.Rounded.Settings),
    SplashScreen("splash_screen", "Splash", Icons.Rounded.Home, false),
    LogDataScreen("log_data_screen", "Log Data", Icons.Rounded.Hive, false),
    QuickLogScreen("quick_log_screen", "Quick Log", Icons.Rounded.Hive, false),
    HiveDetailsScreen("hive_details_screen", "Hive Details", Icons.Rounded.Hive, false),
    InspectionsScreen("inspection_screen", "Inspections", Icons.Rounded.Hive, false),
}
