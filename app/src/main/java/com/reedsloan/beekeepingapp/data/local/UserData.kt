package com.reedsloan.beekeepingapp.data.local

import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.data.local.tasks.Task

data class UserData(
    val userPreferences: UserPreferences = UserPreferences(),
    val hives: List<Hive> = emptyList(),
    val lastUpdated: Long = 0L,
    val userId: String = "",
    val isUserPremium: Boolean = false,
    val tasks: List<Task> = emptyList(),
)
