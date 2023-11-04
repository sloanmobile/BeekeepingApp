package com.reedsloan.beekeepingapp.presentation.hives_screen

import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasAnyChild
import androidx.compose.ui.test.hasParent
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reedsloan.beekeepingapp.MainActivity
import com.reedsloan.beekeepingapp.core.util.TestTags
import com.reedsloan.beekeepingapp.di.AppModule
import com.reedsloan.beekeepingapp.presentation.common.Screen
import com.reedsloan.beekeepingapp.presentation.sign_in.SignInViewModel
import com.reedsloan.beekeepingapp.presentation.ui.theme.AppTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import okhttp3.internal.wait
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.io.FileOutputStream

@HiltAndroidTest
@UninstallModules(AppModule::class)
class HivesScreenKtTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()

        composeTestRule.activity.setContent {
            val navController = rememberNavController()
            val hiveViewModel = hiltViewModel<HiveViewModel>()
            val signInViewModel = hiltViewModel<SignInViewModel>()

            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.HivesScreen.route
                    ) {
                        composable(Screen.HivesScreen.route) {
                            HivesScreen(
                                navController = navController,
                                hiveViewModel = hiveViewModel,
                                signInViewModel = signInViewModel
                            )
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun clickAddHiveButton() {
        composeTestRule.onNodeWithTag(TestTags.HIVE_CARD).assertDoesNotExist()
        composeTestRule.onNodeWithTag(TestTags.ADD_HIVE_BUTTON).performClick()
        composeTestRule.onNodeWithTag(TestTags.HIVE_CARD).assertIsDisplayed()
        composeTestRule.waitForIdle()

        // Click the context menu button on the hive card
        composeTestRule
            .onNode(
                hasTestTag(TestTags.CONTEXT_MENU_BUTTON) and
                        hasParent(
                            hasTestTag(TestTags.HIVE_CARD)
                        ),
                useUnmergedTree = true
            )
            .assertExists().performClick()

        // Press the delete button in the context menu
        composeTestRule
            .onNode(
                hasTestTag(TestTags.CONTEXT_MENU_ITEM) and
                        hasAnyChild(
                            hasText("Delete")
                        ),
                useUnmergedTree = true
            )
            .assertExists().performClick()

        // Click the confirm button in the delete dialog
        composeTestRule
            .onNode(
                hasTestTag(TestTags.DELETE_HIVE_CONFIRMATION_BUTTON),
                useUnmergedTree = true
            )
            .assertExists().performClick()

        // take a picture of the screen
        val bitmap = composeTestRule.onRoot().captureToImage().asAndroidBitmap()
        saveScreenshot("hive_card", bitmap)

        // why doesn't this work?
        composeTestRule.waitUntilDoesNotExist(
            hasTestTag(TestTags.HIVE_CARD),
            timeoutMillis = 5000
        )
    }

    private fun saveScreenshot(s: String, bitmap: Bitmap) {
        val filename = "${s}_${System.currentTimeMillis()}.png"
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            filename
        )
        file.createNewFile()
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    }

}