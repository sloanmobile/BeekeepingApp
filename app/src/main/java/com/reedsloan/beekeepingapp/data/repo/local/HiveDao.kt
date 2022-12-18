package com.reedsloan.beekeepingapp.data.repo.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.reedsloan.beekeepingapp.data.local.UserPreferencesEntity
import com.reedsloan.beekeepingapp.data.local.hive.HiveEntity

@Dao
interface HiveDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertHive(hive: HiveEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateHive(hive: HiveEntity)

    @Query("SELECT * FROM hive WHERE id = :hiveId")
    suspend fun getHive(hiveId: Int): HiveEntity

    @Query("SELECT * FROM hive")
    suspend fun getAllHives(): List<HiveEntity>

    @Query("DELETE FROM hive WHERE id = :hiveId")
    suspend fun deleteHive(hiveId: String)

    @Query("DELETE FROM hive")
    suspend fun deleteAllHives()

    // user preferences
    @Query("SELECT * FROM user_preferences")
    suspend fun getUserPreferences(): UserPreferencesEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUserPreferences(userPreferences: UserPreferencesEntity)
}