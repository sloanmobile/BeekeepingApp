package com.reedsloan.beekeepingapp.data.repo.local.hive_repo

import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.repo.local.hive_repo.UserPreferencesEntity

fun UserPreferencesEntity.toUserPreferences(): UserPreferences {
    return this.userPreferences
}

fun UserPreferences.toUserPreferencesEntity(): UserPreferencesEntity {
    return UserPreferencesEntity(this)
}