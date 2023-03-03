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

@Composable
fun YearPicker(
    dateTimeNow: LocalDateTime,
    hiveViewModel: HiveViewModel,
    onYearSelected: (LocalDateTime) -> Unit
) {
    // 4x4 grid of years (16 years)
    val previousDecadeStart = dateTimeNow.year - (dateTimeNow.year % 10)
    val endRange = previousDecadeStart + 15
    val selectedYear = dateTimeNow.year
    val years = (previousDecadeStart..endRange).toList()
    val yearRows = years.chunked(4)
    Column {
        yearRows.forEach { yearRow ->
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                yearRow.forEach { year ->
                    val isCurrentYear = year == dateTimeNow.year
                    val textColor =
                        if (isCurrentYear) customTheme.onPrimaryText else customTheme.onSurfaceText
                    val backgroundColor =
                        if (isCurrentYear) customTheme.primaryColor else Color.Transparent
                    CircleButton(
                        onTap = {
                            onYearSelected(
                                dateTimeNow.withYear(year)
                            )
                        },
                        icon = null,
                        iconColor = textColor,
                        backgroundColor = backgroundColor,
                        size = 64.dp,
                        padding = 8.dp,
                    ) {
                        Text(
                            text = year.toString(),
                            fontSize = 20.sp,
                            color = if (year == selectedYear) customTheme.onPrimaryColor else customTheme.onSurfaceColor
                        )
                    }
                }
            }
        }
    }
}