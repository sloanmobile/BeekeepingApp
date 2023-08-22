package com.reedsloan.beekeepingapp.presentation.home_screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Hive
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.reedsloan.beekeepingapp.data.local.hive.HiveConditions
import com.reedsloan.beekeepingapp.data.local.hive.HiveDataEntry
import com.reedsloan.beekeepingapp.data.local.hive.HiveFeeding
import com.reedsloan.beekeepingapp.data.local.hive.HiveHealth
import com.reedsloan.beekeepingapp.presentation.common.DataEntryChip
import com.reedsloan.beekeepingapp.presentation.hive_info.Day
import com.reedsloan.beekeepingapp.presentation.hive_info.Month
import com.reedsloan.beekeepingapp.presentation.viewmodel.hives.HiveViewModel
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun QuickLogScreen(navController: NavController, hiveViewModel: HiveViewModel) {

    val state by hiveViewModel.state.collectAsState()
    val hives by hiveViewModel.hives.collectAsState()
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
    val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
    val firstDayOfWeek =
        remember { firstDayOfWeekFromLocale() } // Available from the library

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // Aligns content horizontally to the center
        verticalArrangement = Arrangement.Top, // Arranges content vertically from the top
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Record Hive Data",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        DataEntryChip(
            stringValues = hives.map { it.hiveInfo.name },
            selectedValue = state.selectedHive?.hiveInfo?.name,
            onChipSelected = { selectedHiveName ->
                val selectedHive = hives.find { it.hiveInfo.name == selectedHiveName }
                if (selectedHive != null) {
                    hiveViewModel.setSelectedHive(selectedHive.id)
                }
            }
        )
        Spacer(modifier = Modifier.width(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Date: ",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = selectedDate.toString(),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(16.dp))
            ElevatedButton(onClick = {
                showDatePicker = !showDatePicker
            }, contentPadding = PaddingValues(16.dp), shape = MaterialTheme.shapes.medium) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null)
            }
        }

        if (showDatePicker) {
            HorizontalCalendar(
                state = calendarState,
                dayContent = { calendarDay ->
                    val currentHiveDataEntries = state.selectedHive?.hiveDataEntries ?: emptyList()

                    val hasDataEntry =
                        currentHiveDataEntries.any { it.date == calendarDay.date.toString() }

                    val isSelected = calendarDay.date.toString() == selectedDate.toString()

                    Day(
                        day = calendarDay,
                        isSelected = isSelected,
                        hasDataEntry = hasDataEntry,
                        onClick = { day ->
                            // update the selected data entry
                            currentHiveDataEntries.firstOrNull { it.date == day.date.toString() }
                                ?.let {
                                    hiveViewModel.setSelectedDataEntry(it)
                                    return@Day
                                }

                            val newHiveDataEntry = HiveDataEntry(
                                hiveId = state.selectedHive?.id ?: return@Day,
                                date = day.date.toString(),
                                hiveConditions = HiveConditions(),
                                hiveHealth = HiveHealth(),
                                feeding = HiveFeeding(),
                                localPhotoUris = emptyList()
                            )
                            hiveViewModel.setSelectedDataEntry(newHiveDataEntry)
                            selectedDate = day.date
                        })

                },
                monthHeader = { Month(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                        shape = MaterialTheme.shapes.medium
                    ),
                contentPadding = PaddingValues(4.dp),
            )
        }
    }

    Box(Modifier.fillMaxSize()) {
        ExtendedFloatingActionButton(
            onClick = {

            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Save Entry")
        }
    }
}