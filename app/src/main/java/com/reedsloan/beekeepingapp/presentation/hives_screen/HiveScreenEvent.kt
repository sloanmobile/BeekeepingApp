package com.reedsloan.beekeepingapp.presentation.hives_screen


import androidx.navigation.NavController
import com.reedsloan.beekeepingapp.data.local.UserData

sealed class HiveScreenEvent {
    data class OnUpdateUserData(val userData: UserData) : HiveScreenEvent()
    data class OnNavigateToHiveDetailsScreen(val hiveId: String) : HiveScreenEvent()
    data object OnNavigateToHivesScreen : HiveScreenEvent()
    data class OnNavigateToHiveInspectionScreen(val hiveId: String) : HiveScreenEvent()

    data class OnCreateNewInspection(val inspectionId: String, val navController: NavController) :
        HiveScreenEvent()
}
