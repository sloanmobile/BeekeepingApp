package com.reedsloan.beekeepingapp.presentation.tasks_screen

sealed class TasksFilter {
    data object AllTasks : TasksFilter()
    data object CompletedTasks : TasksFilter()
    data object IncompleteTasks : TasksFilter()
    data class Category(val category: String) : TasksFilter()

}
