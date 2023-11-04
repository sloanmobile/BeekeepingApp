package com.reedsloan.beekeepingapp.data.repo.remote.user_data_repo

import com.reedsloan.beekeepingapp.data.local.UserData
import com.reedsloan.beekeepingapp.domain.repo.UserDataRepository

class UserDataRepositoryImplTest: UserDataRepository {
    private var userData: UserData = UserData()

    override suspend fun updateUserData(userData: UserData) {
        this.userData = userData
    }
    override suspend fun getUserData(): Result<UserData> {
        return Result.success(userData)
    }
}
