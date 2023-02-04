package com.reedsloan.beekeepingapp.presentation.add_page

import android.Manifest
import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.permissions.*
import com.reedsloan.beekeepingapp.data.local.TemperatureMeasurement
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.presentation.common.*
import com.reedsloan.beekeepingapp.presentation.home_screen.MenuState
import com.reedsloan.beekeepingapp.ui.custom_theme.customTheme
import com.reedsloan.isPermanentlyDenied
import java.util.*

@Composable
fun AddScreen(navController: NavController, hiveViewModel: HiveViewModel) {
    val state = hiveViewModel.state
    val context = LocalContext.current
    // side effect when arriving at this screen to reset the open state of the various menus
    LaunchedEffect(navController.currentBackStackEntry) {
        hiveViewModel.onArrivalAtAddHiveScreen()
    }

    // prevent the user from leaving the screen if they are in delete mode
    BackHandler(state.hiveDeleteMode) {
        hiveViewModel.onTapOutside()
    }


    Box {
        Box(contentAlignment = Alignment.Center) {
            when (state.hiveInfoMenuState) {
                MenuState.CLOSED -> {}
                MenuState.OPEN -> {
                    HiveDetailsMenu(hiveViewModel = hiveViewModel)
                }
            }
            Column(modifier = Modifier
                .fillMaxSize()
                // onTap event to close various menus if the user taps outside of them
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        hiveViewModel.onTapOutside()
                    })
                }) {
                NavigationBar(navController, hiveViewModel)

                Column(
                    Modifier.fillMaxSize()
                ) {

                    Container {
                        // show list of hives
                        HiveListSection(hiveViewModel, navController)
                    }

                }
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.hiveInfoMenuState == MenuState.CLOSED && state.navigationBarMenuState == MenuState.CLOSED) {
                if (state.hiveDeleteMode) {
                    CircleCornerButton(
                        onTap = { hiveViewModel.onTapDeleteHiveButton() },
                        backgroundColor = customTheme.cancelColor
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Hives",
                            tint = customTheme.onCancelColor,
                            modifier = Modifier.testTag("DeleteHiveButton")
                        )
                    }
                } else {
                    CircleCornerButton(
                        onTap = { hiveViewModel.onTapAddHiveButton() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Hive",
                            tint = customTheme.onPrimaryColor,
                            modifier = Modifier.testTag("AddHiveButton")
                        )
                    }
                }
            }
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

    CustomButton(onClick = { navController.navigateUp() }) {
        Text("Go back")
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

    CustomButton(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
        Text("Request permissions")
    }

    bitmapOrNull?.let { bitmap ->
        Image(bitmap.asImageBitmap(), "Image", Modifier.fillMaxWidth())
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (!state.isCameraPermissionAllowed) {
            Text(" (Camera permission needed)")
        }
        CustomButton(
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
        CustomButton(onClick = { }, enabled = state.isStoragePermissionAllowed) {
            Text("Choose Photo")
        }
    }
}


@Composable
fun TemperatureMeasurementSelection(hiveViewModel: HiveViewModel) {
    val state = hiveViewModel.state
    // expanded state
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .border(2.dp, customTheme.primaryColor, RoundedCornerShape(8.dp))
            .width(128.dp)
            .padding(8.dp)
            .clickable(
                onClick = { expanded = !expanded },
                interactionSource = MutableInteractionSource(),
                indication = null
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                state.userPreferences.temperatureMeasurement.displayValue,
                fontWeight = FontWeight.Bold,
            )
            // dropdown expanded icon
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = "Expand temperature mode dropdown",
            )
        }
        // temperature mode celsius or fahrenheit dropdown
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(128.dp)
                .background(customTheme.backgroundColor, RoundedCornerShape(8.dp))
                .border(2.dp, customTheme.primaryColor, RoundedCornerShape(8.dp))
                .padding(top = 0.dp),
            offset = DpOffset(0.dp, 4.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
            ) {
                // Fahrenheit option
                Row(
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                hiveViewModel.setTemperatureMeasurement(
                                    TemperatureMeasurement.Fahrenheit.displayValue
                                )
                                expanded = false
                            },
                            interactionSource = MutableInteractionSource(),
                            indication = LocalIndication.current
                        )
                        .fillMaxWidth()
                        .height(32.dp)
                        .padding(0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        TemperatureMeasurement.Fahrenheit.displayValue,
                        Modifier.padding(8.dp),
                    )
                }


                // Spacer (line)
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(customTheme.onBackgroundText)
                )
                // Celsius option
                Row(
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                hiveViewModel.setTemperatureMeasurement(
                                    TemperatureMeasurement.Celsius.displayValue
                                )
                                expanded = false
                            },
                            interactionSource = MutableInteractionSource(),
                            indication = LocalIndication.current
                        )
                        .fillMaxWidth()
                        .height(32.dp)
                        .padding(0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        TemperatureMeasurement.Celsius.displayValue,
                        Modifier.padding(8.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun HiveListSection(
    hiveViewModel: HiveViewModel, navController: NavController
) {
    val state = hiveViewModel.state
    val screenHeight = LocalContext.current.resources.displayMetrics.heightPixels.dp
    val hiveList = state.hives

    // list of hives
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight)
            .testTag("HiveListSection")
        ,
        contentPadding = PaddingValues(8.dp),
    ) {
        // without the key, the list will not update when the list changes
        // this is because the list is not aware of the changes
        items(items = hiveList, key = { it.id }) {hive ->
            HiveListItem(hive, hiveViewModel, navController)
        }
    }

}

