package com.reedsloan.beekeepingapp.presentation.common.viewmodel.hives

import android.app.Activity
import android.app.Application
import android.content.Context
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.data.TimeFormat
import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.TemperatureMeasurement
import com.reedsloan.beekeepingapp.data.local.hive.*
import com.reedsloan.beekeepingapp.domain.repo.HiveRepository
import com.reedsloan.beekeepingapp.presentation.home_screen.MenuState
import com.reedsloan.beekeepingapp.presentation.screens.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.*
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HiveViewModel @Inject constructor(
    private val app: Application, private val hiveRepository: HiveRepository
) : ViewModel() {
    var state by mutableStateOf(HiveState())

    init {
        viewModelScope.launch {
            getUserPreferences()
            getAllHives()
        }
    }

    private suspend fun getUserPreferences() {
        runCatching { hiveRepository.getUserPreferences() }.onSuccess {
            state = state.copy(userPreferences = it)
        }
    }

    fun backHandler(navController: NavController) {
        if(state.hiveDeleteMode) {
            toggleHiveDeleteMode()
            return
        }

        val destination = navController.currentBackStackEntry?.destination?.route.let {
           when(it) {
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
        state = state.copy(selectedHive = state.hives.find { it.id == hiveId })

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
        this.state = this.state.copy(hiveInfoMenuState = state)
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

    fun onTapOutside() {
        closeOpenMenus()
    }

    fun writeBitmapToFile(bitmap: Bitmap?) {
        viewModelScope.launch {
            runCatching {
                bitmap?.let {
                    val file = File(
                        app.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "hive_${state.selectedHive!!.id}.jpg"
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
                state = state.copy(isSuccess = true, hives = state.selectedHive?.let {
                    state.hives.map { hive ->
                        if (hive.id == it.id) {
                            hive.copy(hiveInfo = hive.hiveInfo.copy(image = it.hiveInfo.image))
                        } else {
                            hive
                        }
                    }
                } ?: state.hives)
            }.onFailure {
                state = state.copy(isError = true, errorMessage = it.message ?: "")
            }
        }
    }

    fun setHiveImageUri(uri: Uri?) {
        // update selected hive to be edited with the new image uri
        state.selectedHive?.let { hive ->
            updateHive(
                hive.copy(
                    hiveInfo = hive.hiveInfo.copy(
                        image = uri.toString()
                    )
                )
            )
        }
    }

    fun onClickLogDataButton(id: String, navController: NavController) {
        // set the selected hive
        state.hives.find { hive -> hive.id == id }?.let { hive ->
            state = state.copy(selectedHive = hive)
        }
        // close open menus
        closeOpenMenus()
        // navigate to the log data screen
        navController.navigate(Screen.HiveInfoScreen.route)

    }

    fun onClickViewLogHistoryButton(id: String, navController: NavController) {
        // set the selected hive
        state.hives.find { hive -> hive.id == id }?.let { hive ->
            state = state.copy(selectedHive = hive)
        }
        // close open menus
        closeOpenMenus()
        TODO("Navigate to the log history screen")
    }
}