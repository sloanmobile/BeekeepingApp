package com.reedsloan.beekeepingapp.presentation.home_screen

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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.reedsloan.beekeepingapp.R
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.presentation.common.data.PermissionRequest
import com.reedsloan.beekeepingapp.presentation.viewmodel.hives.HiveViewModel

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavController, hiveViewModel: HiveViewModel) {
    val state by hiveViewModel.state.collectAsState()
    val hives by hiveViewModel.hives.collectAsState()
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
                hiveViewModel.onTapDeleteHiveConfirmationButton(state.selectedHive!!.id)
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
                    multiplePermissionsLauncher.launch(arrayOf(it.permission))
                },
                onGoToAppSettingsClick = {
                    context.openAppSettings()
                    hiveViewModel.dismissDialog()
                },
            )
        }
    }

    Column(Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = {
                    Text(text = "Hives")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        hiveViewModel.backHandler(navController)
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        hiveViewModel.onTapSettingsButton(navController)
                    }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        2.dp
                    ),
                ),
            )

            // get display width
            val displayWidth = LocalContext.current.resources.displayMetrics.widthPixels

            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxWidth()) {
                items(hives) { hive ->
                    HiveCard(
                        hive = hive,
                        navController = navController,
                        hiveViewModel = hiveViewModel
                    )
                }
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        ExtendedFloatingActionButton(
            onClick = { hiveViewModel.onClickAddHiveButton() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "ADD HIVE")
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


    val context = LocalContext.current

    val imagePickerIntent =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
            isLoading = false
            uri = imageUri
        }

    val cameraOpenIntent =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                isLoading = false
            }
        }

    val hiveName = remember { mutableStateOf(TextFieldValue(hive.hiveDetails.name)) }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        Column {
            // Hive Name
            TextField(
                value = hiveName.value,
                onValueChange = { hiveName.value = it },
                label = { Text(text = "Hive Name") },
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (!isLoading) {
                // Hive image
                AsyncImage(
                    model = uri ?: hive.hiveDetails.image,
                    contentDescription = "Hive image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.large),
                    contentScale = ContentScale.Crop,
                    onError = {
                        Log.e(
                            this::class.java.simpleName,
                            "Error loading image: ${it.result.throwable}"
                        )
                    },
                    filterQuality = FilterQuality.High,
                )
            } else {
                // Hive image loading (just a placeholder)
                AsyncImage(
                    model = null,
                    contentDescription = "Hive image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.large),
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
            // set image button
            ElevatedButton(
                onClick = {
                    if (checkSelfPermission(
                            context, Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        isLoading = true
                        uri?.let { hiveViewModel.deleteImage(it) }
                        uri = hiveViewModel.getImageUri(hive.id)
                        cameraOpenIntent.launch(uri)
                    } else {
                        hiveViewModel.onPermissionRequested(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Icon(Icons.Filled.CameraAlt, contentDescription = null)
                Text(text = "Take photo", modifier = Modifier.padding(start = 4.dp))
            }
            // choose image button
            ElevatedButton(
                onClick = {
                    isLoading = true
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (checkSelfPermission(
                                context, Manifest.permission.READ_MEDIA_IMAGES
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            imagePickerIntent.launch("image/*")
                        } else {
                            hiveViewModel.onPermissionRequested(Manifest.permission.READ_MEDIA_IMAGES)
                        }
                    } else {
                        if (checkSelfPermission(
                                context, Manifest.permission.READ_EXTERNAL_STORAGE
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
            ) {
                Icon(Icons.Filled.Image, contentDescription = null)
                Text(text = "Choose photo", modifier = Modifier.padding(start = 4.dp))
            }
        }

        // save button
        Button(
            onClick = {
                hiveViewModel.onTapSaveButton(uri = uri, name = hiveName.value.text)
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Save")
            }
        }
    }
}

@Composable
fun HiveCard(
    hive: Hive,
    navController: NavController,
    hiveViewModel: HiveViewModel,
) {
    OutlinedCard(
        Modifier
            .padding(8.dp)
            .clickable {
                hiveViewModel.onTapHiveCard(hive.id, navController)
            }) {
        // Card content
        Column(
            Modifier
                .fillMaxWidth()
                .height(230.dp)) {
            hive.hiveDetails.image?.let { image ->
                // Hive image
                AsyncImage(
                    model = image,
                    contentDescription = "Hive image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(MaterialTheme.shapes.large),
                    contentScale = ContentScale.Crop,
                    onError = {
                        Log.e(
                            this::class.java.simpleName,
                            "Error loading image: ${it.result.throwable}"
                        )
                    },
                    filterQuality = FilterQuality.High,
                )
            } ?: run {

                // use camera icon as placeholder
                Image(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
                        .padding(16.dp)
                        .alpha(0.5f),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                        MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                )
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.padding(16.dp)) {
                    Row {
                        // Hive name
                        Text(
                            text = hive.hiveDetails.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        // Last inspection date
                        Text(
                            text = "Last inspection: ${hive.hiveDataEntries.lastOrNull()?.date ?: "Never"}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        )
                    }
                }
            }
//            Row(
//                horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(4.dp)
//            ) {
//                HiveCardAction(
//                    icon = Icons.Filled.Book, text = "Log Data", modifier = Modifier
//                        .weight(1f)
//                        .padding(end = 4.dp)
//                ) {
//                    hiveViewModel.onTapLogDataButton(hive.id, navController)
//                }
//                HiveCardAction(
//                    icon = Icons.Filled.History, text = "View Logs", modifier = Modifier
//                        .weight(1f)
//                        .padding(end = 4.dp)
//                ) {
//                    hiveViewModel.onTapViewLogsButton(hive.id)
//                }
//                HiveCardAction(
//                    icon = Icons.Filled.Edit, text = "Edit", modifier = Modifier
//                        .weight(1f)
//                        .padding(end = 4.dp)
//                ) {
//                    hiveViewModel.onTapEditHiveButton(hive.id)
//                }
//                HiveCardAction(
//                    icon = Icons.Filled.Delete, text = "Delete", modifier = Modifier.weight(1f)
//                ) {
//                    hiveViewModel.showDeleteHiveDialog(hive.id)
//                }
//            }
        }
    }
}

@Composable
fun HiveCardAction(
    icon: ImageVector, text: String, modifier: Modifier = Modifier, onClick: () -> Unit
) {
    ElevatedButton(
        modifier = modifier
            .height(80.dp)
            .fillMaxWidth(),
        onClick = { onClick() },
        shape = MaterialTheme.shapes.medium,
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
    val state by hiveViewModel.state.collectAsState()
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