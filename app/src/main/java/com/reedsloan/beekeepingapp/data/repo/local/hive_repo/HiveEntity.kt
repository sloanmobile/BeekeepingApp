package com.reedsloan.beekeepingapp.data.repo.local.hive_repo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.reedsloan.beekeepingapp.data.local.hive.Hive

@Entity(tableName = "hive")
data class HiveEntity(
    @PrimaryKey val id: String,
    val displayOrder: Int,
    val hive: Hive
)
