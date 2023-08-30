package com.reedsloan.beekeepingapp.presentation.hive_details_screen

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Hive
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.reedsloan.beekeepingapp.icons.rememberHive
import com.reedsloan.beekeepingapp.icons.rememberTaskAlt
import com.reedsloan.beekeepingapp.presentation.home_screen.HiveScreenState
import com.reedsloan.beekeepingapp.presentation.viewmodel.hives.HiveViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HiveDetailsScreen(navController: NavController, hiveViewModel: HiveViewModel) {
    val state by hiveViewModel.state.collectAsState()
    val hive = state.selectedHive ?: return
    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by remember { mutableStateOf(false) }

    BackHandler {
        if (isSheetOpen) {
            isSheetOpen = false
        } else {
            hiveViewModel.backHandler(navController)
        }
    }

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

                    val context = LocalContext.current

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

                    val hiveName =
                        remember { mutableStateOf(TextFieldValue(hive.hiveDetails.name)) }

                    Column(
                        Modifier.padding(horizontal = 16.dp)
                    ) {
                        // set image button
                        ElevatedButton(
                            onClick = {
                                if (ActivityCompat.checkSelfPermission(
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
                            }, modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            ConstraintLayout(Modifier.fillMaxSize()) {
                                val (icon, text) = createRefs()

                                Icon(
                                    Icons.Filled.PhotoCamera,
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
                        ElevatedButton(
                            onClick = {
                                isLoading = true
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    if (ActivityCompat.checkSelfPermission(
                                            context, Manifest.permission.READ_MEDIA_IMAGES
                                        ) == PackageManager.PERMISSION_GRANTED
                                    ) {
                                        imagePickerIntent.launch("image/*")
                                    } else {
                                        hiveViewModel.onPermissionRequested(Manifest.permission.READ_MEDIA_IMAGES)
                                    }
                                } else {
                                    if (ActivityCompat.checkSelfPermission(
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
                                .height(56.dp),
                        ) {
                            ConstraintLayout(Modifier.fillMaxSize()) {
                                val (icon, text) = createRefs()
                                Icon(
                                    Icons.Filled.Image,
                                    contentDescription = null,
                                    modifier = Modifier.constrainAs(icon) {
                                        start.linkTo(parent.start)
                                        top.linkTo(parent.top)
                                        bottom.linkTo(parent.bottom)
                                    })

                                Text(text = "USE GALLERY", modifier = Modifier.constrainAs(text) {
                                    start.linkTo(parent.start)
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    end.linkTo(parent.end)
                                }, style = MaterialTheme.typography.titleMedium)
                            }
                        }

                        // Remove image button
                        if (hive.hiveDetails.image != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            ElevatedButton(
                                onClick = {
                                    hiveViewModel.removeImageForSelectedHive()
                                    isSheetOpen = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                            ) {
                                ConstraintLayout(Modifier.fillMaxSize()) {
                                    val (icon, text) = createRefs()

                                    Icon(
                                        Icons.Filled.Image,
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

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(text = hive.hiveDetails.name)
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
                    Icon(Icons.Filled.Edit, contentDescription = "Edit Hive Details")
                }
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
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            item(key = state.isLoading, contentType = HiveScreenState::class) {
                // Photo of hive if it exists otherwise show an add photo button
                hive.hiveDetails.image?.let { image ->
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                        // Hive image
                        AsyncImage(
                            model = image,
                            contentDescription = "Hive image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clickable {
                                    isSheetOpen = true
                                }
                                .clip(
                                    RoundedCornerShape(
                                        bottomEndPercent = 20, bottomStartPercent = 20
                                    )
                                ),
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
                } ?: run {
                    // Open the bottom sheet to take a photo or select from gallery
                    Column {
                        // Add photo
                        Row(horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clickable {
                                    isSheetOpen = true
                                }
                                .background(
                                    MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                                    RoundedCornerShape(
                                        bottomEndPercent = 35, bottomStartPercent = 35
                                    )
                                )
                                .clip(
                                    RoundedCornerShape(
                                        bottomEndPercent = 35, bottomStartPercent = 35
                                    )
                                )) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Add Photo",
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(8.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Inspections Button
                HiveDetailsAction(title = "Inspections",
                    description = "View and edit inspections",
                    icon = Icons.Default.Hive,
                    onClick = {
                        hiveViewModel.onTapInspectionsButton(navController)
                    })

                Spacer(modifier = Modifier.height(16.dp))
                // Tasks Button
                HiveDetailsAction(title = "Tasks",
                    description = "View and edit tasks",
                    icon = Icons.Default.TaskAlt,
                    onClick = {
                        hiveViewModel.onTapTasksButton(navController)
                    })

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun HiveDetailsAction(title: String, description: String, icon: ImageVector, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(.9F)
            .height(100.dp),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        ConstraintLayout {
            val (iconLeft, columnCenter) = createRefs()
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .constrainAs(iconLeft) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(columnCenter) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
            ) {
                // Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = title, style = MaterialTheme.typography.titleLarge)
                }
                // Description
                Text(text = description)
            }
        }
    }
}