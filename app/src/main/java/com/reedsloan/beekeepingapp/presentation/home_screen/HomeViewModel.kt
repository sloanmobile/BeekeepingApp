package com.reedsloan.beekeepingapp.presentation.home_screen

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.domain.repo.HiveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val app: Application,
    private val hiveRepository: HiveRepository
) : ViewModel() {
    var state by mutableStateOf(HomeScreenState())

    init {
        getAllHives()
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
            state = try {
                val hives = hiveRepository.getAllHives()
                state.copy(isLoading = false, isSuccess = true, hives = hives)
            } catch (e: Exception) {
                state.copy(isLoading = false, isError = true, errorMessage = e.message ?: "")
            }
        }
    }

    fun createHive(hive: Hive) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            state = try {
                hiveRepository.createHive(hive)
                val hives = hiveRepository.getAllHives()
                state.copy(isLoading = false, isSuccess = true, hives = hives)
            } catch (e: Exception) {
                state.copy(isLoading = false, isError = true, errorMessage = e.message ?: "")
            }
        }
    }

    fun updateHive(hive: Hive) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            state = try {
                hiveRepository.updateHive(hive)
                val hives = hiveRepository.getAllHives()
                state.copy(isLoading = false, isSuccess = true, hives = hives)
            } catch (e: Exception) {
                state.copy(isLoading = false, isError = true, errorMessage = e.message ?: "")
            }
        }
    }

    fun deleteHive(hiveId: Int) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            state = try {
                hiveRepository.deleteHive(hiveId)
                val hives = hiveRepository.getAllHives()
                state.copy(isLoading = false, isSuccess = true, hives = hives)
            } catch (e: Exception) {
                state.copy(isLoading = false, isError = true, errorMessage = e.message ?: "")
            }
        }
    }

    /**
     * **Use with caution.** This will delete all hives from the database.
     */
    fun deleteAllHives() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            state = try {
                hiveRepository.deleteAllHives()
                val hives = hiveRepository.getAllHives()
                state.copy(isLoading = false, isSuccess = true, hives = hives)
            } catch (e: Exception) {
                state.copy(isLoading = false, isError = true, errorMessage = e.message ?: "")
            }
        }
    }

}