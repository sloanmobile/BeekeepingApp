package com.reedsloan.beekeepingapp.presentation.hive_info

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reedsloan.beekeepingapp.MainActivity
import com.reedsloan.beekeepingapp.di.AppModule
import com.reedsloan.beekeepingapp.presentation.hives_screen.AddScreen
import com.reedsloan.beekeepingapp.presentation.viewmodel.hives.HiveViewModel
import com.reedsloan.beekeepingapp.presentation.home_screen.HomeScreen
import com.reedsloan.beekeepingapp.presentation.screens.Screen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class HiveInfoScreenTest {
    @get:Rule(order = 0)
    val rule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        rule.inject()
        composeRule.activity.setContent {
            val hiveViewModel = hiltViewModel<HiveViewModel>()
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Screen.HiveInfoScreen.route
            ) {
                composable(
                    route = Screen.HiveScreen.route
                ) {
                    AddScreen(navController, hiveViewModel)
                }
                composable(
                    route = Screen.HomeScreen.route
                ) {
                    HomeScreen(navController, hiveViewModel)
                }
                composable(
                    route = Screen.HiveInfoScreen.route
                ) {
                    HiveInfoScreen(navController, hiveViewModel)
                }
            }
        }
    }

    @Test
    fun checkHiveInfo() {
        // check that HiveInfoScreen is displayed
        composeRule.onNodeWithTag("HiveInfoScreen").assertExists()
    }

    @After
    fun tearDown() {
        composeRule.activity.finish()
    }

}