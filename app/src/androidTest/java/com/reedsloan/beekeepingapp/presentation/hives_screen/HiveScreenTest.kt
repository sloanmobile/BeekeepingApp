package com.reedsloan.beekeepingapp.presentation.hives_screen

import androidx.activity.compose.setContent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reedsloan.beekeepingapp.MainActivity
import com.reedsloan.beekeepingapp.di.AppModule
import com.reedsloan.beekeepingapp.presentation.common.HiveViewModel
import com.reedsloan.beekeepingapp.presentation.home_screen.HomeScreen
import com.reedsloan.beekeepingapp.presentation.screens.Screen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class HiveScreenTest {
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
                startDestination = Screen.HiveScreen.route
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
            }
        }
    }

    @Test
    fun createHive() {
        composeRule.onNodeWithTag("HiveListItem").assertDoesNotExist()
        pressAddHiveButton()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("HiveListItem").assertExists()
    }

    @Test
    fun navigateToHomeScreen() {
        pressNavbarButton()
        pressHomeButton()
        // verify we are on the home screen
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("HiveListItem").assertDoesNotExist()
        composeRule.onNodeWithTag("HomeScreen").assertExists()
    }

    @Test
    fun deleteHive() {
        composeRule.onNodeWithTag("HiveListItem").assertDoesNotExist()
        pressAddHiveButton()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("HiveListItem").assertExists()
        composeRule.onNodeWithTag("HiveListItem").performTouchInput { longClick() }
        pressDeleteHiveButton()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("HiveListItem").assertDoesNotExist()
    }

    @Test
    fun deleteMultipleHives() {
        // add 3 hives
        repeat(3) { pressAddHiveButton() }
        longClickHiveListItem()
        // the first hive is automatically selected, so we need to select the other two hives
        composeRule.waitForIdle()
        for(i in 1..2) {
            tapHiveListItem(i)
        }
        // delete the hives
        pressDeleteHiveButton()
        // assert that the hives were deleted
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("HiveListItem").assertDoesNotExist()
    }

    private fun pressNavbarButton() {
        // click on the menu icon with content description "navbar"
        composeRule.onNodeWithTag("Navbar").performClick()
    }

    private fun pressHomeButton() {
        // click on the item with text "Home"
        composeRule.onNodeWithText("Home").performClick()
    }

    private fun pressAddHiveButton() {
        // add a hive by clicking on the button with tag AddHiveButton
        composeRule.onNodeWithTag("AddHiveButton").performClick()
    }

    private fun pressDeleteHiveButton() {
        // press button with the tag DeleteHiveButton
        composeRule.onNodeWithTag("DeleteHiveButton").performClick()
    }

    private fun tapHiveListItem(index: Int = 0) {
        // tap on the first HiveListItem
        composeRule.onAllNodesWithTag("HiveListItem")[index].performTouchInput { click() }
    }

    private fun longClickHiveListItem(index: Int = 0) {
        // long click on the first HiveListItem
        composeRule.onAllNodesWithTag("HiveListItem")[index].performTouchInput { longClick() }
    }

    @After
    fun tearDown() {
        composeRule.activity.finish()
    }
}