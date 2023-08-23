package com.reedsloan.beekeepingapp.presentation.hive_info

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.reedsloan.beekeepingapp.data.local.hive.EquipmentCondition
import com.reedsloan.beekeepingapp.data.local.hive.FoundationType
import com.reedsloan.beekeepingapp.data.local.hive.FramesAndCombs
import com.reedsloan.beekeepingapp.data.local.hive.HiveConditions
import com.reedsloan.beekeepingapp.data.local.hive.HiveDataEntry
import com.reedsloan.beekeepingapp.data.local.hive.HiveFeeding
import com.reedsloan.beekeepingapp.data.local.hive.HiveHealth
import com.reedsloan.beekeepingapp.data.local.hive.Odor
import com.reedsloan.beekeepingapp.presentation.common.Container
import com.reedsloan.beekeepingapp.presentation.common.DataEntryChip
import com.reedsloan.beekeepingapp.presentation.viewmodel.hives.HiveViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.util.*

@Composable
fun LogDataScreen(navController: NavController, hiveViewModel: HiveViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(1) {
            val state by hiveViewModel.state.collectAsState()
            val hives by hiveViewModel.hives.collectAsState()
            var currentHiveDataEntries = state.selectedHive?.hiveDataEntries ?: emptyList()

            LaunchedEffect(key1 = state) {
                currentHiveDataEntries = state.selectedHive?.hiveDataEntries ?: emptyList()
            }

            // Initializing a Calendar
            val calendar = Calendar.getInstance()

            // Fetching current year, month and day
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            calendar.time = Date()

            // Declaring a string value to
            // store date in string format
            val date = remember { mutableStateOf("") }
            val context = LocalContext.current
            // Declaring DatePickerDialog and setting
            // initial values as current values (present year, month and day)
            val datePickerDialog = DatePickerDialog(
                context,
                { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                    date.value = "$mDayOfMonth/${mMonth + 1}/$mYear"
                }, year, month, day
            )
            val selectedDate = remember { mutableStateOf(LocalDate.now()) }

            // Hive Name
            state.selectedHive?.hiveInfo?.let { hiveInfo ->
                Text(
                    text = hiveInfo.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Text(
                text = "Select a date to update or enter data.",
                modifier = Modifier.padding(16.dp)
            )


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

            HorizontalCalendar(
                state = calendarState,
                dayContent = { calendarDay ->

                    Log.d("LogDataScreen", "${state.selectedDataEntry?.date} == ${calendarDay.date.toString()}")
                    val hasDataEntry =
                        currentHiveDataEntries.any { it.date == calendarDay.date.toString() }

                    val isSelected = state.selectedDataEntry?.date == calendarDay.date.toString()

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

            // Hive Data Entry
            HiveDataEntryScreen(
                navController = navController,
                hiveViewModel = hiveViewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalStdlibApi::class)
@Composable
fun HiveDataEntryScreen(
    navController: NavController,
    hiveViewModel: HiveViewModel
) {
    Container {

        Column(
            Modifier
                .fillMaxSize()
                .testTag("HiveInfoScreen")
        ) {

            val state by hiveViewModel.state.collectAsState()

            state.selectedDataEntry?.let { entry ->
                // Hive Name
                state.selectedHive?.hiveInfo?.let { hiveInfo ->
                    Text(
                        text = hiveInfo.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                // Hive Data Entry
                Text(
                    text = entry.date,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )

                // Hive Conditions
                Text(
                    text = "Hive Conditions",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }

}

@Composable
fun Day(
    day: CalendarDay,
    hasDataEntry: Boolean,
    isSelected: Boolean,
    onClick: (CalendarDay) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .padding(2.dp)
            .aspectRatio(1f) // this is necessary for the buttons evenly to fit in the layout
        , contentAlignment = Alignment.Center
    ) {

        // Show a primary color normal button if highlighted else show a surface color elevated button
        if (isSelected) {
            // highlighted days are normal buttons
            OutlinedButton(
                onClick = { onClick(day) },
                modifier = Modifier.size(48.dp),
                contentPadding = PaddingValues(0.dp), // this is necessary for the text to fit
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else if (hasDataEntry) {
            // non-highlighted days are elevated buttons
            FilledTonalButton(
                onClick = { onClick(day) },
                modifier = Modifier.size(48.dp),
                contentPadding = PaddingValues(0.dp) // this is necessary for the text to fit
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            // non-highlighted days are elevated buttons
            ElevatedButton(
                onClick = { onClick(day) },
                modifier = Modifier.size(48.dp),
                elevation = ButtonDefaults.buttonElevation(2.dp, 2.dp, 2.dp, 2.dp),
                contentPadding = PaddingValues(0.dp) // this is necessary for the text to fit
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun Month(month: CalendarMonth) {
    Text(
        text = "${month.yearMonth.month.name} ${month.yearMonth.year}",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        textAlign = TextAlign.Center
    )
}