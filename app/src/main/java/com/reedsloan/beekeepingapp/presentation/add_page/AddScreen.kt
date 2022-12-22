package com.reedsloan.beekeepingapp.presentation.add_page

import android.Manifest
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Hive
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.*
import com.reedsloan.beekeepingapp.data.local.TemperatureMeasurement
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.presentation.common.NavigationBar
import com.reedsloan.beekeepingapp.presentation.home_screen.HomeViewModel
import com.reedsloan.beekeepingapp.presentation.home_screen.MenuState
import com.reedsloan.beekeepingapp.ui.custom_theme.customTheme
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

    val addScreenScrollState = rememberScrollState()

    Column(Modifier.scrollable(state = addScreenScrollState, orientation = Orientation.Vertical)) {
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
            if (!state.isCameraPermissionAllowed) {
                Text(" (Camera permission needed)")
            }
            Button(
                onClick = {
                    // open camera
                    cameraOpenIntent.launch(null)
                },
                enabled = state.isCameraPermissionAllowed
            ) {
                Text("Take Photo")
            }

            if (!state.isStoragePermissionAllowed) {
                Text(" (Storage permission needed)")
            }
            Button(onClick = { }, enabled = state.isStoragePermissionAllowed) {
                Text("Choose Photo")
            }
        }

        Container {
            // Get the max width of the screen and use that to set the width of the popup window.
            // This is so that the popup window is always the same width as the screen.
            // Subtract 16.dp * 2 from the width to account for the padding on the popup window.
            val maxWidth = with(LocalDensity.current) {
                LocalContext.current.resources.displayMetrics.widthPixels.toDp() - 16.dp * 2
            }


            SelectionDropdown(
                title = "Temperature",
                options = listOf(
                    TemperatureMeasurement.Fahrenheit.displayValue,
                    TemperatureMeasurement.Celsius.displayValue
                ),
                selectedOption = state.userPreferences.temperatureMeasurement.displayValue,
                onOptionSelected = { homeViewModel.setTemperatureMeasurement(it) },
                modifier = Modifier
                    .background(customTheme.surfaceColor, RoundedCornerShape(8.dp))
                    .border(
                        2.dp,
                        customTheme.onSurfaceColor,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                dropdownWidth = maxWidth
            )
            // create new hive button
            Button(onClick = {
                // create new hive
                homeViewModel.createHive()
            }) {
                Text("Create New Hive")
            }

            // show list of hives
            HiveListSection(homeViewModel)
        }

        HiveInfoSection(homeViewModel)
    }
}

/**
 * A container to add padding to the left and right of the content.
 */
@Composable
fun Container(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        content()
    }
}

@Composable
fun SelectionDropdown(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    dropdownWidth: Dp,
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { expanded = !expanded }
                )
                .padding(8.dp)
        ) {
            Text(selectedOption, fontSize = 16.sp)
            Icon(
                if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
    Column(modifier = Modifier.padding(top = 8.dp)) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        offset = DpOffset(0.dp, 0.dp),
        modifier = Modifier
            .width(dropdownWidth)
            .background(customTheme.surfaceColor, RoundedCornerShape(8.dp))
            .border(
                2.dp,
                customTheme.onSurfaceColor,
                RoundedCornerShape(8.dp)
            )
    ) {
        options.forEach { option ->
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onOptionSelected(option)
                }
            ) {
                Text(option)
            }
        }
    }}
}

@Composable
fun TemperatureMeasurementSelection(homeViewModel: HomeViewModel) {
    val state = homeViewModel.state
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
                                homeViewModel.setTemperatureMeasurement(TemperatureMeasurement.Fahrenheit.displayValue)
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
                                homeViewModel.setTemperatureMeasurement(TemperatureMeasurement.Celsius.displayValue)
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
fun HiveListSection(homeViewModel: HomeViewModel) {
    val state = homeViewModel.state
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp)
    ) {
        items(state.hives.size) { index ->
            val hive = state.hives[index]
            HiveListItem(hive, homeViewModel)
        }
    }
}

@Composable
fun HiveListItem(hive: Hive, homeViewModel: HomeViewModel) {
    // add screen
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(4.dp)
            .background(customTheme.primaryColor, RoundedCornerShape(8.dp))
            .clickable {
                homeViewModel.setSelectedHive(hive.hiveInfo.id)
            },
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box() {
                Icon(
                    imageVector = Icons.Default.Hive,
                    contentDescription = null,
                    tint = customTheme.onPrimaryColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = hive.hiveInfo.name,
                color = customTheme.onPrimaryColor,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}


@Composable
fun HiveInfoSection(homeViewModel: HomeViewModel) {
    val state = homeViewModel.state
    Column {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(customTheme.primaryColor)
                    .height(64.dp)
                    .clickable {
                        homeViewModel.setHiveInfoMenuState(
                            when (state.hiveInfoMenuState) {
                                MenuState.Closed -> MenuState.Open
                                MenuState.Open -> MenuState.Closed
                            }
                        )
                    }
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // expand/collapse button
                Icon(
                    imageVector = when (state.hiveInfoMenuState) {
                        MenuState.Closed -> Icons.Default.ExpandMore
                        MenuState.Open -> Icons.Default.ExpandLess
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .padding(12.dp),
                    tint = customTheme.onPrimaryColor
                )
                Text(
                    text = "Hive Info",
                    fontWeight = FontWeight.Bold,
                    color = customTheme.onPrimaryColor,
                    fontSize = 24.sp,
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
        if (state.hiveInfoMenuState == MenuState.Open) {
            // ID selector
            state.selectedHive?.let {
                Text(
                    text = "Name: ${it.hiveInfo.name}",
                )
                Text(text = "ID: ${it.hiveInfo.id}")
                // Location
                Text(text = "Location: ${it.hiveInfo.location}")
                // date created
                Text(
                    text = "Date Created: ${
                        homeViewModel.dateMillisToDateString(
                            it.hiveInfo.dateCreated
                        )
                    }"
                )
                // weather
                Text(text = "Weather: ${it.hiveInfo.weather}")
                // notes
                Text(text = "Notes: ${it.hiveInfo.notes}")
                // temperature
                Text(
                    text = "Temperature: ${
                        // convert temperature to user's preferred measurement
                        homeViewModel.getTemperatureValue(
                            it.hiveInfo.temperatureFahrenheit ?: 0.0
                        )
                    }"
                )
            }
        }
    }
}