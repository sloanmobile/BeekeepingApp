package com.reedsloan.beekeepingapp.presentation.common

import android.app.Activity
import android.app.Application
import android.content.Context
import android.icu.text.DateFormat
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.data.TimeFormat
import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.TemperatureMeasurement
import com.reedsloan.beekeepingapp.data.local.hive.*
import com.reedsloan.beekeepingapp.domain.repo.HiveRepository
import com.reedsloan.beekeepingapp.presentation.home_screen.HiveScreenState
import com.reedsloan.beekeepingapp.presentation.home_screen.MenuState
import com.reedsloan.beekeepingapp.presentation.screens.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HiveViewModel @Inject constructor(
    private val app: Application, private val hiveRepository: HiveRepository
) : ViewModel() {
    var state by mutableStateOf(HiveScreenState())

    init {
        viewModelScope.launch {
            getUserPreferences()
            getAllHives()
        }
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
//        return date.format(DateTimeFormatter.ofPattern("HH:mm"))
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
                date.format(DateTimeFormatter.ofPattern("hh"))
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


    private suspend fun getUserPreferences() {
        runCatching { hiveRepository.getUserPreferences() }.onSuccess {
            state = state.copy(userPreferences = it)
        }
    }

    fun onTapOutside() {
        closeOpenMenus()
    }

    fun onTapAddHiveButton() {
        // make a toast notification
        Toast.makeText(app, "New hive created.", Toast.LENGTH_SHORT).show()
        closeOpenMenus()
        createHive()
    }

    fun onArrivalAtAddHiveScreen() {
        closeOpenMenus()
    }

    fun onTapHiveListItem(selectedHiveId: String, navController: NavController) {
        Log.d("HiveListItem", "Thread: ${Thread.currentThread().name}")
        if (state.hiveDeleteMode) {
            toggleSelected(selectedHiveId)
            return
        }
        setSelectedHive(selectedHiveId)
        navController.navigate(Screen.HiveInfoScreen.route)
    }


    fun onLongPressHiveListItem(selectedHiveId: String) {
        // enable delete mode
        toggleHiveDeleteMode()
        // add hive to selection list
        addToSelectionList(selectedHiveId)
    }

    fun onTapNavigationExpandButton() {
        toggleNavigationBarMenuState()
    }

    fun onTapNavigationButton() {
        closeOpenMenus()
    }

    fun onTapDeleteHiveButton() {
        viewModelScope.launch {
            deleteSelectedHives()
            toggleHiveDeleteMode()
        }
    }

    private fun addToSelectionList(hiveId: String) {
        state = state.copy(selectionList = state.selectionList + hiveId)
    }

    private fun removeFromSelectionList(hiveId: String) {
        state = state.copy(selectionList = state.selectionList - hiveId)
    }

    private fun clearSelectionList() {
        state = state.copy(selectionList = emptyList())
    }

    private fun toggleSelected(hiveId: String) {
        if (state.selectionList.contains(hiveId)) {
            removeFromSelectionList(hiveId)
        } else {
            addToSelectionList(hiveId)
        }
    }

    private fun toggleHiveDeleteMode() {
        state = state.copy(hiveDeleteMode = !state.hiveDeleteMode)
    }

    /**
     * Closes all open menus and clears the selection list.
     */
    private fun closeOpenMenus() {
        state = state.copy(
            navigationBarMenuState = MenuState.CLOSED,
            hiveDeleteMode = false,
            hiveInfoMenuState = MenuState.CLOSED,
            showExtraButtons = false
        )
        clearSelectionList()
    }

    private suspend fun deleteSelectedHives() {
        // delete all hives in selection list
        state.selectionList.forEach { deleteHive(it) }
        clearSelectionList()
    }

    private fun updateUserPreferences(userPreferences: UserPreferences) {
        viewModelScope.launch {
            runCatching { hiveRepository.updateUserPreferences(userPreferences) }.onSuccess {
                state = state.copy(userPreferences = userPreferences)
            }.onFailure {
                Toast.makeText(
                    app, "Error updating user preferences", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Sets the user's preferred temperature unit based on the [TemperatureMeasurement.displayValue]
     * (e.g. "Fahrenheit")
     *
     * @param [TemperatureMeasurement.displayValue] The display value of the temperature unit.
     */
    fun setTemperatureMeasurement(string: String) {
        val temperatureMeasurement = TemperatureMeasurement.values().find { it.displayValue == string }
        runCatching { temperatureMeasurement!! }.onSuccess {
            updateUserPreferences(state.userPreferences.copy(temperatureMeasurement = it))
        }.onFailure {
            Toast.makeText(
                app, "Error updating temperature unit measurement", Toast.LENGTH_SHORT
            ).show()
            // log error
            Log.e("HiveViewModel", "Error updating temperature unit measurement ${it.stackTraceToString()}")
        }
    }

    /**
     * Sets the user's preferred time format based on the [TimeFormat.displayValue]
     * (e.g. "12-hour", "24-hour")
     * @param [TimeFormat.displayValue] The display value of the time format.
     * @see [TimeFormat]
     */
    fun setTimeFormat(string: String) {
        val timeFormat = TimeFormat.values().find { it.displayValue == string }
        runCatching { timeFormat!! }.onSuccess {
            updateUserPreferences(state.userPreferences.copy(timeFormat = it))
        }.onFailure {
            Toast.makeText(
                app, "Error updating time format", Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun hideKeyboard(context: Context) {
        // hide the keyboard
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(
            (context as Activity).currentFocus?.windowToken, 0
        )
    }

    private fun setSelectedHive(hiveId: String) {
        // Set the selected hive in the state by finding the hive with the matching id
        state = state.copy(selectedHiveToBeEdited = state.hives.find { it.id == hiveId })

    }

    fun setHiveNotes(notes: String) {
        state = state.copy(
            selectedHiveToBeEdited = state.selectedHiveToBeEdited?.copy(
                hiveInfo = state.selectedHiveToBeEdited!!.hiveInfo.copy(notes = notes)
            )
        )
    }

    fun setHiveName(name: String) {
        runCatching { state.selectedHiveToBeEdited!! }.onSuccess {
            state = state.copy(
                selectedHiveToBeEdited = it.copy(
                    hiveInfo = it.hiveInfo.copy(
                        name = name, dateModified = System.currentTimeMillis().toString()
                    )
                )
            )
        }.onFailure {
            Toast.makeText(
                app, "Error updating hive name", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun toggleNavigationBarMenuState() {
        state = state.copy(navigationBarMenuState = state.navigationBarMenuState.toggle())
    }

    fun toggleHiveEditMenu() {
        state = state.copy(hiveInfoMenuState = state.hiveInfoMenuState.toggle())
    }

    fun setCameraPermissionAllowed(isAllowed: Boolean) {
        state = state.copy(isCameraPermissionAllowed = isAllowed)
    }

    fun setStoragePermissionAllowed(isAllowed: Boolean) {
        state = state.copy(isStoragePermissionAllowed = isAllowed)
    }

    private suspend fun getAllHives() {
        state = state.copy(isLoading = true)
        runCatching {
            hiveRepository.getAllHives()
        }.onSuccess { hives ->
            state = state.copy(
                isLoading = false, isSuccess = true, hives = hives
            )
        }.onFailure { error ->
            state = state.copy(
                isLoading = false,
                isError = true,
                errorMessage = error.message ?: "Unknown error"
            )
            // make a toast
            Toast.makeText(
                app, "Error getting hives.", Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun dateMillisToDateString(dateMillis: String, longFormat: Boolean = false): String {
        val format = DateFormat.getDateInstance(DateFormat.LONG)
        return (format.format(dateMillis.toLong()) + if (longFormat) " at " + DateFormat.getTimeInstance(
            DateFormat.SHORT
        ).format(dateMillis.toLong()) else "")
    }

    fun getTemperatureValue(temperatureFahrenheit: Double): Double {
        return when (state.userPreferences.temperatureMeasurement) {
            TemperatureMeasurement.FAHRENHEIT -> temperatureFahrenheit
            TemperatureMeasurement.CELSIUS -> (temperatureFahrenheit - 32) * 5 / 9
        }
    }

    private fun createHive() {
        viewModelScope.launch {
            runCatching {
                val hives = hiveRepository.getAllHives()
                val hive = Hive(
                    id = UUID.randomUUID().toString(),
                    HiveInfo(
                        name = "Hive ${hives.size + 1}",
                    )
                )
                hiveRepository.createHive(hive)
            }.onSuccess {
                getAllHives()
            }.onFailure {
                state = state.copy(isError = true, errorMessage = it.message ?: "")
                // Show error message
                Toast.makeText(app, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun updateHive(hive: Hive) {
        viewModelScope.launch {
            runCatching {
                hiveRepository.updateHive(
                    hive.copy(
                        hiveInfo = hive.hiveInfo.copy(
                            dateModified = System.currentTimeMillis().toString()
                        )
                    )
                )
            }.onSuccess {
                getAllHives()
            }.onFailure {
                state = state.copy(isError = true, errorMessage = it.message ?: "")
                // Show error message
                Toast.makeText(app, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun deleteHive(hiveId: String) {
        runCatching {
            state = state.copy(isLoading = true)
            hiveRepository.deleteHive(hiveId)
        }.onSuccess {
            // get all hives again
            getAllHives()
        }.onFailure {
            state =
                state.copy(
                    isLoading = false,
                    isError = true,
                    errorMessage = it.message ?: ""
                )
            // Show error message
            Toast.makeText(app, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * **Use with caution.** This will delete all hives from the database.
     */
    private fun deleteAllHives() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            runCatching {
                hiveRepository.deleteAllHives()
            }.onSuccess {
                getAllHives()
            }.onFailure {
                state =
                    state.copy(isLoading = false, isError = true, errorMessage = it.message ?: "")
            }
        }
    }

    private fun exportToCsv() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            runCatching {
                hiveRepository.exportToCsv()
            }.onSuccess {
                state = state.copy(isLoading = false, isSuccess = true)
            }.onFailure {
                state =
                    state.copy(isLoading = false, isError = true, errorMessage = it.message ?: "")
            }
        }
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



}