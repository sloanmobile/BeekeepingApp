package com.reedsloan.beekeepingapp.presentation.hive_info

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.reedsloan.beekeepingapp.data.local.hive.HiveConditions
import com.reedsloan.beekeepingapp.data.local.hive.HiveDataEntry
import com.reedsloan.beekeepingapp.data.local.hive.HiveFeeding
import com.reedsloan.beekeepingapp.data.local.hive.HiveHealth
import com.reedsloan.beekeepingapp.presentation.viewmodel.hives.HiveViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.util.*

@Composable
fun LogDataScreen(navController: NavController, hiveViewModel: HiveViewModel) {
    Column(
        Modifier
            .fillMaxSize()
            .testTag("HiveInfoScreen")
    ) {

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
        var selectedDataEntry by remember { mutableStateOf<HiveDataEntry?>(null) }

        // Hive Name
        state.selectedHive?.hiveInfo?.let { hiveInfo ->
            Text(
                text = hiveInfo.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }
        Text(text = "Select a date to update or enter data.", modifier = Modifier.padding(16.dp))


        val currentMonth = remember { YearMonth.now() }
        val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
        val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
        val firstDayOfWeek = remember { firstDayOfWeekFromLocale() } // Available from the library

        val calendarState = rememberCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            firstVisibleMonth = currentMonth,
            firstDayOfWeek = firstDayOfWeek
        )

        HorizontalCalendar(
            state = calendarState,
            dayContent = { calendarDay ->
                val isHighlighted = currentHiveDataEntries.any {
                    it.date == calendarDay.date.toString()
                }

                Day(calendarDay, isHighlighted, onClick = { day ->
                    // update the selected data entry
                    currentHiveDataEntries.firstOrNull { it.date == day.date.toString() }?.let {
                        selectedDataEntry = it
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
                    hiveViewModel.addHiveDataEntry(newHiveDataEntry)
                    selectedDataEntry = newHiveDataEntry
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
        selectedDataEntry?.let { hiveDataEntry ->
            HiveDataEntryScreen(
                navController = navController,
                hiveViewModel = hiveViewModel,
                hiveDataEntry = hiveDataEntry
            )
        }
    }
}

@Composable
fun HiveDataEntryScreen(
    navController: NavController,
    hiveViewModel: HiveViewModel,
    hiveDataEntry: HiveDataEntry
) {
    Column(
        Modifier
            .fillMaxSize()
            .testTag("HiveInfoScreen")
    ) {

        val state by hiveViewModel.state.collectAsState()

        // Hive Name
        state.selectedHive?.hiveInfo?.let { hiveInfo ->
            Text(
                text = hiveInfo.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Hive Data Entry
        Text(
            text = hiveDataEntry.date,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun Day(day: CalendarDay, isHighlighted: Boolean, onClick: (CalendarDay) -> Unit = {}) {
    Box(
        modifier = Modifier
            .padding(2.dp)
            .aspectRatio(1f) // this is necessary for the buttons evenly to fit in the layout
        , contentAlignment = Alignment.Center
    ) {

        // Show a primary color normal button if highlighted else show a surface color elevated button
        if (isHighlighted) {
            // highlighted days are normal buttons
            Button(
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