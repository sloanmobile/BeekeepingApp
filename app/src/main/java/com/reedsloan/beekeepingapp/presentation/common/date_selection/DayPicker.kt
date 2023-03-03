package com.reedsloan.beekeepingapp.presentation.common.date_selection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reedsloan.beekeepingapp.presentation.common.CircleButton
import com.reedsloan.beekeepingapp.presentation.common.HiveViewModel
import com.reedsloan.beekeepingapp.presentation.ui.custom_theme.customTheme
import java.time.LocalDateTime

@Composable
fun DayPicker(
    dateTimeNow: LocalDateTime,
    hiveViewModel: HiveViewModel,
    disabledDays: List<LocalDateTime>,
    onDaySelected: (LocalDateTime) -> Unit,
    highlightedDays: List<LocalDateTime> = emptyList(),
) {
    val month = dateTimeNow.month

    val daysOfCalendar = hiveViewModel.getDaysOfCalendar(dateTimeNow)

    val daysOfWeekNames by remember {
        mutableStateOf(
            listOf(
                "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"
            )
        )
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(daysOfWeekNames) {
            // 0.dp padding to remove the default padding of LazyVerticalGrid items
            Column(Modifier.padding(0.dp)) {
                Column(
                    modifier = Modifier.size(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = it,
                        color = customTheme.onBackgroundText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(items = daysOfCalendar) { day ->
            val dayOfMonth = day.dayOfMonth
            val isCurrentMonth = day.month == month
            val isToday = day == dateTimeNow
            val isHighlighted = day in highlightedDays
            val isDisabled = day in disabledDays
            val textColor =
                if (isHighlighted) customTheme.primaryColor else if (isCurrentMonth) customTheme.onPrimaryText else customTheme.onPrimaryText.copy(
                    alpha = 0.4F
                )
            val backgroundColor =
                if (isToday) customTheme.primaryColor else if (isHighlighted) customTheme.primaryColor.copy(
                    alpha = 0.2F
                ) else Color.Transparent

            val dayText = dayOfMonth.toString()

            CircleButton(
                backgroundColor = backgroundColor,
                size = 40.dp,
                onTap = {
                    if (!isDisabled) {
                        onDaySelected(day)
                    }
                },
            ) {
                Text(
                    text = dayText, color = textColor, fontSize = 16.sp
                )
            }
        }
    }
}