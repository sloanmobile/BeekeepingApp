package com.reedsloan.beekeepingapp.data.mapper

import com.reedsloan.beekeepingapp.data.local.hive.Hive
import com.reedsloan.beekeepingapp.data.repo.local.hive_repo.HiveEntity

fun HiveEntity.toHive(): Hive {
    return this.hive
}

fun Hive.toHiveEntity(): HiveEntity {
    return HiveEntity(this.id, this.displayOrder,this)
}