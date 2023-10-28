package com.reedsloan.beekeepingapp.domain.repo

import com.reedsloan.beekeepingapp.data.UserPreferences
import com.reedsloan.beekeepingapp.data.local.hive.Hive

interface HiveRepository {
    suspend fun getHive(hiveId: Int): Hive
    suspend fun getAllHives(): List<Hive>
    suspend fun createHive(hive: Hive)
    suspend fun updateHive(hive: Hive)
    suspend fun deleteHive(hiveId: String)
    suspend fun deleteAllHives()
    suspend fun exportToCsv()

    suspend fun getUserPreferences(): UserPreferences
    suspend fun updateUserPreferences(userPreferences: UserPreferences)
    suspend fun resetUserPreferences()
}