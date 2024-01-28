package com.reedsloan.beekeepingapp.data.repo.local.hive_repo

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [UserDataEntity::class], version = 1, exportSchema = false)
@TypeConverters(UserDataTypeConverter::class)
abstract class UserDataDatabase: RoomDatabase() {
    abstract val dao: UserDataDao
}