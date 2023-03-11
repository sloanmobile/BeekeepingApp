package com.reedsloan.beekeepingapp.presentation.common.viewmodel.calendar

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reedsloan.beekeepingapp.data.TimeFormat
import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.domain.repo.HiveRepository
import com.reedsloan.beekeepingapp.presentation.common.date_selection.DateSelectionMode
import com.reedsloan.beekeepingapp.presentation.common.viewmodel.hives.HiveViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.Month
import java.time.Year
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val app: Application,
    private val hiveRepository: HiveRepository
) : ViewModel() {
    private val _selectedHour = MutableStateFlow(0)
    val selectedHour: StateFlow<Int> = _selectedHour

    private val _selectedMinute = MutableStateFlow(0)
    val selectedMinute: StateFlow<Int> = _selectedMinute

    var state by mutableStateOf(CalendarState())


    init {
        // update values with current time
        val dateTime: LocalDateTime = LocalDateTime.now()
        _selectedHour.update { dateTime.hour }
        _selectedMinute.update { dateTime.minute }
        getTimeFormat()
    }

    /**
     * Sets the selected hour.
     * @param hour The hour to set.
     */
    fun setSelectedHour(hour: Int) {
        _selectedHour.update { hour }
    }

    fun setSelectedMinute(minute: Int) {
        _selectedMinute.update { minute }
    }

    fun incrementDatePicker() {
        when (state.dateSelection.dateSelectionMode) {
            DateSelectionMode.DAY_OF_MONTH -> {
                // month
                val nextMonth = state.dateSelection.selectedDate.plusMonths(1)
                setSelectedDate(nextMonth)
            }
            DateSelectionMode.YEAR -> {
                // nothing
            }
            DateSelectionMode.MONTH -> {
                // year
                val nextYear = state.dateSelection.selectedDate.plusYears(1)
                setSelectedDate(nextYear)
            }
            DateSelectionMode.HOUR_AND_MINUTE -> {
                // hour
                val nextHour = state.dateSelection.selectedDate.plusHours(1)
                setSelectedDate(nextHour)
            }
        }
    }

    fun increaseDateSelectionScope() {
        when (state.dateSelection.dateSelectionMode) {
            DateSelectionMode.DAY_OF_MONTH -> {
                // increase to month
                state = state.copy(
                    dateSelection =
                    state.dateSelection.copy(dateSelectionMode = DateSelectionMode.MONTH)
                )
            }
            DateSelectionMode.YEAR -> {
                // nothing it is the highest allowed
            }
            DateSelectionMode.MONTH -> {
                // increase to year
                state = state.copy(
                    dateSelection =
                    state.dateSelection.copy(dateSelectionMode = DateSelectionMode.YEAR)
                )
            }
            DateSelectionMode.HOUR_AND_MINUTE -> {
                // increase to day
                state = state.copy(
                    dateSelection =
                    state.dateSelection.copy(dateSelectionMode = DateSelectionMode.DAY_OF_MONTH)
                )
            }
        }
    }

    private fun decreaseDateSelectionScope() {
        when (state.dateSelection.dateSelectionMode) {
            DateSelectionMode.DAY_OF_MONTH -> {
                // decrease to hour
                state = state.copy(
                    dateSelection =
                    state.dateSelection.copy(dateSelectionMode = DateSelectionMode.HOUR_AND_MINUTE)
                )
            }
            DateSelectionMode.YEAR -> {
                // decrease to month
                state = state.copy(
                    dateSelection =
                    state.dateSelection.copy(dateSelectionMode = DateSelectionMode.MONTH)
                )
            }
            DateSelectionMode.MONTH -> {
                // decrease to day
                state = state.copy(
                    dateSelection =
                    state.dateSelection.copy(dateSelectionMode = DateSelectionMode.DAY_OF_MONTH)
                )
            }
            DateSelectionMode.HOUR_AND_MINUTE -> {
                // nothing it is the lowest allowed
            }
        }
    }

    fun onHourSelected(localDate: LocalDateTime) {
        setSelectedDate(localDate)
        decreaseDateSelectionScope()
    }

    fun onDaySelected(date: LocalDateTime) {
        setSelectedDate(date)
        decreaseDateSelectionScope()
    }

    fun getHourMinuteString(date: LocalDateTime): String {
        // adjust to user preferences of 24 hour or 12 hour
        return when (state.userPreferences.timeFormat) {
            TimeFormat.TWENTY_FOUR_HOUR -> {
                date.format(DateTimeFormatter.ofPattern("HH:mm"))
            }
            TimeFormat.TWELVE_HOUR -> {
                date.format(DateTimeFormatter.ofPattern("hh:mm a"))
            }
        }
    }

    fun getHourString(date: LocalDateTime): String {
        return when (state.userPreferences.timeFormat) {
            TimeFormat.TWENTY_FOUR_HOUR -> {
                date.format(DateTimeFormatter.ofPattern("HH"))
            }
            TimeFormat.TWELVE_HOUR -> {
                date.format(DateTimeFormatter.ofPattern("h"))
            }
        }
    }

    fun decrementDatePicker() {
        when (state.dateSelection.dateSelectionMode) {
            DateSelectionMode.DAY_OF_MONTH -> {
                // month
                val nextMonth = state.dateSelection.selectedDate.minusMonths(1)
                setSelectedDate(nextMonth)
            }
            DateSelectionMode.YEAR -> {
                // nothing
            }
            DateSelectionMode.MONTH -> {
                // year
                val nextYear = state.dateSelection.selectedDate.minusYears(1)
                setSelectedDate(nextYear)
            }
            DateSelectionMode.HOUR_AND_MINUTE -> {
                // hour
                val nextHour = state.dateSelection.selectedDate.minusHours(1)
                setSelectedDate(nextHour)
            }
        }
    }

    private fun setSelectedDate(date: LocalDateTime) {
        state = state.copy(dateSelection = state.dateSelection.copy(selectedDate = date))
    }

    fun getDaysOfCalendar(dateTimeNow: LocalDateTime): List<LocalDateTime> {
        val year = dateTimeNow.year

        val month = dateTimeNow.month
        val isLeapYear = Year.isLeap(year.toLong())

        val daysInMonth =
            when (month) {
                Month.FEBRUARY -> if (isLeapYear) 29 else 28
                Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
                else -> 31
            }
        val firstDayOfMonth = dateTimeNow.withDayOfMonth(1)

        val days: MutableList<LocalDateTime> = mutableListOf()

        val daysFromSunday = when (firstDayOfMonth.dayOfWeek) {
            DayOfWeek.SUNDAY -> 0
            DayOfWeek.MONDAY -> 1
            DayOfWeek.TUESDAY -> 2
            DayOfWeek.WEDNESDAY -> 3
            DayOfWeek.THURSDAY -> 4
            DayOfWeek.FRIDAY -> 5
            DayOfWeek.SATURDAY -> 6
            else -> 0
        }

        val firstDayInNextMonth = firstDayOfMonth.plusMonths(1).withDayOfMonth(1)

        val daysInPreviousMonth = when (dateTimeNow.minusMonths(1).withDayOfMonth(1).month) {
            Month.FEBRUARY -> if (isLeapYear) 29 else 28
            Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
            else -> 31
        }
        val finalDayInPreviousMonth = dateTimeNow.minusMonths(1).withDayOfMonth(daysInPreviousMonth)


        // add days from previous month (or skip if first day of month is Sunday)
        for (i in daysFromSunday downTo 1) {
            days.add(finalDayInPreviousMonth.minusDays(i - 1.toLong()))
        }

        // add days from current month
        for (i in 0 until daysInMonth) {
            days.add(firstDayOfMonth.plusDays(i.toLong()))
        }

        // add days from next month (or skip if last day of month is Saturday)
        for (i in 0 until 42 - days.size) {
            days.add(firstDayInNextMonth.plusDays((i).toLong()))
        }


        return days.toList()
    }


    fun onYearSelected(localDate: LocalDateTime) {
        setSelectedDate(localDate)
        // decrement date picker
        decreaseDateSelectionScope()
    }

    fun onMonthSelected(localDate: LocalDateTime) {
        setSelectedDate(localDate)
        // decrement date picker
        decreaseDateSelectionScope()
    }

    private fun getTimeFormat() {
        // update state from hiveRepository
        viewModelScope.launch {
            runCatching {
                state = state.copy(userPreferences = hiveRepository.getUserPreferences())
            }.onFailure {
                // else set default and log
                state = state.copy(userPreferences = UserPreferences())
                Log.d(this::javaClass.name, "Error getting user preferences: ${it.message}")
            }.onSuccess {
                // log
                Log.d(this::javaClass.name, "Got user preferences: $it")
            }
        }
    }
}