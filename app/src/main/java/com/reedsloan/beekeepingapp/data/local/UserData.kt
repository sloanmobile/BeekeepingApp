package com.reedsloan.beekeepingapp.data.local

import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.hive.Hive

data class UserData(
    val userPreferences: UserPreferences,
    val hives: List<Hive>,
    val lastUpdated: Long,
    val userId: String,
)
