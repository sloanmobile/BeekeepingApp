package com.reedsloan.beekeepingapp.presentation.add_page

import android.Manifest
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.*
import com.reedsloan.beekeepingapp.presentation.common.NavigationBar
import com.reedsloan.beekeepingapp.presentation.home_screen.HomeViewModel
import com.reedsloan.isPermanentlyDenied

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddScreen(navController: NavController, homeViewModel: HomeViewModel = hiltViewModel()) {
    val state = homeViewModel.state
    var bitmapOrNull: Bitmap? by remember { mutableStateOf(null) }
    val cameraOpenIntent =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            // update the state with the bitmap
            bitmapOrNull = bitmap
        }

    Column {
        NavigationBar(navController, homeViewModel)
        Text("This screen is a test.", fontSize = 24.sp)

        val permissionsState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                )
            )
        } else {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.CAMERA,
                )
            )
        }

        Button(onClick = { navController.navigateUp() }) {
            Text("Go back")
        }

        permissionsState.permissions.forEach { perm ->
            when (perm.permission) {
                Manifest.permission.CAMERA -> {
                    when {
                        perm.status.isGranted -> {
                            Text("Camera permission granted")
                            homeViewModel.setCameraPermissionAllowed(true)
                        }
                        perm.status.shouldShowRationale -> {
                            Text("Camera permission is needed.")
                            homeViewModel.setStoragePermissionAllowed(false)
                        }
                        perm.isPermanentlyDenied() -> {
                            Text("Camera permission is permanently denied.")
                            homeViewModel.setStoragePermissionAllowed(false)
                        }
                    }
                }
                Manifest.permission.READ_MEDIA_IMAGES -> {
                    when {
                        perm.status.isGranted -> {
                            Text("Read Media Images permission granted")
                            homeViewModel.setStoragePermissionAllowed(true)
                        }
                        perm.status.shouldShowRationale -> {
                            Text("Read Media Images permission is needed.")
                            homeViewModel.setStoragePermissionAllowed(false)
                        }
                        perm.isPermanentlyDenied() -> {
                            Text("Read Media Images permission is permanently denied.")
                            homeViewModel.setStoragePermissionAllowed(false)
                        }
                    }
                }
            }
        }

        Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
            Text("Request permissions")
        }

        bitmapOrNull?.let { bitmap ->
            Image(bitmap.asImageBitmap(), "Image", Modifier.fillMaxWidth())
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            if(!state.isCameraPermissionAllowed) {
                Text(" (Camera permission needed)")
            }
            Button(onClick = {
                // open camera
                cameraOpenIntent.launch(null)
            },
                enabled = state.isCameraPermissionAllowed
            ) {
                Text("Take Photo")
            }

            if(!state.isStoragePermissionAllowed) {
                Text(" (Storage permission needed)")
            }
            Button(onClick = { }, enabled = state.isStoragePermissionAllowed) {
                Text("Choose Photo")
            }
        }

        
    }
}