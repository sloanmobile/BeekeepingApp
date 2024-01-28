package com.reedsloan.beekeepingapp.presentation.home_screen

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.reedsloan.beekeepingapp.data.local.UserData
import com.reedsloan.beekeepingapp.data.local.tasks.Task
import com.reedsloan.beekeepingapp.domain.repo.LocalUserDataRepository
import com.reedsloan.beekeepingapp.domain.repo.UserDataRepository
import com.reedsloan.beekeepingapp.presentation.common.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val app: Application,
    private val remoteUserDataRepository: UserDataRepository,
    private val localUserDataRepository: LocalUserDataRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())

    val state = _state.asStateFlow()

    fun onEvent(it: HomeScreenEvent, navController: NavHostController) {
        when (it) {
            is HomeScreenEvent.OnAllHivesClicked -> {
                navController.navigate(Screen.HivesScreen.route)
            }

            is HomeScreenEvent.OnTaskClicked -> {
                toggleTaskCompleted(it.task)
            }

            is HomeScreenEvent.OnAllTasksClicked -> {
                navController.navigate(Screen.TasksScreen.route)
            }

            is HomeScreenEvent.OnHiveClicked -> {
                navController.navigate(
                    Screen.HiveDetailsScreen.route + "?hiveId=${it.hive.id}"
                )
            }

            is HomeScreenEvent.OnProfileClicked -> {
                TODO("Not yet implemented")
            }

            is HomeScreenEvent.OnSettingsClicked -> {
                navController.navigate(Screen.SettingsScreen.route)
            }

            is HomeScreenEvent.OnBackClicked -> {
                closeOpenMenus()
            }

            is HomeScreenEvent.OnUpdateUserData -> {
                updateUserData(
                    it.userData
                )
            }

            is HomeScreenEvent.OnUpdateDataUpdate -> {
                updateUserData(it.userData)
            }

            is HomeScreenEvent.OnNavigateToScreen -> {
                viewModelScope.launch {
                    getUserDataFromLocal()
                }
            }

            is HomeScreenEvent.OnSignInSuccess -> {
                viewModelScope.launch {
                    getUserDataFromRemote()
                }
            }
        }
    }

    private suspend fun getUserDataFromLocal() {
        _state.update {
            it.copy(
                isLoading = true,
                error = null
            )
        }
        localUserDataRepository.getUserData().onSuccess {
            updateUserData(it)
            showSuccess()
        }.onFailure {
            showError(it.message ?: "Unknown error")
        }
    }

    private suspend fun saveUserDataToLocal() {
        localUserDataRepository.updateUserData(state.value.userData)
    }

    private fun toggleTaskCompleted(task: Task) {
        _state.update {
            it.copy(
                userData = it.userData.copy(
                    tasks = it.userData.tasks.map { t ->
                        if (t.id == task.id) {
                            t.copy(isCompleted = !t.isCompleted)
                        } else {
                            t
                        }
                    }
                )
            )
        }
        viewModelScope.launch {
            saveUserDataToLocal()
            saveUserDataToRemote()
        }
    }

    private suspend fun saveUserDataToRemote() {
        remoteUserDataRepository.updateUserData(state.value.userData)
    }

    private suspend fun getUserDataFromRemote() {
        _state.update {
            it.copy(
                isLoading = true,
                error = null
            )
        }
        remoteUserDataRepository.getUserData().onSuccess { userData ->
            // get local data, compare the two, and update remote with local if its newer
            localUserDataRepository.getUserData().onSuccess { localUserData ->
                if (localUserData.lastUpdated > userData.lastUpdated) {
                    remoteUserDataRepository.updateUserData(localUserData)
                } else {
                    // remote data is newer, so save it locally
                    updateUserData(userData)
                }
            }.onFailure {
                // no local data, so save remote data locally
                localUserDataRepository.updateUserData(userData)
            }
            saveUserDataToLocal()
        }.onFailure {
            showError(it.message ?: "Unknown error")
        }

    }

    private fun updateUserData(userData: UserData) {
        _state.update {
            it.copy(
                isLoading = false,
                userData = userData
            )
        }
    }

    private fun closeOpenMenus() {
        TODO("Not yet implemented")
    }

    private fun showError(s: String) {
        _state.update {
            it.copy(
                isLoading = false,
                error = s
            )
        }
    }

    private fun showSuccess() {
        _state.update {
            it.copy(
                isLoading = false,
                error = null
            )
        }
    }
}