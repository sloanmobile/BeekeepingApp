package com.reedsloan.beekeepingapp.presentation.common.input_types

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reedsloan.beekeepingapp.data.local.CheckboxSelectionValues
import com.reedsloan.beekeepingapp.presentation.common.CustomButton
import com.reedsloan.beekeepingapp.presentation.common.HiveViewModel
import com.reedsloan.beekeepingapp.presentation.ui.custom_theme.customTheme
import java.util.*

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
                                Toast.makeText(
                                    context, "This option is disabled", Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else if (selectedOptions.size == checkboxSelectionValues.maxSelectionCount) {
                                // if the option is not disabled, but the max selection count has been
                                // reached, show a toast
                                Toast.makeText(
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