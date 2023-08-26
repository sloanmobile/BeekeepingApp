package com.reedsloan.beekeepingapp.presentation.viewmodel.hives

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.icu.text.DateFormat
import android.net.Uri
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.FileProvider.getUriForFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.data.TimeFormat
import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.TemperatureMeasurement
import com.reedsloan.beekeepingapp.data.local.hive.*
import com.reedsloan.beekeepingapp.domain.repo.HiveRepository
import com.reedsloan.beekeepingapp.presentation.common.data.PermissionRequest
import com.reedsloan.beekeepingapp.presentation.home_screen.HiveScreenState
import com.reedsloan.beekeepingapp.presentation.home_screen.MenuState
import com.reedsloan.beekeepingapp.presentation.screens.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HiveViewModel @Inject constructor(
    private val app: Application, private val hiveRepository: HiveRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HiveScreenState())
    val state = _state.asStateFlow()

    private val _hives = MutableStateFlow<List<Hive>>(emptyList())
    val hives = _hives.asStateFlow()

    val visiblePermissionDialogQueue = mutableStateListOf<PermissionRequest>()


    init {
        viewModelScope.launch {
            getUserPreferences()
            getAllHives()
        }
    }

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    /**
     * On permission result, remove the permission from the queue if it was granted.
     * If it was denied, the open app settings dialog will be shown.
     */
    fun onPermissionResult(permission: String, granted: Boolean) {
        if (granted) {
            dismissDialog()
        } else {
            dismissDialog()
            visiblePermissionDialogQueue.add(getPermissionRequest(permission = permission))
        }
    }

    fun onPermissionRequested(permission: String) {
        visiblePermissionDialogQueue.add(getPermissionRequest(permission))
    }

    private fun getPermissionRequest(permission: String): PermissionRequest {
        return when (permission) {
            android.Manifest.permission.CAMERA -> {
                PermissionRequest.CameraPermissionRequest
            }

            android.Manifest.permission.READ_MEDIA_IMAGES -> {
                PermissionRequest.StoragePermissionRequestAPI33
            }

            else -> {
                throw IllegalArgumentException("Unknown permission: $permission, please add it to the getPermissionRequest function.")
            }
        }

    }

    fun getHourMinuteString(date: LocalDateTime): String {
        // adjust to user preferences of 24 hour or 12 hour
        return when (state.value.userPreferences.timeFormat) {
            TimeFormat.TWENTY_FOUR_HOUR -> {
                date.format(DateTimeFormatter.ofPattern("HH:mm"))
            }

            TimeFormat.TWELVE_HOUR -> {
                date.format(DateTimeFormatter.ofPattern("hh:mm a"))
            }
        }
    }

    fun getHourString(date: LocalDateTime): String {
        return when (state.value.userPreferences.timeFormat) {
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

        val daysInMonth = when (month) {
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
        runCatching { hiveRepository.getUserPreferences() }.onSuccess { userPreferences ->
            _state.update { it.copy(userPreferences = userPreferences) }
        }
    }

    /**
     * Use this function to handle the back button press instead of using popBackStack() directly.
     */
    fun backHandler(navController: NavController) {
        if (state.value.hiveDeleteMode) {
            toggleHiveDeleteMode()
            return
        } else if (state.value.editingTextField) {
            toggleEditingTextField()
            return
        } else if (state.value.showDeleteHiveDialog) {
            closeOpenMenus()
            return
        } else if (state.value.editHiveMenuState == MenuState.OPEN) {
            closeOpenMenus()
            return
        }

        // pop backstack if there is a previous screen
        if (navController.previousBackStackEntry != null) {
            navController.popBackStack()
        }
    }

    private fun toggleEditingTextField() {
        _state.update { it.copy(editingTextField = !it.editingTextField) }
    }

    fun onClickAddHiveButton() {
        // make a toast notification
        Toast.makeText(app, "New hive created.", Toast.LENGTH_SHORT).show()
        closeOpenMenus()
        createHive()
    }


    fun onTapNavigationExpandButton() {
        toggleNavigationBarMenuState()
    }

    fun onTapDeleteSelectedHiveButton() {
        viewModelScope.launch {
            deleteSelectedHives()
            toggleHiveDeleteMode()
        }
    }

    fun showDeleteHiveDialog(selectedHive: String) {
        setSelectedHive(selectedHive)
        // show confirmation dialog
        _state.update { it.copy(showDeleteHiveDialog = true) }
    }

    fun dismissDeleteHiveDialog() {
        _state.update {
            it.copy(showDeleteHiveDialog = false)
        }
    }

    fun onTapDeleteHiveConfirmationButton(selectedHiveId: String) {
        viewModelScope.launch {
            deleteHive(selectedHiveId)
            closeOpenMenus()
        }
    }

    private fun addToSelectionList(hiveId: String) {
        _state.update { it.copy(selectionList = it.selectionList + hiveId) }
    }

    private fun removeFromSelectionList(hiveId: String) {
        _state.update { it.copy(selectionList = it.selectionList - hiveId) }
    }

    private fun clearSelectionList() {
        _state.update { it.copy(selectionList = emptyList()) }
    }

    private fun toggleSelected(hiveId: String) {
        if (state.value.selectionList.contains(hiveId)) {
            removeFromSelectionList(hiveId)
        } else {
            addToSelectionList(hiveId)
        }
    }

    private fun toggleHiveDeleteMode() {
        _state.value = state.value.copy(hiveDeleteMode = !state.value.hiveDeleteMode)
    }

    /**
     * Closes all open menus and clears the selection list.
     */
    private fun closeOpenMenus() {
        _state.value = state.value.copy(
            navigationBarMenuState = MenuState.CLOSED,
            hiveDeleteMode = false,
            editHiveMenuState = MenuState.CLOSED,
            showExtraButtons = false
        )
        clearSelectionList()
    }

    private suspend fun deleteSelectedHives() {
        // delete all hives in selection list
        state.value.selectionList.forEach { deleteHive(it) }
        clearSelectionList()
    }

    private fun updateUserPreferences(userPreferences: UserPreferences) {
        viewModelScope.launch {
            runCatching { hiveRepository.updateUserPreferences(userPreferences) }.onSuccess {
                _state.update { state.value.copy(userPreferences = userPreferences) }
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
            updateUserPreferences(state.value.userPreferences.copy(temperatureMeasurement = it))
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
            updateUserPreferences(state.value.userPreferences.copy(timeFormat = it))
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

    fun setSelectedHive(hiveId: String) {
        // Set the selected hive in the state by finding the hive with the matching id
        _state.update { it.copy(selectedHive = hives.value.find { it.id == hiveId }) }
    }

    fun setHiveNotes(notes: String) {
        _state.update {
            it.copy(
                selectedHive = it.selectedHive?.copy(
                    hiveDetails = it.selectedHive!!.hiveDetails.copy(notes = notes)
                )
            )
        }
    }

    fun setHiveName(name: String) {
        runCatching { state.value.selectedHive!! }.onSuccess {
            _state.update {
                it.copy(
                    selectedHive = it.selectedHive?.copy(
                        hiveDetails = it.selectedHive.hiveDetails.copy(
                            name = name, dateModified = LocalDate.now()
                        )
                    )
                )
            }
        }.onFailure {
            Toast.makeText(
                app, "Error updating hive name", Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun toggleNavigationBarMenuState() {
        _state.update {
            it.copy(navigationBarMenuState = it.navigationBarMenuState.toggle())
        }
    }

    fun onClickEditHiveButton(hiveId: String) {
        setSelectedHive(hiveId)
        closeOpenMenus()
        setHiveInfoMenuState(MenuState.OPEN)
    }

    private fun setHiveInfoMenuState(state: MenuState) {
        _state.update { it.copy(editHiveMenuState = state) }
    }

    private fun setIsLoading(isLoading: Boolean) {
        _state.update { it.copy(isLoading = isLoading) }
    }

    private fun setIsSuccess(isSuccess: Boolean) {
        _state.update { it.copy(isSuccess = isSuccess, isLoading = false) }
    }

    private fun showError(error: String) {
        _state.update { it.copy(isError = true, errorMessage = error, isLoading = false) }
    }

    private suspend fun getAllHives() {
        setIsLoading(true)
        runCatching {
            hiveRepository.getAllHives()
        }.onSuccess { hives ->
            setIsSuccess(true)
            _hives.value = hives
        }.onFailure { error ->
            showError(error.message ?: "Unknown error")

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
        return when (state.value.userPreferences.temperatureMeasurement) {
            TemperatureMeasurement.FAHRENHEIT -> temperatureFahrenheit
            TemperatureMeasurement.CELSIUS -> (temperatureFahrenheit - 32) * 5 / 9
        }
    }

    private fun createHive() {
        viewModelScope.launch {
            runCatching {
                val hives = hiveRepository.getAllHives()
                val hive = Hive(
                    id = UUID.randomUUID().toString(), HiveInfo(
                        name = "Hive ${hives.size + 1}",
                    ), displayOrder = hives.size + 1
                )
                hiveRepository.createHive(hive)
            }.onSuccess {
                getAllHives()
            }.onFailure {
                showError(it.message ?: "Unknown error")
                // Show error message
                Toast.makeText(app, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private suspend fun updateHive(hive: Hive) {
        runCatching {
            setIsLoading(true)
            hiveRepository.updateHive(
                hive.copy(
                    hiveDetails = hive.hiveDetails.copy(
                        dateModified = LocalDate.now()
                    )
                )
            )
        }.onSuccess {
            getAllHives()
        }.onFailure {
            showError(it.message ?: "Unknown error")
            // Show error message
            Toast.makeText(app, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun deleteHive(hiveId: String) {
        runCatching {
            setIsLoading(true)
            hiveRepository.deleteHive(hiveId)
        }.onSuccess {
            // get all hives again
            getAllHives()
        }.onFailure {
            showError(it.message ?: "Unknown error")
            // Show error message
            Toast.makeText(app, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * **Use with caution.** This will delete all hives from the database.
     */
    private fun deleteAllHives() {
        viewModelScope.launch {
            setIsLoading(true)
            runCatching {
                hiveRepository.deleteAllHives()
            }.onSuccess {
                setIsSuccess(true)
                getAllHives()
            }.onFailure {
                showError(it.message ?: "Unknown error")
            }
        }
    }

    private fun exportToCsv() {
        viewModelScope.launch {
            setIsLoading(true)
            runCatching {
                hiveRepository.exportToCsv()
            }.onSuccess {
                setIsSuccess(true)
            }.onFailure {
                showError(it.message ?: "Unknown error")
            }
        }
    }

    fun onTapOutside() {
        closeOpenMenus()
    }

    fun writeBitmapToFile(bitmap: Bitmap?) {
        viewModelScope.launch {
            setIsLoading(true)
            runCatching {
                bitmap?.let {
                    val uri = getImageUri("${state.value.selectedHive?.id}")
                    val file = File(
                        uri.path ?: throw NotFoundException("Error getting file path")
                    )
                    val out = FileOutputStream(file)
                    it.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    out.flush()
                    out.close()
                    // update the hive with the new image
                    state.value.selectedHive?.let { hive ->
                        updateHive(
                            hive.copy(
                                hiveDetails = hive.hiveDetails.copy(
                                    image = file.absolutePath
                                )
                            )
                        )
                    }
                }
            }.onSuccess {
                setIsSuccess(true)
                _hives.value = hiveRepository.getAllHives()
            }.onFailure {
                showError(it.message ?: "Unknown error")
            }
        }
    }

    fun deleteImage() {
        setIsLoading(true)
        viewModelScope.launch {
            runCatching {
                state.value.selectedHive?.let { hive ->
                    updateHive(
                        hive.copy(
                            hiveDetails = hive.hiveDetails.copy(
                                image = ""
                            )
                        )
                    )
                }
            }.onSuccess {
                setIsSuccess(true)
                _hives.update { hiveRepository.getAllHives() }
            }.onFailure {
                showError(it.message ?: "Unknown error")
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
                val uri = getImageUri(state.value.selectedHive!!.id)
                val inputStream = app.contentResolver.openInputStream(it)
                val outputStream = app.contentResolver.openOutputStream(uri)
                inputStream?.copyTo(outputStream!!)
                inputStream?.close()
                outputStream?.close()
                // update the hive with the new image
                state.value.selectedHive?.let { hive ->
                    updateHive(
                        hive.copy(
                            hiveDetails = hive.hiveDetails.copy(
                                image = uri.toString()
                            )
                        )
                    )
                }
            }
        }
    }

    private fun deselectHive() {
        _state.update { state.value.copy(selectedHive = null) }
    }

    fun onClickViewLogHistoryButton(id: String, navController: NavController) {
        // set the selected hive
        hives.value.find { hive -> hive.id == id }?.let { hive ->
            setSelectedHive(hive.id)
        }
        // close open menus
        closeOpenMenus()
        TODO("Navigate to the log history screen")
    }

    fun deleteImage(uri: Uri) {
        viewModelScope.launch {
            setIsLoading(true)
            runCatching {
                app.contentResolver.delete(uri, null, null)
            }.onSuccess {
                setIsSuccess(true)
                getAllHives()
            }.onFailure {
                showError(it.message ?: "Unknown error")
            }
        }
    }

    fun onTapChoosePhotoButton(selectedHiveId: String) {
        setSelectedHive(selectedHiveId)
    }

    fun onTapEditHiveNameButton(id: String) {
        setSelectedHive(id)
        // set editingTextField to true
        _state.update { state.value.copy(editingTextField = true) }
    }

    fun onTapSaveHiveNameButton(id: String, editableString: String) {
        // set editingTextField to false
        _state.update { state.value.copy(editingTextField = false) }
        // update the hive name
        hives.value.find { hive -> hive.id == id }?.let { hive ->
            viewModelScope.launch {
                updateHive(
                    hive.copy(
                        hiveDetails = hive.hiveDetails.copy(
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
        _state.update {
            state.value.copy(
                editHiveMenuState = when (state.value.editHiveMenuState) {
                    MenuState.CLOSED -> MenuState.OPEN
                    MenuState.OPEN -> MenuState.CLOSED
                }
            )
        }
    }

    fun onTapLogDataButton(id: String, navController: NavController) {
        setSelectedHive(id)
        closeOpenMenus()
        navController.navigate(Screen.LogDataScreen.route)
    }

    fun onTapViewLogsButton(id: String) {
        TODO("Navigate to the log history screen")
    }

    fun onDismissEditHiveMenu() {
        closeOpenMenus()
    }

    fun onTapSaveButton(uri: Uri?, name: String) {
        closeOpenMenus()
        setIsLoading(true)
        viewModelScope.launch {
            runCatching {
                state.value.selectedHive?.let { hive ->
                    copyImageToInternalStorage(uri)
                    updateHive(
                        hive.copy(
                            hiveDetails = hive.hiveDetails.copy(
                                name = name, image = uri?.toString() ?: hive.hiveDetails.image
                            )
                        )
                    )
                    deselectHive()
                }
            }.onSuccess {
                setIsSuccess(true)
                getAllHives()
            }.onFailure {
                showError(it.message ?: "Unknown error")
            }
        }
    }

    fun addHiveDataEntry(hiveDataEntry: HiveDataEntry) {
        _state.update {
            state.value.copy(
                selectedHive =
                state.value.selectedHive?.copy(
                    hiveDataEntries = state.value.selectedHive?.hiveDataEntries?.plus(hiveDataEntry)
                        ?: listOf(hiveDataEntry)
                )
            )
        }
        saveDataEntry(hiveDataEntry)
    }

    private fun removeHiveDataEntry(hiveDataEntry: HiveDataEntry) {
        state.value.selectedHive?.let { hive ->
            viewModelScope.launch {
                updateHive(
                    hive.copy(
                        hiveDataEntries = hive.hiveDataEntries - hiveDataEntry
                    )
                )
            }
        }
    }

    private fun saveDataEntry(hiveDataEntry: HiveDataEntry) {
        state.value.selectedHive?.let { hive ->
            viewModelScope.launch {
                // check if there is already a hive data entry for the same date
                val updateInsteadOfCreate = hive.hiveDataEntries.find {
                    it.date == hiveDataEntry.date
                } != null

                if (updateInsteadOfCreate) {
                    updateHiveDataEntry(hiveDataEntry)
                } else {
                    createHiveDataEntry(hiveDataEntry)
                }
            }
        }
    }

    private suspend fun updateHiveDataEntry(hiveDataEntry: HiveDataEntry) {
        runCatching {
            updateHive(
                hive = state.value.selectedHive!!.copy(
                    hiveDataEntries = state.value.selectedHive!!.hiveDataEntries.map {
                        if (it.date == hiveDataEntry.date) {
                            hiveDataEntry
                        } else {
                            it
                        }
                    }
                )
            )
        }.onSuccess {
            setIsSuccess(true)
        }.onFailure {
            showError(it.message ?: "Unknown error")
        }
    }

    private suspend fun createHiveDataEntry(hiveDataEntry: HiveDataEntry) {
        runCatching {
            updateHive(
                hive = state.value.selectedHive!!.copy(
                    hiveDataEntries = state.value.selectedHive!!.hiveDataEntries + hiveDataEntry
                )
            )
        }.onSuccess {
            setIsSuccess(true)
        }.onFailure {
            showError(it.message ?: "Unknown error")
        }
    }

    fun setSelectedDataEntry(newHiveDataEntry: HiveDataEntry) {
        _state.update {
            state.value.copy(
                selectedDataEntry = newHiveDataEntry
            )
        }
    }

    fun onTapQuickLogButton(navController: NavController) {
        closeOpenMenus()
        navController.navigate(Screen.QuickLogScreen.route)
    }

    fun onTapSaveDataEntry() {
        state.value.selectedDataEntry.let { hiveDataEntry ->
            addHiveDataEntry(hiveDataEntry)
        }
    }

    fun getDefaultDataEntry(): HiveDataEntry {
        return HiveDataEntry(
            hiveId = state.value.selectedHive?.id ?: "",
            date = LocalDate.now().toString(),
            hiveConditions = HiveConditions(),
            hiveHealth = HiveHealth(),
            feeding = HiveFeeding(),
            localPhotoUris = emptyList()
        )
    }

    fun onTapSettingsButton(navController: NavController) {
        closeOpenMenus()
        navController.navigate(Screen.SettingsScreen.route)
    }

    fun onTapHiveCard(id: String, navController: NavController) {
        setSelectedHive(id)
        closeOpenMenus()
        navController.navigate(Screen.HiveDetailsScreen.route)
    }

    fun setImageForSelectedHive(uri: Uri?) {
        setIsLoading(true)
        viewModelScope.launch {
            runCatching {
                state.value.selectedHive?.let { hive ->
                    copyImageToInternalStorage(uri)
                    updateHive(
                        hive.copy(
                            hiveDetails = hive.hiveDetails.copy(
                                image = uri?.toString() ?: hive.hiveDetails.image
                            )
                        )
                    )
                }
            }.onSuccess {
                setIsSuccess(true)
                getAllHives()
            }.onFailure {
                showError(it.message ?: "Unknown error")
            }
        }
    }

    fun onTapInspectionsButton(navController: NavController) {
        closeOpenMenus()
        navController.navigate(Screen.InspectionsScreen.route)
    }

    fun onClickAddInspectionButton() {
        TODO("Not yet implemented")
    }
}