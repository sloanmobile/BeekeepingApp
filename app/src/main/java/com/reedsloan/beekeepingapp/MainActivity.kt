package com.reedsloan.beekeepingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.reedsloan.beekeepingapp.presentation.ApiariesScreen
import com.reedsloan.beekeepingapp.presentation.HiveDetailsScreen
import com.reedsloan.beekeepingapp.presentation.InspectionsScreen
import com.reedsloan.beekeepingapp.presentation.LogInspectionScreen
import com.reedsloan.beekeepingapp.presentation.SettingsScreen
import com.reedsloan.beekeepingapp.presentation.WorkInProgressOverlayText
import com.reedsloan.beekeepingapp.presentation.common.Screen
import com.reedsloan.beekeepingapp.presentation.hives_screen.HiveViewModel
import com.reedsloan.beekeepingapp.presentation.hives_screen.HivesScreen
import com.reedsloan.beekeepingapp.presentation.sign_in.GoogleAuthUiClient
import com.reedsloan.beekeepingapp.presentation.sign_in.SignInScreen
import com.reedsloan.beekeepingapp.presentation.sign_in.SignInViewModel
import com.reedsloan.beekeepingapp.presentation.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var googleAuthUiClient: GoogleAuthUiClient

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
                                startDestination = Screen.SignInScreen.route,
                            ) {
                                composable(
                                    route = Screen.SignInScreen.route
                                ) {
                                    val signInViewModel = hiltViewModel<SignInViewModel>()
                                    val state by signInViewModel.state.collectAsState()

                                    val launcher = rememberLauncherForActivityResult(
                                        contract = ActivityResultContracts
                                            .StartIntentSenderForResult(),
                                        onResult = { result ->
                                            if (result.resultCode == RESULT_OK) {
                                                lifecycleScope.launch {
                                                    val signInResult =
                                                        googleAuthUiClient.signInWithIntent(
                                                            intent = result.data!!
                                                        )
                                                    signInViewModel.onSignInResult(signInResult)
                                                }
                                            }
                                        }
                                    )

                                    LaunchedEffect(key1 = state.isSignInSuccessful) {
                                        if (state.isSignInSuccessful) {
                                            navController.navigate(Screen.HivesScreen.route) {
                                                popUpTo(Screen.SignInScreen.route) {
                                                    inclusive = true
                                                }
                                            }
                                            // sync of data from the remote now that we're signed in
                                            hiveViewModel.syncData()
                                        }
                                    }

                                    SignInScreen(state = state) {
                                        signInViewModel.onSignInClick()

                                        lifecycleScope.launch {
                                            googleAuthUiClient.signIn().onSuccess { intentSender ->
                                                launcher.launch(
                                                    IntentSenderRequest.Builder(intentSender)
                                                        .build()
                                                )
                                            }
                                        }
                                    }
                                }
                                composable(
                                    route = Screen.HivesScreen.route
                                ) {
                                    HivesScreen(navController, hiveViewModel)
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
                                composable(
                                    route = Screen.LogInspectionScreen.route
                                ) {
                                    LogInspectionScreen(navController, hiveViewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}