package com.reedsloan.beekeepingapp.data.repo.local.hive_repo

import com.reedsloan.beekeepingapp.data.local.UserData

fun UserDataEntity.toUserData(): UserData {
    return this.userData
}

fun UserData.toUserDataEntity(): UserDataEntity {
    return UserDataEntity(
        userData = this
    )
}