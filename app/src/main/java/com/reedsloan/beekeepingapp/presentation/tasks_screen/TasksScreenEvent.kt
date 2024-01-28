package com.reedsloan.beekeepingapp.presentation.tasks_screen

import com.reedsloan.beekeepingapp.data.local.UserData
import com.reedsloan.beekeepingapp.data.local.tasks.Task

sealed class TasksScreenEvent {
    data object OnBackClicked : TasksScreenEvent()

    data class OnTaskClicked(val task: Task) : TasksScreenEvent()

    data class OnCreateNewTaskClicked(val task: Task) : TasksScreenEvent()

    data class OnUpdateTaskClicked(val task: Task) : TasksScreenEvent()

    data class OnUpdateCategoryClicked(val oldCategory: String, val newCategory: String) : TasksScreenEvent()

    data class OnDeleteClicked(val task: Task) : TasksScreenEvent()
    data class OnCategoryClicked(val category: String) : TasksScreenEvent()
    data class OnUpdateUserData(val userData: UserData) : TasksScreenEvent()
    data object ClearTasksFilter : TasksScreenEvent()
    data object OnSettingsClicked : TasksScreenEvent()
    data object OnNavigateToScreen: TasksScreenEvent()
}
