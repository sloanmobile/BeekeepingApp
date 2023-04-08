package com.reedsloan.beekeepingapp.presentation.hives_screen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.presentation.common.data.PermissionRequest
import com.reedsloan.beekeepingapp.presentation.viewmodel.hives.HiveViewModel

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

@Composable
fun HivesScreen(navController: NavController, hiveViewModel: HiveViewModel) {
    val state = hiveViewModel.state
    val hives by hiveViewModel.hives.collectAsState()
    var bitmapOrNull: Bitmap? by remember { mutableStateOf(null) }
    val permissionDialogQueue = hiveViewModel.visiblePermissionDialogQueue.firstOrNull()
    val context = LocalContext.current

    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.forEach { (permission, granted) ->
            hiveViewModel.onPermissionResult(
                permission = permission, granted = granted
            )
        }
    }
    Box(Modifier.fillMaxSize()) {
        if (state.showDeleteHiveDialog && state.selectedHive != null) DeleteConfirmationDialog(
            onDismiss = { hiveViewModel.dismissDeleteHiveDialog() },
            onClick = {
                hiveViewModel.onTapDeleteHiveConfirmationButton(state.selectedHive.id)
                hiveViewModel.dismissDeleteHiveDialog()
            })
    }

    Box(Modifier.fillMaxSize()) {
        permissionDialogQueue?.let {
            PermissionDialog(
                permissionRequest = it,
                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                    context as Activity, it.permission
                ),
                onDismiss = { hiveViewModel.dismissDialog() },
                onConfirm = {
                    if (it.permission == Manifest.permission.CAMERA) {
                        multiplePermissionsLauncher.launch(
                            arrayOf(
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        )
                    } else {
                        multiplePermissionsLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                onGoToAppSettingsClick = {
                    context.openAppSettings()
                    hiveViewModel.dismissDialog()
                },
            )
        }
    }

    Column(Modifier.fillMaxSize()) {
//        com.reedsloan.beekeepingapp.presentation.hives_screen.PermissionsTest(navController = navController, hiveViewModel = hiveViewModel)

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()
        ) {
            items(items = hives, key = { it.id }) { hive ->
                HiveCard(
                    hive = hive,
                    navController = navController,
                    hiveViewModel = hiveViewModel,
                )
            }
        }
    }
    Box(Modifier.fillMaxSize()) {
        ExtendedFloatingActionButton(
            onClick = { hiveViewModel.onTapAddHiveButton() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "New hive")
        }
    }
}

fun Modifier.ignoreVerticalParentPadding(vertical: Dp): Modifier {
    return this.layout { measurable, constraints ->
        val overridenHeight = constraints.maxHeight + 2 * vertical.roundToPx()
        val placeable = measurable.measure(constraints.copy(maxHeight = overridenHeight))
        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }
}

@Composable
fun DeleteConfirmationDialog(onClick: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        icon = {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        },
        onDismissRequest = { onDismiss() }, title = {
        Text(text = "Delete hive?")
    }, text = {
        Text(text = "Are you sure you want to delete this hive?")
    }, confirmButton = {
        Button(onClick = { onClick() }) {
            Text(text = "Delete")
        }
    }, dismissButton = {
        Button(onClick = { onDismiss() }) {
            Text(text = "Cancel")
        }
    })
}

