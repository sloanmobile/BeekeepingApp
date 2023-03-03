package com.reedsloan.beekeepingapp.presentation.common.input_types

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reedsloan.beekeepingapp.presentation.common.HiveViewModel
import com.reedsloan.beekeepingapp.presentation.ui.custom_theme.customTheme

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