package com.reedsloan.beekeepingapp.data.repo.local.hive_repo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.reedsloan.beekeepingapp.data.local.UserData

@Entity(tableName = "user_data")
data class UserDataEntity(
    @PrimaryKey
    val id: Int = 0,
    val userData: UserData
)
