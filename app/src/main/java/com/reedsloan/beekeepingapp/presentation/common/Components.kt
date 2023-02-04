package com.reedsloan.beekeepingapp.presentation.common

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.data.local.CheckboxSelectionValues
import com.reedsloan.beekeepingapp.presentation.home_screen.MenuState
import com.reedsloan.beekeepingapp.presentation.screens.Screen
import com.reedsloan.beekeepingapp.ui.custom_theme.customTheme
import kotlinx.coroutines.launch
import java.util.*


@Composable
fun NavigationBar(navController: NavController, hiveViewModel: HiveViewModel) {
    val state = hiveViewModel.state

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(customTheme.primaryColor)
    ) {
        val (menuButton, title, loading) = createRefs()
        // hamburger menu button
        Column(
            modifier = Modifier
                .size(48.dp)
                .background(customTheme.onPrimaryColor, RoundedCornerShape(8.dp))
                // used to get the ripple effect fit to the shape
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    hiveViewModel.onTapNavigationExpandButton()
                }
                .constrainAs(menuButton) {
                    start.linkTo(parent.start)
                    end.linkTo(title.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = when (state.navigationBarMenuState) {
                    MenuState.CLOSED -> Icons.Default.Menu
                    MenuState.OPEN -> Icons.Default.ExpandLess
                },
                contentDescription = "Menu",
                tint = customTheme.onPrimaryText
            )
        }

        // title
        Text(
            text = "Beekeeping App",
            fontWeight = Bold,
            color = customTheme.onPrimaryColor,
            fontSize = 24.sp,
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(title) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
        Column(
            Modifier
                .padding(8.dp)
                .alpha(if (state.isLoading) 1f else 1f)
                .constrainAs(loading) {
                    start.linkTo(title.end)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
        ) {
            LoadingIndicator(hiveViewModel.state.isLoading)
        }
    }

    if (state.navigationBarMenuState == MenuState.OPEN) {
        Menu(navController, hiveViewModel)
    }
}

@Composable
fun LoadingIndicator(isLoading: Boolean) {
    val animatedAlpha = remember { Animatable(0F) }

    LaunchedEffect(key1 = isLoading) {
        animatedAlpha.animateTo(
            targetValue = if (isLoading) 1f else 0f,
            animationSpec = tween(
                durationMillis = 150,
                delayMillis = 150,
                easing = FastOutLinearInEasing
            )
        )
    }

    Column(
        Modifier
            .padding(8.dp)
            .size(32.dp)
            .alpha(animatedAlpha.value)
    ) {
        CircularProgressIndicator(
            color = customTheme.onPrimaryColor,
            strokeWidth = 4.dp
        )
    }
}

@Composable
fun TextInput(
    label: String,
    value: String,
    onSubmit: (String) -> Unit,
    hiveViewModel: HiveViewModel,
    modifier: Modifier = Modifier,
    submitButtonText: String = "Submit",
    customInputValidationFunction: (String) -> Boolean = { true },
) {
    var editableValue by remember { mutableStateOf(value) }
    val context = LocalContext.current
    // side effect to reset the value when the selected hive id is changed
    LaunchedEffect(key1 = hiveViewModel.state.selectedHiveToBeEdited?.id) {
        hiveViewModel.hideKeyboard(context)
        editableValue = value
    }
    // title
    Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp)

    Row(modifier = modifier) {
        // show a text field for the user to enter a custom value
        TextField(
            value = editableValue,
            onValueChange = { string ->
                // capitalize the first letter of the value
                editableValue = string.replaceFirstChar {
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
                if (editableValue.isNotBlank() && customInputValidationFunction(editableValue)) {
                    // submit the value
                    onSubmit(editableValue)

                    // hide the keyboard
                    hiveViewModel.hideKeyboard(context)
                } else {
                    Toast.makeText(
                        context, "Please enter a valid value.", Toast.LENGTH_SHORT
                    ).show()
                }
            }),
        )
        CustomButton(
            onClick = {
                // show toast
                if (editableValue.isNotBlank() && customInputValidationFunction(editableValue)) {
                    // submit the value
                    onSubmit(editableValue)

                    // hide the keyboard
                    hiveViewModel.hideKeyboard(context)
                } else {
                    Toast.makeText(
                        context, "Please enter a valid value.", Toast.LENGTH_SHORT
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
fun Menu(navController: NavController, hiveViewModel: HiveViewModel) {
    val state = hiveViewModel.state

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(customTheme.primaryColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // home screen
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, customTheme.onPrimaryColor, RoundedCornerShape(8.dp))
                .height(48.dp)
                .padding(horizontal = 16.dp)
                .clickable {
                    hiveViewModel.onTapNavigationButton()
                    navController.navigate(Screen.HomeScreen.route)
                }) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                tint = customTheme.onPrimaryColor
            )
            Text(
                text = "Home",
                color = customTheme.onPrimaryColor,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        // add screen
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, customTheme.onPrimaryColor, RoundedCornerShape(8.dp))
                .height(48.dp)
                .padding(horizontal = 16.dp)
                .clickable {
                    hiveViewModel.onTapNavigationButton()
                    navController.navigate(Screen.AddScreen.route)
                }) {
            Icon(
                imageVector = Icons.Default.Hive,
                contentDescription = null,
                tint = customTheme.onPrimaryColor
            )
            Text(
                text = "Hives",
                color = customTheme.onPrimaryColor,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        // settings screen
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, customTheme.onPrimaryColor, RoundedCornerShape(8.dp))
                .height(48.dp)
                .padding(horizontal = 16.dp)
                .clickable {
                    hiveViewModel.onTapNavigationButton()
                    navController.navigate(Screen.SettingsScreen.route)
                }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = customTheme.onPrimaryColor
            )
            Text(
                text = "Settings",
                color = customTheme.onPrimaryColor,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        // spacer with line
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(customTheme.onPrimaryColor)
        )
        ConstraintLayout(Modifier.fillMaxWidth()) {
            val fontSize by remember {
                mutableStateOf(14.sp)
            }
            val (versionName, versionDot, versionNumber) = createRefs()
            // version name
            Text(text = "Version ${state.appVersionNumber}",
                color = customTheme.onPrimaryColor,
                fontSize = fontSize,
                modifier = Modifier
                    .constrainAs(versionName) {
                        top.linkTo(parent.top)
                        end.linkTo(versionDot.start)
                    }
                    .padding(horizontal = 4.dp))
            // version dot (centered)
            Text(text = "•",
                color = customTheme.onPrimaryColor,
                fontSize = fontSize,
                modifier = Modifier.constrainAs(versionDot) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                })
            // version number
            Text(text = "Build ${state.appVersionCode}",
                color = customTheme.onPrimaryColor,
                fontSize = fontSize,
                modifier = Modifier
                    .constrainAs(versionNumber) {
                        top.linkTo(parent.top)
                        start.linkTo(versionDot.end)
                    }
                    .padding(horizontal = 4.dp))
        }

    }
}


@Composable
fun CustomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String? = null,
    textColor: Color = customTheme.onPrimaryColor,
    content: @Composable () -> Unit? = {},
) {
    Button(
        onClick = onClick, modifier = modifier, colors = ButtonDefaults.buttonColors(
            backgroundColor = customTheme.primaryColor, contentColor = customTheme.onPrimaryColor
        ), enabled = enabled
    ) {
        content()
        // text if provided
        text?.let {
            Text(
                text = it,
                color = textColor,
            )
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
fun WorkInProgressOverlayText() {
    // the text should overlay the screen irregardless of where it is called
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {

        Text(
            text = "Work in progress.",
            color = Color.Black,
            fontSize = 36.sp,
            modifier = Modifier
                .alpha(0.05F)
                .align(Alignment.Center),
            maxLines = 1
        )
    }
}

fun Modifier.tapOrReleaseClickable(
    interactionSource: MutableInteractionSource,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
): Modifier = composed {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    pointerInput(interactionSource) {
        val pressInteraction = PressInteraction.Press(Offset.Zero)
        val cancelInteraction = PressInteraction.Cancel(pressInteraction)
        detectTapGestures(
            onPress = {
                interactionSource.tryEmit(pressInteraction)
            },
            onTap = {
                interactionSource.tryEmit(cancelInteraction)
                onTap()
            },
            onLongPress = {
                interactionSource.tryEmit(cancelInteraction)
                scope.launch {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                onLongPress()
            }
        )
    }
}

@Composable
fun CircleCornerButton(
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {},
    onLongPress: () -> Unit = {},
    icon: ImageVector? = null,
    iconColor: Color = customTheme.onPrimaryColor,
    backgroundColor: Color = customTheme.primaryColor,
    padding: Dp = 0.dp,
    size: Dp = 64.dp,
    content: @Composable () -> Unit? = {},
) {
    // replace with a column instead of a button
    val interactionSource = remember { MutableInteractionSource() }
    val indication =
        rememberRipple(bounded = false, radius = size / 2)

    Column(modifier = Modifier.padding(padding)) {
        Column(
            modifier = modifier
                .size(size)
                .background(backgroundColor, CircleShape)
                .tapOrReleaseClickable(
                    interactionSource = interactionSource,
                    onTap = onTap,
                    onLongPress = onLongPress
                )
                .indication(interactionSource, indication),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            content()
            // icon if provided
            if (icon != null) {
                Icon(
                    imageVector = icon, contentDescription = null, tint = iconColor
                )
            }
        }
    }
}

@Composable
fun CustomAnimatedCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val animatedColor = animateColorAsState(
        targetValue = if (checked) customTheme.primaryColor else Color.LightGray,
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearOutSlowInEasing
        )
    ).value

    val animatedStrokeWidth = animateDpAsState(
        targetValue = if (checked) 0.dp else 2.dp,
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearOutSlowInEasing
        )
    ).value

    val animatedIconSize = animateDpAsState(
        targetValue = if (checked) 24.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearOutSlowInEasing
        )
    ).value

    val animatedIconColor = animateColorAsState(
        targetValue = if (checked) customTheme.onPrimaryColor else Color.LightGray,
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearOutSlowInEasing
        )
    ).value

    val animatedIcon = animateDpAsState(
        targetValue = if (checked) 0.dp else 4.dp,
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearOutSlowInEasing
        )
    ).value

    val animatedIconPadding = animateDpAsState(
        targetValue = if (checked) 0.dp else 4.dp,
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearOutSlowInEasing
        )
    ).value

    val animatedIconAlpha = animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearOutSlowInEasing
        )
    ).value
    Box(modifier) {
        // checkbox
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(animatedColor, CircleShape)
                .border(
                    width = animatedStrokeWidth,
                    color = Color.LightGray,
                    shape = CircleShape
                )
                .padding(animatedIconPadding)
                .clip(CircleShape)
                .clickable {
                    onCheckedChange(!checked)
                }
        ) {
            // icon
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = animatedIconColor,
                modifier = Modifier
                    .size(animatedIconSize)
                    .alpha(animatedIconAlpha)
            )
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
    hiveViewModel: HiveViewModel
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
                    else customTheme.surfaceColor, shape = RoundedCornerShape(4.dp)
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
        // if the user has selected the "Other" option, show the text field to add a custom value
        if (showCustomValueTextField) {
            Text(text = "Custom value:", fontWeight = FontWeight.Bold)

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
    hiveViewModel: HiveViewModel
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

/**
 * A customizable menu that overlays the entire parent [Box] with the [content] in the center.
 *
 * Must be a direct child element of a [Box].
 */
@Composable
fun OverlayBoxMenu(
    onTapOutside: () -> Unit = {},
    onTapInside: () -> Unit = {},
    height: Dp = 350.dp,
    width: Dp = 300.dp,
    backgroundColor: Color = customTheme.surfaceColor,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier
        .alpha(0.5F)
        .fillMaxSize()
        .background(Color.Black)
        .zIndex(1F)
        .pointerInput(Unit) {
            detectTapGestures(
                // Consume the tap event so that the screen doesn't get tapped.
                onTap = {
                    onTapOutside()
                })
        })
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .alpha(1F)
            .background(backgroundColor)
            .width(width)
            .height(height)
            .zIndex(2F)
            .pointerInput(Unit) {
                detectTapGestures(
                    // Consume the tap event so that the screen doesn't get tapped.
                    onTap = {
                        onTapInside()
                    })
            }) {
        content()
    }
}