@Composable
fun HiveListItem(hive: Hive, hiveViewModel: HiveViewModel, navController: NavController) {
    val state = hiveViewModel.state
    val haptic = LocalHapticFeedback.current

    Row(Modifier.padding(vertical = 8.dp).testTag("HiveListItem")) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .background(customTheme.surfaceColor, RoundedCornerShape(8.dp))
            .border(2.dp, customTheme.primaryColor, RoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    hiveViewModel.onTapHiveListItem(hive.id, navController)
                }, onLongPress = {
                    // play haptic feedback
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    hiveViewModel.onLongPressHiveListItem(hive.id)
                })
            }) {
            if (state.hiveDeleteMode) {
                CustomAnimatedCheckbox(
                    checked = hiveViewModel.state.selectionList.contains(hive.id),
                    onCheckedChange = {
                        hiveViewModel.onTapHiveListItem(hive.id, navController)
                    },
                    modifier = Modifier.padding(8.dp)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = hive.hiveInfo.name,
                    color = customTheme.onSurfaceText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = hive.id,
                    color = customTheme.onSurfaceText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )

            }
        }
    }
}

@Composable
fun HiveDetailsMenu(hiveViewModel: HiveViewModel) {
    val state = hiveViewModel.state
    OverlayBoxMenu(
        onTapOutside = {
            hiveViewModel.onTapOutside()
        }, height = 450.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                text = "Hive Details",
                color = customTheme.primaryColor,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            // ID selector
            state.selectedHiveToBeEdited?.let { hive ->
                Text(
                    text = "Name: ${hive.hiveInfo.name}",
                    color = customTheme.onSurfaceColor,
                )
                TextInput(
                    label = "Name",
                    value = hive.hiveInfo.name,
                    onSubmit = { hiveViewModel.setHiveName(it) },
                    customInputValidationFunction = { true },
                    hiveViewModel = hiveViewModel,
                    submitButtonText = "Set"
                )
                CustomButton(onClick = {
                    hiveViewModel.updateHive(hive)
                }) {
                    Text(text = "Save Changes")
                }

                Text(text = "ID: ${hive.id}")
                // Location
                Text(text = "Location: ${hive.hiveInfo.location}")
                // date created
                Text(
                    text = "Date Created: ${
                        hiveViewModel.dateMillisToDateString(
                            hive.hiveInfo.dateCreated, true
                        )
                    }"
                )
                // date modified
                Text(
                    text = "Date Modified: ${
                        hiveViewModel.dateMillisToDateString(
                            hive.hiveInfo.dateModified, true
                        )
                    }"
                )
                // notes
                Text(text = "Notes: ${hive.hiveInfo.notes}")
                // text box for notes entry
                TextInput(
                    label = "Notes",
                    value = hive.hiveInfo.notes ?: "",
                    onSubmit = { hiveViewModel.setHiveNotes(it) },
                    customInputValidationFunction = { true },
                    hiveViewModel = hiveViewModel,
                    submitButtonText = "Set"
                )
            }
        }
    }
}
