package com.reedsloan.beekeepingapp.presentation.common

import android.app.Activity
import android.app.Application
import android.content.Context
import android.icu.text.DateFormat
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.TemperatureMeasurement
import com.reedsloan.beekeepingapp.data.local.hive.*
import com.reedsloan.beekeepingapp.domain.repo.HiveRepository
import com.reedsloan.beekeepingapp.presentation.home_screen.HiveScreenState
import com.reedsloan.beekeepingapp.presentation.home_screen.MenuState
import com.reedsloan.beekeepingapp.presentation.screens.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HiveViewModel @Inject constructor(
    private val app: Application, private val hiveRepository: HiveRepository
) : ViewModel() {
    var state by mutableStateOf(HiveScreenState())

    init {
        getUserPreferences()
        getAllHives()
    }


    private fun getUserPreferences() {
        viewModelScope.launch {
            runCatching { hiveRepository.getUserPreferences() }.onSuccess {
                state = state.copy(userPreferences = it)
            }
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
        if(state.hiveDeleteMode) {
            toggleSelected(selectedHiveId)
            return
        }
        closeOpenMenus()
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
        deleteSelectedHives()
        // disable delete mode
        toggleHiveDeleteMode()
    }

    fun onTapWhileInDeleteMode(hiveId: String) {
        toggleSelected(hiveId)
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

    fun isSelected(hiveId: String): Boolean {
        return state.selectionList.contains(hiveId)
    }

    private fun toggleHiveDeleteMode() {
        // clear selection list
        clearSelectionList()
        state = state.copy(hiveDeleteMode = !state.hiveDeleteMode)
    }

    private fun closeOpenMenus() {
        state = state.copy(
            navigationBarMenuState = MenuState.CLOSED,
            hiveDeleteMode = false,
            hiveInfoMenuState = MenuState.CLOSED,
            showExtraButtons = false
        )
    }

    private fun deleteSelectedHives() {
        // delete all hives in selection list
        state.selectionList.forEach { deleteHive(it) }
        // clear selection list
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
        val temperatureMeasurement = TemperatureMeasurement.values().find { it.name == string }
        runCatching { temperatureMeasurement!! }.onSuccess {
            updateUserPreferences(state.userPreferences.copy(temperatureMeasurement = it))
        }.onFailure {
            Toast.makeText(
                app, "Error updating temperature unit measurement", Toast.LENGTH_SHORT
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


    private fun setHiveInfoMenuState(menuState: MenuState) {
        state = state.copy(hiveInfoMenuState = menuState)
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

    private fun getAllHives() {
        viewModelScope.launch {
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
            }
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
            TemperatureMeasurement.Fahrenheit -> temperatureFahrenheit
            TemperatureMeasurement.Celsius -> (temperatureFahrenheit - 32) * 5 / 9
        }
    }

    private fun fahrenheitToCelsius(fahrenheit: Double): Double {
        return (fahrenheit - 32) * 5 / 9
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

    fun deleteHive(hiveId: String) {
        state = state.copy(isLoading = true)
        runCatching {
            viewModelScope.launch {
                hiveRepository.deleteHive(hiveId)
            }
        }.onSuccess {
            getAllHives()
        }.onFailure {
            state =
                state.copy(isLoading = false, isError = true, errorMessage = it.message ?: "")
            // Show error message
            Toast.makeText(app, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * **Use with caution.** This will delete all hives from the database.
     */
    fun deleteAllHives() {
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

    fun exportToCsv() {
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

}