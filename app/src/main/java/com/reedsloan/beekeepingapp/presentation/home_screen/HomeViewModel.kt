package com.reedsloan.beekeepingapp.presentation.home_screen

import android.app.Application
import android.icu.text.DateFormat
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.TemperatureMeasurement
import com.reedsloan.beekeepingapp.data.local.hive.*
import com.reedsloan.beekeepingapp.domain.repo.HiveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val app: Application,
    private val hiveRepository: HiveRepository
) : ViewModel() {
    var state by mutableStateOf(HomeScreenState())

    init {
        getUserPreferences()
        getAllHives()
    }

    private fun getUserPreferences() {
        viewModelScope.launch {
            runCatching { hiveRepository.getUserPreferences() }
                .onSuccess { state = state.copy(userPreferences = it) }
        }
    }

    private fun updateUserPreferences(userPreferences: UserPreferences) {
        viewModelScope.launch {
            runCatching { hiveRepository.updateUserPreferences(userPreferences) }
                .onSuccess { state = state.copy(userPreferences = userPreferences) }
                .onFailure {
                    Toast.makeText(
                        app,
                        "Error updating user preferences",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    fun setTemperatureMeasurement(temperatureMeasurement: TemperatureMeasurement) {
        updateUserPreferences(state.userPreferences.copy(temperatureMeasurement = temperatureMeasurement))
    }


    fun setHiveInfoMenuState(menuState: MenuState) {
        state = state.copy(hiveInfoMenuState = menuState)
    }

    fun setSelectedHive(hiveId: String) {
        // Set the selected hive in the state by finding the hive with the matching id
        state = state.copy(selectedHive = state.hives.find { it.hiveInfo.id == hiveId })
    }

    fun toggleMenu() {
        state = state.copy(menuState = state.menuState.toggle())
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
                    isLoading = false,
                    isSuccess = true,
                    hives = hives
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

    fun dateMillisToDateString(dateMillis: String): String {
        val format = DateFormat.getDateInstance(DateFormat.LONG)
        return (format.format(dateMillis.toLong()))
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

    fun createHive() {
        viewModelScope.launch {
            runCatching {
                val hives = hiveRepository.getAllHives()
                val hive = Hive(
                    HiveInfo(
                        id = UUID.randomUUID().toString(),
                        name = "Hive ${hives.size + 1}",
                    ),
                    HiveConditions(),
                    HiveHealth(),
                    HiveFeeding(),
                    HiveNotes()
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
                hiveRepository.updateHive(hive)
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
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            runCatching {
                hiveRepository.deleteHive(hiveId)
            }.onSuccess {
                getAllHives()
            }.onFailure {
                state =
                    state.copy(isLoading = false, isError = true, errorMessage = it.message ?: "")
                // Show error message
                Toast.makeText(app, it.message, Toast.LENGTH_SHORT).show()
            }
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