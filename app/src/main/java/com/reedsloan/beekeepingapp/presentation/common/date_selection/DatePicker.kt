package com.reedsloan.beekeepingapp.presentation.common.date_selection

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reedsloan.beekeepingapp.presentation.common.*
import com.reedsloan.beekeepingapp.presentation.common.extensions.surfaceStyle
import com.reedsloan.beekeepingapp.presentation.ui.custom_theme.customTheme
import com.reedsloan.beekeepingapp.presentation.ui.custom_theme.gradient

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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .surfaceStyle()
    ) {
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
}