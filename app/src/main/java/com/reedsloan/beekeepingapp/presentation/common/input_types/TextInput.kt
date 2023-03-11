package com.reedsloan.beekeepingapp.presentation.common.input_types

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reedsloan.beekeepingapp.presentation.common.CustomButton
import com.reedsloan.beekeepingapp.presentation.common.HiveViewModel
import com.reedsloan.beekeepingapp.presentation.ui.custom_theme.customTheme
import java.util.*

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
    LaunchedEffect(key1 = hiveViewModel.state.selectedHive?.id) {
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