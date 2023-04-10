package com.reedsloan.beekeepingapp.presentation.hive_info

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.reedsloan.beekeepingapp.presentation.viewmodel.hives.HiveViewModel
import java.time.YearMonth
import java.util.*

@Composable
fun LogDataScreen(navController: NavController, hiveViewModel: HiveViewModel) {
    Column(
        Modifier
            .fillMaxSize()
            .testTag("HiveInfoScreen")
    ) {

        val state = hiveViewModel.state

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
                date.value = "$mDayOfMonth/${mMonth+1}/$mYear"
            }, year, month, day
        )


        // Hive Name
        state.selectedHive?.hiveInfo?.let { hiveInfo ->
            Text(
                text = hiveInfo.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            Row {
                Button(onClick = {
                    datePickerDialog.show()
                }) {
                    Text(text = "Select Date")
                }
            }
        }

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
            dayContent = { Day(it) }
        )
    }
}

@Composable
fun Day(day: CalendarDay) {
    Box(
        modifier = Modifier
            .aspectRatio(1f), // This is important for square sizing!
        contentAlignment = Alignment.Center
    ) {
        Text(text = day.date.dayOfMonth.toString())
    }
}