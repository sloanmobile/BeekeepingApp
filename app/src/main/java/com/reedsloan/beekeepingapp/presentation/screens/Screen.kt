package com.reedsloan.beekeepingapp.presentation.screens

sealed class Screen(val route: String, val name: String) {
    object HomeScreen: Screen("home_screen", "Home")
    object HiveScreen: Screen("hive_screen", "Hives")
    object SplashScreen: Screen("splash_screen", "Splash")
    object HiveDetailsScreen: Screen("hive_details_screen", "Hive Info")

    object SettingsScreen: Screen("settings_screen", "Settings")
}
