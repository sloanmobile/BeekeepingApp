package com.reedsloan.beekeepingapp.data.repo.local.hive_repo

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [HiveEntity::class, UserPreferencesEntity::class], version = 1, exportSchema = false)
@TypeConverters(HiveTypeConverter::class)
abstract class HiveDatabase: RoomDatabase() {
    abstract val dao: HiveDao
}