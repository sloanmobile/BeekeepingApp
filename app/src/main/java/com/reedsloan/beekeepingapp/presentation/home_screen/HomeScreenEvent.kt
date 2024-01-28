package com.reedsloan.beekeepingapp.presentation.home_screen

import com.reedsloan.beekeepingapp.data.local.UserData
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.data.local.tasks.Task

sealed class HomeScreenEvent {
    data class OnUpdateUserData(val userData: UserData) : HomeScreenEvent()

    data class OnTaskClicked(val task: Task) : HomeScreenEvent()

    data class OnHiveClicked(val hive: Hive) : HomeScreenEvent()
    data class OnUpdateDataUpdate(val userData: UserData) : HomeScreenEvent()

    data object OnAllTasksClicked : HomeScreenEvent()

    data object OnProfileClicked : HomeScreenEvent()

    data object OnSettingsClicked : HomeScreenEvent()

    data object OnBackClicked : HomeScreenEvent()

    data object OnAllHivesClicked : HomeScreenEvent()
    data object OnNavigateToScreen : HomeScreenEvent()
    data object OnSignInSuccess: HomeScreenEvent()
}