@Composable
fun PermissionDialog(
    permissionRequest: PermissionRequest,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onGoToAppSettingsClick: () -> Unit
) {
    AlertDialog(onDismissRequest = {
        onDismiss()
    }, title = {
        Text(text = "Permission required")
    }, text = {
        Text(
            text = if (isPermanentlyDeclined) {
                permissionRequest.isPermanentlyDeniedMessage
            } else {
                permissionRequest.message
            }
        )
    },
        icon = {
            Icon(
                imageVector = Icons.Filled.Security,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        },
        confirmButton = {
        Button(onClick = {
            if (isPermanentlyDeclined) {
                onGoToAppSettingsClick()
            } else {
                onConfirm()
            }
        }) {
            Text(
                text = if (isPermanentlyDeclined) {
                    "Go to app settings"
                } else {
                    "Confirm"
                }
            )
        }
    }, dismissButton = {
        Button(onClick = {
            onDismiss()
        }) {
            Text(text = "Cancel")
        }
    })
}

@Composable
fun EditHiveMenu(
    hiveViewModel: HiveViewModel,
    hive: Hive,
    navController: NavController,
) {
    val imagePickerIntent =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            // update the state with the uri
            hiveViewModel.copyImageToInternalStorage(uri)
        }

    val state = hiveViewModel.state
    val context = LocalContext.current
    val uri by remember { mutableStateOf(hiveViewModel.getImageUri(hive.id)) }

    val cameraOpenIntent =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                hiveViewModel.updateHiveImage(hive.id, uri)
            }
        }

    Column(Modifier.fillMaxSize()) {
        Row {
            // Hive Name
            Text(
                text = "Hive name",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        // set image button
        ElevatedButton(
            onClick = {
                if (checkSelfPermission(
                        context, Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    cameraOpenIntent.launch(uri)
                } else {
                    hiveViewModel.onPermissionRequested(Manifest.permission.CAMERA)
                }
            }, modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.Filled.CameraAlt, contentDescription = null)
            Text(text = "Take photo", modifier = Modifier.padding(start = 4.dp))
        }
    }
}

@Composable
fun HiveCard(
    hive: Hive,
    navController: NavController,
    hiveViewModel: HiveViewModel,
) {

    var editingHiveName by remember {
        mutableStateOf(false)
    }

    var editableString by remember {
        mutableStateOf(hive.hiveInfo.name)
    }

    // cancel editing the hive name
    LaunchedEffect(key1 = editingHiveName) {
        if (!editingHiveName) {
            // reset the editable string
            editableString = hive.hiveInfo.name
        }
    }

    // launchedeffect to update the hive name when it
    ElevatedCard(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Card content
        Column(Modifier.fillMaxSize()) {
            hive.hiveInfo.image?.let { image ->
                // Hive image
                AsyncImage(
                    model = image,
                    contentDescription = "Hive image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop,
                    onError = {
                        Log.e(
                            this::class.java.simpleName,
                            "Error loading image: ${it.result.throwable}"
                        )
                    },
                    filterQuality = FilterQuality.High,
                )
            }
            Row {
                // Hive name
                if (editingHiveName) {
                    TextField(
                        value = editableString,
                        onValueChange = { editableString = it },
                        label = { Text(text = "Hive name") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        textStyle = MaterialTheme.typography.titleLarge,
                    )
                    // save button
                    IconButton(
                        onClick = {
                            editingHiveName = false
                            hiveViewModel.onTapSaveHiveNameButton(hive.id, editableString)
                        }, modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                    ) {
                        Text(text = "Save")
                    }
                } else {
                    Text(
                        text = hive.hiveInfo.name,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()
            ) {
                HiveCardAction(
                    icon = Icons.Filled.Book, text = "Log Data"
                ) {
                    hiveViewModel.onTapLogDataButton(hive.id, navController)
                }
                HiveCardAction(
                    icon = Icons.Filled.History, text = "View Logs"
                ) {
                    hiveViewModel.onTapViewLogsButton(hive.id)
                }
                HiveCardAction(
                    icon = Icons.Filled.Edit, text = "Edit"
                ) {
                    hiveViewModel.onTapEditHiveButton(hive.id)
                }
                HiveCardAction(
                    icon = Icons.Filled.Delete, text = "Delete"
                ) {
                    hiveViewModel.showDeleteHiveDialog(hive.id)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun HiveCardAction(
    icon: ImageVector, text: String, onClick: () -> Unit
) {
    ElevatedButton(
        modifier = Modifier.size(80.dp),
        onClick = { onClick() },
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsTest(navController: NavController, hiveViewModel: HiveViewModel) {
    val state = hiveViewModel.state
    var bitmapOrNull: Bitmap? by remember { mutableStateOf(null) }
    val cameraOpenIntent =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            // update the state with the bitmap
            bitmapOrNull = bitmap
            // write the bitmap to a file
            hiveViewModel.writeBitmapToFile(bitmap)
        }

    val imagePickerIntent =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            // update the state with the uri
            hiveViewModel.copyImageToInternalStorage(uri)
        }

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

}