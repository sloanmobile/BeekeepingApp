package com.reedsloan.beekeepingapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.reedsloan.beekeepingapp.data.UserPreferences

@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    val userPreferences: UserPreferences,
    @PrimaryKey val id: Int = 0,
)
