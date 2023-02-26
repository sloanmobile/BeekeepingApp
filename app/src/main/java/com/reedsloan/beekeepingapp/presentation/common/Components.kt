package com.reedsloan.beekeepingapp.presentation.common

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.R
import com.reedsloan.beekeepingapp.data.TimeFormat
import com.reedsloan.beekeepingapp.data.local.CheckboxSelectionValues
import com.reedsloan.beekeepingapp.presentation.home_screen.MenuState
import com.reedsloan.beekeepingapp.presentation.screens.Screen
import com.reedsloan.beekeepingapp.presentation.ui.custom_theme.customTheme
import com.reedsloan.beekeepingapp.presentation.ui.theme.Typography
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.Month
import java.util.*


@Composable
fun NavigationBar(navController: NavController, hiveViewModel: HiveViewModel) {
    val state = hiveViewModel.state
    val menuHeight = remember { Animatable(64.dp.value) }
    val screenName = state.currentScreenName

    val menuState = hiveViewModel.state.navigationBarMenuState

    // animate the menuHeight when the menuState changes
    LaunchedEffect(key1 = menuState) {
        if (menuState == MenuState.OPEN) {
            // expand the menuHeight to 192.dp
            menuHeight.animateTo(
                targetValue = 224.dp.value, animationSpec = tween(durationMillis = 300)
            )
        } else if (menuState == MenuState.CLOSED) {
            // collapse the menuHeight to 64.dp
            menuHeight.animateTo(
                targetValue = 64.dp.value, animationSpec = tween(durationMillis = 300)
            )
        }
    }

    Container {
        Column(
            modifier = Modifier
                .testTag("Navbar")
                .padding(top = 16.dp, bottom = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .fillMaxWidth()
                .height(menuHeight.value.dp)
                .background(
                    // gradient from secondary color to onPrimaryColor (top to bottom)
                    Brush.verticalGradient(
                        colors = listOf(
                            customTheme.secondaryColor, customTheme.onPrimaryColor
                        )
                    )
                )
                .clickable {
                    hiveViewModel.onTapNavigationExpandButton()
                },
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth()
            ) {
                val (menuButton, title, loading) = createRefs()
                if(menuState == MenuState.CLOSED) {
                    Icon(
                        painter = painterResource(id = R.drawable.hamburger),
                        contentDescription = "Menu", tint = customTheme.primaryColor,
                        modifier = Modifier
                            .constrainAs(menuButton) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                            .size(42.dp)
                            .padding(start = 16.dp),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ExpandLess,
                        contentDescription = "Menu", tint = customTheme.primaryColor,
                        modifier = Modifier
                            .constrainAs(menuButton) {
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                            .size(42.dp)
                            .padding(start = 16.dp),
                    )
                }

                // screen title
                Text(text = screenName,
                    style = Typography.h1,
                    color = customTheme.onSecondaryText,
                    modifier = Modifier
                        .padding(start = 32.dp, end = 16.dp)
                        .constrainAs(title) {
                            start.linkTo(menuButton.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        })

//        Column(
//            Modifier
//                .padding(8.dp)
//                .alpha(if (state.isLoading) 1f else 1f)
//                .constrainAs(loading) {
//                    start.linkTo(title.end)
//                    end.linkTo(parent.end)
//                    top.linkTo(parent.top)
//                    bottom.linkTo(parent.bottom)
//                },
//        ) {
//            LoadingIndicator(hiveViewModel.state.isLoading)
//        }
                // beehive icon from the drawable folder
                Icon(painter = painterResource(id = R.drawable.beehive),
                    contentDescription = "Beehive",
                    tint = customTheme.onSecondaryColor,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(42.dp)
                        .constrainAs(loading) {
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        })
            }
            Menu(navController, hiveViewModel)
        }
    }
}

@Composable
fun LoadingIndicator(isLoading: Boolean) {
    val animatedAlpha = remember { Animatable(0F) }

    LaunchedEffect(key1 = isLoading) {
        animatedAlpha.animateTo(
            targetValue = if (isLoading) 1f else 0f, animationSpec = tween(
                durationMillis = 150, delayMillis = 150, easing = FastOutLinearInEasing
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
            color = customTheme.onPrimaryColor, strokeWidth = 4.dp
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
fun NavbarEntry(
    title: String,
    icon: ImageVector? = null,
    painterResource: Painter? = null,
    navController: NavController,
    hiveViewModel: HiveViewModel,
    destination: Screen,
    isSelected: Boolean,
) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(
                if (isSelected) customTheme.primaryColor
                else customTheme.primaryColorLight, RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                if (hiveViewModel.state.navigationBarMenuState == MenuState.CLOSED) {
                    hiveViewModel.onTapNavigationExpandButton()
                    return@clickable
                }
                hiveViewModel.navigate(navController, destination)
            }) {
        if (painterResource != null) {
            Icon(
                painter = painterResource,
                contentDescription = null,
                tint = customTheme.onPrimaryColor,
                modifier = Modifier
                    .padding(8.dp)
                    .size(32.dp)
            )
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = customTheme.onPrimaryColor,
                modifier = Modifier
                    .padding(8.dp)
                    .size(32.dp)
            )
        }
        Text(
            text = title,
            style = Typography.h3,
            color = customTheme.onPrimaryColor,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun Menu(navController: NavController, hiveViewModel: HiveViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // home
        NavbarEntry(
            title = "Home",
            icon = Icons.Filled.Home,
            navController = navController,
            hiveViewModel = hiveViewModel,
            destination = Screen.HomeScreen,
            isSelected = navController.currentDestination?.route == Screen.HomeScreen.route
        )

        // hives
        NavbarEntry(
            title = "Hives",
            painterResource = painterResource(id = R.drawable.hive),
            navController = navController,
            hiveViewModel = hiveViewModel,
            destination = Screen.HiveScreen,
            isSelected = navController.currentDestination?.route == Screen.HiveScreen.route
        )

        // settings
        NavbarEntry(
            title = "Settings",
            icon = Icons.Filled.Settings,
            navController = navController,
            hiveViewModel = hiveViewModel,
            destination = Screen.SettingsScreen,
            isSelected = navController.currentDestination?.route == Screen.SettingsScreen.route
        )

        // spacer with line
//        Spacer(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(1.dp)
//                .background(customTheme.onPrimaryColor)
//        )
//        ConstraintLayout(Modifier.fillMaxWidth()) {
//            val fontSize by remember {
//                mutableStateOf(14.sp)
//            }
//            val (versionName, versionDot, versionNumber) = createRefs()
//            // version name
//            Text(text = "Version ${state.appVersionNumber}",
//                color = customTheme.onPrimaryColor,
//                fontSize = fontSize,
//                modifier = Modifier
//                    .constrainAs(versionName) {
//                        top.linkTo(parent.top)
//                        end.linkTo(versionDot.start)
//                    }
//                    .padding(horizontal = 4.dp))
//            // version dot (centered)
//            Text(text = "•",
//                color = customTheme.onPrimaryColor,
//                fontSize = fontSize,
//                modifier = Modifier.constrainAs(versionDot) {
//                    top.linkTo(parent.top)
//                    start.linkTo(parent.start)
//                    end.linkTo(parent.end)
//                })
//            // version number
//            Text(text = "Build ${state.appVersionCode}",
//                color = customTheme.onPrimaryColor,
//                fontSize = fontSize,
//                modifier = Modifier
//                    .constrainAs(versionNumber) {
//                        top.linkTo(parent.top)
//                        start.linkTo(versionDot.end)
//                    }
//                    .padding(horizontal = 4.dp))
//        }

    }
}


@Composable
fun CustomButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String? = null,
    textColor: Color = customTheme.onPrimaryColor,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(
        backgroundColor = customTheme.primaryColor, contentColor = customTheme.onPrimaryColor
    ),
    buttonBorder: BorderStroke? = null,
    elevation: ButtonElevation? = null,
    content: @Composable () -> Unit? = {},
) {
    Button(
        onClick = onClick, modifier = modifier, colors = buttonColors, enabled = enabled, border = buttonBorder, elevation = elevation
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
            .padding(horizontal = 24.dp)
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
    interactionSource: MutableInteractionSource, onTap: () -> Unit, onLongPress: () -> Unit
): Modifier = composed {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    pointerInput(interactionSource) {
        val pressInteraction = PressInteraction.Press(Offset.Zero)
        val releaseInteraction = PressInteraction.Release(pressInteraction)
        detectTapGestures(onPress = {
            interactionSource.tryEmit(pressInteraction)
            // await the release
            tryAwaitRelease()
            interactionSource.tryEmit(releaseInteraction)
        }, onTap = {
            onTap()
        }, onLongPress = {
            interactionSource.tryEmit(pressInteraction)
            scope.launch {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            onLongPress()
        })
    }
}

@Composable
fun CircleButton(
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
    val indication = rememberRipple(bounded = false, radius = size / 2)

    Column(modifier = Modifier.padding(padding)) {
        Column(
            modifier = modifier
                .size(size)
                .background(backgroundColor, CircleShape)
                .tapOrReleaseClickable(
                    interactionSource = interactionSource, onTap = onTap, onLongPress = onLongPress
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
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    val animatedColor = animateColorAsState(
        targetValue = if (checked) customTheme.primaryColor else Color.LightGray,
        animationSpec = tween(
            durationMillis = 200, easing = LinearOutSlowInEasing
        )
    ).value

    val animatedStrokeWidth = animateDpAsState(
        targetValue = if (checked) 0.dp else 2.dp, animationSpec = tween(
            durationMillis = 200, easing = LinearOutSlowInEasing
        )
    ).value

    val animatedIconSize = animateDpAsState(
        targetValue = if (checked) 20.dp else 0.dp, animationSpec = tween(
            durationMillis = 200, easing = LinearOutSlowInEasing
        )
    ).value

    val animatedIconColor = animateColorAsState(
        targetValue = if (checked) customTheme.onPrimaryColor else Color.LightGray,
        animationSpec = tween(
            durationMillis = 200, easing = LinearOutSlowInEasing
        )
    ).value

    val animatedIcon = animateDpAsState(
        targetValue = if (checked) 0.dp else 4.dp, animationSpec = tween(
            durationMillis = 200, easing = LinearOutSlowInEasing
        )
    ).value

    val animatedIconPadding = animateDpAsState(
        targetValue = if (checked) 0.dp else 4.dp, animationSpec = tween(
            durationMillis = 200, easing = LinearOutSlowInEasing
        )
    ).value

    val animatedIconAlpha = animateFloatAsState(
        targetValue = if (checked) 1f else 0f, animationSpec = tween(
            durationMillis = 200, easing = LinearOutSlowInEasing
        )
    ).value
    Box(modifier) {
        // checkbox
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(animatedColor, CircleShape)
                .border(
                    width = animatedStrokeWidth, color = Color.LightGray, shape = CircleShape
                )
                .padding(animatedIconPadding)
                .clip(CircleShape)
                .clickable {
                    onCheckedChange(!checked)
                },
            contentAlignment = Alignment.Center,
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

/**
 * Calendar using kotlin datetime
 */
@Composable
fun DatePicker(hiveViewModel: HiveViewModel) {
    val selectedDate = hiveViewModel.state.dateSelection.selectedDate
    val month = selectedDate.month
    val year = selectedDate.year
    val dateSelectionMode = hiveViewModel.state.dateSelection.dateSelectionMode
    val dayOfMonth = selectedDate.dayOfMonth

    // Year and month
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomButton(onClick = { hiveViewModel.increaseDateSelectionScope() }) {
            Text(
                text = when (dateSelectionMode) {
                    DateSelectionMode.DAY_OF_MONTH -> {
                        "${month.name} $year"
                    }
                    DateSelectionMode.MONTH -> {
                        "$year"
                    }
                    DateSelectionMode.YEAR -> {
                        "${year - 10} - ${year + 10}"
                    }
                    DateSelectionMode.HOUR_AND_MINUTE -> {
                        "${month.name} $dayOfMonth, $year"
                    }
                    else -> {
                        ""
                    }
                }, fontWeight = FontWeight.Bold, fontSize = 20.sp
            )
        }
        Row {
            // previous month
            CircleButton(
                onTap = { hiveViewModel.decrementDatePicker() },
                icon = Icons.Default.ChevronLeft,
                iconColor = customTheme.primaryColor,
                backgroundColor = customTheme.surfaceColor,
                size = 40.dp,
                padding = 8.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            // next month
            CircleButton(
                onTap = { hiveViewModel.incrementDatePicker() },
                icon = Icons.Default.ChevronRight,
                iconColor = customTheme.primaryColor,
                backgroundColor = customTheme.surfaceColor,
                size = 40.dp,
                padding = 8.dp
            )
        }
    }

    when (dateSelectionMode) {
        DateSelectionMode.HOUR_AND_MINUTE -> {
            HourPicker(
                dateTimeNow = selectedDate,
                onHourSelected = { hiveViewModel.onHourSelected(it) },
                hiveViewModel = hiveViewModel,
            )
        }
        DateSelectionMode.DAY_OF_MONTH -> {
            DayPicker(
                dateTimeNow = selectedDate,
                hiveViewModel = hiveViewModel,
                disabledDays = hiveViewModel.state.dateSelection.disabledDays,
                onDaySelected = { hiveViewModel.onDaySelected(it) },
                highlightedDays = hiveViewModel.state.dateSelection.highlightedDays
            )
        }
        DateSelectionMode.MONTH -> {
            MonthPicker(dateTimeNow = selectedDate,
                hiveViewModel = hiveViewModel,
                onMonthSelected = { hiveViewModel.onMonthSelected(it) })
        }
        DateSelectionMode.YEAR -> {
            YearPicker(dateTimeNow = selectedDate,
                hiveViewModel = hiveViewModel,
                onYearSelected = { hiveViewModel.onYearSelected(it) })
        }
        else -> {}
    }
}

@Composable
fun YearPicker(
    dateTimeNow: LocalDateTime,
    hiveViewModel: HiveViewModel,
    onYearSelected: (LocalDateTime) -> Unit
) {
    // 4x4 grid of years (16 years)
    val previousDecadeStart = dateTimeNow.year - (dateTimeNow.year % 10)
    val endRange = previousDecadeStart + 15
    val selectedYear = dateTimeNow.year
    val years = (previousDecadeStart..endRange).toList()
    val yearRows = years.chunked(4)
    Column {
        yearRows.forEach { yearRow ->
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                yearRow.forEach { year ->
                    val isCurrentYear = year == dateTimeNow.year
                    val textColor =
                        if (isCurrentYear) customTheme.onPrimaryText else customTheme.onSurfaceText
                    val backgroundColor =
                        if (isCurrentYear) customTheme.primaryColor else Color.Transparent
                    CircleButton(
                        onTap = {
                            onYearSelected(
                                dateTimeNow.withYear(year)
                            )
                        },
                        icon = null,
                        iconColor = textColor,
                        backgroundColor = backgroundColor,
                        size = 64.dp,
                        padding = 8.dp,
                    ) {
                        Text(
                            text = year.toString(),
                            fontSize = 20.sp,
                            color = if (year == selectedYear) customTheme.onPrimaryColor else customTheme.onSurfaceColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonthPicker(
    dateTimeNow: LocalDateTime,
    hiveViewModel: HiveViewModel,
    onMonthSelected: (LocalDateTime) -> Unit
) {
    // 4x4 grid of months
    val selectedMonth = dateTimeNow.month
    val months = Month.values().toList() + Month.values().toList().take(4)
    val monthRows = months.chunked(4)
    Column {
        monthRows.forEachIndexed { index, monthRow ->
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                monthRow.forEach { month ->
                    val isCurrentMonth = month == dateTimeNow.month
                    val monthInNextYear =
                        monthRow == monthRows.last() && index == monthRows.lastIndex
                    val textColor =
                        if (!monthInNextYear && isCurrentMonth) customTheme.onPrimaryText
                        else if (monthInNextYear) customTheme.onSurfaceText.copy(alpha = 0.5f)
                        else customTheme.onSurfaceText
                    val backgroundColor =
                        if (!monthInNextYear && isCurrentMonth) customTheme.primaryColor
                        else Color.Transparent
                    CircleButton(
                        onTap = {
                            if (monthInNextYear) {
                                onMonthSelected(
                                    dateTimeNow.withMonth(month.value)
                                        .withYear(dateTimeNow.year + 1)
                                )
                            } else {
                                hiveViewModel.onMonthSelected(
                                    dateTimeNow.withMonth(month.value)
                                )
                            }
                        },
                        backgroundColor = backgroundColor,
                        size = 64.dp,
                        padding = 8.dp,
                    ) {
                        Text(
                            text = month.name.substring(0, 3).lowercase().replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(Locale.ROOT)
                                else it.toString()
                            }, fontSize = 20.sp, color = textColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HourPicker(
    dateTimeNow: LocalDateTime,
    onHourSelected: (LocalDateTime) -> Unit,
    calendarViewModel: CalendarViewModel = hiltViewModel(),
    hiveViewModel: HiveViewModel
) {
    val timeFormat = hiveViewModel.state.userPreferences.timeFormat
    val hours = when (timeFormat) {
        TimeFormat.TWENTY_FOUR_HOUR -> (0..23).toList()
        TimeFormat.TWELVE_HOUR -> (1..12).toList()
    }

    // if false, then AM, if true, then PM
    var isPostMeridian = dateTimeNow.hour >= 12

    val minutes = (0..59).toList()
    val height = 240.dp
    val width = 64.dp

    val selectedHour by calendarViewModel.selectedHour.collectAsState()
    val selectedMinute by calendarViewModel.selectedMinute.collectAsState()
    val isPressed by calendarViewModel.isPressed.collectAsState()

    val hourListState = rememberLazyListState(Int.MAX_VALUE / 2 + selectedHour, 0)

    val minuteListState = rememberLazyListState(Int.MAX_VALUE / 2 + selectedMinute, 0)

    // debug text to show the time
    Text(
        text = "${dateTimeNow.hour}:${dateTimeNow.minute}", modifier = Modifier.padding(16.dp)
    )

    LaunchedEffect(
        key1 = hourListState.isScrollInProgress
    ) {
        // return since we only want to run this effect when the scroll is not in progress
        if (hourListState.isScrollInProgress) return@LaunchedEffect
        // We add 2 as the offset to get the middle item in the list of 5
        val offset = 2
        // log
        val hour = ((hourListState.firstVisibleItemIndex + offset) % hours.size)
        when (timeFormat) {
            TimeFormat.TWENTY_FOUR_HOUR -> {
                calendarViewModel.setSelectedHour(hour)
            }
            TimeFormat.TWELVE_HOUR -> {
                calendarViewModel.setSelectedHour(hour + 1)
            }
        }
        onHourSelected(
            dateTimeNow.withHour(selectedHour).withSecond(0).withNano(0)
        )
    }

    LaunchedEffect(key1 = minuteListState.isScrollInProgress) {
        // return since we only want to run this effect when the scroll is not in progress
        if (minuteListState.isScrollInProgress) return@LaunchedEffect
        // We add 2 as the offset to get the middle item in the list of 5
        val offset = 2
        // log
        val minute = ((minuteListState.firstVisibleItemIndex + offset) % minutes.size)
        calendarViewModel.setSelectedMinute(minute)
        onHourSelected(
            dateTimeNow.withMinute(selectedMinute).withSecond(0).withNano(0)
        )
    }

    val textStyle = TextStyle(
        color = customTheme.onSurfaceColor,
        fontSize = 36.sp,
    )

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        ScrollColumn(
            width = width,
            height = height,
            lazyListState = hourListState,
            items = hours.map { it.toString() },
            selectedItem = selectedHour.toString(),
            textStyle = textStyle,
            horizontalAlignment = Alignment.End,
            itemCount = Int.MAX_VALUE
        )
        Column(
            modifier = Modifier
                .height(height)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = ":",
                style = textStyle,
            )
        }
        ScrollColumn(
            width = width,
            height = height,
            lazyListState = minuteListState,
            items = minutes.map {
                // pad with 0 if less than 10
                if (it < 10) "0$it" else it.toString()
            },
            selectedItem = if (selectedMinute < 10) "0$selectedMinute"
            else selectedMinute.toString(),
            textStyle = textStyle,
            horizontalAlignment = Alignment.Start,
            itemCount = Int.MAX_VALUE
        )

        if (timeFormat == TimeFormat.TWELVE_HOUR) {
            Column(
                modifier = Modifier
                    .height(height)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomButton(onClick = { isPostMeridian = !isPostMeridian }) {
                    Text(
                        text = if (isPostMeridian) "PM" else "AM",
                        style = textStyle.copy(color = customTheme.onPrimaryColor),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun ScrollColumn(
    width: Dp,
    height: Dp,
    lazyListState: LazyListState,
    items: List<String>,
    itemCount: Int,
    selectedItem: String,
    textStyle: TextStyle = TextStyle.Default,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.SpaceEvenly,
) {
    LazyColumn(
        modifier = Modifier
            .width(width)
            .height(height),
        flingBehavior = rememberSnapperFlingBehavior(lazyListState = lazyListState),
        state = lazyListState,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment
    ) {
        items(itemCount) {
            val item = items[it % items.size]
            val fontColor = when (item) {
                selectedItem -> customTheme.onBackgroundText
                else -> customTheme.onBackgroundText.copy(alpha = 0.5f)
            }
            Text(item, style = textStyle.copy(color = fontColor))
        }
    }
}

@Composable
fun DayPicker(
    dateTimeNow: LocalDateTime,
    hiveViewModel: HiveViewModel,
    disabledDays: List<LocalDateTime>,
    onDaySelected: (LocalDateTime) -> Unit,
    highlightedDays: List<LocalDateTime> = emptyList(),
) {
    val month = dateTimeNow.month

    val daysOfCalendar = hiveViewModel.getDaysOfCalendar(dateTimeNow)

    val daysOfWeekNames by remember {
        mutableStateOf(
            listOf(
                "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"
            )
        )
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(daysOfWeekNames) {
            // 0.dp padding to remove the default padding of LazyVerticalGrid items
            Column(Modifier.padding(0.dp)) {
                Column(
                    modifier = Modifier.size(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = it,
                        color = customTheme.onBackgroundText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(items = daysOfCalendar) { day ->
            val dayOfMonth = day.dayOfMonth
            val isCurrentMonth = day.month == month
            val isToday = day == dateTimeNow
            val isHighlighted = day in highlightedDays
            val isDisabled = day in disabledDays
            val textColor =
                if (isHighlighted) customTheme.primaryColor else if (isCurrentMonth) customTheme.onPrimaryText else customTheme.onPrimaryText.copy(
                    alpha = 0.4F
                )
            val backgroundColor =
                if (isToday) customTheme.primaryColor else if (isHighlighted) customTheme.primaryColor.copy(
                    alpha = 0.2F
                ) else Color.Transparent

            val dayText = dayOfMonth.toString()

            CircleButton(
                backgroundColor = backgroundColor,
                size = 40.dp,
                onTap = {
                    if (!isDisabled) {
                        onDaySelected(day)
                    }
                },
            ) {
                Text(
                    text = dayText, color = textColor, fontSize = 16.sp
                )
            }
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
            "Select ${checkboxSelectionValues.maxSelectionCount} " + if (checkboxSelectionValues.maxSelectionCount == 1) "option"
            else {
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