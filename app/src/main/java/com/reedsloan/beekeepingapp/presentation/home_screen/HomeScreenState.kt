package com.reedsloan.beekeepingapp.presentation.home_screen

import com.reedsloan.beekeepingapp.data.local.UserData


data class HomeScreenState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val userData: UserData = UserData(),
)