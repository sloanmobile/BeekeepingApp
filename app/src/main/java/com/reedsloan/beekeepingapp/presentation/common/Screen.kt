package com.reedsloan.beekeepingapp.presentation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Hive
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(val route: String, val displayText: String, val icon: ImageVector?, val isBottomNav: Boolean = true) {
    HomeScreen("home_screen", "Home", Icons.Rounded.Home),
    SignInScreen("sign_in_screen", "Sign In", null, false),
    ApiariesScreen("apiaries_screen", "Apiaries", Icons.Rounded.Hive, false),
    HivesScreen("hives_screen", "Hives", Icons.Rounded.Hive, false),
    SettingsScreen("settings_screen", "Settings", Icons.Rounded.Settings),
    LogInspectionScreen("log_inspection_screen", "Log Inspection", Icons.Rounded.Hive, false),
    HiveDetailsScreen("hive_details_screen", "Hive Details", Icons.Rounded.Hive, false),
    InspectionsScreen("inspection_screen", "Inspections", Icons.Rounded.Hive, false),
    TasksScreen("tasks_screen", "Tasks", Icons.Rounded.Hive, false),
}
