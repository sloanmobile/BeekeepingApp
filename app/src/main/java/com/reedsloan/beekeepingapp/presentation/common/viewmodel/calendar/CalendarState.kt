package com.reedsloan.beekeepingapp.presentation.common.viewmodel.calendar

import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.DateSelection

data class CalendarState(
    val dateSelection: DateSelection = DateSelection(),
    val userPreferences: UserPreferences = UserPreferences(),
)