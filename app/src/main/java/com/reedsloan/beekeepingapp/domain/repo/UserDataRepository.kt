package com.reedsloan.beekeepingapp.domain.repo

import com.reedsloan.beekeepingapp.data.local.UserData

interface UserDataRepository {
    suspend fun updateUserData(userData: UserData)
    suspend fun getUserData(): Result<UserData>
}