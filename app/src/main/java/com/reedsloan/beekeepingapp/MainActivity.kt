package com.reedsloan.beekeepingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reedsloan.beekeepingapp.presentation.add_page.AddScreen
import com.reedsloan.beekeepingapp.presentation.common.HiveViewModel
import com.reedsloan.beekeepingapp.presentation.common.WorkInProgressOverlayText
import com.reedsloan.beekeepingapp.presentation.hive_info.HiveInfoScreen
import com.reedsloan.beekeepingapp.presentation.home_screen.HomeScreen
import com.reedsloan.beekeepingapp.presentation.screens.Screen
import com.reedsloan.beekeepingapp.presentation.settings.SettingsScreen
import com.reedsloan.beekeepingapp.ui.custom_theme.customTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val hiveViewModel = hiltViewModel<HiveViewModel>()
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = customTheme.backgroundColor
            ) {
                val isDebugBuild by remember { mutableStateOf(BuildConfig.DEBUG) }

                if(isDebugBuild) {
                    WorkInProgressOverlayText()
                }

                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.HomeScreen.route
                ) {
                    composable(
                        route = Screen.HomeScreen.route
                    ) {
                        HomeScreen(navController, hiveViewModel)
                    }
                    composable(
                        route = Screen.AddScreen.route
                    ) {
                        AddScreen(navController, hiveViewModel)
                    }
                    // hiveinfoscreen
                    composable(
                        route = Screen.HiveInfoScreen.route,
                    ) {
                        HiveInfoScreen(navController, hiveViewModel)
                    }
                    // settings screen
                    composable(
                        route = Screen.SettingsScreen.route
                    ) {
                        SettingsScreen(navController, hiveViewModel)
                    }
                }}

        }
    }
}