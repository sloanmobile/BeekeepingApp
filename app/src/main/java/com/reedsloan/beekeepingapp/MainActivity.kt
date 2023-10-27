package com.reedsloan.beekeepingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reedsloan.beekeepingapp.presentation.ApiariesScreen
import com.reedsloan.beekeepingapp.presentation.HiveDetailsScreen
import com.reedsloan.beekeepingapp.presentation.HomeScreen
import com.reedsloan.beekeepingapp.presentation.InspectionsScreen
import com.reedsloan.beekeepingapp.presentation.LogDataScreen
import com.reedsloan.beekeepingapp.presentation.QuickLogScreen
import com.reedsloan.beekeepingapp.presentation.common.Screen
import com.reedsloan.beekeepingapp.presentation.SettingsScreen
import com.reedsloan.beekeepingapp.presentation.WorkInProgressOverlayText
import com.reedsloan.beekeepingapp.presentation.ui.theme.AppTheme
import com.reedsloan.beekeepingapp.presentation.viewmodel.HiveViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val hiveViewModel = hiltViewModel<HiveViewModel>()
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val isDebugBuild by remember { mutableStateOf(BuildConfig.DEBUG) }

                    if (isDebugBuild) {
                        WorkInProgressOverlayText()
                    }

                    val navController = rememberNavController()

                    Column(
                        Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = {
                                    hiveViewModel.onTapOutside()
                                })
                            }) {

                        BackHandler {
                            hiveViewModel.backHandler(navController)
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = Screen.HomeScreen.route,
                            ) {
                                composable(
                                    route = Screen.HomeScreen.route
                                ) {
                                    HomeScreen(navController, hiveViewModel)
                                }
                                composable(
                                    route = Screen.LogDataScreen.route,
                                ) {
                                    LogDataScreen(navController, hiveViewModel)
                                }
                                composable(
                                    route = Screen.LogInspectionScreen.route,
                                ) {
                                    QuickLogScreen(navController, hiveViewModel)
                                }
                                composable(
                                    route = Screen.SettingsScreen.route
                                ) {
                                    SettingsScreen(navController, hiveViewModel)
                                }
                                composable(
                                    route = Screen.ApiariesScreen.route
                                ) {
                                    ApiariesScreen(navController, hiveViewModel)
                                }
                                composable(
                                    route = Screen.HiveDetailsScreen.route
                                ) {
                                    HiveDetailsScreen(navController, hiveViewModel)
                                }
                                composable(
                                    route = Screen.InspectionsScreen.route
                                ) {
                                    InspectionsScreen(navController, hiveViewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}