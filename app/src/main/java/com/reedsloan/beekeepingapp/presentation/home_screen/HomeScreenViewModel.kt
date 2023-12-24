package com.reedsloan.beekeepingapp.presentation.home_screen

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.reedsloan.beekeepingapp.data.local.UserData
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
    val app: Application,
    val userDataRepository: UserDataRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())

    val state = _state.asStateFlow()

    fun onEvent(it: HomeScreenEvent, navController: NavHostController) {
        when (it) {
            is HomeScreenEvent.OnAllHivesClicked -> {
                navController.navigate(Screen.HivesScreen.route)
            }

            is HomeScreenEvent.OnTaskClicked -> {

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

            is HomeScreenEvent.OnNavigateToHomeScreen -> {
                getUserData()
            }
        }
    }

    private fun getUserData() {
        _state.update {
            it.copy(
                isLoading = true,
                error = null
            )
        }
        viewModelScope.launch {
            userDataRepository.getUserData().onSuccess {
                updateUserData(it)
            }.onFailure {
                showError(it.message ?: "Unknown error")
            }
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