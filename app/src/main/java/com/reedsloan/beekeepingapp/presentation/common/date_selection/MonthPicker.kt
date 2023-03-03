package com.reedsloan.beekeepingapp.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reedsloan.beekeepingapp.presentation.ui.custom_theme.customTheme
import java.time.LocalDateTime
import java.time.Month
import java.util.*

@Composable
fun MonthPicker(
    dateTimeNow: LocalDateTime,
    hiveViewModel: HiveViewModel,
    onMonthSelected: (LocalDateTime) -> Unit
) {
    // 4x4 grid of months
    val selectedMonth = dateTimeNow.month
    val months = Month.values().toList() + Month.values().toList().take(4)
    val monthRows = months.chunked(4)
    Column {
        monthRows.forEachIndexed { index, monthRow ->
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                monthRow.forEach { month ->
                    val isCurrentMonth = month == dateTimeNow.month
                    val monthInNextYear =
                        monthRow == monthRows.last() && index == monthRows.lastIndex
                    val textColor =
                        if (!monthInNextYear && isCurrentMonth) customTheme.onPrimaryText
                        else if (monthInNextYear) customTheme.onSurfaceText.copy(alpha = 0.5f)
                        else customTheme.onSurfaceText
                    val backgroundColor =
                        if (!monthInNextYear && isCurrentMonth) customTheme.primaryColor
                        else Color.Transparent
                    CircleButton(
                        onTap = {
                            if (monthInNextYear) {
                                onMonthSelected(
                                    dateTimeNow.withMonth(month.value)
                                        .withYear(dateTimeNow.year + 1)
                                )
                            } else {
                                hiveViewModel.onMonthSelected(
                                    dateTimeNow.withMonth(month.value)
                                )
                            }
                        },
                        backgroundColor = backgroundColor,
                        size = 64.dp,
                        padding = 8.dp,
                    ) {
                        Text(
                            text = month.name.substring(0, 3).lowercase().replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(Locale.ROOT)
                                else it.toString()
                            }, fontSize = 20.sp, color = textColor
                        )
                    }
                }
            }
        }
    }
}