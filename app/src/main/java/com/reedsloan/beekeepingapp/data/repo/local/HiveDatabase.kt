package com.reedsloan.beekeepingapp.data.repo.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.reedsloan.beekeepingapp.data.local.hive.HiveEntity
import com.reedsloan.beekeepingapp.data.type_converter.HiveTypeConverter

@Database(entities = [HiveEntity::class], version = 1)
@TypeConverters(HiveTypeConverter::class)
abstract class HiveDatabase: RoomDatabase() {
    abstract val dao: HiveDao
}