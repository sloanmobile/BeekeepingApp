package com.reedsloan.beekeepingapp.presentation.hives_screen


import com.reedsloan.beekeepingapp.data.local.UserData

sealed class HiveScreenEvent {
    data class OnUpdateUserData(val userData: UserData) : HiveScreenEvent()
}
