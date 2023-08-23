package com.reedsloan.beekeepingapp.presentation.home_screen

import com.reedsloan.beekeepingapp.BuildConfig
import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.data.local.hive.HiveConditions
import com.reedsloan.beekeepingapp.data.local.hive.HiveDataEntry
import com.reedsloan.beekeepingapp.data.local.hive.HiveFeeding
import com.reedsloan.beekeepingapp.data.local.hive.HiveHealth
import com.reedsloan.beekeepingapp.presentation.screens.Screen
import java.time.LocalDate

data class HiveScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String = "",
    val navigationBarMenuState: MenuState = MenuState.CLOSED,
    val editHiveMenuState: MenuState = MenuState.CLOSED,
    val isCameraPermissionAllowed: Boolean = false,
    val isStoragePermissionAllowed: Boolean = false,
    val selectedHive: Hive? = null,
    val userPreferences: UserPreferences = UserPreferences(),
    val appVersionNumber: String = BuildConfig.VERSION_NAME,
    val appVersionCode: Int = BuildConfig.VERSION_CODE,
    val showExtraButtons: Boolean = false,
    val hiveDeleteMode: Boolean = false,
    val selectionList: List<String> = emptyList(),
    val showAddHiveButton: Boolean = true,
    val currentScreenName: String = Screen.HomeScreen.name,
    val editingTextField: Boolean = false,
    val showDeleteHiveDialog: Boolean = false,
    val selectedDataEntry: HiveDataEntry = HiveDataEntry(
        hiveId = "",
        date = LocalDate.now().toString(),
        hiveConditions = HiveConditions(),
        hiveHealth = HiveHealth(),
        feeding = HiveFeeding(),
        localPhotoUris = emptyList()
    )
)