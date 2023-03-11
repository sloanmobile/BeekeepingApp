package com.reedsloan.beekeepingapp.presentation.hives_screen

import android.Manifest
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.*
import com.reedsloan.beekeepingapp.R
import com.reedsloan.beekeepingapp.data.local.TemperatureMeasurement
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.presentation.common.*
import com.reedsloan.beekeepingapp.presentation.common.extensions.surfaceStyle
import com.reedsloan.beekeepingapp.presentation.common.input_types.TextInput
import com.reedsloan.beekeepingapp.presentation.common.layout.OverlayBoxMenu
import com.reedsloan.beekeepingapp.presentation.home_screen.MenuState
import com.reedsloan.beekeepingapp.presentation.ui.custom_theme.customTheme
import com.reedsloan.beekeepingapp.presentation.ui.theme.Typography
import com.reedsloan.isPermanentlyDenied
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun AddScreen(navController: NavController, hiveViewModel: HiveViewModel) {
    val state = hiveViewModel.state


    Box {
        Box(contentAlignment = Alignment.Center) {
            when (state.hiveInfoMenuState) {
                MenuState.CLOSED -> {}
                MenuState.OPEN -> {
                    HiveDetailsMenu(hiveViewModel = hiveViewModel)
                }
            }
            Column(
                modifier = Modifier.fillMaxSize()
                // onTap event to close various menus if the user taps outside of them
            ) {
                Column(
                    Modifier.fillMaxSize()
                ) {

                    Container {
                        PermissionsTest(navController, hiveViewModel)
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
                    CircleButton(
                        onTap = { hiveViewModel.onTapDeleteSelectedHiveButton() },
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
                    CircleButton(onTap = { hiveViewModel.onTapAddHiveButton() }) {
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
            // write the bitmap to a file
            hiveViewModel.writeBitmapToFile(bitmap)
        }

    val imagePickerIntent =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            // update the state with the uri
            hiveViewModel.setHiveImageUri(uri)
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

    LoadingIndicator(isLoading = state.isLoading)

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
    CustomButton(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
        Text("Request permissions")
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
        CustomButton(onClick = {
            // open image picker
            imagePickerIntent.launch("image/*")
        }, enabled = state.isStoragePermissionAllowed) {
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
                                    TemperatureMeasurement.FAHRENHEIT.displayValue
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
                        TemperatureMeasurement.FAHRENHEIT.displayValue,
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
                                    TemperatureMeasurement.CELSIUS.displayValue
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
                        TemperatureMeasurement.CELSIUS.displayValue,
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
            .testTag("HiveListSection"),
    ) {
        // without the key, the list will not update when the list changes
        // this is because the list is not aware of the changes
        items(items = hiveList, key = { it.id }) { hive ->
            HiveListItem(hive, hiveViewModel, navController)
        }
    }

}

@Composable
fun HiveListItem(hive: Hive, hiveViewModel: HiveViewModel, navController: NavController) {
    val haptic = LocalHapticFeedback.current
    val menuInitialSize by remember { mutableStateOf(108.dp) }

    val menuHeight = remember { Animatable(menuInitialSize.value) }
    var menuExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .testTag("HiveListItem")
            .fillMaxWidth()
            .height(menuHeight.value.dp)
            .surfaceStyle()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    menuExpanded = if (menuExpanded) {
                        scope.launch {
                            menuHeight.animateTo(menuInitialSize.value)
                        }
                        false
                    } else {
                        scope.launch {
                            menuHeight.animateTo(menuInitialSize.value + 176.dp.value)
                        }
                        true
                    }
                },
                    onLongPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        hiveViewModel.onLongPressHiveListItem(hive.id)
                    }
                )
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(menuInitialSize)
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.height(menuInitialSize),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // image else default image
                if (hive.hiveInfo.image != null) {
                    AsyncImage(
                        model = hive.hiveInfo.image,
                        contentDescription = "Hive Image",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        // scale asyncimage to fill the entire imageview
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.app),
                        contentDescription = "Hive Image",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(start = 8.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                // hive name
                Text(
                    text = hive.hiveInfo.name,
                    style = Typography.h2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                // hive description
                Text(
                    text = hive.hiveInfo.notes,
                    style = Typography.body2,
                    color = customTheme.hintColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                val date = hiveViewModel.dateMillisToDateString(hive.hiveInfo.dateModified)

                // align row to bottom end
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            // ignore the parent's bottom padding
                            translationY = 8.dp.toPx()
                        }
                        .padding(0.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End
                ) {
                    // hive date modified
                    Text(
                        text = "Last Updated: $date",
                        style = Typography.caption,
                        color = customTheme.hintColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        if (menuExpanded) {
            Column(Modifier.padding(16.dp)) {
                HiveOption(
                    title = "Log Data",
                    painterResource = painterResource(id = R.drawable.notebook_edit_outline)
                ) {
                    hiveViewModel.onClickLogDataButton(hive.id, navController)
                }
                Spacer(modifier = Modifier.height(8.dp))
                HiveOption(
                    title = "View Log History",
                    painterResource = painterResource(id = R.drawable.history)
                ) {
                    hiveViewModel.onClickViewLogHistoryButton(hive.id, navController)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    HiveOption(
                        title = "Edit",
                        icon = Icons.Default.Edit,
                        backgroundColor = customTheme.primaryColorLight,
                        textColor = customTheme.onPrimaryColor,
                        modifier = Modifier.weight(0.5f),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        hiveViewModel.onClickEditHiveButton(hive.id)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    HiveOption(
                        title = "Delete",
                        icon = Icons.Default.Delete,
                        backgroundColor = customTheme.cancelColor,
                        textColor = customTheme.onCancelColor,
                        modifier = Modifier.weight(0.5f),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        hiveViewModel.onTapDeleteHiveButton(hive.id)
                    }
                }
            }
        }
    }
}

@Composable
fun HiveOption(
    title: String,
    modifier: Modifier = Modifier,
    painterResource: Painter? = null,
    icon: ImageVector? = null,
    backgroundColor: Color = customTheme.secondarySurfaceColor,
    textColor: Color = customTheme.onSecondarySurfaceText,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    onClick: () -> Unit = {},
) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement,
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(
                backgroundColor, RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                onClick()
            }
            .padding(horizontal = 8.dp)
    )
    {
        if (painterResource != null) {
            Icon(
                painter = painterResource,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(32.dp)
            )
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(32.dp)
            )
        }
        Text(
            text = title,
            style = Typography.h3,
            color = textColor,
        )
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
            state.selectedHive?.let { hive ->
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
