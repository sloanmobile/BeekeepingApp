package com.reedsloan.beekeepingapp.presentation.tasks_screen

import com.reedsloan.beekeepingapp.data.local.UserData
import com.reedsloan.beekeepingapp.data.local.tasks.Task
import java.time.LocalDate
import java.util.UUID

data class TasksScreenState(
    val isLoading: Boolean = false,
    val error: String = "",
    val userData: UserData = UserData(),
    val tasksFilter: TasksFilter = TasksFilter.AllTasks,
)