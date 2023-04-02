import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
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
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.presentation.common.containers.SideSheetContainer
import com.reedsloan.beekeepingapp.presentation.viewmodel.hives.HiveViewModel
import com.reedsloan.isPermanentlyDenied
import java.io.File

@Composable
fun HivesScreen(navController: NavController, hiveViewModel: HiveViewModel) {
    val state = hiveViewModel.state
    val hives by hiveViewModel.hives.collectAsState()
    var bitmapOrNull: Bitmap? by remember { mutableStateOf(null) }

    Column(Modifier.fillMaxSize()) {
//        PermissionsTest(navController = navController, hiveViewModel = hiveViewModel)

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
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
                // update the state with the uri
                hiveViewModel.copyImageToInternalStorage(uri)
            }
        }

    Column(Modifier.fillMaxSize()) {
        Row {
            // Hive Name
            Text(
                text = "Hive name",
                modifier = Modifier
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        // set image button
        ElevatedButton(
            onClick = {
                cameraOpenIntent.launch(uri)
            },
            modifier = Modifier
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.CameraAlt, contentDescription = null)
            Text(text = "Take photo")
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
    )
    {
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
                        Log.e("HiveCard", "Error loading image: ${it.result.throwable}")
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
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                    ) {
                        Text(text = "Save")
                    }
                } else {
                    Text(
                        text = hive.hiveInfo.name,
                        modifier = Modifier
                            .padding(16.dp),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                HiveCardAction(
                    icon = Icons.Filled.Book,
                    text = "Log Data"
                ) {
                    hiveViewModel.onTapLogDataButton(hive.id, navController)
                }
                HiveCardAction(
                    icon = Icons.Filled.History,
                    text = "View Logs"
                ) {
                    hiveViewModel.onTapViewLogsButton(hive.id)
                }
                HiveCardAction(
                    icon = Icons.Filled.Edit,
                    text = "Edit"
                ) {
                    hiveViewModel.onTapEditHiveButton(hive.id)
                }
                HiveCardAction(
                    icon = Icons.Filled.Delete,
                    text = "Delete"
                ) {
                    hiveViewModel.onTapDeleteHiveButton(hive.id)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun HiveCardAction(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    ElevatedButton(
        modifier = Modifier
            .size(80.dp),
        onClick = { onClick() },
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
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

    permissionsState.permissions.forEach { perm ->
        when (perm.permission) {
            Manifest.permission.CAMERA -> {
                when {
                    perm.status.isGranted -> {
                        Text("Camera permission granted")
                        hiveViewModel.setCameraPermissionAllowed(true)
                    }
                    perm.status.shouldShowRationale -> {
                        Text("Camera permission is needed.")
                        hiveViewModel.setStoragePermissionAllowed(false)
                    }
                    perm.isPermanentlyDenied() -> {
                        Text("Camera permission is permanently denied.")
                        hiveViewModel.setStoragePermissionAllowed(false)
                    }
                }
            }
            Manifest.permission.READ_MEDIA_IMAGES -> {
                when {
                    perm.status.isGranted -> {
                        Text("Read Media Images permission granted")
                        hiveViewModel.setStoragePermissionAllowed(true)
                    }
                    perm.status.shouldShowRationale -> {
                        Text("Read Media Images permission is needed.")
                        hiveViewModel.setStoragePermissionAllowed(false)
                    }
                    perm.isPermanentlyDenied() -> {
                        Text("Read Media Images permission is permanently denied.")
                        hiveViewModel.setStoragePermissionAllowed(false)
                    }
                }
            }
        }
    }
    // show selected hive id
    Text("Selected Hive ID: ${hiveViewModel.state.selectedHive?.id}")
    Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
        Text("Request permissions")
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (!state.isCameraPermissionAllowed) {
            Text(" (Camera permission needed)")
        }
        Button(
            onClick = {
                // open camera
                cameraOpenIntent.launch(null)
            }, enabled = state.isCameraPermissionAllowed
        ) {
            Text("Take Photo")
        }

        if (!state.isStoragePermissionAllowed) {
            Text(" (Storage permission needed)")
        }
        Button(onClick = {
            // open image picker
            imagePickerIntent.launch("image/*")
        }, enabled = state.isStoragePermissionAllowed) {
            Text("Choose Photo")
        }
    }
}