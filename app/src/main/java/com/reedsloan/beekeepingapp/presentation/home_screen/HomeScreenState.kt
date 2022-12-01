package com.reedsloan.beekeepingapp.presentation.home_screen

import com.reedsloan.beekeepingapp.data.local.hive.Hive

data class HomeScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String = "",
    val menuState: MenuState = MenuState.Closed,
    val isCameraPermissionAllowed: Boolean = false,
    val isStoragePermissionAllowed: Boolean = false,
    val hives: List<Hive> = emptyList()
)
