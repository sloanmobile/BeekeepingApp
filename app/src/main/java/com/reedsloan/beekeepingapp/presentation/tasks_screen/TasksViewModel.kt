package com.reedsloan.beekeepingapp.presentation.tasks_screen

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
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
class TasksViewModel @Inject constructor(
    private val app: Application,
    private val remoteUserDataRepository: UserDataRepository,
    private val localUserDataRepository: LocalUserDataRepository
) : ViewModel() {
    private val _state = MutableStateFlow(TasksScreenState())
    val state = _state.asStateFlow()

    fun onEvent(event: TasksScreenEvent, navController: NavController) {
        when (event) {
            is TasksScreenEvent.OnUpdateUserData -> {
                _state.update {
                    it.copy(
                        userData = event.userData,
                    )
                }
            }

            is TasksScreenEvent.OnBackClicked -> {
                navController.popBackStack()
            }

            is TasksScreenEvent.OnTaskClicked -> {
                viewModelScope.launch {
                    // toggle task completed
                    updateTask(event.task.copy(isCompleted = !event.task.isCompleted))
                }
            }

            is TasksScreenEvent.OnUpdateTaskClicked -> {
                viewModelScope.launch {
                    updateTask(event.task)
                }
            }

            is TasksScreenEvent.OnUpdateCategoryClicked -> {
                viewModelScope.launch {
                    updateCategory(event.oldCategory, event.newCategory)
                }
            }

            is TasksScreenEvent.OnCreateNewTaskClicked -> {
                viewModelScope.launch {
                    createNewTask(event.task)
                }
            }

            is TasksScreenEvent.OnDeleteClicked -> {
                navController.popBackStack()
            }

            is TasksScreenEvent.OnCategoryClicked -> {
                updateTaskFilter(TasksFilter.Category(event.category))
            }

            is TasksScreenEvent.ClearTasksFilter -> {
                updateTaskFilter(TasksFilter.AllTasks)
            }

            is TasksScreenEvent.OnSettingsClicked -> {
                navController.navigate(Screen.SettingsScreen.route)
            }

            is TasksScreenEvent.OnNavigateToScreen -> {
                viewModelScope.launch {
                    getUserDataFromLocal()
                }
            }
        }
    }

    private fun updateTaskFilter(tasksFilter: TasksFilter) {
        if (tasksFilter == _state.value.tasksFilter) {
            _state.update {
                it.copy(
                    tasksFilter = TasksFilter.AllTasks
                )
            }
            return
        }

        _state.update {
            it.copy(
                tasksFilter = tasksFilter
            )
        }
    }

    private suspend fun updateCategory(oldCategory: String, newCategory: String) {
        getUserDataFromLocal()
        _state.update {
            it.copy(
                userData = it.userData.copy(
                    tasks = it.userData.tasks.map { t ->
                        if (t.category == oldCategory) {
                            t.copy(category = newCategory)
                        } else {
                            t
                        }
                    }
                )
            )
        }
            saveUserDataToLocal()
        saveUserDataToRemote()
    }

    private suspend fun updateTask(task: Task) {
        _state.update {
            it.copy(
                userData = it.userData.copy(
                    tasks = it.userData.tasks.map { t ->
                        if (t.id == task.id) {
                            task
                        } else {
                            t
                        }
                    }
                )

            )
        }
        saveUserDataToLocal()
        saveUserDataToRemote()
    }

    private suspend fun createNewTask(task: Task) {
        getUserDataFromLocal()
        _state.update {
            it.copy(
                userData = it.userData.copy(
                    tasks = it.userData.tasks + task
                )
            )
        }
        saveUserDataToLocal()
        saveUserDataToRemote()
    }

    private suspend fun saveUserDataToLocal() {
        localUserDataRepository.updateUserData(state.value.userData)
    }

    private suspend fun getUserDataFromLocal() {
        localUserDataRepository.getUserData().onSuccess { result ->
            _state.update {
                it.copy(
                    userData = result
                )
            }
        }
    }

    private suspend fun saveUserDataToRemote() {
        Log.d(this::class.simpleName, "updateUserData: ${state.value.userData}")
        remoteUserDataRepository.updateUserData(state.value.userData)
    }

    fun updateUserData(userData: UserData) {
        _state.update {
            it.copy(
                userData = userData
            )
        }
    }
}