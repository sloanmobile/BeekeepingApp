package com.reedsloan.beekeepingapp.presentation.viewmodel.hives

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.icu.text.DateFormat
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider.getUriForFile
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.Flow
import javax.inject.Inject

@HiltViewModel
class HiveViewModel @Inject constructor(
    private val app: Application, private val hiveRepository: HiveRepository
) : ViewModel() {
    var state by mutableStateOf(HiveScreenState())
    private val _hives = MutableStateFlow<List<Hive>>(emptyList())
    val hives = _hives.asStateFlow()

    init {
        viewModelScope.launch {
            getUserPreferences()
            getAllHives()
        }
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

    fun backHandler(navController: NavController) {
        if (state.hiveDeleteMode) {
            toggleHiveDeleteMode()
            return
        } else if (state.editingTextField) {
            toggleEditingTextField()
            return
        }

        val destination = navController.currentBackStackEntry?.destination?.route.let {
            when (it) {
                Screen.HomeScreen.route -> {
                    Screen.HomeScreen
                }
                Screen.HiveScreen.route -> {
                    Screen.HiveScreen
                }
                Screen.SplashScreen.route -> {
                    Screen.SplashScreen
                }
                Screen.HiveInfoScreen.route -> {
                    Screen.HiveScreen
                }
                Screen.SettingsScreen.route -> {
                    Screen.SettingsScreen
                }
                else -> {
                    Screen.HomeScreen
                }
            }
        }

        navigate(navController, destination)
    }

    private fun toggleEditingTextField() {
        state = state.copy(editingTextField = !state.editingTextField)
    }

    fun onTapAddHiveButton() {
        // make a toast notification
        Toast.makeText(app, "New hive created.", Toast.LENGTH_SHORT).show()
        closeOpenMenus()
        createHive()
    }

    fun onTapViewHiveLog(selectedHiveId: String, navController: NavController) {
        Log.d("HiveListItem", "Thread: ${Thread.currentThread().name}")
        if (state.hiveDeleteMode) {
            toggleSelected(selectedHiveId)
            return
        }
        setSelectedHive(selectedHiveId)
        navigate(navController, Screen.HiveInfoScreen)
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

    fun navigate(navController: NavController, destination: Screen) {
        Log.d(this::class.simpleName, destination.name)
        state = state.copy(currentScreenName = destination.name)
        closeOpenMenus()
        navController.navigate(destination.route)
    }

    fun onTapDeleteSelectedHiveButton() {
        viewModelScope.launch {
            deleteSelectedHives()
            toggleHiveDeleteMode()
        }
    }

    fun onTapDeleteHiveButton(selectedHiveId: String) {
        viewModelScope.launch {
            deleteHive(selectedHiveId)
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
            editHiveMenuState = MenuState.CLOSED,
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
        val temperatureMeasurement =
            TemperatureMeasurement.values().find { it.displayValue == string }
        runCatching { temperatureMeasurement!! }.onSuccess {
            updateUserPreferences(state.userPreferences.copy(temperatureMeasurement = it))
        }.onFailure {
            Toast.makeText(
                app, "Error updating temperature unit measurement", Toast.LENGTH_SHORT
            ).show()
            // log error
            Log.e(
                "HiveViewModel",
                "Error updating temperature unit measurement ${it.stackTraceToString()}"
            )
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
        state = state.copy(selectedHive = hives.value.find { it.id == hiveId })
    }

    fun setHiveNotes(notes: String) {
        state = state.copy(
            selectedHive = state.selectedHive?.copy(
                hiveInfo = state.selectedHive!!.hiveInfo.copy(notes = notes)
            )
        )
    }

    fun setHiveName(name: String) {
        runCatching { state.selectedHive!! }.onSuccess {
            state = state.copy(
                selectedHive = it.copy(
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

    fun onClickEditHiveButton(hiveId: String) {
        setSelectedHive(hiveId)
        closeOpenMenus()
        setHiveInfoMenuState(MenuState.OPEN)
    }

    private fun setHiveInfoMenuState(state: MenuState) {
        this.state = this.state.copy(editHiveMenuState = state)
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
                isLoading = false, isSuccess = true
            )
            _hives.value = hives
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
                    ),
                    displayOrder = hives.size + 1
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


    private suspend fun updateHive(hive: Hive) {
        runCatching {
            state = state.copy(isLoading = true)
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

    fun onTapOutside() {
        closeOpenMenus()
    }

    fun writeBitmapToFile(bitmap: Bitmap?) {
        viewModelScope.launch {
            runCatching {
                bitmap?.let {
                    val uri = getImageUri("${state.selectedHive?.id}")
                    val file = File(
                        uri.path ?: throw NotFoundException("Error getting file path")
                    )
                    val out = FileOutputStream(file)
                    it.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    out.flush()
                    out.close()
                    // update the hive with the new image
                    state.selectedHive?.let { hive ->
                        updateHive(
                            hive.copy(
                                hiveInfo = hive.hiveInfo.copy(
                                    image = file.absolutePath
                                )
                            )
                        )
                    }
                }
            }.onSuccess {
                state = state.copy(isLoading = false, isSuccess = true)
                _hives.value = hiveRepository.getAllHives()
            }.onFailure {
                state = state.copy(isError = true, errorMessage = it.message ?: "")
            }
        }
    }

    fun getImageUri(name: String): Uri {
        val directory = File(app.cacheDir, "images")
        directory.mkdirs()
        val file = File.createTempFile(
            "image_${name}",
            ".jpg",
            directory,
        )
        val authority = app.packageName + ".fileprovider"
        return getUriForFile(
            app,
            authority,
            file,
        )
    }

    fun copyImageToInternalStorage(uriFromExternalStorage: Uri?) {
        viewModelScope.launch {
            // save the image uri to the disk
            uriFromExternalStorage?.let {
                // copy
                val uri = getImageUri(state.selectedHive!!.id)
                val inputStream = app.contentResolver.openInputStream(it)
                val outputStream = app.contentResolver.openOutputStream(uri)
                inputStream?.copyTo(outputStream!!)
                inputStream?.close()
                outputStream?.close()
                // update the hive with the new image
                state.selectedHive?.let { hive ->
                    updateHive(
                        hive.copy(
                            hiveInfo = hive.hiveInfo.copy(
                                image = uri.toString()
                            )
                        )
                    )
                    deselectHive()
                }
            }
        }
    }

    private fun deselectHive() {
        state = state.copy(selectedHive = null)
    }

    fun onClickLogDataButton(id: String, navController: NavController) {
        // set the selected hive
        hives.value.find { hive -> hive.id == id }?.let { hive ->
            state = state.copy(selectedHive = hive)
        }
        // close open menus
        closeOpenMenus()
        // navigate to the log data screen
        navController.navigate(Screen.HiveInfoScreen.route)

    }

    fun onClickViewLogHistoryButton(id: String, navController: NavController) {
        // set the selected hive
        hives.value.find { hive -> hive.id == id }?.let { hive ->
            state = state.copy(selectedHive = hive)
        }
        // close open menus
        closeOpenMenus()
        TODO("Navigate to the log history screen")
    }

    fun onTapChoosePhotoButton(selectedHiveId: String) {
        setSelectedHive(selectedHiveId)
    }

    fun onTapEditHiveNameButton(id: String) {
        setSelectedHive(id)
        // set editingTextField to true
        state = state.copy(editingTextField = true)
    }

    fun onTapSaveHiveNameButton(id: String, editableString: String) {
        // set editingTextField to false
        state = state.copy(editingTextField = false)
        // update the hive name
        hives.value.find { hive -> hive.id == id }?.let { hive ->
            viewModelScope.launch {
                updateHive(
                    hive.copy(
                        hiveInfo = hive.hiveInfo.copy(
                            name = editableString
                        )
                    )
                )
                deselectHive()
            }
        }
    }

    fun onTapEditHiveButton(id: String) {
        setSelectedHive(id)
        closeOpenMenus()
        toggleEditHiveMenu()
    }

    fun toggleEditHiveMenu() {
        state = state.copy(editHiveMenuState = MenuState.OPEN)
    }

    fun onTapLogDataButton(id: String, navController: NavController) {
        setSelectedHive(id)
        closeOpenMenus()
        navController.navigate(Screen.HiveInfoScreen.route)
    }

    fun onTapViewLogsButton(id: String) {
        TODO("Navigate to the log history screen")
    }

    fun onDismissEditHiveMenu() {
        state = state.copy(editHiveMenuState = MenuState.CLOSED)
    }
}