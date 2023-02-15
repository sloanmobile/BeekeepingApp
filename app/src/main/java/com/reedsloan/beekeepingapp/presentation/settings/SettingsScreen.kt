package com.reedsloan.beekeepingapp.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.data.TimeFormat
import com.reedsloan.beekeepingapp.data.local.TemperatureMeasurement
import com.reedsloan.beekeepingapp.presentation.common.Container
import com.reedsloan.beekeepingapp.presentation.common.HiveViewModel
import com.reedsloan.beekeepingapp.presentation.common.NavigationBar
import com.reedsloan.beekeepingapp.presentation.common.SelectionDropdownMenu
import com.reedsloan.beekeepingapp.ui.custom_theme.customTheme

@Composable
fun SettingsScreen(
    navController: NavController,
    hiveViewModel: HiveViewModel
) {
    val state = hiveViewModel.state
    val maxWidth = with(LocalDensity.current) {
        LocalContext.current.resources.displayMetrics.widthPixels.toDp() - 16.dp * 2
    }

    Column {
        NavigationBar(navController = navController, hiveViewModel)
        Text("This is the settings screen")

        Container {
            SelectionDropdownMenu(
                title = "Temperature",
                options = TemperatureMeasurement.values().map { it.displayValue },
                selectedOption = state.userPreferences.temperatureMeasurement.displayValue,
                onOptionSelected = { hiveViewModel.setTemperatureMeasurement(it.first()) },
                modifier = Modifier
                    .background(customTheme.surfaceColor, RoundedCornerShape(8.dp))
                    .border(
                        2.dp, customTheme.onSurfaceColor, RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                dropdownWidth = maxWidth,
                hiveViewModel = hiveViewModel
            )
            // Time format setting
            SelectionDropdownMenu(
                title = "Time Format",
                options = TimeFormat.values().map { it.displayValue },
                selectedOption = state.userPreferences.timeFormat.displayValue,
                onOptionSelected = { hiveViewModel.setTimeFormat(it.first()) },
                modifier = Modifier
                    .background(customTheme.surfaceColor, RoundedCornerShape(8.dp))
                    .border(
                        2.dp, customTheme.onSurfaceColor, RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp),
                dropdownWidth = maxWidth,
                hiveViewModel = hiveViewModel
            )
        }
    }
}