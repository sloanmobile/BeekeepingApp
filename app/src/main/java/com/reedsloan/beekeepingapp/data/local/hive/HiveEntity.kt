package com.reedsloan.beekeepingapp.data.local.hive

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hive")
data class HiveEntity(
    @PrimaryKey val id: String,
    val hive: Hive
)
