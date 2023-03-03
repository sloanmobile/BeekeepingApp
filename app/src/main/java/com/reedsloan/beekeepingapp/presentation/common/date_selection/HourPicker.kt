package com.reedsloan.beekeepingapp.presentation.common.date_selection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.reedsloan.beekeepingapp.data.TimeFormat
import com.reedsloan.beekeepingapp.presentation.common.CalendarViewModel
import com.reedsloan.beekeepingapp.presentation.common.CustomButton
import com.reedsloan.beekeepingapp.presentation.common.HiveViewModel
import com.reedsloan.beekeepingapp.presentation.common.ScrollColumn
import com.reedsloan.beekeepingapp.presentation.ui.custom_theme.customTheme
import java.time.LocalDateTime

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