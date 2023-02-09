package com.reedsloan.beekeepingapp.data.local

import com.reedsloan.beekeepingapp.presentation.common.DateSelectionMode
import java.time.LocalDateTime

data class DateSelection(
    val dateSelectionMode: DateSelectionMode = DateSelectionMode.DAY_OF_MONTH,
    val selectedDate : LocalDateTime = LocalDateTime.now(),
    val disabledDays : List<LocalDateTime> = emptyList(),
    val highlightedDays : List<LocalDateTime> = emptyList(),
)
