package com.reedsloan.beekeepingapp.presentation.tasks_screen

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.data.local.UserData
import com.reedsloan.beekeepingapp.data.local.tasks.Task
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
    private val userDataRepository: UserDataRepository
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
                // toggle task completed
                updateTask(event.task.copy(isCompleted = !event.task.isCompleted))
            }

            is TasksScreenEvent.OnUpdateTaskClicked -> {
                updateTask(event.task)
            }

            is TasksScreenEvent.OnUpdateCategoryClicked -> {
                updateCategory(event.oldCategory, event.newCategory)
            }

            is TasksScreenEvent.OnCreateNewTaskClicked -> {
                createNewTask(event.task)
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

    private fun updateCategory(oldCategory: String, newCategory: String) {
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
        syncUserDataToRemote()
    }

    private fun updateTask(task: Task) {
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
        syncUserDataToRemote()
    }

    private fun createNewTask(task: Task) {
        _state.update {
            it.copy(
                userData = it.userData.copy(
                    tasks = it.userData.tasks + task
                )
            )
        }
        syncUserDataToRemote()
    }

    private fun getUserData() {
        viewModelScope.launch {
            userDataRepository.getUserData().onSuccess {
                _state.update {
                    it.copy(
                        userData = it.userData
                    )
                }
            }
        }
    }

    private fun syncUserDataToRemote() {
        viewModelScope.launch {
            Log.d(this::class.simpleName, "updateUserData: ${state.value.userData}")
            userDataRepository.updateUserData(state.value.userData)
        }
    }

    fun updateUserData(userData: UserData) {
        _state.update {
            it.copy(
                userData = userData
            )
        }
    }
}