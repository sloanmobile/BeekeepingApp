package com.reedsloan.beekeepingapp.data.local

import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.hive.Hive

data class UserData(
    val userPreferences: UserPreferences = UserPreferences(),
    val hives: List<Hive> = emptyList(),
    val lastUpdated: Long = 0L,
    val userId: String = "",
)
