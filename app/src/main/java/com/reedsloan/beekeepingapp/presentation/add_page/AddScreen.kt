package com.reedsloan.beekeepingapp.presentation.add_page

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.*
import com.reedsloan.beekeepingapp.data.local.CheckboxSelectionValues
import com.reedsloan.beekeepingapp.data.local.SelectionType
import com.reedsloan.beekeepingapp.data.local.TemperatureMeasurement
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.data.local.hive.Treatment
import com.reedsloan.beekeepingapp.presentation.common.NavigationBar
import com.reedsloan.beekeepingapp.presentation.home_screen.HomeViewModel
import com.reedsloan.beekeepingapp.presentation.home_screen.MenuState
import com.reedsloan.beekeepingapp.ui.custom_theme.customTheme
import com.reedsloan.isPermanentlyDenied
import java.util.*

@Composable
fun AddScreen(navController: NavController, homeViewModel: HomeViewModel = hiltViewModel()) {
    val state = homeViewModel.state
    val addScreenScrollState = rememberScrollState()

    Column(
        Modifier
            .verticalScroll(addScreenScrollState)
            .wrapContentHeight(unbounded = false)
    ) {
        NavigationBar(navController, homeViewModel)
        Text("This screen is a test.", fontSize = 24.sp)

        Container {
            // Get the max width of the screen and use that to set the width of the popup window.
            // This is so that the popup window is always the same width as the screen.
            // Subtract 16.dp * 2 from the width to account for the padding on the popup window.
            val maxWidth = with(LocalDensity.current) {
                LocalContext.current.resources.displayMetrics.widthPixels.toDp() - 16.dp * 2
            }


//            SelectionDropdownMenu(
//                title = "Temperature",
//                options = TemperatureMeasurement.values().map { it.displayValue },
//                selectedOption = state.userPreferences.temperatureMeasurement.displayValue,
//                onOptionSelected = { homeViewModel.setTemperatureMeasurement(it.first()) },
//                modifier = Modifier
//                    .background(customTheme.surfaceColor, RoundedCornerShape(8.dp))
//                    .border(
//                        2.dp, customTheme.onSurfaceColor, RoundedCornerShape(8.dp)
//                    )
//                    .padding(8.dp),
//                dropdownWidth = maxWidth,
//                homeViewModel = homeViewModel
//            )
//
//            SelectionCheckboxMenu(
//                title = "Treatment",
//                options = Treatment.values().map { it.displayValue },
//                modifier = Modifier,
//                dropdownWidth = maxWidth,
//                checkboxSelectionValues = CheckboxSelectionValues.Builder().setMaxSelectionCount(6)
//                    .setShowSelectionInstructions(true)
//                    .setDisabledValues(listOf(Treatment.CHECKMITE_PLUS.displayValue))
//                    .setAllowCustomValues(true).build(),
//                homeViewModel = homeViewModel,
//                onSubmit = { /*TODO*/ },
//            )


            // create new hive button
            CustomButton(onClick = {
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

@Composable
fun CustomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = customTheme.primaryColor,
            contentColor = customTheme.onPrimaryColor
        ),
        enabled = enabled
    ) {
        content()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsTest(navController: NavController, homeViewModel: HomeViewModel) {
    val state = homeViewModel.state
    var bitmapOrNull: Bitmap? by remember { mutableStateOf(null) }
    val cameraOpenIntent =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            // update the state with the bitmap
            bitmapOrNull = bitmap
        }

    val addScreenScrollState = rememberScrollState()
    val heightOfContent = with(LocalDensity.current) { addScreenScrollState.maxValue.toDp() }
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

/**
 * A container to add padding to the left and right of the content.
 */
@Composable
fun Container(
    modifier: Modifier = Modifier, content: @Composable () -> Unit
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
fun TextInput(
    title: String,
    initialValue: String,
    onSubmit: (String) -> Unit,
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    submitButtonText: String = "Submit",
    customInputValidationFunction: (String) -> Boolean = { true },
) {
    var value by remember { mutableStateOf(initialValue) }
    val context = LocalContext.current
    // side effect to reset the value when the selected hive id is changed
    LaunchedEffect(key1 = homeViewModel.state.selectedHive?.hiveInfo?.id) {
        homeViewModel.hideKeyboard(context)
        value = initialValue
    }
    // title
    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)

    Row(modifier = modifier) {
        // show a text field for the user to enter a custom value
        TextField(
            value = value,
            onValueChange = { string ->
                // capitalize the first letter of the value
                value = string.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                }
            },
            modifier = Modifier
                .height(48.dp)
                .weight(0.8F),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = customTheme.surfaceColor,
                focusedIndicatorColor = customTheme.primaryColor,
                unfocusedIndicatorColor = customTheme.onSurfaceColor.copy(alpha = 0.5f),
                textColor = customTheme.onSurfaceColor,
                cursorColor = customTheme.primaryColor,
                disabledIndicatorColor = customTheme.onSurfaceColor.copy(alpha = 0.5f),
                disabledLabelColor = customTheme.onSurfaceColor.copy(alpha = 0.5f),
                disabledTextColor = customTheme.onSurfaceColor.copy(alpha = 0.5f),
                focusedLabelColor = customTheme.primaryColor,
                unfocusedLabelColor = customTheme.onSurfaceColor.copy(alpha = 0.5f)
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                // show toast
                if (value.isNotBlank() && customInputValidationFunction(value)) {
                    // submit the value
                    onSubmit(value)

                    // hide the keyboard
                    homeViewModel.hideKeyboard(context)
                } else {
                    Toast.makeText(
                        context,
                        "Please enter a valid value.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }),
        )
        CustomButton(
            onClick = {
                // show toast
                if (value.isNotBlank() && customInputValidationFunction(value)) {
                    // submit the value
                    onSubmit(value)

                    // hide the keyboard
                    homeViewModel.hideKeyboard(context)
                } else {
                    Toast.makeText(
                        context,
                        "Please enter a valid value.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            Modifier
                .weight(0.2F)
                .height(48.dp)
        ) {
            Text(text = submitButtonText)
        }
    }
}

@Composable
fun SelectionCheckboxMenu(
    title: String,
    options: List<String>,
    onSubmit: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    dropdownWidth: Dp,
    checkboxSelectionValues: CheckboxSelectionValues,
    homeViewModel: HomeViewModel
) {
    var selectedOptions by remember { mutableStateOf(listOf<String>()) }
    var optionsList by remember { mutableStateOf(options.toList()) }
    var newOptionValue by remember { mutableStateOf("") }
    val context = LocalContext.current

    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)

    if (checkboxSelectionValues.showSelectionInstructions) {
        // instructions for the user (e.g. "Select up to 3 options")
        Text(
            "Select ${checkboxSelectionValues.maxSelectionCount} " + if (checkboxSelectionValues.maxSelectionCount == 1) "option" else {
                "options"
            } + ":", fontWeight = FontWeight.Bold
        )

        // currently selected amount
        Text("Selected: ${selectedOptions.size}/${checkboxSelectionValues.maxSelectionCount}")
    }

    // show the options
    optionsList.forEach { option ->
        val interactionSource = remember { MutableInteractionSource() }
        val isSelected = selectedOptions.contains(option)
        val isDisabled = checkboxSelectionValues.disabledValues.contains(option)
        Row(
            modifier = Modifier
                .clickable(interactionSource = interactionSource, indication = null,
                    // onClick to add or remove the option from the list of selected options
                    onClick = {
                        // if option is not in the list, add it
                        if (!isSelected) {
                            // if the max selection count has been reached, show a toast
                            if (checkboxSelectionValues.disabledValues.contains(option)) {
                                // if the option is disabled, show a toast
                                Toast
                                    .makeText(
                                        context, "This option is disabled", Toast.LENGTH_SHORT
                                    )
                                    .show()
                            } else if (selectedOptions.size == checkboxSelectionValues.maxSelectionCount) {
                                // if the option is not disabled, but the max selection count has been
                                // reached, show a toast
                                Toast
                                    .makeText(
                                        context,
                                        "You have reached the max selection count",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            } else {
                                // if the option is not disabled and the max selection count has not
                                // been reached, add the option to the list of selected options
                                selectedOptions = selectedOptions.plus(option)
                            }
                        } else {
                            // if the option is in the list, remove it
                            selectedOptions = selectedOptions.filter { it != option }
                        }
                    })
                .fillMaxWidth()
                .height(40.dp)
                .padding(vertical = 2.dp)
                .background(
                    color = if (isSelected) customTheme.primaryColor.copy(alpha = 0.5f)
                    else if (isDisabled) customTheme.onBackgroundColor.copy(alpha = 0.35f)
                    else customTheme.surfaceColor,
                    shape = RoundedCornerShape(4.dp)
                ), verticalAlignment = Alignment.CenterVertically
        ) {
            // create a checkbox
            Checkbox(checked = isSelected,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkmarkColor = customTheme.onPrimaryColor,
                    checkedColor = customTheme.primaryColor,
                    disabledColor = customTheme.onSurfaceColor.copy(alpha = 0.5f),
                    uncheckedColor = customTheme.onSurfaceColor
                ),
                enabled = isDisabled.not(),
                modifier = // prevent clicks since we want to use the entire row as a button
                Modifier
                    .clickable(enabled = false) {}
                    .padding(end = 4.dp))
            Text(
                text = option,
                color = if (isDisabled) customTheme.onSurfaceColor.copy(alpha = 0.5f)
                else customTheme.onSurfaceColor,
            )
        }
    }

    // if the user is allowed to add custom values, show the custom value input
    if (checkboxSelectionValues.allowCustomValues) {
        var showCustomValueTextField by remember { mutableStateOf(false) }

        // Other option that allows the user to add a custom value to the list
        Row(
            Modifier
                .clickable(onClick = {
                    showCustomValueTextField = !showCustomValueTextField
                })
                .fillMaxWidth()
                .height(40.dp)
                .padding(vertical = 2.dp)
                .background(
                    color = if (showCustomValueTextField) customTheme.primaryColor.copy(alpha = 0.5f) else customTheme.surfaceColor,
                    shape = RoundedCornerShape(4.dp)
                ), verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = showCustomValueTextField,
                // for some reason when I use onCheckedChange = { showCustomValueTextField = it } it
                // adds padding to the checkbox and I don't know why but this is okay since
                // I don't need to use onCheckedChange and can just use onClick of the entire row
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkmarkColor = customTheme.onPrimaryColor,
                    checkedColor = customTheme.primaryColor,
                    disabledColor = customTheme.onSurfaceColor.copy(alpha = 0.5f),
                    uncheckedColor = customTheme.onSurfaceColor
                ),
                enabled = true,
                modifier = // prevent clicks since we want to use the entire row as a button
                Modifier
                    .clickable(enabled = false) {}
                    .padding(end = 4.dp))
            Text(
                text = "Other",
                color = customTheme.onSurfaceColor,
            )
        }
        Text(text = "Custom value:", fontWeight = FontWeight.Bold)
        // if the user has selected the "Other" option, show the text field to add a custom value
        if (showCustomValueTextField) {
            Row {
                // show a text field for the user to enter a custom value
                TextField(
                    value = newOptionValue,
                    onValueChange = { string ->
                        // capitalize the first letter of the value
                        newOptionValue = string.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                        }
                    },
                    modifier = Modifier
                        .height(48.dp)
                        .weight(0.8F),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = customTheme.surfaceColor,
                        focusedIndicatorColor = customTheme.primaryColor,
                        unfocusedIndicatorColor = customTheme.onSurfaceColor.copy(alpha = 0.5f),
                        textColor = customTheme.onSurfaceColor,
                        cursorColor = customTheme.primaryColor,
                        disabledIndicatorColor = customTheme.onSurfaceColor.copy(alpha = 0.5f),
                        disabledLabelColor = customTheme.onSurfaceColor.copy(alpha = 0.5f),
                        disabledTextColor = customTheme.onSurfaceColor.copy(alpha = 0.5f),
                        focusedLabelColor = customTheme.primaryColor,
                        unfocusedLabelColor = customTheme.onSurfaceColor.copy(alpha = 0.5f)
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        // show toast
                        if (newOptionValue.isNotBlank() && newOptionValue !in optionsList) {
                            Toast.makeText(
                                context,
                                "Added \"$newOptionValue\" to the list of options",
                                Toast.LENGTH_SHORT
                            ).show()
                            // add the custom value to the list of options
                            optionsList = optionsList.plus(newOptionValue)
                            // add the custom value to the list of selected options
                            selectedOptions = selectedOptions.plus(newOptionValue)
                            // clear the text field
                            newOptionValue = ""
                            // hide the keyboard
                            val inputMethodManager =
                                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                            inputMethodManager.hideSoftInputFromWindow(
                                (context as Activity).currentFocus?.windowToken, 0
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "Please enter a value that is not already in the list of options",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }),
                )
                CustomButton(
                    onClick = {
                        // show toast
                        if (newOptionValue.isNotBlank() && newOptionValue !in optionsList) {
                            Toast.makeText(
                                context,
                                "Added \"$newOptionValue\" to the list of options",
                                Toast.LENGTH_SHORT
                            ).show()
                            // add the custom value to the list of options
                            optionsList = optionsList.plus(newOptionValue)
                            // add the custom value to the list of selected options
                            selectedOptions = selectedOptions.plus(newOptionValue)
                            // clear the text field
                            newOptionValue = ""
                            // hide the keyboard
                            val inputMethodManager =
                                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                            inputMethodManager.hideSoftInputFromWindow(
                                (context as Activity).currentFocus?.windowToken, 0
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "Please enter a value that is not already in the list of options",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    Modifier
                        .weight(0.2F)
                        .height(48.dp)
                ) {
                    Text(text = "Add")
                }
            }
        }
    }
}

@Composable
fun SelectionDropdownMenu(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    dropdownWidth: Dp,
    homeViewModel: HomeViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(interactionSource = interactionSource,
                    indication = null,
                    onClick = { expanded = !expanded })
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
                    2.dp, customTheme.onSurfaceColor, RoundedCornerShape(8.dp)
                )
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onOptionSelected(listOf(option))
                }) {
                    Text(option)
                }
            }
        }
    }
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
        columns = GridCells.Adaptive(minSize = 128.dp),
        Modifier.height(256.dp)
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
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
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
            Row(modifier = Modifier
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
                verticalAlignment = Alignment.CenterVertically) {
                // expand/collapse button
                Icon(
                    imageVector = when (state.hiveInfoMenuState) {
                        MenuState.Closed -> Icons.Default.ExpandMore
                        MenuState.Open -> Icons.Default.ExpandLess
                    },
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
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
            state.selectedHive?.let { hive ->
                Text(
                    text = "Name: ${hive.hiveInfo.name}",
                )
                TextInput(
                    title = "Name",
                    initialValue = hive.hiveInfo.name,
                    onSubmit = { homeViewModel.setHiveName(it) },
                    customInputValidationFunction = { true },
                    homeViewModel = homeViewModel,
                    submitButtonText = "Set"
                )
                CustomButton(onClick = {
                    homeViewModel.updateHive(hive)
                }) {
                    Text(text = "Save Changes")
                }

                Text(text = "ID: ${hive.hiveInfo.id}")
                // Location
                Text(text = "Location: ${hive.hiveInfo.location}")
                // date created
                Text(
                    text = "Date Created: ${
                        homeViewModel.dateMillisToDateString(
                            hive.hiveInfo.dateCreated,
                            true
                        )
                    }"
                )
                // date modified
                Text(
                    text = "Date Modified: ${
                        homeViewModel.dateMillisToDateString(
                            hive.hiveInfo.dateModified,
                            true
                        )
                    }"
                )
                // notes
                Text(text = "Notes: ${hive.hiveInfo.notes}")
            }
        }
    }
}
