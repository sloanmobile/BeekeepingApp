package com.reedsloan.beekeepingapp.domain.repo

import com.reedsloan.beekeepingapp.data.Message
import com.reedsloan.beekeepingapp.data.local.UserData
import com.reedsloan.beekeepingapp.data.local.hive.Hive

interface LocalUserDataRepository {
    suspend fun getUserData(): Result<UserData>
    suspend fun exportToCsv(hive: Hive): Result<Message>

    suspend fun updateUserData(userData: UserData)

    suspend fun deleteUserData()
}