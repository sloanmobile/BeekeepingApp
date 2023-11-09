package com.reedsloan.beekeepingapp

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.auth.FirebaseAuth
import com.reedsloan.beekeepingapp.presentation.ApiariesScreen
import com.reedsloan.beekeepingapp.presentation.HiveDetailsScreen
import com.reedsloan.beekeepingapp.presentation.inspections_screen.InspectionsScreen
import com.reedsloan.beekeepingapp.presentation.log_inspection_screen.LogInspectionScreen
import com.reedsloan.beekeepingapp.presentation.SettingsScreen
import com.reedsloan.beekeepingapp.presentation.WorkInProgressOverlayText
import com.reedsloan.beekeepingapp.presentation.common.PermissionDialog
import com.reedsloan.beekeepingapp.presentation.common.Screen
import com.reedsloan.beekeepingapp.presentation.hives_screen.HiveViewModel
import com.reedsloan.beekeepingapp.presentation.hives_screen.HivesScreen
import com.reedsloan.beekeepingapp.presentation.hives_screen.openAppSettings
import com.reedsloan.beekeepingapp.presentation.ads.AdViewModel
import com.reedsloan.beekeepingapp.presentation.inspection_insights_screen.InspectionInsightsScreen
import com.reedsloan.beekeepingapp.presentation.inspection_insights_screen.InspectionsInsightsViewModel
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

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val testDeviceIds = listOf("A402D2707790DAA8C1C5ECB954D61686")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
        MobileAds.initialize(this)

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    val hiveViewModel = hiltViewModel<HiveViewModel>()
                    val adViewModel = hiltViewModel<AdViewModel>()
                    val permissionDialogQueue =
                        hiveViewModel.visiblePermissionDialogQueue.firstOrNull()
                    val context = LocalContext.current
                    val hiveScreenState by hiveViewModel.state.collectAsState()
                    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestMultiplePermissions()
                    ) { permissions ->
                        permissions.forEach { (permission, granted) ->
                            hiveViewModel.onPermissionResult(
                                permission = permission, granted = granted
                            )
                        }
                    }
                    var isSheetOpen by remember { mutableStateOf(false) }
                    val sheetState = rememberModalBottomSheetState()

                    // Permission dialog
                    Box(Modifier.fillMaxSize()) {
                        permissionDialogQueue?.let { permissionRequest ->
                            val activity = context as Activity
                            PermissionDialog(
                                permissionRequest = permissionRequest,
                                isPermanentlyDeclined = hiveViewModel.isPermissionPermanentlyDeclined(
                                    activity, permissionRequest.permission
                                ),
                                onDismiss = { hiveViewModel.dismissDialog() },
                                onConfirm = {
                                    multiplePermissionsLauncher.launch(
                                        arrayOf(permissionRequest.permission)
                                    )
                                },
                                onGoToAppSettingsClick = {
                                    activity.openAppSettings()
                                    hiveViewModel.dismissDialog()
                                },
                            )
                        }
                    }

                    // Gallery or camera sheet
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (isSheetOpen) {
                            ModalBottomSheet(onDismissRequest = {
                                isSheetOpen = false
                            }, sheetState = sheetState) {
                                Column {
                                    var uri: Uri? by rememberSaveable {
                                        mutableStateOf(
                                            null
                                        )
                                    }
                                    var isLoading by remember {
                                        mutableStateOf(
                                            false
                                        )
                                    }

                                    val imagePickerIntent =
                                        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
                                            isLoading = false
                                            uri = imageUri
                                            hiveViewModel.setImageForSelectedHive(imageUri)
                                            isSheetOpen = false
                                        }

                                    val cameraOpenIntent =
                                        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
                                            if (it) {
                                                isLoading = false
                                                hiveViewModel.setImageForSelectedHive(uri)
                                                isSheetOpen = false
                                            }
                                        }
                                    Column(
                                        Modifier.padding(horizontal = 16.dp)
                                    ) {
                                        // set image button
                                        FilledTonalButton(
                                            onClick = {
                                                if (ActivityCompat.checkSelfPermission(
                                                        context, Manifest.permission.CAMERA
                                                    ) == PackageManager.PERMISSION_GRANTED
                                                ) {
                                                    isLoading = true
                                                    uri?.let { hiveViewModel.deleteImage(it) }
                                                    uri =
                                                        hiveViewModel.getImageUri(hiveScreenState.selectedHive!!.id)
                                                    cameraOpenIntent.launch(uri)
                                                } else {
                                                    hiveViewModel.onPermissionRequested(Manifest.permission.CAMERA)
                                                }
                                            }, modifier = Modifier
                                                .fillMaxWidth()
                                                .height(56.dp)
                                        ) {
                                            ConstraintLayout(Modifier.fillMaxSize()) {
                                                val (icon, text) = createRefs()

                                                Icon(Icons.Filled.PhotoCamera,
                                                    contentDescription = null,
                                                    modifier = Modifier.constrainAs(icon) {
                                                        start.linkTo(parent.start)
                                                        top.linkTo(parent.top)
                                                        bottom.linkTo(parent.bottom)
                                                    })

                                                Text(
                                                    text = "USE CAMERA",
                                                    modifier = Modifier.constrainAs(text) {
                                                        start.linkTo(parent.start)
                                                        top.linkTo(parent.top)
                                                        bottom.linkTo(parent.bottom)
                                                        end.linkTo(parent.end)
                                                    },
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                        // choose image button
                                        FilledTonalButton(
                                            onClick = {
                                                isLoading = true
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                    if (ActivityCompat.checkSelfPermission(
                                                            context,
                                                            Manifest.permission.READ_MEDIA_IMAGES
                                                        ) == PackageManager.PERMISSION_GRANTED
                                                    ) {
                                                        imagePickerIntent.launch("image/*")
                                                    } else {
                                                        hiveViewModel.onPermissionRequested(Manifest.permission.READ_MEDIA_IMAGES)
                                                    }
                                                } else {
                                                    if (ActivityCompat.checkSelfPermission(
                                                            context,
                                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                                        ) == PackageManager.PERMISSION_GRANTED
                                                    ) {
                                                        imagePickerIntent.launch("image/*")
                                                    } else {
                                                        hiveViewModel.onPermissionRequested(Manifest.permission.READ_EXTERNAL_STORAGE)
                                                    }
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(56.dp),
                                        ) {
                                            ConstraintLayout(Modifier.fillMaxSize()) {
                                                val (icon, text) = createRefs()
                                                Icon(Icons.Filled.Image,
                                                    contentDescription = null,
                                                    modifier = Modifier.constrainAs(icon) {
                                                        start.linkTo(parent.start)
                                                        top.linkTo(parent.top)
                                                        bottom.linkTo(parent.bottom)
                                                    })

                                                Text(
                                                    text = "USE GALLERY",
                                                    modifier = Modifier.constrainAs(text) {
                                                        start.linkTo(parent.start)
                                                        top.linkTo(parent.top)
                                                        bottom.linkTo(parent.bottom)
                                                        end.linkTo(parent.end)
                                                    },
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                            }
                                        }

                                        // Remove image button
                                        if (hiveScreenState.selectedHive?.hiveDetails?.image != null) {
                                            Spacer(modifier = Modifier.height(16.dp))
                                            FilledTonalButton(
                                                onClick = {
                                                    hiveViewModel.removeImageForSelectedHive()
                                                    isSheetOpen = false
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(56.dp),
                                                colors = ButtonDefaults.filledTonalButtonColors(
                                                    contentColor = MaterialTheme.colorScheme.error,
                                                )
                                            ) {
                                                ConstraintLayout(Modifier.fillMaxSize()) {
                                                    val (icon, text) = createRefs()

                                                    Icon(Icons.Filled.Delete,
                                                        contentDescription = null,
                                                        modifier = Modifier.constrainAs(icon) {
                                                            start.linkTo(parent.start)
                                                            top.linkTo(parent.top)
                                                            bottom.linkTo(parent.bottom)
                                                        })

                                                    Text(
                                                        text = "REMOVE IMAGE",
                                                        modifier = Modifier.constrainAs(text) {
                                                            start.linkTo(parent.start)
                                                            top.linkTo(parent.top)
                                                            bottom.linkTo(parent.bottom)
                                                            end.linkTo(parent.end)
                                                        },
                                                        style = MaterialTheme.typography.titleMedium
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    val isDebugBuild by remember { mutableStateOf(BuildConfig.DEBUG) }

                    if (isDebugBuild) {
                        WorkInProgressOverlayText()
                    }

                    val signedInUser by remember {
                        mutableStateOf(
                            firebaseAuth.currentUser
                        )
                    }

                    val signInViewModel = hiltViewModel<SignInViewModel>()
                    val signInState by signInViewModel.state.collectAsState()

                    LaunchedEffect(key1 = firebaseAuth.currentUser) {
                        if (firebaseAuth.currentUser != null && !signInState.isSignInSuccessful) {
                            hiveViewModel.onSignInSuccess()
                        }
                    }

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
                            modifier = Modifier.fillMaxSize()
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = if (signedInUser != null) Screen.HivesScreen.route else Screen.SignInScreen.route,
                            ) {
                                composable(
                                    route = Screen.SignInScreen.route
                                ) {

                                    val launcher =
                                        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult(),
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
                                            })

                                    // Sign in from previous session
                                    LaunchedEffect(key1 = signInState.isSignInSuccessful) {
                                        if (signInState.isSignInSuccessful) {
                                            hiveViewModel.onSignInSuccess()

                                            navController.navigate(Screen.HivesScreen.route) {
                                                popUpTo(Screen.SignInScreen.route) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    }

                                    SignInScreen(state = signInState) {
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
                                    HivesScreen(navController, hiveViewModel, signInViewModel)
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
                                    HiveDetailsScreen(
                                        navController,
                                        hiveViewModel,
                                        { isSheetOpen = true },
                                        isSheetOpen
                                    )
                                }
                                composable(
                                    route = Screen.InspectionsScreen.route
                                ) {
                                    InspectionsScreen(navController, hiveViewModel)
                                }
                                composable(
                                    route = Screen.LogInspectionScreen.route
                                ) {
                                    LogInspectionScreen(navController, hiveViewModel, adViewModel)
                                }
                                composable(
                                    route = Screen.InspectionInsightsScreen.route
                                ) {
                                    val inspectionsInsightsViewModel =
                                        hiltViewModel<InspectionsInsightsViewModel>()
                                    val inspectionInsightsScreenState by
                                    inspectionsInsightsViewModel.state.collectAsState()

                                    LaunchedEffect(key1 = hiveScreenState.selectedHive) {
                                        if (hiveScreenState.selectedHive != null) {
                                            inspectionsInsightsViewModel.initialize(hiveScreenState.selectedHive!!.id)
                                        }
                                    }

                                    InspectionInsightsScreen(
                                        state = inspectionInsightsScreenState,
                                        onEvent = { event ->
                                            inspectionsInsightsViewModel.onEvent(
                                                event,
                                                navController
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}