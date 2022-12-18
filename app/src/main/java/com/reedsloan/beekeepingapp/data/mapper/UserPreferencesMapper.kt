package com.reedsloan.beekeepingapp.data.mapper

import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.UserPreferencesEntity

fun UserPreferencesEntity.toUserPreferences(): UserPreferences {
    return this.userPreferences
}

fun UserPreferences.toUserPreferencesEntity(): UserPreferencesEntity {
    return UserPreferencesEntity(this)
}