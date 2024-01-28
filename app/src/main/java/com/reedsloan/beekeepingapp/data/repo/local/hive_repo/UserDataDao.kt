package com.reedsloan.beekeepingapp.data.repo.local.hive_repo

import androidx.room.Dao
import androidx.room.Query
import com.reedsloan.beekeepingapp.data.local.UserData

@Dao
interface UserDataDao {

    // get user data
    @Query("SELECT * FROM user_data WHERE id = 0")
    suspend fun getUserData(): UserDataEntity?

    // insert or update user data
    @Query("INSERT OR REPLACE INTO user_data (id, userData) VALUES (0, :userData)")
    suspend fun updateUserData(userData: UserData)

    // delete user data
    @Query("DELETE FROM user_data WHERE id = 0")
    suspend fun deleteUserData()
}