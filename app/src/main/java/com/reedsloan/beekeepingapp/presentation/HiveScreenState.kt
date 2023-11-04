package com.reedsloan.beekeepingapp.presentation

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpOffset
import com.reedsloan.beekeepingapp.BuildConfig
import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.UserData
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.data.local.hive.HiveInspection
import com.reedsloan.beekeepingapp.presentation.common.MenuState
import com.reedsloan.beekeepingapp.presentation.common.Screen

data class HiveScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isSuccess: Boolean = false,
    val userData: UserData = UserData(),
    val errorMessage: String = "",
    val navigationBarMenuState: MenuState = MenuState.CLOSED,
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
    val selectedHiveInspection: HiveInspection? = null,
)

data class ContextMenuItem(
    val title: String,
    val icon: ImageVector? = null,
    val action: () -> Unit,
)